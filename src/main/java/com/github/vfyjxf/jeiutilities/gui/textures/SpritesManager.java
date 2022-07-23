package com.github.vfyjxf.jeiutilities.gui.textures;

import com.github.vfyjxf.jeiutilities.config.Globals;
import mezz.jei.gui.textures.JeiSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Based on {@link JeiSpriteUploader}
 */
public class SpritesManager extends TextureAtlasHolder {

    private final Set<ResourceLocation> sprites = new HashSet<>();

    public SpritesManager(TextureManager textureManager) {
        super(textureManager, Globals.LOCATION_JEIU_GUI_TEXTURE_ATLAS, "gui");
    }

    public void registerSprite(ResourceLocation location) {
        sprites.add(location);
    }

    @Override
    protected @NotNull Stream<ResourceLocation> getResourcesToLoad() {
        return Collections.unmodifiableSet(sprites).stream();
    }

    @Override
    public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation location) {
        return super.getSprite(location);
    }

}
