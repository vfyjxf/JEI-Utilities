package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.gui.Focus;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.ingredients.TypedIngredient;
import mezz.jei.recipes.FocusGroup;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static <V> V copyIngredient(V ingredient) {
        return getIngredientHelper(ingredient).copyIngredient(ingredient);
    }

    public static <T> String getUniqueId(@NotNull T ingredient, UidContext context) {
        return getIngredientHelper(ingredient).getUniqueId(ingredient, context);
    }

    public static <T> IIngredientHelper<T> getIngredientHelper(@NotNull T ingredient) {
        return JeiUtilitiesPlugin.ingredientManager.getIngredientHelper(ingredient);
    }

    public static <T> IIngredientHelper<T> getIngredientHelper(@NotNull IIngredientType<T> ingredientType) {
        return JeiUtilitiesPlugin.ingredientManager.getIngredientHelper(ingredientType);
    }

    public static <T> IIngredientRenderer<T> getIngredientRenderer(@NotNull T ingredient) {
        return JeiUtilitiesPlugin.ingredientManager.getIngredientRenderer(ingredient);
    }

    public static <T> IIngredientRenderer<T> getIngredientRenderer(@NotNull IIngredientType<T> ingredientType) {
        return JeiUtilitiesPlugin.ingredientManager.getIngredientRenderer(ingredientType);
    }

    public static <V> IIngredientType<V> getIngredientType(@NotNull V ingredient) {
        return JeiUtilitiesPlugin.registeredIngredients.getIngredientType(ingredient);
    }

    public static IFocusGroup createFocusGroup(ITypedIngredient<?> typedIngredient, boolean isInput) {
        List<RecipeIngredientRole> roles = isInput ? List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST) : List.of(RecipeIngredientRole.OUTPUT);

        List<IFocus<?>> focuses = roles.stream()
                .map(role -> new Focus<>(role, typedIngredient))
                .collect(Collectors.toList());
        return FocusGroup.create(focuses);

    }

}
