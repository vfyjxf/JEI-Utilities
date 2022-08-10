package com.github.vfyjxf.jeiutilities.gui.filter;

import com.google.common.base.Preconditions;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class GuiFilterProperties implements IGuiProperties{

    private final Class<? extends Screen> screenClass;
    private final int guiLeft;
    private final int guiTop;
    private final int guiXSize;
    private final int guiYSize;
    private final int screenWidth;
    private final int screenHeight;

    public static GuiFilterProperties create(RecipesFilterScreen screen) {

        if (screen.width <= 0 || screen.height <= 0) {
            return null;
        }
        ImmutableRect2i area = screen.getArea();
        if (area.getWidth() <= 0 || area.getHeight() <= 0) {
            return null;
        }

        return new GuiFilterProperties(screen.getClass(), area.getX(), area.getY(), area.getWidth(), area.getHeight(), screen.width, screen.height);
    }

    private GuiFilterProperties(Class<? extends Screen> screenClass, int guiLeft, int guiTop, int guiXSize, int guiYSize, int screenWidth, int screenHeight) {
        Preconditions.checkArgument(guiLeft >= 0, "guiLeft must be >= 0");
        Preconditions.checkArgument(guiTop >= 0, "guiTop must be >= 0");
        Preconditions.checkArgument(guiXSize > 0, "guiXSize must be > 0");
        Preconditions.checkArgument(guiYSize > 0, "guiYSize must be > 0");
        Preconditions.checkArgument(screenWidth > 0, "screenWidth must be > 0");
        Preconditions.checkArgument(screenHeight > 0, "screenHeight must be > 0");
        this.screenClass = screenClass;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.guiXSize = guiXSize;
        this.guiYSize = guiYSize;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public @NotNull Class<? extends Screen> getScreenClass() {
        return screenClass;
    }

    @Override
    public int getGuiLeft() {
        return guiLeft;
    }

    @Override
    public int getGuiTop() {
        return guiTop;
    }

    @Override
    public int getGuiXSize() {
        return guiXSize;
    }

    @Override
    public int getGuiYSize() {
        return guiYSize;
    }

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }
}
