package com.github.vfyjxf.jeiutilities.ui.event;

import com.github.vfyjxf.jeiutilities.event.EventFactory;
import com.github.vfyjxf.jeiutilities.event.IEvent;
import com.github.vfyjxf.jeiutilities.event.IEventDefinition;

import java.util.List;
import java.util.function.Function;

public class UIEventDefinition<T> implements IEventDefinition<T> {

    private final Class<T> type;
    private final Function<List<T>, T> function;
    private final IEvent<T> global;

    UIEventDefinition(Class<T> type, Function<List<T>, T> function) {
        this.type = type;
        this.function = function;
        this.global = EventFactory.createEvent(function);
    }

    public Function<List<T>, T> function() {
        return function;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public IEvent<T> create() {
        return EventFactory.createEvent(function);
    }

    @Override
    public IEvent<T> global() {
        return global;
    }

}
