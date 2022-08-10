package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.gui.elements.GuiImageToggleButton;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class FilterModeConfigButton extends GuiImageToggleButton {

    private boolean isSearchIngredient;

    public static FilterModeConfigButton create(OnPress pressable) {
        IDrawable offIcon = JeiUtilitiesTextures.getInstance().getOutputModeIcon();
        IDrawable onIcon = JeiUtilitiesTextures.getInstance().getIngredientModeIcon();
        return new FilterModeConfigButton(offIcon, onIcon, pressable);
    }

    public FilterModeConfigButton(IDrawable offIcon, IDrawable onIcon, OnPress pressable) {
        super(offIcon, onIcon, pressable);
        this.isSearchIngredient = true;
    }

    @Override
    protected void getTooltips(List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("jeiutilities.gui.tooltip.button.filter.title"));
        MutableComponent modeName = new TranslatableComponent("jeiutilities.gui.tooltip.button.filter.mode." + (isSearchIngredient ? "ingredients" : "output")).withStyle(ChatFormatting.YELLOW);
        tooltip.add(new TranslatableComponent("jeiutilities.gui.tooltip.button.filter.description1", modeName).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
        this.isSearchIngredient = !this.isSearchIngredient;
    }

    @Override
    protected boolean isIconToggledOn() {
        return isSearchIngredient;
    }

    public boolean isSearchIngredient() {
        return isSearchIngredient;
    }

    public void setSearchIngredient(boolean searchIngredient) {
        isSearchIngredient = searchIngredient;
    }
}
