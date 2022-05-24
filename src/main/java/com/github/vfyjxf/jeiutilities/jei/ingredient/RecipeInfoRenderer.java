package com.github.vfyjxf.jeiutilities.jei.ingredient;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
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
public class RecipeInfoRenderer implements IIngredientRenderer<RecipeInfo> {

    @Override
    public void render(@Nonnull Minecraft minecraft, int xPosition, int yPosition, @Nullable RecipeInfo ingredient) {
        if (ingredient != null) {
            IIngredientType<Object> ingredientType = JeiUtilitiesPlugin.ingredientRegistry.getIngredientType(ingredient.getResult());
            IIngredientRenderer<Object> ingredientRenderer = JeiUtilitiesPlugin.ingredientRegistry.getIngredientRenderer(ingredientType);
            ingredientRenderer.render(minecraft, xPosition, yPosition, ingredient.getResult());
            if (JeiUtilitiesConfig.isShowRecipeBookmarkReminders()) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                this.getFontRenderer(minecraft, ingredient).drawStringWithShadow("R", xPosition, yPosition - 2, 0xFF555555);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableBlend();
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
}
