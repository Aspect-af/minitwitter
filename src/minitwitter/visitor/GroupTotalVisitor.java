package minitwitter.visitor;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

/**
 * VISITOR: counts the total number of groups in the tree.
 *
 */
public class GroupTotalVisitor implements Visitor {

    private int total = 0;

    @Override
    public void visit(User user) {
        // a user is not a group
    }

    @Override
    public void visit(UserGroup group) {
        total++;
    }

    public int getTotal() {
        return total;
    }
}
