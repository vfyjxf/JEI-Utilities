package dev.vfyjxf.jeiutilities.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Event<T> implements IEvent<T> {
    private final Function<List<T>, T> function;
    private T invoker = null;
    private final ArrayList<T> listeners;

    private Event(Function<List<T>, T> function) {
        this.function = function;
        listeners = new ArrayList<>();
    }

    @Override
    public T invoker() {
        if (invoker == null) {
            update();
        }
        return invoker;
    }

    @Override
    public T register(T listener) {
        listeners.add(listener);
        invoker = null;
        return listener;
    }

    @Override
    public void unregister(T listener) {
        listeners.remove(listener);
        listeners.trimToSize();
        invoker = null;
    }

    @Override
    public boolean isRegistered(T listener) {
        return listeners.contains(listener);
    }

    @Override
    public void clearListeners() {
        listeners.clear();
        invoker = null;
    }

    public void update() {
        if (listeners.size() == 1) {
            invoker = listeners.get(0);
        } else {
            invoker = function.apply(listeners);
        }
    }
}
