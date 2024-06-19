package dev.vfyjxf.jeiutilities.api.event;

import dev.vfyjxf.jeiutilities.ui.InputContext;
import dev.vfyjxf.jeiutilities.api.ui.event.UIEventDefinition;
import dev.vfyjxf.jeiutilities.api.ui.event.UIEventFactory;

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
        boolean onKeyPressed(IEventContext.Common context, InputContext input);
    }

    interface OnKeyReleased {
        boolean onKeyReleased(IEventContext.Common context, InputContext input);
    }

    interface OnMouseClicked {
        boolean onClicked(IEventContext.Common context, InputContext input);
    }

    interface OnMouseReleased {
        boolean onReleased(IEventContext.Common context, InputContext input);
    }

    interface OnMouseDragged {
        void onDragged(IEventContext.Common context, InputContext input, double deltaX, double deltaY);
    }

    interface OnMouseScrolled {
        void onScrolled(IEventContext.Common context, InputContext input, double scrollDelta);
    }

    interface OnMouseMoved {
        void onMoved(IEventContext.Common context, InputContext input);
    }

}
