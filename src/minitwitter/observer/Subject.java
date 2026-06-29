package minitwitter.observer;

/**
 * Subject interface (OBSERVER design pattern).
 *
 */
public interface Subject {

    void registerObserver(Observer observer);

    void unregisterObserver(Observer observer);

    void notifyObservers();
}
