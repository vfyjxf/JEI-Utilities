package com.github.vfyjxf.jeiutilities.event;

public interface IEventDefinition<T> {

    Class<T> type();

    IEvent<T> create();

    IEvent<T> global();

}
