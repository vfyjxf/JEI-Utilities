package dev.vfyjxf.jeiutilities.ui;

import com.github.bsideup.jabel.Desugar;
import dev.vfyjxf.jeiutilities.math.FloatingPoint;
import dev.vfyjxf.jeiutilities.math.Point;
import dev.vfyjxf.jeiutilities.ui.widgets.Widget;
import dev.vfyjxf.jeiutilities.ui.widgets.WidgetGroup;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatAllowedCharacters;

@Desugar
public record InputContext(Type type, int key, int mouseX, int mouseY, int modifiers, boolean released) {

    public enum Type {
        MOUSE,
        KEYBOARD
    }

    static InputContext fromMouse(int x, int y, int button, boolean isReleased) {
        return new InputContext(Type.MOUSE, button, x, y, 0, isReleased);
    }

    static InputContext fromMouse(int x, int y, int button) {
        return new InputContext(Type.MOUSE, button, x, y, 0, false);
    }

    static InputContext fromKeyboard(int keyCode, int modifiers, int mouseX, int mouseY, boolean isReleased) {
        return new InputContext(Type.KEYBOARD, keyCode, mouseX, mouseY, modifiers, isReleased);
    }

    static InputContext fromKeyboard(int keyCode, int modifiers, int mouseX, int mouseY) {
        return new InputContext(Type.KEYBOARD, keyCode, mouseX, mouseY, modifiers, false);
    }

    public boolean isCtrlDown() {
        return GuiScreen.isCtrlKeyDown();
    }

    public boolean isShiftDown() {
        return GuiScreen.isShiftKeyDown();
    }

    public boolean isAltDown() {
        return GuiScreen.isAltKeyDown();
    }

    public boolean isMouse() {
        return type == Type.MOUSE;
    }

    public boolean isLeftClick() {
        return isMouse() && key() == 0;
    }

    public boolean isRightClick() {
        return isMouse() && key() == 1;
    }

    public boolean isKeyboard() {
        return type == Type.KEYBOARD;
    }

    public boolean isAllowedChatCharacter() {
        return ChatAllowedCharacters.isAllowedCharacter((char) key());
    }

    public boolean is(KeyBinding keyMapping) {
        return keyMapping.isActiveAndMatches(key());
    }

    public boolean is(int key) {
        return this.key() == key;
    }

    public FloatingPoint getRelative(Widget widget) {
        WidgetGroup<?> parent = widget.getParent();
        if (parent == null) return new FloatingPoint(mouseX(), mouseY());
        Point absolute = widget.getAbsolute();
        return new FloatingPoint(mouseX() - absolute.x, mouseY() - absolute.y);
    }
}
