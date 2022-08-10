package com.github.vfyjxf.jeiutilities.gui.filter;

import com.github.vfyjxf.jeiutilities.gui.elements.RenderableNineSliceTexture;
import com.github.vfyjxf.jeiutilities.gui.textures.JeiUtilitiesTextures;
import com.github.vfyjxf.jeiutilities.helper.DragHelper;
import com.github.vfyjxf.jeiutilities.helper.IngredientHelper;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.Internal;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.render.IngredientRenderHelper;
import mezz.jei.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("rawtypes")
public class FocusValueSlot extends GuiComponent {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final int BORDER_PADDING = 4;

    private final RenderableNineSliceTexture background;
    private final FilterScreenLogic logic;
    @Nullable
    private ITypedIngredient<?> focusValue;
    private IIngredientRenderer focusRender;
    private ImmutableRect2i area;
    private ImmutableRect2i contentArea;

    public FocusValueSlot(FilterScreenLogic filterScreenLogic) {
        this.logic = filterScreenLogic;
        this.area = new ImmutableRect2i(0, 0, 24, 24);
        this.contentArea = new ImmutableRect2i(0, 0, 16, 16);
        this.background = JeiUtilitiesTextures.getInstance().getFocusSlotBackground();
    }

    public void updateBounds(int x, int y) {
        this.area = new ImmutableRect2i(x, y, 24, 24);
        this.contentArea = new ImmutableRect2i(x + BORDER_PADDING, y + BORDER_PADDING, 16, 16);
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableDepthTest();
        poseStack.pushPose();
        {
            this.background.draw(poseStack, this.area.getX(), this.area.getY(), this.area.getWidth(), this.area.getWidth());
            RenderSystem.enableBlend();
            drawIngredient(poseStack);
            RenderSystem.disableBlend();
        }
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return contentArea.contains(mouseX, mouseY);
    }

    @SuppressWarnings("unchecked")
    public void drawTooltips(PoseStack poseStack, int mouseX, int mouseY) {

        if (!isMouseOver(mouseX,mouseY) || DragHelper.isDragging()){
            return;
        }

        poseStack.pushPose();
        {
            drawHighlight(poseStack);
        }
        poseStack.popPose();

        if (this.focusValue != null && this.focusRender != null) {
            IIngredientType ingredientType = focusValue.getType();
            Object value = focusValue.getIngredient();
            try {
                IIngredientRenderer ingredientRenderer = this.focusRender;
                IModIdHelper modIdHelper = Internal.getHelpers().getModIdHelper();
                IIngredientHelper ingredientHelper = IngredientHelper.getIngredientHelper(ingredientType);
                List<Component> tooltip = IngredientRenderHelper.getIngredientTooltipSafe(value, ingredientRenderer, ingredientHelper, modIdHelper);
                TooltipRenderer.drawHoveringText(poseStack, tooltip, mouseX, mouseY, value, ingredientRenderer);

                RenderSystem.enableDepthTest();
            } catch (RuntimeException e) {
                LOGGER.error("Exception when rendering tooltip on {}.", value, e);
            }
        } else {
            List<Component> tooltip = List.of(
                    new TranslatableComponent("jeiutilities.gui.tooltip.focus.title"),
                    new TranslatableComponent("jeiutilities.gui.tooltip.focus.description1").withStyle(ChatFormatting.GRAY),
                    new TranslatableComponent("jeiutilities.gui.tooltip.focus.description2").withStyle(ChatFormatting.GRAY),
                    new TranslatableComponent("jeiutilities.gui.tooltip.focus.description3").withStyle(ChatFormatting.GRAY)
            );
            TooltipRenderer.drawHoveringText(poseStack, tooltip, mouseX, mouseY);
        }

    }

    private void drawHighlight(PoseStack poseStack) {
        int x = this.contentArea.getX();
        int y = this.contentArea.getY();
        int width = this.contentArea.getWidth();
        int height = this.contentArea.getHeight();

        RenderSystem.disableDepthTest();
        fill(poseStack, x, y, x + width, y + height, 0x80FFFFFF);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @SuppressWarnings("unchecked")
    private void drawIngredient(PoseStack poseStack) {
        if (this.focusValue != null) {
            Object ingredient = this.focusValue.getIngredient();
            IIngredientRenderer ingredientRenderer = this.focusRender;
            try {
                poseStack.pushPose();
                poseStack.translate(this.contentArea.getX(), this.contentArea.getY(), 0);
                ingredientRenderer.render(poseStack, ingredient);
                poseStack.popPose();
            } catch (RuntimeException | LinkageError e) {
                throw ErrorUtil.createRenderIngredientException(e, ingredient, JeiUtilitiesPlugin.registeredIngredients);
            }
        }
    }

    @Nullable
    public ITypedIngredient<?> getFocusValue() {
        return this.focusValue;
    }

    public ImmutableRect2i getArea() {
        return area;
    }

    public ImmutableRect2i getContentArea() {
        return contentArea;
    }

    public boolean handleMouseClick(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            this.cleanFocus();
            return true;
        }
        return false;
    }

    private void cleanFocus() {
        this.focusValue = null;
        this.focusRender = null;
        this.logic.cleanFocus();
    }

    public void setFocusValue(@NotNull ITypedIngredient<?> focusValue) {
        this.focusValue = focusValue;
        this.focusRender = IngredientHelper.getIngredientRenderer(focusValue.getIngredient());
        this.logic.setFocus(focusValue);
    }

}
