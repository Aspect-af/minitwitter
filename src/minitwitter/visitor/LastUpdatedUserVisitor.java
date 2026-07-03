package minitwitter.visitor;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

public class LastUpdatedUserVisitor implements Visitor {

    private User lastUpdatedUser = null;
    private long latestTime = 0L;

    @Override
    public void visit(User user) {
        if (user.getLastUpdateTime() > latestTime) {
            latestTime = user.getLastUpdateTime();
            lastUpdatedUser = user;
        }
    }

    @Override
    public void visit(UserGroup group) {
        // groups don't post tweets
    }

    public User getLastUpdatedUser() {
        return lastUpdatedUser;
    }

    public long getLatestTime() {
        return latestTime;
    }
}
