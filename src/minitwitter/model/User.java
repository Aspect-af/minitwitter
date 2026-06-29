package minitwitter.model;

import java.util.ArrayList;
import java.util.List;

import minitwitter.observer.Observer;
import minitwitter.observer.Subject;
import minitwitter.visitor.Visitor;

/**
 * A Twitter user.
 *
 */
public class User implements Entry, Subject, Observer {

    private final String id;

    /** Followers of this user = the Observers notified when this user posts. */
    private final List<Observer> followers = new ArrayList<>();

    /** Users this user follows (kept for display in the User View). */
    private final List<User> followings = new ArrayList<>();

    /** This user's news feed: own tweets plus tweets from followed users. */
    private final List<String> newsFeed = new ArrayList<>();

    /** The most recent tweet posted by this user, e.g. {@code "john: hello"}. */
    private String lastTweet = "";

    /**
     * Hook used by the open {@code UserView} (if any) to refresh its news-feed
     * list whenever this user's feed changes. Stored as a plain {@link Runnable}
     * so the model layer has no compile-time dependency on the Swing UI.
     */
    private Runnable feedChangeListener;

    public User(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    // ----- COMPOSITE -----

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    // ----- OBSERVER (Subject side: this user is observed by its followers) -----

    @Override
    public void registerObserver(Observer observer) {
        if (!followers.contains(observer)) {
            followers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(Observer observer) {
        followers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer follower : followers) {
            follower.update(this);
        }
    }

    // ----- OBSERVER (Observer side: this user follows other users) -----

    /**
     * Called when a user we follow posts a tweet: copy that tweet into our own
     * news feed and refresh our view. We deliberately do NOT re-notify our own
     * followers here, so tweets are not re-broadcast (no cascade / re-tweet).
     */
    @Override
    public void update(User followee) {
        addToFeed(followee.getLastTweet());
    }

    // ----- Domain behaviour -----

    /** Follow another user (following groups is not supported by design). */
    public void follow(User target) {
        if (target == this || followings.contains(target)) {
            return;
        }
        followings.add(target);
        target.registerObserver(this);
    }

    /** Post a tweet: add it to our own feed, then notify all followers. */
    public void postTweet(String message) {
        lastTweet = id + ": " + message;
        addToFeed(lastTweet);   // refreshes this user's own view
        notifyObservers();      // delivers to followers (refreshes their views too)
    }

    private void addToFeed(String tweet) {
        newsFeed.add(tweet);
        if (feedChangeListener != null) {
            feedChangeListener.run();
        }
    }

    public void setFeedChangeListener(Runnable listener) {
        this.feedChangeListener = listener;
    }

    public String getLastTweet() {
        return lastTweet;
    }

    public List<User> getFollowings() {
        return followings;
    }

    public List<String> getNewsFeed() {
        return newsFeed;
    }

    /** Used as the node label in the admin control-panel tree. */
    @Override
    public String toString() {
        return id;
    }
}
