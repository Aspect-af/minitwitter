package minitwitter.visitor;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

/** VISITOR: counts the total number of users in the tree. */
public class UserTotalVisitor implements Visitor {

    private int total = 0;

    @Override
    public void visit(User user) {
        total++;
    }

    @Override
    public void visit(UserGroup group) {
        // a group is not a user; nothing to count here
    }

    public int getTotal() {
        return total;
    }
}
