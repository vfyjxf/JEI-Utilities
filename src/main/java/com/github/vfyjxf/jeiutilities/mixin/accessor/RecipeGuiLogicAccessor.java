package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.ingredients.RegisteredIngredients;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeGuiLogic.class, remap = false)
public interface RecipeGuiLogicAccessor {

    @Accessor
    IngredientLookupState getState();

    @Accessor
    RegisteredIngredients getRegisteredIngredients();

}
