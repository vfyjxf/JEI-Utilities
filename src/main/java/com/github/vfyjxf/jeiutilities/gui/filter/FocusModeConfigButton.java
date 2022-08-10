package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.gui.elements.GuiImageToggleButton;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class FocusModeConfigButton extends GuiImageToggleButton {

    private boolean isInput = true;

    public static FocusModeConfigButton create(OnPress pressable) {
        IDrawable offIcon = JeiUtilitiesTextures.getInstance().getFocusOutputModeIcon();
        IDrawable onIcon = JeiUtilitiesTextures.getInstance().getFocusInputModeIcon();
        return new FocusModeConfigButton(offIcon, onIcon, pressable);
    }

    public FocusModeConfigButton(IDrawable offIcon, IDrawable onIcon, OnPress pressable) {
        super(offIcon, onIcon, pressable);
    }

    @Override
    protected void getTooltips(List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("jeiutilities.gui.tooltip.button.focus.title"));
        MutableComponent modeName = new TranslatableComponent("jeiutilities.gui.tooltip.button.focus.mode." + (isInput ? "ingredient" : "output")).withStyle(ChatFormatting.YELLOW);
        tooltip.add(new TranslatableComponent("jeiutilities.gui.tooltip.button.focus.description1", modeName).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
        this.isInput = !this.isInput;
    }

    @Override
    protected boolean isIconToggledOn() {
        return isInput;
    }

    public boolean isInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        isInput = input;
    }
}
