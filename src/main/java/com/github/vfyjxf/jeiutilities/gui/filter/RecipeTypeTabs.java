package com.github.vfyjxf.jeiutilities.gui.filter;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.input.IPaged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class RecipeTypeTabs implements IPaged, IFilterLogicListener {

    private static final int BORDER_PADDING = 4;
    private static final int TAB_SIZE = 24;

    private final List<RecipeTypeTab> recipeTypeTabs = new ArrayList<>();
    private final FilterScreenLogic filterLogic;

    private int pageCount = 1;
    private int pageNumber;
    private int tabPerPage;
    private int x;
    private int y;

    public RecipeTypeTabs(FilterScreenLogic filterLogic) {
        this.filterLogic = filterLogic;
    }

    public void initLayout(int x, int y, int screenWidth, int screenHeight) {
        this.x = x;
        this.y = y;
        this.updateTabPerPage(screenHeight);
        updateLayout();
    }

    public void updateTabPerPage(int height) {
        int freeSpace = height - 54;
        int min = freeSpace / (TAB_SIZE + BORDER_PADDING);
        if (freeSpace - min * (TAB_SIZE + BORDER_PADDING) >= TAB_SIZE) {
            tabPerPage = min + 1;
        } else {
            tabPerPage = min;
        }
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY) {
        IRecipeCategory<?> selectedCategory = filterLogic.getSelectedRecipeCategory();
        for (RecipeTypeTab tab : this.recipeTypeTabs) {
            boolean selected = selectedCategory != null && tab.isSelected(selectedCategory);
            tab.render(selected, poseStack, mouseX, mouseY);
        }
    }

    public void drawTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        for (RecipeTypeTab tab : this.recipeTypeTabs) {
            if (tab.isMouseOver(mouseX, mouseY)) {
                TooltipRenderer.drawHoveringText(poseStack, tab.getTooltips(), mouseX, mouseY);
            }
        }
    }

    public void updateLayout() {
        recipeTypeTabs.clear();
        List<IRecipeCategory<?>> categories = filterLogic.getRecipeCategories();
        this.pageCount = MathUtil.divideCeil(categories.size(), this.tabPerPage);
        int tabY = y;
        final int startIndex = pageNumber * tabPerPage;
        for (int i = 0; i < tabPerPage; i++) {
            int index = i + startIndex;
            if (index >= categories.size()) {
                break;
            }
            IRecipeCategory<?> category = categories.get(index);
            this.recipeTypeTabs.add(new RecipeTypeTab(category, x, tabY));
            tabY += (RecipeTypeTab.TAB_HEIGHT + 4);
        }
    }

    public boolean handleMouseClick(double mouseX, double mouseY) {
        for (RecipeTypeTab tab : this.recipeTypeTabs) {
            if (tab.isMouseOver(mouseX, mouseY)) {
                filterLogic.setCategory(tab.getCategory());
                SoundManager soundHandler = Minecraft.getInstance().getSoundManager();
                soundHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange() {
        updateLayout();
    }

    @Override
    public void onCategoryChange(IRecipeCategory<?> selectedCategory) {
        //:P
    }

    @Override
    public boolean hasNext() {
        return pageNumber + 1 < pageCount;
    }

    @Override
    public boolean nextPage() {
        if (hasNext()) {
            pageNumber++;
        } else {
            pageNumber = 0;
        }
        updateLayout();
        return true;
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    @Override
    public boolean previousPage() {
        if (hasPrevious()) {
            pageNumber--;
        } else {
            pageNumber = pageCount - 1;
        }
        updateLayout();
        return true;
    }

    @Override
    public int getPageCount() {
        return this.pageCount;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }

}
