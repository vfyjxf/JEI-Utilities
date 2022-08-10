package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.gui.elements.GuiImageButton;
import com.github.vfyjxf.jeiutilities.gui.elements.GuiImageButtonSmall;
import com.github.vfyjxf.jeiutilities.gui.elements.GuiImageToggleButton;
import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import com.github.vfyjxf.jeiutilities.gui.input.GuiRecipeFilter;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.search.RecipeFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.overlay.IFilterTextSource;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipesFilterScreen extends Screen {

    private static final int BORDER_PADDING = 6;
    private static final int BUTTON_SIZE = 15;
    private static final int SMALL_BUTTON_WIDTH = 24;
    private static final int SMALL_BUTTON_HEIGHT = 11;

    private final FilterConfig filterConfig;
    private final FilterScreenLogic logic;
    private final GuiRecipeFilter searchField;
    private final FocusValueSlot focusValueSlot;
    private final RecipeTypeTabs recipeTypeTabs;
    private final RecipesFilterContent contents;
    private final RecipeFilter recipeFilter;

    private final GuiImageButtonSmall nextPage;
    private final GuiImageButtonSmall previousPage;
    private final GuiImageToggleButton switchFocusMode;
    private final GuiImageToggleButton switchFilterMode;
    private final GuiImageButton switchWindowSize;
    private final RenderableNineSliceTexture background;

    private ImmutableRect2i area = ImmutableRect2i.EMPTY;

    public RecipesFilterScreen(IFilterTextSource filterTextContent, IRecipeManager recipeManager) {
        super(new TextComponent("Recipes Filter"));
        this.filterConfig = new FilterConfig();
        this.logic = new FilterScreenLogic(recipeManager, filterConfig);
        this.recipeTypeTabs = new RecipeTypeTabs(logic);
        logic.addFocusListener(recipeTypeTabs);
        this.focusValueSlot = new FocusValueSlot(logic);
        this.recipeFilter = new RecipeFilter(filterTextContent, filterConfig, logic);
        this.contents = new RecipesFilterContent(this, logic, recipeFilter);

        this.searchField = new GuiRecipeFilter();
        this.searchField.setValue(filterTextContent.getFilterText());
        this.searchField.setFocused(false);
        this.searchField.setResponder(filterTextContent::setFilterText);

        JeiUtilitiesTextures textures = JeiUtilitiesTextures.getInstance();
        IDrawableStatic arrowNext = textures.getArrowNext();
        IDrawableStatic arrowPrevious = textures.getArrowPrevious();

        this.nextPage = new GuiImageButtonSmall(arrowNext, SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT, button -> recipeTypeTabs.nextPage());
        this.previousPage = new GuiImageButtonSmall(arrowPrevious, SMALL_BUTTON_WIDTH, SMALL_BUTTON_HEIGHT, button -> recipeTypeTabs.previousPage());
        this.switchFocusMode = FocusModeConfigButton.create(button -> {
            filterConfig.setFocusInput(!filterConfig.isFocusInput());
            logic.onFocusModeChange();
        });
        this.switchFilterMode = FilterModeConfigButton.create(button -> {
            filterConfig.setSearchInput(!filterConfig.isSearchInput());
            logic.onFilterModeChange();
        });
        this.switchWindowSize = new GuiImageButton(textures.getMaximizeButtonIcon(), button -> {
            //TODO implement
        });

        this.background = JeiUtilitiesTextures.getInstance().getFilterBackground();
    }

    @Override
    protected void init() {
        super.init();

        int xSize = 248;
        int ySize = this.height - 47;
        int extraSpace = 0;
        final int maxHeight = JeiUtilitiesConfig.getMaxFilterGUiHeight();
        if (ySize > maxHeight) {
            extraSpace = ySize - maxHeight;
            ySize = maxHeight;
        }
        final int guiLeft = (this.width - xSize) / 2;
        final int guiTop = 21 + (extraSpace / 2);
        this.area = new ImmutableRect2i(guiLeft, guiTop, xSize, ySize);
        int leftButtonX = guiLeft + BORDER_PADDING;
        this.nextPage.x = leftButtonX;
        this.nextPage.y = guiTop + ySize - 19;
        this.previousPage.x = leftButtonX;
        this.previousPage.y = guiTop + 11;
        this.recipeTypeTabs.initLayout(leftButtonX, guiTop + 29, xSize, ySize);

        int searchX = guiLeft + 36;
        int searchY = guiTop + 6;
        int searchWidth = (int) (xSize * 0.44);
        int slotX = searchX + searchWidth + 2;
        int focusButtonX = slotX + 24 + 2;
        int buttonY = guiTop + 9;
        this.searchField.updateBounds(searchX, searchY, searchWidth, 21);
        this.focusValueSlot.updateBounds(slotX, guiTop + 5);
        this.switchFocusMode.updateBounds(focusButtonX, buttonY, BUTTON_SIZE, BUTTON_SIZE);
        int filterModeButtonX = focusButtonX + BUTTON_SIZE + 5;
        this.switchFilterMode.updateBounds(filterModeButtonX, buttonY, BUTTON_SIZE, BUTTON_SIZE);
        this.switchWindowSize.updateBounds(filterModeButtonX + BUTTON_SIZE + 3, buttonY, BUTTON_SIZE, BUTTON_SIZE);

        int contentWidth = filterModeButtonX + BUTTON_SIZE - searchX;
        int contentHeight = ySize - 43;
        this.contents.updateBounds(searchX, guiTop + 32, contentWidth, contentHeight);

        this.addWidget(searchField);
        this.setInitialFocus(searchField);
        this.addRenderableWidget(nextPage);
        this.addRenderableWidget(previousPage);
        this.addRenderableWidget(switchFocusMode);
        this.addRenderableWidget(switchFilterMode);
        this.addRenderableWidget(switchWindowSize);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (minecraft == null) {
            return;
        }
        renderBackground(poseStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        final int x = area.getX();
        final int y = area.getY();
        final int width = area.getWidth();
        final int height = area.getHeight();
        this.background.draw(poseStack, x, y, width, height);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderBackgroundAndContent(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltips(poseStack, mouseX, mouseY);

    }

    private void renderBackgroundAndContent(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.contents.render(poseStack, mouseX, mouseY);
        //TODO : Remove
        if (this.recipeTypeTabs.getPageCount() <= 1) {
            nextPage.active = false;
            nextPage.visible = false;
            previousPage.active = false;
            previousPage.visible = false;
        } else {
            nextPage.active = true;
            nextPage.visible = true;
            previousPage.active = true;
            previousPage.visible = true;
        }
        this.nextPage.render(poseStack, mouseX, mouseY, partialTick);
        this.previousPage.render(poseStack, mouseX, mouseY, partialTick);
        this.switchFocusMode.render(poseStack, mouseX, mouseY, partialTick);
        this.switchFilterMode.render(poseStack, mouseX, mouseY, partialTick);
        this.switchWindowSize.render(poseStack, mouseX, mouseY, partialTick);
        if (JeiUtilitiesPlugin.ingredientListOverlay.hasKeyboardFocus()) {
            this.searchField.setFocused(false);
        }
        this.searchField.render(poseStack, mouseX, mouseY, partialTick);
        this.focusValueSlot.render(poseStack, mouseX, mouseY);
        this.recipeTypeTabs.render(poseStack, mouseX, mouseY);
    }

    private void renderTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        this.contents.drawTooltips(poseStack, mouseX, mouseY);
        this.switchFocusMode.drawTooltips(poseStack, mouseX, mouseY);
        this.switchFilterMode.drawTooltips(poseStack, mouseX, mouseY);
        this.focusValueSlot.drawTooltips(poseStack, mouseX, mouseY);
        this.recipeTypeTabs.drawTooltips(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) ||
                handleInput(mouseX, mouseY) ||
                recipeTypeTabs.handleMouseClick(mouseX, mouseY) ||
                focusValueSlot.handleMouseClick(mouseX, mouseY);
    }

    @Override
    public void tick() {
        this.searchField.tick();
    }

    private boolean handleInput(double mouseX, double mouseY) {

        if (searchField.isMouseOver(mouseX, mouseY)) {
            searchField.setFocused(true);
            return true;
        } else {
            searchField.setFocused(false);
        }

        return false;
    }

    public void updateLayout() {

    }

    public boolean isOpen() {
        return minecraft != null && minecraft.screen == this;
    }

    public ImmutableRect2i getArea() {
        return this.area;
    }

    public FocusValueSlot getFocusValueSlot() {
        return focusValueSlot;
    }

}
