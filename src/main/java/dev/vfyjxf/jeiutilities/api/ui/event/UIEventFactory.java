package dev.vfyjxf.jeiutilities.api.ui.event;

import java.util.List;
import java.util.function.Function;

public final class UIEventFactory {

    private UIEventFactory() {
    }

    public static <T> UIEventDefinition<T> define(Class<T> type, Function<List<T>, T> listener) {
        return new UIEventDefinition<>(type, listener);
    }

}
