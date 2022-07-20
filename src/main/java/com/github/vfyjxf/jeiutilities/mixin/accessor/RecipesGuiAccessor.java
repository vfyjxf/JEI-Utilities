package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipesGui.class,remap = false)
public interface RecipesGuiAccessor {

    @Accessor
    List<RecipeLayout<?>> getRecipeLayouts();

    @Accessor
    IRecipeGuiLogic getLogic();

}
