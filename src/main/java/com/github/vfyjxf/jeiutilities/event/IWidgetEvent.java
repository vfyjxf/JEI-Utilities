package com.github.vfyjxf.jeiutilities.event;

import com.github.vfyjxf.jeiutilities.event.IEventContext.Common;
import com.github.vfyjxf.jeiutilities.math.Dimension;
import com.github.vfyjxf.jeiutilities.math.Point;
import com.github.vfyjxf.jeiutilities.ui.event.UIEventDefinition;
import com.github.vfyjxf.jeiutilities.ui.event.UIEventFactory;
import com.github.vfyjxf.jeiutilities.ui.widgets.Widget;
import net.minecraft.client.Minecraft;

import java.util.List;

public interface IWidgetEvent {

    UIEventDefinition<OnPositionChanged> onPositionChanged = UIEventFactory.define(OnPositionChanged.class, listeners -> (context, position) -> {
        for (var listener : listeners) {
            listener.onPositionChanged(context, position);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnSizeChanged> onSizeChanged = UIEventFactory.define(OnSizeChanged.class, listeners -> (context, size) -> {
        for (var listener : listeners) {
            listener.onSizeChanged(context, size);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnRender> onRender = UIEventFactory.define(OnRender.class, listeners -> (graphics, mouseX, mouseY, partialTicks, context) -> {
        for (var listener : listeners) {
            listener.onRender(graphics, mouseX, mouseY, partialTicks, context);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnRender> onRenderPost = UIEventFactory.define(OnRender.class, listeners -> (graphics, mouseX, mouseY, partialTicks, context) -> {
        for (var listener : listeners) {
            listener.onRender(graphics, mouseX, mouseY, partialTicks, context);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnInit> onInit = UIEventFactory.define(OnInit.class, listeners -> (widget) -> {
        for (var listener : listeners) {
            listener.onInit(widget);
        }
    });

    UIEventDefinition<OnTick> onTick = UIEventFactory.define(OnTick.class, listeners -> () -> {
        for (var listener : listeners) {
            listener.onTick();
        }
    });

    UIEventDefinition<OnDelete> onDelete = UIEventFactory.define(OnDelete.class, listeners -> (self) -> {
        for (var listener : listeners) {
            listener.onDeleted(self);
        }
    });

    UIEventDefinition<OnChildAdded> onChildAdded = UIEventFactory.define(OnChildAdded.class, listeners -> (context, widget) -> {
        for (var listener : listeners) {
            listener.onChildAdded(context, widget);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnChildAddedPost> onChildAddedPost = UIEventFactory.define(OnChildAddedPost.class, listeners -> (context, widget) -> {
        for (var listener : listeners) {
            listener.onChildAdded(context, widget);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnChildRemoved> onChildRemoved = UIEventFactory.define(OnChildRemoved.class, listeners -> (context, widget) -> {
        for (var listener : listeners) {
            listener.onChildRemoved(context, widget);
            if (context.isInterrupted()) return;
        }
    });

    UIEventDefinition<OnChildRemovedPost> onChildRemovedPost = UIEventFactory.define(OnChildRemovedPost.class, listeners -> (context, widget) -> {
        for (var listener : listeners) {
            listener.onChildRemoved(context, widget);
            if (context.isInterrupted()) return;
        }
    });


    UIEventDefinition<OnTooltip> onTooltip = UIEventFactory.define(OnTooltip.class, listeners -> (context, tooltip) -> {
        for (var listener : listeners) {
            listener.onTooltip(context, tooltip);
            if (context.isInterrupted()) return;
        }
    });

    @FunctionalInterface
    interface OnPositionChanged extends IWidgetEvent {
        void onPositionChanged(Common context, Point position);
    }

    @FunctionalInterface
    interface OnSizeChanged extends IWidgetEvent {
        void onSizeChanged(Common context, Dimension size);
    }


    @FunctionalInterface
    interface OnRender extends IWidgetEvent {
        void onRender(Minecraft mc, int mouseX, int mouseY, float partialTicks, Common context);
    }

    @FunctionalInterface
    interface OnInit extends IWidgetEvent {
        void onInit(Widget widget);
    }

    @FunctionalInterface
    interface OnUpdate extends IWidgetEvent {
        void onUpdate(Widget widget);
    }

    @FunctionalInterface
    interface OnTick extends IWidgetEvent {
        void onTick();
    }

    @FunctionalInterface
    interface OnDelete extends IWidgetEvent {
        void onDeleted(Widget self);
    }

    @FunctionalInterface
    interface OnChildAdded extends IWidgetEvent {
        void onChildAdded(Common context, Widget widget);
    }

    @FunctionalInterface
    interface OnChildAddedPost extends IWidgetEvent {
        void onChildAdded(Common context, Widget widget);
    }

    @FunctionalInterface
    interface OnChildRemoved extends IWidgetEvent {
        void onChildRemoved(Common context, Widget widget);
    }

    @FunctionalInterface
    interface OnChildRemovedPost extends IWidgetEvent {
        void onChildRemoved(Common context, Widget widget);
    }

    @FunctionalInterface
    interface OnMouseHover extends IWidgetEvent {
        void onHover(int mouseX, int mouseY);
    }

    @FunctionalInterface
    interface OnTooltip extends IWidgetEvent {
        void onTooltip(Common context, List<String> tooltip);
    }

    interface OnMeasure extends IWidgetEvent {
        void onMeasure(Common context, Dimension size);
    }

    interface OnLayout extends IWidgetEvent {
        void onLayout(Common context);
    }


}
