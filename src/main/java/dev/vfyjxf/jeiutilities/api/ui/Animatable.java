package dev.vfyjxf.jeiutilities.api.ui;

public interface Animatable<T extends Animatable<T>> {

    T interpolate(T next, float delta);

}
