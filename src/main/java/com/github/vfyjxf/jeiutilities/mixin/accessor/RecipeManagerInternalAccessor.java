package com.github.vfyjxf.jeiutilities.mixin.accessor;

import mezz.jei.recipes.RecipeManagerInternal;
import mezz.jei.recipes.RecipeTypeDataMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeManagerInternal.class,remap = false)
public interface RecipeManagerInternalAccessor {

    @Accessor
    RecipeTypeDataMap getRecipeTypeDataMap();

}
