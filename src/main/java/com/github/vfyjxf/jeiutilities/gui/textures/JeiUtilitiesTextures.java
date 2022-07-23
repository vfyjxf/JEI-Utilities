package com.github.vfyjxf.jeiutilities.gui.textures;

import com.github.vfyjxf.jeiutilities.JeiUtilities;
import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Based on {@link mezz.jei.gui.textures.Textures}
 */
public class JeiUtilitiesTextures {

    private static JeiUtilitiesTextures instance;

    private final SpritesManager spritesManager;

    private final RenderableNineSliceTexture recipeBackground;

    public JeiUtilitiesTextures(SpritesManager spritesManager) {
        this.spritesManager = spritesManager;
        {
            this.recipeBackground = registerNineSliceGuiSprite("preview_recipe_background", 64, 64, 16, 16, 16, 16);
        }
        instance = this;
    }

    public static JeiUtilitiesTextures getInstance() {
        return instance;
    }

    private ResourceLocation registerSprite(String name) {
        ResourceLocation location = new ResourceLocation(JeiUtilities.MODE_ID, name);
        spritesManager.registerSprite(location);
        return location;
    }

    private RenderableNineSliceTexture registerNineSliceGuiSprite(String name, int width, int height, int left, int right, int top, int bottom) {
        ResourceLocation location = registerSprite(name);
        return new RenderableNineSliceTexture(spritesManager, location, width, height, left, right, top, bottom);
    }

    public RenderableNineSliceTexture getRecipeBackground() {
        return recipeBackground;
    }
}
