package com.github.vfyjxf.jeiutilities.gui.elements;

import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.elements.GuiIconButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Based on {@link GuiIconButton}
 */
public abstract class AbstractGuiImageButton extends Button {

    public AbstractGuiImageButton(OnPress pressable) {
        super(0, 0, 0, 0, TextComponent.EMPTY, pressable);
    }

    public AbstractGuiImageButton(int width, int height, OnPress pressable) {
        super(0, 0, width, height,TextComponent.EMPTY, pressable);
    }

    public void updateBounds(int x, int y, int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.x = x;
        this.y = y;
    }

    public void updateBounds(ImmutableRect2i area) {
        this.x = area.getX();
        this.y = area.getY();
        this.width = area.getWidth();
        this.height = area.getHeight();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft minecraft = Minecraft.getInstance();
            RenderableNineSliceTexture texture = this.getButtonForState(hovered);
            texture.draw(poseStack, this.x, this.y, this.width, this.height);
            this.renderBg(poseStack, minecraft, mouseX, mouseY);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private RenderableNineSliceTexture getButtonForState(boolean hovered) {
        if (hovered) {
            return JeiUtilitiesTextures.getInstance().getButtonLightBackground();
        } else {
            return JeiUtilitiesTextures.getInstance().getButtonBackground();
        }
    }


}
