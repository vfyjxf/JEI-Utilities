package com.github.vfyjxf.jeiutilities.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import org.jetbrains.annotations.NotNull;

public class GuiImageButton extends AbstractGuiImageButton {

    private final IDrawable icon;

    public GuiImageButton(IDrawable icon, OnPress pressable) {
        super(pressable);
        this.icon = icon;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        if (this.visible) {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

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

            double xOffset = x + (width - icon.getWidth()) / 2.0;
            double yOffset = y + (height - icon.getHeight()) / 2.0;
            poseStack.pushPose();
            {
                poseStack.translate(xOffset, yOffset, 0);
                icon.draw(poseStack);
            }
            poseStack.popPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
