package com.github.vfyjxf.jeiutilities.gui.recipe;

import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DraggableRecipeWidget implements IRecipeLayoutDrawable {
    @Override
    public void setPosition(int posX, int posY) {

    }

    @Override
    public void drawRecipe(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {

    }

    @Override
    public void drawOverlays(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {

    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return false;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public void draw(Minecraft minecraft, int mouseX, int mouseY) {

    }

    @Override
    public IGuiItemStackGroup getItemStacks() {
        return null;
    }

    @Override
    public IGuiFluidStackGroup getFluidStacks() {
        return null;
    }

    @Override
    public <T> IGuiIngredientGroup<T> getIngredientsGroup(IIngredientType<T> ingredientType) {
        return null;
    }

    @Nullable
    @Override
    public IFocus<?> getFocus() {
        return null;
    }

    @Override
    public IRecipeCategory<?> getRecipeCategory() {
        return null;
    }

    @Override
    public void setRecipeTransferButton(int posX, int posY) {

    }

    @Override
    public void setShapeless() {

    }

    @Override
    public <T> IGuiIngredientGroup<T> getIngredientsGroup(Class<T> ingredientClass) {
        return null;
    }
}
