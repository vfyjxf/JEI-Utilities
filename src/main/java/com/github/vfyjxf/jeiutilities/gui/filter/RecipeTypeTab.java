package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.Internal;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.HoverChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeTypeTab {

    public static final int TAB_HEIGHT = 24;
    public static final int TAB_WIDTH = 24;
    @NotNull
    private final IRecipeCategory<?> category;

    private int x;
    private int y;
    private final HoverChecker hoverChecker;

    public RecipeTypeTab(@NotNull IRecipeCategory<?> category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.hoverChecker = new HoverChecker();
        this.hoverChecker.updateBounds(y, y + TAB_HEIGHT, x, x + TAB_WIDTH);
    }

    public void render(boolean selected, PoseStack poseStack, int mouseX, int mouseY) {
        JeiUtilitiesTextures textures = JeiUtilitiesTextures.getInstance();
        IDrawable background = selected ? textures.getTabSelectedBackground() : textures.getTabBackground();
        background.draw(poseStack, x, y);
        IDrawable icon = this.category.getIcon();
        int iconX = x + 4;
        int iconY = y + 4;
        iconX += (16 - icon.getWidth()) / 2;
        iconY += (16 - icon.getHeight()) / 2;
        icon.draw(poseStack, iconX, iconY);
    }

    public boolean isSelected(@NotNull IRecipeCategory<?> selectedCategory) {
        ResourceLocation categoryUid = category.getRecipeType().getUid();
        ResourceLocation selectedCategoryUid = selectedCategory.getRecipeType().getUid();
        return categoryUid.equals(selectedCategoryUid);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return hoverChecker.checkHover(mouseX, mouseY);
    }

    public List<Component> getTooltips() {
        List<Component> tooltip = new ArrayList<>();
        Component title = category.getTitle();
        //noinspection ConstantConditions
        if (title != null) {
            tooltip.add(title);
        }

        ResourceLocation uid = category.getRecipeType().getUid();
        String modId = uid.getNamespace();
        IModIdHelper modIdHelper = Internal.getHelpers().getModIdHelper();
        if (modIdHelper.isDisplayingModNameEnabled()) {
            String modName = modIdHelper.getFormattedModNameForModId(modId);
            tooltip.add(new TextComponent(modName));
        }
        return tooltip;
    }

    public @NotNull IRecipeCategory<?> getCategory() {
        return category;
    }
}
