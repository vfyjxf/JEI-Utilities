package dev.vfyjxf.jeiutilities.api.event;

public interface IEventHandler<T extends IEventHandler<T>> {

    EventChannel<T> channel();

}
