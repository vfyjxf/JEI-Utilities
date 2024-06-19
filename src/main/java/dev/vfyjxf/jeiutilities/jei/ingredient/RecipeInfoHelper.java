package dev.vfyjxf.jeiutilities.jei.ingredient;

import dev.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collection;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RecipeInfoHelper<T extends RecipeInfo> implements IIngredientHelper<T> {

    @Nullable
    @Override
    public T getMatch(@Nonnull Iterable<T> ingredients, @Nonnull T ingredientToMatch) {
        return null;
    }

    @Override
    @Nonnull
    public String getDisplayName(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getDisplayName(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getUniqueId(@Nonnull T ingredient) {
        return ingredient.toString();
    }

    @Override
    @Nonnull
    public String getWildcardId(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getWildcardId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getModId(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getModId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public String getResourceId(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getResourceId(ingredient.getResult());
    }

    @Override
    @Nonnull
    public RecipeInfo copyIngredient(@Nonnull RecipeInfo ingredient) {
        return ingredient.copy();
    }

    @Override
    @Nonnull
    public String getErrorInfo(@Nullable RecipeInfo ingredient) {
        if (ingredient == null) {
            return "null";
        }
        return getIngredientHelper(ingredient.getResult()).getErrorInfo(ingredient.getResult());
    }

    private <E> IIngredientHelper<E> getIngredientHelper(E ingredient) {
        return JeiUtilitiesPlugin.ingredientRegistry.getIngredientHelper(ingredient);
    }

    @Nonnull
    @Override
    public IFocus<?> translateFocus(@Nonnull IFocus<T> focus, @Nonnull IFocusFactory focusFactory) {
        IFocus real = focusFactory.createFocus(focus.getMode(), focus.getValue().getResult());
        return getIngredientHelper(focus.getValue().getResult()).translateFocus(real, focusFactory);
    }

    @Nonnull
    @Override
    public String getDisplayModId(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getDisplayModId(ingredient.getResult());
    }

    @Nonnull
    @Override
    public Iterable<Color> getColors(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getColors(ingredient.getResult());
    }

    @Nonnull
    @Override
    public ItemStack getCheatItemStack(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getCheatItemStack(ingredient.getResult());
    }

    @Override
    public boolean isValidIngredient(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).isValidIngredient(ingredient.getResult());
    }

    @Override
    public boolean isIngredientOnServer(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).isIngredientOnServer(ingredient.getResult());
    }

    @Nonnull
    @Override
    public Collection<String> getOreDictNames(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getOreDictNames(ingredient.getResult());
    }

    @Nonnull
    @Override
    public Collection<String> getCreativeTabNames(@Nonnull T ingredient) {
        return getIngredientHelper(ingredient.getResult()).getCreativeTabNames(ingredient.getResult());
    }
}

