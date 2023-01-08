package com.github.vfyjxf.jeiutilities.gui.history;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.SplittingMode;
import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.config.Config;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.gui.overlay.GridAlignment;
import mezz.jei.gui.overlay.IngredientGrid;
import mezz.jei.ingredients.IngredientListElement;
import mezz.jei.input.ClickedIngredient;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.render.IngredientListBatchRenderer;
import mezz.jei.render.IngredientListSlot;
import mezz.jei.render.IngredientRenderer;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.GiveMode;
import mezz.jei.util.MathUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ORDER_TRACKER;
import static com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin.ingredientRegistry;

/**
 * @author vfyjxf
 */
public class AdvancedIngredientGrid extends IngredientGrid {

    public static final int USE_ROWS = 2;
    public static final int MIN_ROWS = 6;

    private int historySize;
    private int columns;
    private final IngredientListBatchRenderer guiHistoryIngredientSlots;
    @SuppressWarnings("rawtypes")
    private final List<IIngredientListElement> historyIngredientElements = new ArrayList<>();

    private boolean showHistory;

    public AdvancedIngredientGrid() {
        super(GridAlignment.LEFT);
        this.guiHistoryIngredientSlots = new IngredientListBatchRenderer();
    }

    @Override
    public boolean updateBounds(@Nonnull Rectangle availableArea, int minWidth, @Nonnull Collection<Rectangle> exclusionAreas) {
        final int columns = Math.min(availableArea.width / INGREDIENT_WIDTH, Config.getMaxColumns());
        this.columns = columns;
        int rows = availableArea.height / INGREDIENT_HEIGHT;

        final int ingredientsWidth = columns * INGREDIENT_WIDTH;
        final int width = Math.max(ingredientsWidth, minWidth);
        final int height = rows * INGREDIENT_HEIGHT;
        final int x = availableArea.x + (availableArea.width - width);
        final int y = availableArea.y + (availableArea.height - height) / 2;
        final int xOffset = x + Math.max(0, (width - ingredientsWidth) / 2);
        final int useRows = JeiUtilitiesConfig.getUseRows();

        this.getArea().setBounds(x, y, width, height);
        this.guiIngredientSlots.clear();
        this.guiHistoryIngredientSlots.clear();
        this.historySize = columns * useRows;

        if (rows == 0 || columns < Config.smallestNumColumns) {
            return false;
        }

        if (rows >= MIN_ROWS) {
            rows = rows - useRows;
            showHistory = true;
        } else {
            showHistory = false;
        }

        for (int row = 0; row < rows; row++) {
            int y1 = y + (row * INGREDIENT_HEIGHT);
            for (int column = 0; column < columns; column++) {
                int x1 = xOffset + (column * INGREDIENT_WIDTH);
                IngredientListSlot ingredientListSlot = new IngredientListSlot(x1, y1, 1);
                Rectangle stackArea = ingredientListSlot.getArea();
                final boolean blocked = MathUtil.intersects(exclusionAreas, stackArea);
                ingredientListSlot.setBlocked(blocked);
                this.guiIngredientSlots.add(ingredientListSlot);
            }
        }

        if (showHistory) {
            for (int row = 0; row < useRows; row++) {
                int y1 = y + ((row + rows) * INGREDIENT_HEIGHT);
                for (int column = 0; column < columns; column++) {
                    int x1 = xOffset + (column * INGREDIENT_WIDTH);
                    IngredientListSlot ingredientListSlot = new IngredientListSlot(x1, y1, 1);
                    Rectangle stackArea = ingredientListSlot.getArea();
                    final boolean blocked = MathUtil.intersects(exclusionAreas, stackArea);
                    ingredientListSlot.setBlocked(blocked);
                    this.guiHistoryIngredientSlots.add(ingredientListSlot);
                }
            }
            guiHistoryIngredientSlots.set(0, this.historyIngredientElements);
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void draw(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        GlStateManager.disableBlend();

        guiIngredientSlots.render(minecraft);

        if (showHistory) {
            Rectangle firstRect = guiHistoryIngredientSlots.getAllGuiIngredientSlots().get(0).getArea();

            if (JeiUtilitiesConfig.getSplittingMode() == SplittingMode.DOTTED_LINE) {
                drawSpillingArea(firstRect.x, firstRect.y,
                        firstRect.width * this.columns,
                        firstRect.height * JeiUtilitiesConfig.getUseRows(),
                        JeiUtilitiesConfig.getBackgroundColour()
                );
            } else {
                GuiUtils.drawGradientRect(
                        0, firstRect.x, firstRect.y,
                        firstRect.x + firstRect.width * this.columns,
                        firstRect.y + firstRect.height * JeiUtilitiesConfig.getUseRows(),
                        JeiUtilitiesConfig.getBackgroundColour(),
                        JeiUtilitiesConfig.getBackgroundColour()
                );
            }

            guiHistoryIngredientSlots.render(minecraft);

        }

        if (!shouldDeleteItemOnClick(minecraft, mouseX, mouseY) && isMouseOver(mouseX, mouseY)) {
            IngredientRenderer hovered = guiIngredientSlots.getHovered(mouseX, mouseY);
            if (hovered != null) {
                hovered.drawHighlight();
            }
            if (showHistory) {
                IngredientRenderer hoveredHistory = guiHistoryIngredientSlots.getHovered(mouseX, mouseY);
                if (hoveredHistory != null) {
                    hoveredHistory.drawHighlight();
                }
            }
        }

        GlStateManager.enableAlpha();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void drawTooltips(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            if (shouldDeleteItemOnClick(minecraft, mouseX, mouseY)) {
                String deleteItem = Translator.translateToLocal("jei.tooltip.delete.item");
                TooltipRenderer.drawHoveringText(minecraft, deleteItem, mouseX, mouseY);
            } else {
                IngredientRenderer hovered = guiIngredientSlots.getHovered(mouseX, mouseY);
                if (hovered != null) {
                    hovered.drawTooltip(minecraft, mouseX, mouseY);
                }
                if (showHistory) {
                    IngredientRenderer hoveredHistory = getGuiHistoryIngredientSlots().getHovered(mouseX, mouseY);
                    if (hoveredHistory != null) {
                        hoveredHistory.drawTooltip(minecraft, mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
        IClickedIngredient<?> clicked = super.getIngredientUnderMouse(mouseX, mouseY);
        if (clicked != null) {
            return clicked;
        }
        if (showHistory) {
            ClickedIngredient<?> clickedHistory = this.guiHistoryIngredientSlots.getIngredientUnderMouse(mouseX, mouseY);
            if (clickedHistory != null) {
                clickedHistory.setAllowsCheating();
            }
            return clickedHistory;
        }
        return null;
    }

    private boolean shouldDeleteItemOnClick(Minecraft minecraft, int mouseX, int mouseY) {
        if (Config.isDeleteItemsInCheatModeActive()) {
            EntityPlayer player = minecraft.player;
            if (player != null) {
                ItemStack itemStack = player.inventory.getItemStack();
                if (!itemStack.isEmpty()) {
                    JeiRuntime runtime = Internal.getRuntime();
                    if (runtime == null || !runtime.getRecipesGui().isOpen()) {
                        GiveMode giveMode = Config.getGiveMode();
                        if (giveMode == GiveMode.MOUSE_PICKUP) {
                            IClickedIngredient<?> ingredientUnderMouse = getIngredientUnderMouse(mouseX, mouseY);
                            if (ingredientUnderMouse != null && ingredientUnderMouse.getValue() instanceof ItemStack) {
                                ItemStack value = (ItemStack) ingredientUnderMouse.getValue();
                                return !ItemHandlerHelper.canItemStacksStack(itemStack, value);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public IngredientListBatchRenderer getGuiHistoryIngredientSlots() {
        return guiHistoryIngredientSlots;
    }

    public void addHistoryIngredient(Object value) {
        if (value != null) {

            if (value instanceof RecipeInfo) {
                return;
            }

            Object normalized = IngredientHelper.getNormalize(value);
            IIngredientListElement<?> ingredient = IngredientListElement.create(
                    normalized,
                    ingredientRegistry.getIngredientHelper(normalized),
                    ingredientRegistry.getIngredientRenderer(normalized),
                    ForgeModIdHelper.getInstance(),
                    ORDER_TRACKER.getOrderIndex(normalized, ingredientRegistry.getIngredientHelper(normalized))
            );
            historyIngredientElements.removeIf(element -> areIngredientEqual(element.getIngredient(), normalized, JeiUtilitiesConfig.isMatchesNBTs()));
            historyIngredientElements.add(0, ingredient);
            if (historyIngredientElements.size() > this.historySize) {
                historyIngredientElements.remove(historyIngredientElements.size() - 1);
            }
            while (historyIngredientElements.size() > USE_ROWS * Config.largestNumColumns) {
                historyIngredientElements.remove(historyIngredientElements.size() - 1);
            }
            guiHistoryIngredientSlots.set(0, historyIngredientElements);
        }

    }

    public void removeElement(int index) {
        historyIngredientElements.remove(index);
        guiHistoryIngredientSlots.set(0, historyIngredientElements);
    }

    private boolean areIngredientEqual(@Nonnull Object ingredient1, @Nonnull Object ingredient2, boolean matchesNbt) {

        if (ingredient1 == ingredient2) {
            return true;
        }

        if (ingredient1.getClass() == ingredient2.getClass()) {
            IIngredientHelper<Object> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredient1);
            if (matchesNbt) {
                return ingredientHelper.getUniqueId(ingredient1).equals(ingredientHelper.getUniqueId(ingredient2));
            }
            return ingredientHelper.getWildcardId(ingredient1).equals(ingredientHelper.getWildcardId(ingredient2));
        }

        return false;
    }

    private void drawSpillingArea(int x, int y, int width, int height, int color) {

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_STIPPLE);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glLineWidth(2F);
        GL11.glLineStipple(2, (short) 0x00FF);

        GL11.glBegin(GL11.GL_LINE_LOOP);

        GL11.glVertex2i(x, y);
        GL11.glVertex2i(x + width, y);
        GL11.glVertex2i(x + width, y + height);
        GL11.glVertex2i(x, y + height);

        GL11.glEnd();

        GL11.glLineStipple(2, (short) 0xFFFF);
        GL11.glLineWidth(2F);
        GL11.glDisable(GL11.GL_LINE_STIPPLE);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.popMatrix();

    }

    //TODO:implements it

    /**
     * An ingredient such as energy ingredient in ender:io and Multiblocked.
     * It should not be added to the browsing history because it is meaningless in itself
     *
     * @return Whether the current ingredient is an ignored ingredient
     */
    private boolean isIgnoredIngredients() {
        return false;
    }


}
