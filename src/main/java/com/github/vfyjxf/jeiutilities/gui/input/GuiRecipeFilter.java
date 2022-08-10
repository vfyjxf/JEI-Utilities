package com.github.vfyjxf.jeiutilities.gui.input;

import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.gui.HoverChecker;
import mezz.jei.input.GuiTextFieldFilter;
import mezz.jei.input.TextHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Based on {@link GuiTextFieldFilter}
 */
public class GuiRecipeFilter extends EditBox {

    private static final int MAX_SEARCH_LENGTH = 112;

    private static final TextHistory history = new TextHistory();

    private final HoverChecker hoverChecker;
    private final RenderableNineSliceTexture background;
    private boolean previousKeyboardRepeatEnabled;

    public GuiRecipeFilter() {
        super(Minecraft.getInstance().font, 0, 0, 0, 0, TextComponent.EMPTY);

        setMaxLength(MAX_SEARCH_LENGTH);
        this.hoverChecker = new HoverChecker();

        this.background = JeiUtilitiesTextures.getInstance().getSearchBackground();
    }

    public void updateBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hoverChecker.updateBounds(y, y + height, x, x + width);
        setHighlightPos(getCursorPosition());
    }


    @Override
    public void setValue(String filterText) {
        if (!filterText.equals(getValue())) {
            super.setValue(filterText);
        }
    }

    public Optional<String> getHistory(TextHistory.Direction direction) {
        String currentText = getValue();
        return history.get(direction, currentText);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return hoverChecker.checkHover(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean keyboardFocus) {
        final boolean previousFocus = isFocused();
        super.setFocused(keyboardFocus);

        if (previousFocus != keyboardFocus) {
            Minecraft minecraft = Minecraft.getInstance();
            if (keyboardFocus) {
                previousKeyboardRepeatEnabled = minecraft.keyboardHandler.sendRepeatsToGui;
                minecraft.keyboardHandler.setSendRepeatsToGui(true);
            } else {
                minecraft.keyboardHandler.setSendRepeatsToGui(previousKeyboardRepeatEnabled);
            }

            String text = getValue();
            history.add(text);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isDrawing = false;

    @Override
    protected boolean isBordered() {
        if (this.isDrawing) {
            return false;
        }
        return super.isBordered();
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.isDrawing = true;
        if (this.isVisible()) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            background.draw(poseStack, this.x, this.y, this.width, this.height);
        }
        super.renderButton(poseStack, mouseX, mouseY, partialTicks);
        this.isDrawing = false;
    }

}
