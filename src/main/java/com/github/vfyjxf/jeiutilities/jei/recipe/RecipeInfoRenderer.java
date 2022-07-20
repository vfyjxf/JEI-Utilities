package com.github.vfyjxf.jeiutilities.jei.recipe;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RecipeInfoRenderer<T extends IRecipeInfo> implements IIngredientRenderer<T> {

    public static final RecipeInfoRenderer RECIPE_INFO_RENDERER = new RecipeInfoRenderer();

    @Override
    public void render(@NotNull PoseStack stack, @Nullable T ingredient) {
        if (ingredient != null) {
            ingredient.getIngredientRenderer().render(stack, ingredient.getOutput());
            if (JeiUtilitiesConfig.getShowRecipeBookmarkReminders()) {
                stack.pushPose();
                {
                    Minecraft minecraft = Minecraft.getInstance();
                    ItemRenderer itemRenderer = minecraft.getItemRenderer();
                    stack.translate(0.0D, 0.0D, itemRenderer.blitOffset + 200.0F);
                    this.getFontRenderer(Minecraft.getInstance(), ingredient).drawShadow(stack, "R", 0, 0, 0xFF555555);
                }
                stack.popPose();
            }
        }
    }

    @Override
    public @NotNull List<Component> getTooltip(@NotNull T ingredient, @NotNull TooltipFlag tooltipFlag) {
        return ingredient.getIngredientRenderer().getTooltip(ingredient.getOutput(), tooltipFlag);
    }

    @Override
    public @NotNull Font getFontRenderer(@NotNull Minecraft minecraft, @NotNull T ingredient) {
        return ingredient.getIngredientRenderer().getFontRenderer(minecraft, ingredient.getOutput());
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @SuppressWarnings("removal")
    @Override
    public void render(@NotNull PoseStack stack, int xPosition, int yPosition, @Nullable T ingredient) {
        stack.pushPose();
        {
            stack.translate(xPosition, yPosition, 0);
            render(stack, ingredient);
        }
        stack.popPose();
    }

}


