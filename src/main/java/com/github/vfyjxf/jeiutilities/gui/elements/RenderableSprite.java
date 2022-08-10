package com.github.vfyjxf.jeiutilities.gui.elements;

import com.github.vfyjxf.jeiutilities.config.Globals;
import com.github.vfyjxf.jeiutilities.gui.textures.SpritesManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Based on {@link mezz.jei.gui.elements.DrawableSprite}
 */
public class RenderableSprite implements IDrawableStatic {

    private final SpritesManager spritesManager;
    private final ResourceLocation location;
    private final int width;
    private final int height;
    private int trimLeft;
    private int trimRight;
    private int trimTop;
    private int trimBottom;

    public RenderableSprite(SpritesManager spritesManager, ResourceLocation location, int width, int height) {
        this.spritesManager = spritesManager;
        this.location = location;
        this.width = width;
        this.height = height;
    }

    public RenderableSprite trim(int left, int right, int top, int bottom) {
        this.trimLeft = left;
        this.trimRight = right;
        this.trimTop = top;
        this.trimBottom = bottom;
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw(@NotNull PoseStack poseStack, int xOffset, int yOffset) {
        draw(poseStack, xOffset, yOffset, 0, 0, 0, 0);
    }

    @Override
    public void draw(PoseStack poseStack, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight) {
        TextureAtlasSprite sprite = spritesManager.getSprite(location);
        int textureWidth = this.width;
        int textureHeight = this.height;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Globals.LOCATION_JEIU_GUI_TEXTURE_ATLAS);

        maskTop += trimTop;
        maskBottom += trimBottom;
        maskLeft += trimLeft;
        maskRight += trimRight;

        int x = xOffset + maskLeft;
        int y = yOffset + maskTop;
        int width = textureWidth - maskRight - maskLeft;
        int height = textureHeight - maskBottom - maskTop;
        float uSize = sprite.getU1() - sprite.getU0();
        float vSize = sprite.getV1() - sprite.getV0();

        float minU = sprite.getU0() + uSize * (maskLeft / (float) textureWidth);
        float minV = sprite.getV0() + vSize * (maskTop / (float) textureHeight);
        float maxU = sprite.getU1() - uSize * (maskRight / (float) textureWidth);
        float maxV = sprite.getV1() - vSize * (maskBottom / (float) textureHeight);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = poseStack.last().pose();
        bufferBuilder.vertex(matrix, x, y + height, 0)
                .uv(minU, maxV)
                .endVertex();
        bufferBuilder.vertex(matrix, x + width, y + height, 0)
                .uv(maxU, maxV)
                .endVertex();
        bufferBuilder.vertex(matrix, x + width, y, 0)
                .uv(maxU, minV)
                .endVertex();
        bufferBuilder.vertex(matrix, x, y, 0)
                .uv(minU, minV)
                .endVertex();
        tessellator.end();
    }
}
