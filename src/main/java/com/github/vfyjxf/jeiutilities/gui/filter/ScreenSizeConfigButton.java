package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.gui.elements.GuiImageToggleButton;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ScreenSizeConfigButton extends GuiImageToggleButton {

    private boolean isWindowed = true;

    public static ScreenSizeConfigButton create(OnPress pressable) {
        IDrawable offIcon = JeiUtilitiesTextures.getInstance().getMaximizeButtonIcon();
        IDrawable onIcon = JeiUtilitiesTextures.getInstance().getWindowingButtonIcon();
        return new ScreenSizeConfigButton(offIcon, onIcon, pressable);
    }

    public ScreenSizeConfigButton(IDrawable offIcon, IDrawable onIcon, OnPress pressable) {
        super(offIcon, onIcon, pressable);
    }

    @Override
    protected void getTooltips(List<Component> tooltip) {

    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
        this.isWindowed = !this.isWindowed;
    }

    @Override
    protected boolean isIconToggledOn() {
        return isWindowed;
    }

    public boolean isWindowed() {
        return isWindowed;
    }
}
