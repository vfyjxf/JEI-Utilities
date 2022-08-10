package com.github.vfyjxf.jeiutilities.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.gui.HoverChecker;
import mezz.jei.gui.TooltipRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Based pm {@link mezz.jei.gui.elements.GuiIconToggleButton}
 */
public abstract class GuiImageToggleButton extends AbstractGuiImageButton {

    private final IDrawable offIcon;
    private final IDrawable onIcon;
    private final HoverChecker hoverChecker;

    public GuiImageToggleButton(IDrawable offIcon, IDrawable onIcon, OnPress pressable) {
        super(pressable);
        this.offIcon = offIcon;
        this.onIcon = onIcon;
        this.hoverChecker = new HoverChecker();
        this.hoverChecker.updateBounds(this);
    }

    public void updateBounds(int x, int y, int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.x = x;
        this.y = y;
        this.hoverChecker.updateBounds(this);
    }


    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        if (this.visible) {
            IDrawable icon = isIconToggledOn() ? this.onIcon : this.offIcon;
            icon.draw(poseStack, this.x + 2, this.y + 2);
        }
    }

    public final boolean isMouseOver(double mouseX, double mouseY) {
        return this.hoverChecker.checkHover(mouseX, mouseY);
    }

    public final void drawTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            getTooltips(tooltip);
            TooltipRenderer.drawHoveringText(poseStack, tooltip, mouseX, mouseY);
        }
    }

    protected abstract void getTooltips(List<Component> tooltip);

    protected abstract boolean isIconToggledOn();

}
