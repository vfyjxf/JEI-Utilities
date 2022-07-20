package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeManagerInternalAccessor;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.recipes.RecipeTypeData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RecipeHelper {

    public static Pair<?, Integer> getRecipeAndIndexByName(
            @NotNull RecipeTypeData<?> data,
            @NotNull ResourceLocation registerName,
            Collection<? extends IFocus<?>> focuses
    ) {
        IRecipeCategory<?> recipeCategory = data.getRecipeCategory();
        IRecipeLookup<?> lookup = JeiUtilitiesPlugin.recipeManager.createRecipeLookup(recipeCategory.getRecipeType());
        lookup.limitFocus(focuses);
        List<?> limitedRecipes = lookup.get().toList();
        return lookup.get()
                .parallel()
                .filter(recipe -> recipe instanceof Recipe && registerName.equals(((Recipe<?>) recipe).getId()))
                .findFirst()
                .map(recipe -> Pair.of(recipe, limitedRecipes.indexOf(recipe)))
                .orElse(null);
    }

    public static RecipeTypeData<?> getRecipeTypeDataFromUid(@NotNull ResourceLocation recipeCategoryUid) {
        return ((RecipeManagerInternalAccessor) JeiUtilitiesPlugin.managerInternal).getRecipeTypeDataMap().get(recipeCategoryUid);
    }

}
