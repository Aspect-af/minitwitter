package minitwitter.observer;

import minitwitter.model.User;

/**
 * Observer interface (OBSERVER design pattern).
 *
 */
public interface Observer {

    /**
     * @param followee the Subject (a followed user) that just posted a tweet.
     */
    void update(User followee);
}
