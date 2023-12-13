package com.github.vfyjxf.jeiutilities.gui.recipe;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import mezz.jei.Internal;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.Focus;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.ingredients.GuiFluidStackGroup;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.GuiIngredientGroup;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.ShapelessIcon;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.recipes.RecipeRegistry;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.transfer.RecipeTransferErrorInternal;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.IdentityHashMap;
import java.util.Map;

import static com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig.isAdaptiveRecipePreview;

/**
 * A lite version of {@link RecipeLayout}, used to display recipes in the bookmark area.
 * Base on the {@link RecipeLayout}, some changes were made to display the recipe more concisely.
 */
public class RecipePreviewWidget implements IRecipeLayoutDrawable {

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

    private IRecipeTransferError transferError;

    private final DrawableNineSliceTexture recipeBorder;
    private int posX;
    private int posY;

    @SuppressWarnings("rawtypes")
    @Nullable
    public static <T extends IRecipeWrapper> RecipePreviewWidget create(
            IRecipeCategory<T> recipeCategory,
            T recipeWrapper,
            @Nullable IFocus focus,
            int posX,
            int posY
    ) {
        RecipePreviewWidget recipeLayout = new RecipePreviewWidget(recipeCategory, recipeWrapper, focus, posX, posY);
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

    @SuppressWarnings("unchecked")
    public static RecipePreviewWidget createLayout(RecipeInfo<?, ?> recipeInfo, int posX, int posY) {
        return create(JeiUtilitiesPlugin.recipeRegistry.getRecipeCategory(recipeInfo.getRecipeCategoryUid()),
                recipeInfo.getRecipeWrapper(),
                new Focus<>(recipeInfo.getMode(),
                        recipeInfo.getIngredient()),
                posX, posY);
    }

    private <T extends IRecipeWrapper> RecipePreviewWidget(IRecipeCategory<T> recipeCategory, T recipeWrapper, @Nullable IFocus<?> focus, int posX, int posY) {
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
        int width = background.getWidth() + (2 * RECIPE_BORDER_PADDING);
        int height = background.getHeight() + (2 * RECIPE_BORDER_PADDING);
        checkBounds(minecraft.currentScreen, height);
        float scaling = isAdaptiveRecipePreview() ? getScaling(minecraft.currentScreen) : JeiUtilitiesConfig.getRecipePreviewScaling();
        if (scaling > 1.5F) {
            scaling = 1.5F;//prevent scaling too much.
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX * scaling, posY * scaling, 0.0F);
        GlStateManager.scale(scaling, scaling, 1.0F);
        {
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 200.0F);
        GlStateManager.scale(scaling, scaling, 1.0F);
        for (GuiIngredientGroup<?> guiIngredientGroup : guiIngredientGroups.values()) {
            guiIngredientGroup.draw(minecraft, posX, posY, highlightColor, mouseX, mouseY);
        }
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
    }

    private float getScaling(GuiScreen guiScreen) {
        if (guiScreen != null) {
            IDrawable categoryBackground = recipeCategory.getBackground();
            int backgroundWidth = categoryBackground.getWidth() + (2 * RECIPE_BORDER_PADDING);
            int backgroundHeight = categoryBackground.getHeight() + (2 * RECIPE_BORDER_PADDING);
            int maxWidth = guiScreen.width / 2;
            int maxHeight = guiScreen.height / 2;
            //when backgroundWidth > maxWidth and backgroundHeight > maxHeight,Scaling by width.
            if (backgroundWidth > maxWidth && backgroundHeight > maxHeight) {
                return (float) maxWidth / backgroundWidth + 0.1F;
            }
            if (backgroundWidth < maxWidth * 0.5 && backgroundHeight < maxHeight * 0.5) {
                return (float) maxWidth / backgroundWidth - 0.2F;
            }
            if (backgroundWidth < maxWidth && backgroundHeight > maxHeight) {
                return (float) maxHeight / backgroundHeight + 0.2F;
            }
            if (backgroundWidth >= maxWidth && backgroundHeight < maxHeight) {
                return 1.0F;
            }
            return 0.95F;
        }
        return JeiUtilitiesConfig.getRecipePreviewScaling();
    }

    private void checkBounds(GuiScreen guiScreen, int height) {
        if (guiScreen != null) {
            if (posX < 6) {
                posX += 6;
            }
            if (posY < 6) {
                posY += 6;
            }
            if (posY + height > guiScreen.height) {
                posY -= posY + height - guiScreen.height;
            }
        }
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

    public IRecipeTransferError getTransferError() {
        return transferError;
    }

    @SuppressWarnings("rawtypes")
    public IRecipeTransferError transferRecipe(Container container, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        final JeiRuntime runtime = Internal.getRuntime();
        if (runtime == null) {
            return RecipeTransferErrorInternal.INSTANCE;
        }
        final RecipeRegistry recipeRegistry = runtime.getRecipeRegistry();

        final IRecipeTransferHandler transferHandler = recipeRegistry.getRecipeTransferHandler(container, this.recipeCategory);
        if (transferHandler == null) {
            if (doTransfer) {
                Log.get().error("No Recipe Transfer handler for container {}", container.getClass());
            }
            return RecipeTransferErrorInternal.INSTANCE;
        }

        //noinspection unchecked
        transferError = transferHandler.transferRecipe(container, this, player, maxTransfer, doTransfer);
        return transferError;
    }

    public void showError(Minecraft minecraft, int mouseX, int mouseY) {
        if (transferError != null) {
            final float scaling = isAdaptiveRecipePreview() ? getScaling(minecraft.currentScreen) : JeiUtilitiesConfig.getRecipePreviewScaling();
            GlStateManager.pushMatrix();
            GlStateManager.scale(scaling, scaling, 1.0F);
            transferError.showError(minecraft, mouseX, mouseY, this, posX, posY);
            GlStateManager.popMatrix();
        }
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

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
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
