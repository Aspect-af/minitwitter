package minitwitter.visitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import minitwitter.model.User;
import minitwitter.model.UserGroup;

/**
 * VISITOR: computes the percentage of "positive" tweet messages across all
 * users' news feeds.
 *
 */
public class PositivePercentageVisitor implements Visitor {

    /** Our chosen set of positive words (easily extended). */
    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "good", "great", "excellent", "awesome", "amazing", "nice",
            "love", "loved", "happy", "wonderful", "fantastic", "cool",
            "best", "perfect", "fun", "like", "wow", "win", "won"));

    private int totalMessages = 0;
    private int positiveMessages = 0;

    @Override
    public void visit(User user) {
        for (String message : user.getNewsFeed()) {
            totalMessages++;
            if (isPositive(message)) {
                positiveMessages++;
            }
        }
    }

    @Override
    public void visit(UserGroup group) {
        // messages live on users, not on groups
    }

    private boolean isPositive(String message) {
        // Feed entries look like "author: body". Only scan the body so an
        // author whose ID happens to be a positive word is not miscounted.
        int colon = message.indexOf(':');
        String body = (colon >= 0) ? message.substring(colon + 1) : message;
        for (String token : body.toLowerCase().split("[^a-z]+")) {
            if (POSITIVE_WORDS.contains(token)) {
                return true;
            }
        }
        return false;
    }

    public double getPercentage() {
        if (totalMessages == 0) {
            return 0.0;
        }
        return (positiveMessages * 100.0) / totalMessages;
    }

    public int getPositiveCount() {
        return positiveMessages;
    }

    public int getTotalMessages() {
        return totalMessages;
    }
}
