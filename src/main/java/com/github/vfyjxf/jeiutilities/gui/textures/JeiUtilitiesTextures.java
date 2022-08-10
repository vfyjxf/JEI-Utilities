package com.github.vfyjxf.jeiutilities.gui.textures;

import com.github.vfyjxf.jeiutilities.JeiUtilities;
import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import com.github.vfyjxf.jeiutilities.gui.elements.RenderableSprite;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.resources.ResourceLocation;

/**
 * Based on {@link mezz.jei.gui.textures.Textures}
 */
public class JeiUtilitiesTextures {

    private static JeiUtilitiesTextures instance;

    private final SpritesManager spritesManager;

    private final RenderableNineSliceTexture recipeBackground;
    private final RenderableNineSliceTexture filterBackground;
    private final RenderableNineSliceTexture filterContentBackground;
    private final RenderableNineSliceTexture filterScrollBarBackground;
    private final RenderableNineSliceTexture searchBackground;
    private final RenderableNineSliceTexture focusSlotBackground;
    private final RenderableNineSliceTexture buttonBackground;
    private final RenderableNineSliceTexture buttonLightBackground;
    private final RenderableNineSliceTexture smallButtonBackground;
    private final RenderableNineSliceTexture smallButtonLightBackground;
    private final RenderableNineSliceTexture smallButtonDisabledBackground;
    private final RenderableNineSliceTexture scrollBar;
    private final RenderableNineSliceTexture scrollBarSelected;

    private final IDrawableStatic tabBackground;
    private final IDrawableStatic tabSelectedBackground;
    private final IDrawableStatic arrowPrevious;
    private final IDrawableStatic arrowNext;
    private final IDrawableStatic ingredientModeIcon;
    private final IDrawableStatic outputModeIcon;
    private final IDrawableStatic focusInputModeIcon;
    private final IDrawableStatic focusOutputModeIcon;
    private final IDrawableStatic maximizeButtonIcon;
    private final IDrawableStatic windowingButtonIcon;
    private final IDrawableStatic recipeFilterButtonIcon;
    private final IDrawableStatic recordButtonEnabledIcon;
    private final IDrawableStatic recordButtonDisabledIcon;


    public JeiUtilitiesTextures(SpritesManager spritesManager) {
        this.spritesManager = spritesManager;
        {
            this.recipeBackground = registerNineSliceGuiSprite("preview_recipe_background", 64, 64, 16, 16, 16, 16);
            this.filterBackground = registerNineSliceGuiSprite("filter_background", 64, 64, 16, 16, 16, 16);
            this.filterContentBackground = registerNineSliceGuiSprite("filter_content_background", 64, 64, 16, 16, 16, 16);
            this.filterScrollBarBackground = registerNineSliceGuiSprite("filter_scroll_background", 14, 14, 2, 2, 2, 2);
            this.searchBackground = registerNineSliceGuiSprite("search_background", 21, 21, 7, 7, 7, 7);
            this.focusSlotBackground = registerNineSliceGuiSprite("focus_slot_background", 24, 24, 6, 6, 6, 6);
            this.buttonBackground = registerNineSliceGuiSprite("button_background", 15, 15, 4, 4, 4, 4);
            this.buttonLightBackground = registerNineSliceGuiSprite("button_background_light", 15, 15, 4, 4, 4, 4);
            this.smallButtonBackground = registerNineSliceGuiSprite("small_button_background", 24, 11, 4, 4, 4, 4);
            this.smallButtonLightBackground = registerNineSliceGuiSprite("small_button_background_light", 24, 11, 4, 4, 4, 4);
            this.smallButtonDisabledBackground = registerNineSliceGuiSprite("small_button_background_disabled", 24, 11, 4, 4, 4, 4);
            this.scrollBar = registerNineSliceGuiSprite("filter_scroll_bar", 12, 15, 2, 2, 2, 2);
            this.scrollBarSelected = registerNineSliceGuiSprite("filter_scroll_bar_light", 12, 15, 2, 2, 2, 2);


            this.tabBackground = registerGuiSprite("tab_background", 24, 24);
            this.tabSelectedBackground = registerGuiSprite("tab_background_light", 24, 24);
            this.arrowPrevious = registerGuiSprite("icon/arrow_previous", 9, 9)
                    .trim(0, 0, 1, 1);
            this.arrowNext = registerGuiSprite("icon/arrow_next", 9, 9)
                    .trim(0, 0, 1, 1);
            this.ingredientModeIcon = registerGuiSprite("icon/button_ingredient_mode_icon", 11, 11);
            this.outputModeIcon = registerGuiSprite("icon/button_output_mode_icon", 11, 11);
            this.focusInputModeIcon = registerGuiSprite("icon/focus_mode_input_icon", 11, 11);
            this.focusOutputModeIcon = registerGuiSprite("icon/focus_mode_output_icon", 11, 11);
            this.maximizeButtonIcon = registerGuiSprite("icon/button_maximize_icon", 11, 11);
            this.windowingButtonIcon = registerGuiSprite("icon/button_windowing_icon", 11, 11);
            this.recipeFilterButtonIcon = registerGuiSprite("icon/recipe_filter_button", 16, 16);
            this.recordButtonEnabledIcon = registerGuiSprite("icon/bookmark_button_config_enable", 16, 16);
            this.recordButtonDisabledIcon = registerGuiSprite("icon/bookmark_button_config_disable", 16, 16);

        }
        instance = this;
    }

    public static JeiUtilitiesTextures getInstance() {
        return instance;
    }

    private ResourceLocation registerSprite(String name) {
        ResourceLocation location = new ResourceLocation(JeiUtilities.MOD_ID, name);
        spritesManager.registerSprite(location);
        return location;
    }

    private RenderableSprite registerGuiSprite(String name, int width, int height) {
        ResourceLocation location = registerSprite(name);
        return new RenderableSprite(spritesManager, location, width, height);
    }

    private RenderableNineSliceTexture registerNineSliceGuiSprite(String name, int width, int height, int left, int right, int top, int bottom) {
        ResourceLocation location = registerSprite(name);
        return new RenderableNineSliceTexture(spritesManager, location, width, height, left, right, top, bottom);
    }

    public RenderableNineSliceTexture getRecipeBackground() {
        return recipeBackground;
    }

    public RenderableNineSliceTexture getSearchBackground() {
        return searchBackground;
    }

    public RenderableNineSliceTexture getFocusSlotBackground() {
        return focusSlotBackground;
    }

    public RenderableNineSliceTexture getFilterBackground() {
        return filterBackground;
    }

    public RenderableNineSliceTexture getFilterContentBackground() {
        return filterContentBackground;
    }

    public RenderableNineSliceTexture getFilterScrollBarBackground() {
        return filterScrollBarBackground;
    }

    public RenderableNineSliceTexture getButtonBackground() {
        return buttonBackground;
    }

    public RenderableNineSliceTexture getButtonLightBackground() {
        return buttonLightBackground;
    }

    public RenderableNineSliceTexture getSmallButtonBackground() {
        return smallButtonBackground;
    }

    public RenderableNineSliceTexture getSmallButtonLightBackground() {
        return smallButtonLightBackground;
    }

    public RenderableNineSliceTexture getSmallButtonDisabledBackground() {
        return smallButtonDisabledBackground;
    }

    public RenderableNineSliceTexture getScrollBar() {
        return scrollBar;
    }

    public RenderableNineSliceTexture getScrollBarSelected() {
        return scrollBarSelected;
    }

    public IDrawableStatic getTabBackground() {
        return tabBackground;
    }

    public IDrawableStatic getTabSelectedBackground() {
        return tabSelectedBackground;
    }

    public IDrawableStatic getArrowPrevious() {
        return arrowPrevious;
    }

    public IDrawableStatic getArrowNext() {
        return arrowNext;
    }

    public IDrawableStatic getIngredientModeIcon() {
        return ingredientModeIcon;
    }

    public IDrawableStatic getOutputModeIcon() {
        return outputModeIcon;
    }

    public IDrawableStatic getFocusInputModeIcon() {
        return focusInputModeIcon;
    }

    public IDrawableStatic getFocusOutputModeIcon() {
        return focusOutputModeIcon;
    }

    public IDrawableStatic getMaximizeButtonIcon() {
        return maximizeButtonIcon;
    }

    public IDrawableStatic getWindowingButtonIcon() {
        return windowingButtonIcon;
    }

    public IDrawableStatic getRecipeFilterButtonIcon() {
        return recipeFilterButtonIcon;
    }

    public IDrawableStatic getRecordButtonEnabledIcon() {
        return recordButtonEnabledIcon;
    }

    public IDrawableStatic getRecordButtonDisabledIcon() {
        return recordButtonDisabledIcon;
    }
}
