package dev.vfyjxf.jeiutilities.api.event;

import org.lwjgl.opengl.Display;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class EventChannel<E extends IEventHandler<E>> {

    private final E holder;
    private final Map<IEventDefinition<?>, IEvent<?>> listeners = new IdentityHashMap<>();

    public EventChannel(E holder) {
        this.holder = holder;
    }

    public E holder() {
        return holder;
    }

    public <T> IEvent<T> get(IEventDefinition<T> definition) {
        IEvent<T> event = (IEvent<T>) listeners.get(definition);
        if (event == null) {
            event = definition.create();
            listeners.put(definition, event);
        }
        return event;
    }

    public void clearAllListeners() {
        for (IEvent<?> listener : listeners.values()) {
            listener.clearListeners();
        }
    }

    public IEventContext.Common context() {
        return new IEventContext.Common(this);
    }

    public IEventContext.Cancelable cancelable() {
        return new IEventContext.Cancelable(this);
    }

    public IEventContext.Interruptible interruptible() {
        return new IEventContext.Interruptible(this);
    }

    public <T> void register(IEventDefinition<T> definition, T listener) {
        get(definition).register(listener);
    }

}
