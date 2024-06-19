package dev.vfyjxf.jeiutilities.jei.ingredient;

import dev.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import dev.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("rawtypes")
public class RecipeInfoRenderer<T extends RecipeInfo> implements IIngredientRenderer<T> {

    @Override
    public void render(@Nonnull Minecraft minecraft, int xPosition, int yPosition, @Nullable RecipeInfo ingredient) {
        if (ingredient != null) {
            IIngredientType<Object> ingredientType = JeiUtilitiesPlugin.ingredientRegistry.getIngredientType(ingredient.getResult());
            IIngredientRenderer<Object> ingredientRenderer = JeiUtilitiesPlugin.ingredientRegistry.getIngredientRenderer(ingredientType);
            ingredientRenderer.render(minecraft, xPosition, yPosition, ingredient.getResult());
            if (JeiUtilitiesConfig.isShowRecipeBookmarkReminders()) {
                GlStateManager.disableDepth();
                this.getFontRenderer(minecraft, ingredient).drawStringWithShadow("R", xPosition, yPosition - 2, 0xFF555555);
                GlStateManager.enableDepth();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public List<String> getTooltip(@Nonnull Minecraft minecraft, @Nonnull RecipeInfo ingredient, @Nonnull ITooltipFlag tooltipFlag) {
        return ingredient.getResultIngredientRenderer().getTooltip(minecraft, ingredient.getResult(), tooltipFlag);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public FontRenderer getFontRenderer(@Nonnull Minecraft minecraft, @Nonnull RecipeInfo ingredient) {
        return ingredient.getResultIngredientRenderer().getFontRenderer(minecraft, ingredient.getResult());
    }

    @Nonnull
    @SuppressWarnings({"unchecked","deprecation"})
    @Override
    public List<String> getTooltip(@Nonnull Minecraft minecraft, @Nonnull T ingredient, boolean advanced) {
        return ingredient.getResultIngredientRenderer().getTooltip(minecraft, ingredient.getResult(), advanced);
    }

    @Nonnull
    @SuppressWarnings({"unchecked","deprecation"})
    @Override
    public List<String> getTooltip(@Nonnull Minecraft minecraft, @Nonnull T ingredient) {
        return ingredient.getResultIngredientRenderer().getTooltip(minecraft, ingredient.getResult());
    }
}
