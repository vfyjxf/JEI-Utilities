package com.github.vfyjxf.jeiutilities.event;

import com.github.vfyjxf.jeiutilities.event.IEventContext.Common;
import com.github.vfyjxf.jeiutilities.ui.InputContext;
import com.github.vfyjxf.jeiutilities.ui.event.UIEventDefinition;
import com.github.vfyjxf.jeiutilities.ui.event.UIEventFactory;

public interface IInputEvent {

    UIEventDefinition<OnKeyPressed> onKeyPressed = UIEventFactory.define(OnKeyPressed.class, listeners -> (context, input) -> {
        boolean result = false;
        for (var listener : listeners) {
            result |= listener.onKeyPressed(context, input);
            if (context.isInterrupted()) return result;
        }
        return result;
    });

    UIEventDefinition<OnKeyReleased> onKeyReleased = UIEventFactory.define(OnKeyReleased.class, listeners -> (context, input) -> {
        boolean result = false;
        for (var listener : listeners) {
            result |= listener.onKeyReleased(context, input);
            if (context.isInterrupted()) return result;
        }
        return result;
    });

    UIEventDefinition<OnMouseClicked> onMouseClicked = UIEventFactory.define(OnMouseClicked.class, listeners -> (context, input) -> {
        boolean result = false;
        for (var listener : listeners) {
            result |= listener.onClicked(context, input);
            if (context.isCancelled()) return result;
        }
        return result;
    });

    UIEventDefinition<OnMouseReleased> onMouseReleased = UIEventFactory.define(OnMouseReleased.class, listeners -> (context, input) -> {
        boolean result = false;
        for (var listener : listeners) {
            result |= listener.onReleased(context, input);
            if (context.isCancelled()) return result;
        }
        return result;
    });


    interface OnKeyPressed {
        boolean onKeyPressed(Common context, InputContext input);
    }

    interface OnKeyReleased {
        boolean onKeyReleased(Common context, InputContext input);
    }

    interface OnMouseClicked {
        boolean onClicked(Common context, InputContext input);
    }

    interface OnMouseReleased {
        boolean onReleased(Common context, InputContext input);
    }

    interface OnMouseDragged {
        void onDragged(Common context, InputContext input, double deltaX, double deltaY);
    }

    interface OnMouseScrolled {
        void onScrolled(Common context, InputContext input, double scrollDelta);
    }

    interface OnMouseMoved {
        void onMoved(Common context, InputContext input);
    }

}
