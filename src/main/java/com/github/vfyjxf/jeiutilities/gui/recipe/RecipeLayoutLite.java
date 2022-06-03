package com.github.vfyjxf.jeiutilities.gui.recipe;

import mezz.jei.Internal;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.Focus;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.ingredients.GuiFluidStackGroup;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.GuiIngredientGroup;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.ShapelessIcon;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A lite version of {@link RecipeLayout}, used to display recipes in the bookmark area.
 * Base on the {@link RecipeLayout}, some changes were made to display the recipe more concisely.
 */
public class RecipeLayoutLite implements IRecipeLayoutDrawable {

    private static final int RECIPE_BORDER_PADDING = 4;

    private final int ingredientCycleOffset = (int) ((Math.random() * 10000) % Integer.MAX_VALUE);
    private final IRecipeCategory recipeCategory;
    private final Map<IIngredientType, GuiIngredientGroup> guiIngredientGroups;
    private final IRecipeWrapper recipeWrapper;
    @Nullable
    private final IFocus<?> focus;
    private final Color highlightColor = new Color(0x7FFFFFFF, true);
    @Nullable
    private ShapelessIcon shapelessIcon;
    private final DrawableNineSliceTexture recipeBorder;
    private int posX;
    private int posY;

    @SuppressWarnings("rawtypes")
    @Nullable
    public static <T extends IRecipeWrapper> RecipeLayoutLite create(
            IRecipeCategory<T> recipeCategory,
            T recipeWrapper,
            @Nullable IFocus focus,
            int posX,
            int posY
    ) {
        RecipeLayoutLite recipeLayout = new RecipeLayoutLite(recipeCategory, recipeWrapper, focus, posX, posY);
        try {
            IIngredients ingredients = new Ingredients();
            recipeWrapper.getIngredients(ingredients);
            recipeCategory.setRecipe(recipeLayout, recipeWrapper, ingredients);
            return recipeLayout;
        } catch (RuntimeException | LinkageError e) {
            Log.get().error("Error caught from Recipe Category: {}", recipeCategory.getClass().getCanonicalName(), e);
        }
        return null;
    }

    private <T extends IRecipeWrapper> RecipeLayoutLite(IRecipeCategory<T> recipeCategory, T recipeWrapper, @Nullable IFocus<?> focus, int posX, int posY) {
        ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
        ErrorUtil.checkNotNull(recipeWrapper, "recipeWrapper");
        if (focus != null) {
            focus = Focus.check(focus);
        }
        this.recipeCategory = recipeCategory;
        this.focus = focus;

        IFocus<ItemStack> itemStackFocus = null;
        IFocus<FluidStack> fluidStackFocus = null;
        if (focus != null) {
            Object focusValue = focus.getValue();
            if (focusValue instanceof ItemStack) {
                //noinspection unchecked
                itemStackFocus = (IFocus<ItemStack>) focus;
            } else if (focusValue instanceof FluidStack) {
                //noinspection unchecked
                fluidStackFocus = (IFocus<FluidStack>) focus;
            }
        }

        this.guiIngredientGroups = new IdentityHashMap<>();

        GuiItemStackGroup itemStackGroup = new GuiItemStackGroup(itemStackFocus, ingredientCycleOffset) {
            @Override
            public void draw(@Nonnull Minecraft minecraft, int xOffset, int yOffset, @Nonnull Color highlightColor, int mouseX, int mouseY) {
                for (GuiIngredient<ItemStack> ingredient : super.getGuiIngredients().values()) {
                    ingredient.draw(minecraft, xOffset, yOffset);
                }
            }
        };

        GuiFluidStackGroup fluidStackGroup = new GuiFluidStackGroup(fluidStackFocus, ingredientCycleOffset) {
            @Override
            public void draw(@Nonnull Minecraft minecraft, int xOffset, int yOffset, @Nonnull Color highlightColor, int mouseX, int mouseY) {
                for (GuiIngredient<FluidStack> ingredient : super.getGuiIngredients().values()) {
                    ingredient.draw(minecraft, xOffset, yOffset);
                }
            }
        };

        this.guiIngredientGroups.put(VanillaTypes.ITEM, itemStackGroup);
        this.guiIngredientGroups.put(VanillaTypes.FLUID, fluidStackGroup);

        setPosition(posX, posY);

        this.recipeWrapper = recipeWrapper;
        this.recipeBorder = Internal.getHelpers().getGuiHelper().getRecipeBackground();
    }


    @Override
    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void drawRecipe(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        IDrawable background = recipeCategory.getBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        final int recipeMouseX = mouseX - posX;
        final int recipeMouseY = mouseY - posY;
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 0.0F);
        {
            IDrawable categoryBackground = recipeCategory.getBackground();
            int width = categoryBackground.getWidth() + (2 * RECIPE_BORDER_PADDING);
            int height = categoryBackground.getHeight() + (2 * RECIPE_BORDER_PADDING);
            recipeBorder.draw(minecraft, -RECIPE_BORDER_PADDING, -RECIPE_BORDER_PADDING, width, height);
            background.draw(minecraft);
            recipeCategory.drawExtras(minecraft);
            recipeWrapper.drawInfo(minecraft, background.getWidth(), background.getHeight(), recipeMouseX, recipeMouseY);
            // drawExtras and drawInfo often render text which messes with the color, this clears it
            GlStateManager.color(1, 1, 1, 1);
            if (shapelessIcon != null) {
                shapelessIcon.draw(minecraft, background.getWidth());
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();

        minecraft.getRenderItem().zLevel += 120.0F;
        for (GuiIngredientGroup<?> guiIngredientGroup : guiIngredientGroups.values()) {
            guiIngredientGroup.draw(minecraft, posX, posY, highlightColor, mouseX, mouseY);
        }
        minecraft.getRenderItem().zLevel -= 120.0F;

        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
    }

    @Override
    public void drawOverlays(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {

    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        final IDrawable background = recipeCategory.getBackground();
        final Rectangle backgroundRect = new Rectangle(posX, posY, background.getWidth(), background.getHeight());
        return backgroundRect.contains(mouseX, mouseY);
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public void draw(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        drawRecipe(minecraft, mouseX, mouseY);
        drawOverlays(minecraft, mouseX, mouseY);
    }

    @Nonnull
    @Override
    public IGuiItemStackGroup getItemStacks() {
        return (IGuiItemStackGroup) this.guiIngredientGroups.get(VanillaTypes.ITEM);
    }

    @Nonnull
    @Override
    public IGuiFluidStackGroup getFluidStacks() {
        return (IGuiFluidStackGroup) this.guiIngredientGroups.get(VanillaTypes.FLUID);
    }

    @Nonnull
    @Override
    public <T> IGuiIngredientGroup<T> getIngredientsGroup(@Nonnull IIngredientType<T> ingredientType) {
        @SuppressWarnings("unchecked")
        GuiIngredientGroup<T> guiIngredientGroup = guiIngredientGroups.get(ingredientType);
        if (guiIngredientGroup == null) {
            IFocus<T> focus = null;
            if (this.focus != null) {
                Object focusValue = this.focus.getValue();
                if (ingredientType.getIngredientClass().isInstance(focusValue)) {
                    //noinspection unchecked
                    focus = (IFocus<T>) this.focus;
                }
            }
            guiIngredientGroup = new GuiIngredientGroup<T>(ingredientType, focus, ingredientCycleOffset) {
                @Override
                public void draw(@Nonnull Minecraft minecraft, int xOffset, int yOffset, @Nonnull Color highlightColor, int mouseX, int mouseY) {
                    for (GuiIngredient<T> ingredient : super.getGuiIngredients().values()) {
                        ingredient.draw(minecraft, xOffset, yOffset);
                    }
                }
            };
            guiIngredientGroups.put(ingredientType, guiIngredientGroup);
        }
        return guiIngredientGroup;
    }

    @Nonnull
    @Override
    public <T> IGuiIngredientGroup<T> getIngredientsGroup(@Nonnull Class<T> ingredientClass) {
        IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
        IIngredientType<T> ingredientType = ingredientRegistry.getIngredientType(ingredientClass);
        return getIngredientsGroup(ingredientType);
    }

    @Nullable
    @Override
    public IFocus<?> getFocus() {
        return focus;
    }

    @Nonnull
    @Override
    public IRecipeCategory<?> getRecipeCategory() {
        return recipeCategory;
    }

    @Override
    public void setRecipeTransferButton(int posX, int posY) {
        //:P
    }

    @Override
    public void setShapeless() {
        this.shapelessIcon = new ShapelessIcon();
    }

}
