package com.github.vfyjxf.jeiutilities.gui.elements;

import com.github.vfyjxf.jeiutilities.gui.input.handler.IMouseInputHandler;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.elements.GuiIconButtonSmall;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import org.jetbrains.annotations.NotNull;


/**
 * Based on {@link GuiIconButtonSmall}
 */
public class GuiImageButtonSmall extends AbstractGuiImageButton implements IMouseInputHandler {

    private final IDrawable icon;

    public GuiImageButtonSmall(IDrawable icon, int width, int height, Button.OnPress pressable) {
        super(width, height, pressable);
        this.icon = icon;
    }


    public ImmutableRect2i getArea() {
        return new ImmutableRect2i(x, y, width, height);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            boolean hovered = isMouseOver(mouseX, mouseY);
            RenderableNineSliceTexture texture = this.getButtonForState(this.active, hovered);
            texture.draw(poseStack, this.x, this.y, this.width, this.height);
            this.renderBg(poseStack, minecraft, mouseX, mouseY);

            int color = 14737632;
            if (packedFGColor != 0) {
                color = packedFGColor;
            } else if (!this.active) {
                color = 10526880;
            } else if (hovered) {
                color = 16777120;
            }
            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            float red = (color >> 16 & 255) / 255.0F;
            float blue = (color >> 8 & 255) / 255.0F;
            float green = (color & 255) / 255.0F;
            float alpha = (color >> 24 & 255) / 255.0F;
            RenderSystem.setShaderColor(red, blue, green, alpha);

            double xOffset = x + (width - this.icon.getWidth()) / 2.0;
            double yOffset = y + (height - this.icon.getHeight()) / 2.0;
            poseStack.pushPose();
            {
                poseStack.translate(xOffset, yOffset, 0);
                this.icon.draw(poseStack);
            }
            poseStack.popPose();
        }
    }

    private RenderableNineSliceTexture getButtonForState(boolean enabled, boolean hovered) {
        if (!enabled) {
            return JeiUtilitiesTextures.getInstance().getSmallButtonDisabledBackground();
        } else if (hovered) {
            return JeiUtilitiesTextures.getInstance().getSmallButtonLightBackground();
        } else {
            return JeiUtilitiesTextures.getInstance().getSmallButtonBackground();
        }
    }


}
