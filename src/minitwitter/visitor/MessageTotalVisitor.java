package minitwitter.visitor;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

/** VISITOR: counts every tweet message across all users' news feeds. */
public class MessageTotalVisitor implements Visitor {

    private int total = 0;

    @Override
    public void visit(User user) {
        total += user.getNewsFeed().size();
    }

    @Override
    public void visit(UserGroup group) {
        // messages live on users, not on groups
    }

    public int getTotal() {
        return total;
    }
}
