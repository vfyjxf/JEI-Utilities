package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.Focus;
import mezz.jei.ingredients.Ingredients;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientRegistry;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.recipeRegistry;

public class RecipeHelper {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <V, T> Pair<IRecipeWrapper, Integer> getRecipeWrapperAndIndex(@Nonnull Map<IIngredientType, List<String>> recipeUidMap, String recipeCategoryUid, T recipeOutput, @Nonnull IFocus<V> focus) {
        IRecipeCategory recipeCategory = recipeRegistry.getRecipeCategory(recipeCategoryUid);
        if (recipeCategory != null) {
            IIngredientHelper<V> ingredientHelper = ingredientRegistry.getIngredientHelper(focus.getValue());
            IFocus<?> translatedFocus = ingredientHelper.translateFocus(focus, Focus::new);
            List<IRecipeWrapper> recipes = recipeRegistry.getRecipeWrappers(recipeCategory, translatedFocus);
            IIngredientType<T> outputType = ingredientRegistry.getIngredientType(recipeOutput);
            String outputUid = IngredientHelper.getUniqueId(recipeOutput);
            List<IRecipeWrapper> outputMatchRecipes = recipes.stream()
                    .filter(recipe -> {
                        Ingredients outputIngredients = new Ingredients();
                        recipe.getIngredients(outputIngredients);
                        List<List<T>> outputSlots = outputIngredients.getOutputs(outputType);
                        return outputSlots.stream()
                                .flatMap(Collection::stream)
                                .map(t -> IngredientHelper.getUniqueId(IngredientHelper.getNormalize(t)))
                                .anyMatch(outputIngredientUid -> outputIngredientUid.equals(outputUid));
                    }).collect(Collectors.toCollection(ArrayList::new));
            return outputMatchRecipes.stream()
                    .filter(recipe -> isRecipeMatch(recipe, recipeUidMap))
                    .findFirst()
                    .map(recipe -> Pair.of(recipe, recipes.indexOf(recipe)))
                    .orElse(null);
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean isRecipeMatch(IRecipeWrapper recipeWrapper, Map<IIngredientType, List<String>> recipeUidMap) {
        Ingredients recipeIngredients = new Ingredients();
        recipeWrapper.getIngredients(recipeIngredients);
        Set<IIngredientType> ingredientTypes = ((Map<IIngredientType, List<List>>) ReflectionUtils.getField(
                Ingredients.class,
                recipeIngredients,
                "inputs"
        )).keySet();

        Set<IIngredientType> inputTypes = recipeUidMap.keySet();

        if (ingredientTypes.size() != inputTypes.size()) {
            return false;
        }

        if (!ingredientTypes.containsAll(inputTypes)) {
            return false;
        }

        for (IIngredientType ingredientType : ingredientTypes) {
            List<String> inputUid = recipeUidMap.get(ingredientType);
            List<String> ingredientUid = getIngredientsUid(recipeIngredients, ingredientType);
            if (inputUid.size() != ingredientUid.size()) {
                return false;
            }

            for (int i = 0; i < inputUid.size(); i++) {
                if (!inputUid.get(i).equals(ingredientUid.get(i))) {
                    return false;
                }
            }

        }

        return true;
    }

    private static <V> List<String> getIngredientsUid(Ingredients ingredients, IIngredientType<V> ingredientType) {
        List<String> ingredientsUid = new ArrayList<>();
        List<List<V>> ingredientList = ingredients.getInputs(ingredientType);
        for (List<V> ingredientPerSlot : ingredientList) {
            if (ingredientPerSlot.isEmpty()) {
                ingredientsUid.add(RecipeInfo.NONE_MARK);
            } else {
                ingredientsUid.add(IngredientHelper.getUniqueId(ingredientPerSlot.get(0)));
            }
        }
        return ingredientsUid;
    }

}
