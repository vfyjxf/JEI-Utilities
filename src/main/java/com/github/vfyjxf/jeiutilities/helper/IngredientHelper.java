package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.ingredients.TypedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IngredientHelper {

    public static <T> ITypedIngredient<?> createTypedIngredient(T ingredient) {
        return TypedIngredient.create(JeiUtilitiesPlugin.registeredIngredients, ingredient).orElse(null);
    }

    public static <T> Optional<ITypedIngredient<?>> createTypedIngredient(T ingredient, RegisteredIngredients registeredIngredients) {
        return TypedIngredient.create(registeredIngredients, ingredient);
    }

    public static <V> V normalizeIngredient(@NotNull V ingredient) {
        return getIngredientHelper(ingredient).normalizeIngredient(ingredient);
    }

    public static <V> V copyIngredient(V ingredient){
        return getIngredientHelper(ingredient).copyIngredient(ingredient);
    }

    public static <T> String getUniqueId(@NotNull T ingredient, UidContext context) {
        return getIngredientHelper(ingredient).getUniqueId(ingredient, context);
    }

    public static <T> IIngredientHelper<T> getIngredientHelper(@NotNull T ingredient) {
        return JeiUtilitiesPlugin.ingredientManager.getIngredientHelper(ingredient);
    }

    public static <T> IIngredientRenderer<T> getIngredientRenderer(@NotNull T ingredient) {
        return JeiUtilitiesPlugin.ingredientManager.getIngredientRenderer(ingredient);
    }

    public static  <V> IIngredientType<V> getIngredientType(@NotNull V ingredient) {
        return JeiUtilitiesPlugin.registeredIngredients.getIngredientType(ingredient);
    }

}
