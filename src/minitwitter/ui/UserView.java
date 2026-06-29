package minitwitter.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import minitwitter.model.User;

/**
 * The User View window (see Figure 2 in the assignment). Several of these may be
 * open at once, one per user.
 *
 */
public class UserView extends JFrame {

    private final User user;
    private final AdminControlPanel admin;

    private final DefaultListModel<String> followingModel = new DefaultListModel<>();
    private final DefaultListModel<String> newsFeedModel = new DefaultListModel<>();

    private JTextField followIdField;
    private JTextField tweetField;

    public UserView(User user, AdminControlPanel admin) {
        super("User View - " + user.getId());
        this.user = user;
        this.admin = admin;

        buildUI();

        // OBSERVER -> view refresh hook: rebuild the feed list on every change.
        user.setFeedChangeListener(this::refreshNewsFeed);
        refreshFollowing();
        refreshNewsFeed();

        setSize(420, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                admin.onUserViewClosed(user);
            }
        });
    }

    private void buildUI() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.weightx = 1;

        // Row 0: follow controls
        followIdField = new JTextField();
        followIdField.setToolTipText("User Id to follow");
        JButton followBtn = new JButton("Follow User");
        JPanel followPanel = new JPanel(new BorderLayout(6, 0));
        followPanel.add(labeled("User Id", followIdField), BorderLayout.CENTER);
        followPanel.add(followBtn, BorderLayout.EAST);
        c.gridy = 0; c.weighty = 0;
        main.add(followPanel, c);

        // Row 1: current following list (grows)
        JList<String> followingList = new JList<>(followingModel);
        JScrollPane followingScroll = new JScrollPane(followingList);
        followingScroll.setBorder(BorderFactory.createTitledBorder("Current Following"));
        c.gridy = 1; c.weighty = 0.5;
        main.add(followingScroll, c);

        // Row 2: tweet controls
        tweetField = new JTextField();
        tweetField.setToolTipText("Type a tweet");
        JButton postBtn = new JButton("Post Tweet");
        JPanel tweetPanel = new JPanel(new BorderLayout(6, 0));
        tweetPanel.add(labeled("Tweet Message", tweetField), BorderLayout.CENTER);
        tweetPanel.add(postBtn, BorderLayout.EAST);
        c.gridy = 2; c.weighty = 0;
        main.add(tweetPanel, c);

        // Row 3: news feed list (grows)
        JList<String> newsFeedList = new JList<>(newsFeedModel);
        JScrollPane newsFeedScroll = new JScrollPane(newsFeedList);
        newsFeedScroll.setBorder(BorderFactory.createTitledBorder("News Feed"));
        c.gridy = 3; c.weighty = 0.5;
        main.add(newsFeedScroll, c);

        setContentPane(main);

        followBtn.addActionListener(e -> followUser());
        postBtn.addActionListener(e -> postTweet());
    }

    private JPanel labeled(String title, JTextField field) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void followUser() {
        String targetId = followIdField.getText().trim();
        if (targetId.isEmpty()) {
            error("Please enter a User Id to follow.");
            return;
        }
        if (targetId.equals(user.getId())) {
            error("You cannot follow yourself.");
            return;
        }
        User target = admin.findUser(targetId);
        if (target == null) {
            error("No user with ID '" + targetId + "' exists.");
            return;
        }
        user.follow(target);
        refreshFollowing();
        followIdField.setText("");
    }

    private void postTweet() {
        String message = tweetField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        // Posting drives the OBSERVER notification: this user's feed updates and
        // every follower's feed (and open view) updates automatically.
        user.postTweet(message);
        tweetField.setText("");
    }

    private void refreshFollowing() {
        followingModel.clear();
        for (User followed : user.getFollowings()) {
            followingModel.addElement(followed.getId());
        }
    }

    private void refreshNewsFeed() {
        newsFeedModel.clear();
        for (String tweet : user.getNewsFeed()) {
            newsFeedModel.addElement(tweet);
        }
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "User View",
                JOptionPane.ERROR_MESSAGE);
    }
}
