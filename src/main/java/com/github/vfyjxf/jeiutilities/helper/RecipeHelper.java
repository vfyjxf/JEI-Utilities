package com.github.vfyjxf.jeiutilities.helper;

import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.recipe.IRecipeInfo;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeLayoutBuilderAccessor;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeManagerInternalAccessor;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.builder.RecipeLayoutBuilder;
import mezz.jei.ingredients.IIngredientSupplier;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.ingredients.RegisteredIngredients;
import mezz.jei.recipes.RecipeTypeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mezz.jei.recipes.IngredientSupplierHelper.getIngredientSupplier;

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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Pair<?, Integer> getRecipeAndIndexByInputs(
            @NotNull RecipeTypeData<?> data,
            Collection<? extends IFocus<?>> focuses,
            Map<IIngredientType<?>, List<String>> recipeUidMap,
            Object output,
            RegisteredIngredients registeredIngredients
    ) {
        IRecipeCategory recipeCategory = data.getRecipeCategory();
        IRecipeLookup<?> lookup = JeiUtilitiesPlugin.recipeManager.createRecipeLookup(recipeCategory.getRecipeType());
        lookup.limitFocus(focuses);
        List<?> limitedRecipes = lookup.get().toList();
        return lookup.get()
                .parallel()
                .filter(recipe -> isOutputMatch(output, recipe, recipeCategory, registeredIngredients))
                .filter(recipe -> isRecipeMatch(recipe, recipeCategory, recipeUidMap, registeredIngredients))
                .findAny()
                .map(recipe -> Pair.of(recipe, limitedRecipes.indexOf(recipe)))
                .orElse(null);
    }

    public static RecipeTypeData<?> getRecipeTypeDataFromUid(@NotNull ResourceLocation recipeCategoryUid) {
        return ((RecipeManagerInternalAccessor) JeiUtilitiesPlugin.managerInternal).getRecipeTypeDataMap().get(recipeCategoryUid);
    }

    public static <T> Map<IIngredientType<?>, List<? extends List<?>>> getRecipeMap(@NotNull T recipe, @NotNull IRecipeCategory<T> recipeCategory, RegisteredIngredients registeredIngredients) {
        IIngredientSupplier ingredientSupplier = getIngredientSupplier(recipe, recipeCategory, registeredIngredients);
        if (ingredientSupplier != null) {
            Stream<? extends IIngredientType<?>> ingredientTypes = ingredientSupplier.getIngredientTypes(RecipeIngredientRole.INPUT);
            if (ingredientSupplier instanceof RecipeLayoutBuilder builder) {
                return ingredientTypes.collect(Collectors.toMap(type -> type,
                        type -> getIngredients(builder, type, RecipeIngredientRole.INPUT),
                        (a, b) -> b
                ));
            }
            if (ingredientSupplier instanceof Ingredients ingredients) {
                return ingredientTypes.collect(Collectors.toMap(type -> type, ingredients::getInputs, (a, b) -> b));
            }
        }
        return Map.of();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R> Map<IIngredientType<?>, List<String>> getRecipeUidMap(R recipe, IRecipeCategory recipeCategory, RegisteredIngredients registeredIngredients) {
        Map<IIngredientType<?>, List<? extends List<?>>> recipeMap = RecipeHelper.getRecipeMap(recipe, recipeCategory, registeredIngredients);
        Map<IIngredientType<?>, List<String>> recipeUidMap = new HashMap<>();
        for (IIngredientType<?> type : recipeMap.keySet()) {
            List<? extends List<?>> ingredientInType = recipeMap.get(type);
            List<String> uidListForType = new ArrayList<>();
            for (List<?> ingredientInSlot : ingredientInType) {
                if (ingredientInSlot.isEmpty()) {
                    uidListForType.add(IRecipeInfo.NONE_MARK);
                } else {
                    uidListForType.add(getIngredientUid(ingredientInSlot.get(0)));
                }
            }
            recipeUidMap.put(type, uidListForType);
        }
        return recipeUidMap;
    }

    public static <T> List<List<T>> getIngredients(RecipeLayoutBuilder builder, IIngredientType<T> ingredientType, RecipeIngredientRole role) {
        return ((RecipeLayoutBuilderAccessor) builder).invokerSlotStream()
                .filter(slot -> slot.getRole() == role)
                .map(slot -> slot.getIngredients(ingredientType).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <R> boolean isRecipeMatch(R recipe, IRecipeCategory recipeCategory, Map<IIngredientType<?>, List<String>> recipeUidMap, RegisteredIngredients registeredIngredients) {
        IIngredientSupplier supplier = getIngredientSupplier(recipe, recipeCategory, registeredIngredients);
        if (supplier != null) {
            List<? extends IIngredientType<?>> recipeIngredientTypes = supplier.getIngredientTypes(RecipeIngredientRole.INPUT).toList();
            if (recipeIngredientTypes.size() != recipeUidMap.size()) return false;
            if (!recipeUidMap.keySet().containsAll(recipeIngredientTypes)) return false;

            Map<IIngredientType<?>, List<String>> otherMap = getRecipeUidMap(recipe, recipeCategory, registeredIngredients);

            for (IIngredientType<?> type : recipeIngredientTypes) {
                List<String> inputMapUid = recipeUidMap.get(type);
                List<String> otherMapUid = otherMap.get(type);
                if (!inputMapUid.equals(otherMapUid)) {
                    return false;
                }
            }


        }
        return true;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <R> boolean isOutputMatch(Object output, R recipe, IRecipeCategory recipeCategory, RegisteredIngredients registeredIngredients) {
        IIngredientType<?> outputType = registeredIngredients.getIngredientType(output);
        String outputUid = IngredientHelper.getUniqueId(output, UidContext.Ingredient);
        IIngredientSupplier supplier = getIngredientSupplier(recipe, recipeCategory, registeredIngredients);
        if (supplier != null) {
            return supplier.getIngredientStream(outputType, RecipeIngredientRole.OUTPUT)
                    .parallel()
                    .anyMatch(recipeOutput -> outputUid.equals(IngredientHelper.getUniqueId(recipeOutput, UidContext.Ingredient)));
        }
        return false;
    }

    private static <E> String getIngredientUid(E ingredient) {
        if (ingredient instanceof ItemStack itemStack) {
            return itemStack.save(new CompoundTag()).toString();
        } else {
            return IngredientHelper.getUniqueId(ingredient, UidContext.Ingredient);
        }
    }

}
