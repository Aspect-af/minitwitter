package minitwitter.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import minitwitter.model.User;
import minitwitter.model.UserGroup;
import minitwitter.visitor.GroupTotalVisitor;
import minitwitter.visitor.MessageTotalVisitor;
import minitwitter.visitor.PositivePercentageVisitor;
import minitwitter.visitor.UserTotalVisitor;

/**
 * The Admin Control Panel: the main window and entry point of the program
 */
public class AdminControlPanel extends JFrame {

    // -------------------- SINGLETON --------------------

    private static AdminControlPanel instance;

    /** @return the single shared Admin Control Panel instance. */
    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

    // -------------------- Model --------------------

    /** The COMPOSITE root that contains every user and group. */
    private final UserGroup root = new UserGroup("Root");

    /** id -> user, for unique-ID checks and follow look-ups. */
    private final Map<String, User> users = new HashMap<>();

    /** id -> group, for unique-ID checks (includes the Root group). */
    private final Map<String, UserGroup> groups = new HashMap<>();

    /** Open user views, so a user is only shown in one window at a time. */
    private final Map<User, UserView> openViews = new HashMap<>();

    // -------------------- Widgets --------------------

    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
    private DefaultTreeModel treeModel;
    private JTree tree;

    private JTextField userIdField;
    private JTextField groupIdField;

    private AdminControlPanel() {
        super("Mini Twitter - Admin Control Panel");
        groups.put(root.getId(), root);
        buildUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 460);
        setLocationRelativeTo(null);
    }

    // -------------------- UI construction --------------------

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        // ----- Tree view (left) -----
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new EntryTreeCellRenderer());
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setBorder(BorderFactory.createTitledBorder("Tree View"));
        treeScroll.setPreferredSize(new Dimension(260, 0));
        add(treeScroll, BorderLayout.WEST);

        // ----- Control area (right) -----
        JPanel controls = new JPanel(new BorderLayout(8, 8));
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top: add user / add group / open user view
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        userIdField = new JTextField();
        userIdField.setToolTipText("User Id");
        groupIdField = new JTextField();
        groupIdField.setToolTipText("Group Id");

        JButton addUserBtn = new JButton("Add User");
        JButton addGroupBtn = new JButton("Add Group");
        JButton openViewBtn = new JButton("Open User View");

        c.gridx = 0; c.gridy = 0; c.weightx = 1; top.add(labeled("User Id", userIdField), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 0; top.add(addUserBtn, c);
        c.gridx = 0; c.gridy = 1; c.weightx = 1; top.add(labeled("Group Id", groupIdField), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 0; top.add(addGroupBtn, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weightx = 1; top.add(openViewBtn, c);
        controls.add(top, BorderLayout.NORTH);

        // Bottom: the four analysis buttons (2 x 2 grid)
        JPanel analysis = new JPanel(new GridLayout(2, 2, 8, 8));
        JButton userTotalBtn = new JButton("Show User Total");
        JButton groupTotalBtn = new JButton("Show Group Total");
        JButton messageTotalBtn = new JButton("Show Messages Total");
        JButton positiveBtn = new JButton("Show Positive Percentage");
        analysis.add(userTotalBtn);
        analysis.add(groupTotalBtn);
        analysis.add(messageTotalBtn);
        analysis.add(positiveBtn);
        controls.add(analysis, BorderLayout.SOUTH);

        add(controls, BorderLayout.CENTER);

        // ----- Actions -----
        addUserBtn.addActionListener(e -> addUser());
        addGroupBtn.addActionListener(e -> addGroup());
        openViewBtn.addActionListener(e -> openUserView());

        userTotalBtn.addActionListener(e -> {
            UserTotalVisitor v = new UserTotalVisitor();
            root.accept(v);
            info("Total number of users: " + v.getTotal());
        });
        groupTotalBtn.addActionListener(e -> {
            GroupTotalVisitor v = new GroupTotalVisitor();
            root.accept(v);
            info("Total number of groups: " + v.getTotal());
        });
        messageTotalBtn.addActionListener(e -> {
            MessageTotalVisitor v = new MessageTotalVisitor();
            root.accept(v);
            info("Total number of tweet messages: " + v.getTotal());
        });
        positiveBtn.addActionListener(e -> {
            PositivePercentageVisitor v = new PositivePercentageVisitor();
            root.accept(v);
            info(String.format("Positive messages: %d of %d (%.1f%%)",
                    v.getPositiveCount(), v.getTotalMessages(), v.getPercentage()));
        });

        tree.expandPath(new TreePath(rootNode.getPath()));
    }

    /** Wrap a field with a small title border so the UI is self-explaining. */
    private JPanel labeled(String title, Component field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    // -------------------- Actions --------------------

    private void addUser() {
        String id = userIdField.getText().trim();
        if (id.isEmpty()) {
            error("Please enter a User Id.");
            return;
        }
        if (users.containsKey(id) || groups.containsKey(id)) {
            error("ID '" + id + "' already exists. IDs must be unique.");
            return;
        }
        DefaultMutableTreeNode parentNode = resolveParentGroupNode();
        UserGroup parentGroup = (UserGroup) parentNode.getUserObject();

        User user = new User(id);
        parentGroup.addEntry(user);
        users.put(id, user);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(user);
        treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
        userIdField.setText("");
    }

    private void addGroup() {
        String id = groupIdField.getText().trim();
        if (id.isEmpty()) {
            error("Please enter a Group Id.");
            return;
        }
        if (users.containsKey(id) || groups.containsKey(id)) {
            error("ID '" + id + "' already exists. IDs must be unique.");
            return;
        }
        DefaultMutableTreeNode parentNode = resolveParentGroupNode();
        UserGroup parentGroup = (UserGroup) parentNode.getUserObject();

        UserGroup group = new UserGroup(id);
        parentGroup.addEntry(group);
        groups.put(id, group);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(group);
        treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
        groupIdField.setText("");
    }

    private DefaultMutableTreeNode resolveParentGroupNode() {
        DefaultMutableTreeNode selected =
                (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selected == null) {
            return rootNode;
        }
        if (selected.getUserObject() instanceof UserGroup) {
            return selected;
        }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selected.getParent();
        return (parent != null) ? parent : rootNode;
    }

    private void openUserView() {
        DefaultMutableTreeNode selected =
                (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selected == null || !(selected.getUserObject() instanceof User)) {
            error("Please select a user in the tree first.");
            return;
        }
        User user = (User) selected.getUserObject();

        UserView existing = openViews.get(user);
        if (existing != null) {           // already open: just bring it forward
            existing.toFront();
            existing.requestFocus();
            return;
        }
        UserView view = new UserView(user, this);
        openViews.put(user, view);
        view.setVisible(true);
    }

    /** Look up a user by ID (used by the User View's "Follow" button). */
    public User findUser(String id) {
        return users.get(id);
    }

    /** Called by a {@link UserView} when its window is closed. */
    void onUserViewClosed(User user) {
        openViews.remove(user);
        user.setFeedChangeListener(null);
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Mini Twitter",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Mini Twitter",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Custom tree renderer so groups and users are visually distinct: groups
     * use a folder icon, users use a file/leaf icon.
     */
    private static class EntryTreeCellRenderer extends DefaultTreeCellRenderer {

        private final Icon groupIcon = UIManager.getIcon("FileView.directoryIcon");
        private final Icon userIcon = UIManager.getIcon("FileView.fileIcon");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof UserGroup && groupIcon != null) {
                setIcon(groupIcon);
            } else if (userObject instanceof User && userIcon != null) {
                setIcon(userIcon);
            }
            return this;
        }
    }
}
