package dev.vfyjxf.jeiutilities.api.event;


/**
 * Architectury like event system.
 */
public interface IEvent<T> {

    T invoker();

    T register(T listener);

    void unregister(T listener);

    boolean isRegistered(T listener);

    void clearListeners();

}
