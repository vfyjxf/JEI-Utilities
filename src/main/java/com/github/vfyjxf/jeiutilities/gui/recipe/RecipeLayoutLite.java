package com.github.vfyjxf.jeiutilities.gui.recipe;

import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A lite version of {@link RecipeLayout}, used to display recipes in the bookmark area.
 */
public class RecipeLayoutLite implements IRecipeLayoutDrawable {

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
    public void draw(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {

    }

    @Nonnull
    @Override
    public IGuiItemStackGroup getItemStacks() {
        return null;
    }

    @Nonnull
    @Override
    public IGuiFluidStackGroup getFluidStacks() {
        return null;
    }

    @Nonnull
    @Override
    public <T> IGuiIngredientGroup<T> getIngredientsGroup(@Nonnull IIngredientType<T> ingredientType) {
        return null;
    }

    @Nullable
    @Override
    public IFocus<?> getFocus() {
        return null;
    }

    @Nonnull
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

    @Nonnull
    @Override
    public <T> IGuiIngredientGroup<T> getIngredientsGroup(@Nonnull Class<T> ingredientClass) {
        return null;
    }
}
