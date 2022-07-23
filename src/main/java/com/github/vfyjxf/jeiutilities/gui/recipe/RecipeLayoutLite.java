package com.github.vfyjxf.jeiutilities.gui.recipe;

import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.mixin.accessor.RecipeLayoutAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.ingredients.RecipeSlot;
import mezz.jei.gui.ingredients.RecipeSlots;
import mezz.jei.gui.recipes.OutputSlotTooltipCallback;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.ShapelessIcon;
import mezz.jei.gui.recipes.builder.RecipeLayoutBuilder;
import mezz.jei.ingredients.RegisteredIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A lite version of {@link RecipeLayout}, used to display recipes in the bookmark area.
 */
public class RecipeLayoutLite<R> extends RecipeLayout<R> {

    private static final int RECIPE_BORDER_PADDING = 4;

    private final RecipeLayoutAccessor accessor = (RecipeLayoutAccessor) this;

    private final RenderableNineSliceTexture recipeBorder;
    private final RegisteredIngredients registeredIngredients;

    @Nullable
    public static <T> RecipeLayoutLite<T> create(IRecipeCategory<T> recipeCategory, T recipe, IFocusGroup focuses, int posX, int posY) {
        RecipeLayoutLite<T> recipeLayout = new RecipeLayoutLite<>(recipeCategory, recipe, focuses, JeiUtilitiesPlugin.registeredIngredients, posX, posY);
        if (

                recipeLayout.setRecipeLayout(recipeCategory, recipe, JeiUtilitiesPlugin.registeredIngredients, focuses) ||
                        recipeLayout.getLegacyAdapter().setRecipeLayout(recipeCategory, recipe)
        ) {
            ResourceLocation recipeName = recipeCategory.getRegistryName(recipe);
            if (recipeName != null) {
                addOutputSlotTooltip(recipeLayout, recipeName, JeiUtilitiesPlugin.modIdHelper);
            }
            return recipeLayout;
        }
        return null;
    }

    private boolean setRecipeLayout(
            IRecipeCategory<R> recipeCategory,
            R recipe,
            RegisteredIngredients registeredIngredients,
            IFocusGroup focuses
    ) {
        RecipeLayoutBuilder builder = new RecipeLayoutBuilder(registeredIngredients, accessor.getOffset());
        try {
            recipeCategory.setRecipe(builder, recipe, focuses);
            if (builder.isUsed()) {
                builder.setRecipeLayout(this, focuses);
                return true;
            }
        } catch (RuntimeException | LinkageError e) {
            accessor.getLogger().error("Error caught from Recipe Category: {}", recipeCategory.getRecipeType().getUid(), e);
        }
        return false;
    }

    private static void addOutputSlotTooltip(RecipeLayoutLite<?> recipeLayout, ResourceLocation recipeName, IModIdHelper modIdHelper) {
        RecipeSlots recipeSlots = recipeLayout.getRecipeSlots();
        List<RecipeSlot> outputSlots = recipeSlots.getSlots().stream()
                .filter(r -> r.getRole() == RecipeIngredientRole.OUTPUT)
                .toList();

        if (!outputSlots.isEmpty()) {
            OutputSlotTooltipCallback callback = new OutputSlotTooltipCallback(recipeName, modIdHelper, recipeLayout.registeredIngredients);
            for (RecipeSlot outputSlot : outputSlots) {
                outputSlot.addTooltipCallback(callback);
            }
        }
    }

    public RecipeLayoutLite(
            IRecipeCategory<R> recipeCategory,
            R recipe,
            IFocusGroup focuses,
            RegisteredIngredients registeredIngredients,
            int posX,
            int posY
    ) {
        super(-1, recipeCategory, recipe, focuses, registeredIngredients, posX, posY);
        this.recipeBorder = JeiUtilitiesTextures.getInstance().getRecipeBackground();
        this.registeredIngredients = registeredIngredients;
        accessor.setRecipeSlots(new RecipeSlots() {
            @Override
            public void draw(@NotNull PoseStack poseStack, int highlightColor, int recipeMouseX, int recipeMouseY) {
                for (RecipeSlot slot : this.getSlots()) {
                    slot.draw(poseStack);
                }
            }
        });
    }

    @Override
    public void drawRecipe(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
        IRecipeCategory<R> recipeCategory = getRecipeCategory();
        IDrawable background = recipeCategory.getBackground();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();

        int posX = this.getPosX();
        int posY = this.getPosY();

        final int recipeMouseX = mouseX - posX;
        final int recipeMouseY = mouseY - posY;

        poseStack.pushPose();
        {
            poseStack.translate(posX, posY, 0);

            IDrawable categoryBackground = recipeCategory.getBackground();
            int width = categoryBackground.getWidth() + (2 * RECIPE_BORDER_PADDING);
            int height = categoryBackground.getHeight() + (2 * RECIPE_BORDER_PADDING);
            recipeBorder.draw(poseStack, -RECIPE_BORDER_PADDING, -RECIPE_BORDER_PADDING, width, height);
            background.draw(poseStack);
            // defensive push/pop to protect against recipe categories changing the last pose
            poseStack.pushPose();
            {
                poseStack.translate(0.0D, 0.0D, Minecraft.getInstance().getItemRenderer().blitOffset + 400.0D);
                recipeCategory.draw(this.getRecipe(), this.getRecipeSlots().getView(), poseStack, recipeMouseX, recipeMouseY);
                // drawExtras and drawInfo often render text which messes with the color, this clears it
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
            poseStack.popPose();
            ShapelessIcon shapelessIcon = accessor.getShapelessIcon();
            if (shapelessIcon != null) {
                shapelessIcon.draw(poseStack);
            }
            poseStack.translate(0.0F, 0.0F, 200.0F);
            accessor.getRecipeSlots().draw(poseStack, 0x80FFFFFF, mouseX, mouseY);
        }
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

}
