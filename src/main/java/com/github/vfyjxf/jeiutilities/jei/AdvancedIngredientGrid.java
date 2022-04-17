package com.github.vfyjxf.jeiutilities.jei;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
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
import mezz.jei.util.LegacyUtil;
import mezz.jei.util.MathUtil;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.items.ItemHandlerHelper;

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

    private int historySize;
    private int columns;
    private final IngredientListBatchRenderer guiHistoryIngredientSlots;
    @SuppressWarnings("rawtypes")
    private final List<IIngredientListElement> historyIngredientElements = new ArrayList<>();

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

        this.getArea().setBounds(x, y, width, height);
        this.guiIngredientSlots.clear();
        this.guiHistoryIngredientSlots.clear();
        this.historySize = columns * USE_ROWS;

        if (rows == 0 || columns < Config.smallestNumColumns) {
            return false;
        }

        rows = rows - USE_ROWS;

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

        for (int row = 0; row < USE_ROWS; row++) {
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

        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void draw(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        GlStateManager.disableBlend();

        guiIngredientSlots.render(minecraft);

        Rectangle firstRect = guiHistoryIngredientSlots.getAllGuiIngredientSlots().get(0).getArea();
        GuiUtils.drawGradientRect(0, firstRect.x, firstRect.y, firstRect.x + firstRect.width * this.columns, firstRect.y + firstRect.height * USE_ROWS, JeiUtilitiesConfig.backgroundColour, JeiUtilitiesConfig.backgroundColour);

        guiHistoryIngredientSlots.render(minecraft);

        if (!shouldDeleteItemOnClick(minecraft, mouseX, mouseY) && isMouseOver(mouseX, mouseY)) {
            IngredientRenderer hovered = guiIngredientSlots.getHovered(mouseX, mouseY);
            if (hovered != null) {
                hovered.drawHighlight();
            }
            IngredientRenderer hoveredHistory = guiHistoryIngredientSlots.getHovered(mouseX, mouseY);
            if (hoveredHistory != null) {
                hoveredHistory.drawHighlight();
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
                IngredientRenderer hoveredHistory = getGuiHistoryIngredientSlots().getHovered(mouseX, mouseY);
                if (hoveredHistory != null) {
                    hoveredHistory.drawTooltip(minecraft, mouseX, mouseY);
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
        ClickedIngredient<?> clickedHistory = this.guiHistoryIngredientSlots.getIngredientUnderMouse(mouseX, mouseY);
        if (clickedHistory != null) {
            clickedHistory.setAllowsCheating();
        }
        return clickedHistory;
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
                                if (ItemHandlerHelper.canItemStacksStack(itemStack, value)) {
                                    return false;
                                }
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
            Object normalized = normalize(value);
            IIngredientListElement<?> ingredient = IngredientListElement.create(
                    normalized,
                    ingredientRegistry.getIngredientHelper(normalized),
                    ingredientRegistry.getIngredientRenderer(normalized),
                    ForgeModIdHelper.getInstance(),
                    ORDER_TRACKER.getOrderIndex(normalized, ingredientRegistry.getIngredientHelper(normalized))
            );
            historyIngredientElements.removeIf(element -> areIngredientEqual(element.getIngredient(), normalized));
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

    public void removeElement(int index){
    	historyIngredientElements.remove(index);
    	guiHistoryIngredientSlots.set(0, historyIngredientElements);
    }

    private <T> T normalize(T ingredient) {
        IIngredientHelper<T> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredient);
        T copy = LegacyUtil.getIngredientCopy(ingredient, ingredientHelper);
        if (copy instanceof ItemStack) {
            ((ItemStack) copy).setCount(1);
        } else if (copy instanceof FluidStack) {
            ((FluidStack) copy).amount = 1000;
        }
        return copy;
    }

    private boolean areIngredientEqual(Object ingredient1, Object ingredient2) {
        if (ingredient1 instanceof ItemStack && ingredient2 instanceof ItemStack) {
            ItemStack itemStack1 = (ItemStack) ingredient1;
            ItemStack itemStack2 = (ItemStack) ingredient2;
            return itemStack1.isItemEqual(itemStack2) && ItemStack.areItemStackTagsEqual(itemStack1, itemStack2);
        }
        if (ingredient1 instanceof FluidStack && ingredient2 instanceof FluidStack) {
            return ((FluidStack) ingredient1).isFluidEqual((FluidStack) ingredient2);
        }
        return ingredient1.hashCode() == ingredient2.hashCode();
    }

}
