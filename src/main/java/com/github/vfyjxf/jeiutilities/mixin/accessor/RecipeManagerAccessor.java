package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.recipes.RecipeManager;
import mezz.jei.recipes.RecipeManagerInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeManager.class,remap = false)
public interface RecipeManagerAccessor {

    @Accessor
    RecipeManagerInternal getInternal();

}
