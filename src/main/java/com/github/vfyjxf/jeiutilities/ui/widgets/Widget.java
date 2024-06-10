package com.github.vfyjxf.jeiutilities.ui.widgets;

import com.github.vfyjxf.jeiutilities.event.EventManager;
import com.github.vfyjxf.jeiutilities.event.IEventContext;
import com.github.vfyjxf.jeiutilities.event.IEventDefinition;
import com.github.vfyjxf.jeiutilities.event.IInputEvent;
import com.github.vfyjxf.jeiutilities.event.IWidgetEvent;
import com.github.vfyjxf.jeiutilities.math.Dimension;
import com.github.vfyjxf.jeiutilities.math.Point;
import com.github.vfyjxf.jeiutilities.ui.InputContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Widget {

    protected EventManager<Widget> eventManager = new EventManager<>(this);
    protected WidgetGroup<?> parent;

    protected boolean initialized = false;
    protected Point position = Point.ZERO;
    protected Point absolute;
    protected Dimension size = Dimension.ZERO;
    protected boolean visible = true;
    protected boolean active = true;

    public Widget setParent(WidgetGroup<?> parent) {
        this.parent = parent;
        return this;
    }

    public Point getPos() {
        return position;
    }

    public Widget setPosition(Point position) {
        this.position = position;
        return this;
    }

    public Widget setPosition(int x, int y) {
        return setPosition(new Point(x, y));
    }

    public Widget setX(int x) {
        return setPosition(x, position.y);
    }

    public Widget setY(int y) {
        return setPosition(position.x, y);
    }

    public Widget translate(int x, int y) {
        return setPosition(position.x + x, position.y + y);
    }

    protected Point calculateAbsolute() {
        if (parent == null) return position;
        WidgetGroup<?> parent = this.parent;
        Point absolute = position.copy();
        while (parent != null && parent != this) {
            Point parentPos = parent.getPos();
            absolute.translate(parentPos.x, parentPos.y);
            parent = parent.parent;
        }
        return absolute;
    }

    protected void onPositionUpdate() {
        absolute = calculateAbsolute();
    }

    public Point getAbsolute() {
        return absolute;
    }

    public WidgetGroup<?> getParent() {
        return parent;
    }

    public Widget setSize(Dimension size) {
        this.size = size;
        return this;
    }

    public Widget setSize(int width, int height) {
        return setSize(new Dimension(width, height));
    }

    public Dimension getSize() {
        return size;
    }

    public boolean visible() {
        return visible;
    }

    public boolean active() {
        return active;
    }

    public Widget setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public Widget setActive(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= getAbsolute().x &&
                mouseX <= getAbsolute().x + getSize().width &&
                mouseY >= getAbsolute().y &&
                mouseY <= getAbsolute().y + getSize().height;
    }

    public boolean isMouseOver(InputContext input) {
        return isMouseOver(input.mouseX(), input.mouseY());
    }

    public EventManager<Widget> events() {
        return eventManager;
    }

    public IEventContext.Common context() {
        return events().context();
    }

    public IEventContext.Cancelable cancelableCtx() {
        return events().cancelable();
    }

    public IEventContext.Interruptible interruptibleCtx() {
        return events().interruptible();
    }

    public <T> T listeners(IEventDefinition<T> definition) {
        return events().get(definition).invoker();
    }

    public <T> T registerListener(IEventDefinition<T> definition, T listener) {
        return events().get(definition).register(listener);
    }

    public <T> void unregisterListener(IEventDefinition<T> definition, T listener) {
        events().get(definition).unregister(listener);
    }

    public <T> void clearListeners(IEventDefinition<T> definition) {
        events().get(definition).clearListeners();
    }

    public void clearAllListeners() {
        events().clearAllListeners();
    }

    public <T> Widget onEvent(IEventDefinition<T> definition, T listener) {
        events().register(definition, listener);
        return this;
    }

    public void onDelete() {
        listeners(IWidgetEvent.onDelete).onDeleted(this);
    }

    public void init() {
        listeners(IWidgetEvent.onInit).onInit(this);
        initialized = true;
    }

    public void tick() {
        listeners(IWidgetEvent.onTick).onTick();
    }

    public void render(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible()) return;
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(position.x, position.y, 0);
            var context = context();
            listeners(IWidgetEvent.onRender).onRender(mc, mouseX, mouseY, partialTicks, context);
            if (context.isCancelled()) return;
            listeners(IWidgetEvent.onRenderPost).onRender(mc, mouseX, mouseY, partialTicks, context());
        }
        GlStateManager.popMatrix();
    }

    public boolean mouseClicked(InputContext input) {
        if (!visible() || !active() || !isMouseOver(input)) return false;
        return listeners(IInputEvent.onMouseClicked).onClicked(context(), input);
    }

    public boolean mouseReleased(InputContext input) {
        if (!visible() || !active() || !isMouseOver(input)) return false;
        return listeners(IInputEvent.onKeyReleased).onKeyReleased(context(), input);
    }

    public boolean mouseScrolled(int mouseX, int mouseY, double amount) {
        return false;
    }

    public boolean mouseDragged(int mouseX, int mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseMoved(int mouseX, int mouseY) {
        return false;
    }

    public boolean keyPressed(InputContext input) {
        if (!visible() || !active()) return false;
        return listeners(IInputEvent.onKeyPressed).onKeyPressed(context(), input);
    }

    public boolean keyReleased(InputContext input) {
        if (!visible() || !active()) return false;
        return listeners(IInputEvent.onKeyReleased).onKeyReleased(context(), input);
    }

}
