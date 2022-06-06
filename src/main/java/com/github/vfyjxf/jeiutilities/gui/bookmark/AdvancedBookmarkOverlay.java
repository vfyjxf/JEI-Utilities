package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.gui.recipe.RecipeLayoutLite;
import com.github.vfyjxf.jeiutilities.jei.JeiUtilitiesPlugin;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.gui.Focus;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Set;

import static com.github.vfyjxf.jeiutilities.config.KeyBindings.displayRecipe;

@SuppressWarnings("unused")
public class AdvancedBookmarkOverlay extends BookmarkOverlay {

    private static final int BUTTON_SIZE = 20;

    private final IngredientGridWithNavigation contents;
    private final GuiIconToggleButton recordConfigButton;
    private final BookmarkInputHandler inputHandler;

    private RecipeInfo<?, ?> infoUnderMouse;
    private RecipeLayoutLite recipeLayout;

    public static BookmarkOverlay create(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            return new AdvancedBookmarkOverlay(bookmarkList, guiHelper, guiScreenHelper);
        } else {
            return new BookmarkOverlay(bookmarkList, guiHelper, guiScreenHelper);
        }
    }

    private AdvancedBookmarkOverlay(BookmarkList bookmarkList, GuiHelper guiHelper, GuiScreenHelper guiScreenHelper) {
        super(bookmarkList, guiHelper, guiScreenHelper);
        this.recordConfigButton = RecordConfigButton.create(this);
        this.contents = ObfuscationReflectionHelper.getPrivateValue(BookmarkOverlay.class, this, "contents");
        this.inputHandler = BookmarkInputHandler.getInstance();
    }

    @Override
    public void updateBounds(@Nonnull Rectangle area, @Nonnull Set<Rectangle> guiExclusionAreas) {
        super.updateBounds(area, guiExclusionAreas);
        Rectangle rectangle = new Rectangle(area);
        rectangle.x = contents.getArea().x;
        rectangle.width = contents.getArea().width;
        this.recordConfigButton.updateBounds(new Rectangle(
                rectangle.x + BUTTON_SIZE + 2,
                (int) Math.floor(rectangle.getMaxY()) - BUTTON_SIZE - 2,
                BUTTON_SIZE,
                BUTTON_SIZE
        ));
    }

    @Override
    public void drawScreen(@Nonnull Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(minecraft, mouseX, mouseY, partialTicks);
        this.recordConfigButton.draw(minecraft, mouseX, mouseY, partialTicks);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void drawTooltips(@Nonnull Minecraft minecraft, int mouseX, int mouseY) {
        boolean renderRecipe = false;

        if (Keyboard.isKeyDown(displayRecipe.getKeyCode())) {
            Object ingredientUnderMouse = this.getIngredientUnderMouse();
            if (ingredientUnderMouse instanceof RecipeInfo) {
                RecipeInfo recipeInfo = (RecipeInfo) ingredientUnderMouse;
                RecipeLayoutLite recipeLayout;
                if (this.infoUnderMouse == recipeInfo) {
                    recipeLayout = this.recipeLayout;
                } else {
                    this.infoUnderMouse = recipeInfo;

                    recipeLayout = RecipeLayoutLite.create(
                            JeiUtilitiesPlugin.recipeRegistry.getRecipeCategory(recipeInfo.getRecipeCategoryUid()),
                            recipeInfo.getRecipeWrapper(),
                            new Focus<>(recipeInfo.getMode(),
                                    recipeInfo.getIngredient()),
                            mouseX, mouseY);
                    this.recipeLayout = recipeLayout;
                }

                if (recipeLayout != null) {
                    recipeLayout.drawRecipe(minecraft, mouseX, mouseY);
                    renderRecipe = true;
                }

            }
        }

        if (!renderRecipe) {
            super.drawTooltips(minecraft, mouseX, mouseY);
            this.recordConfigButton.drawTooltips(minecraft, mouseX, mouseY);
        }
        if (inputHandler.getDraggedElement() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 200.0F);
            IIngredientRenderer ingredientRenderer = inputHandler.getDraggedElement().getIngredientRenderer();
            ingredientRenderer.render(minecraft, mouseX, mouseY, inputHandler.getDraggedElement().getIngredient());
            GlStateManager.popMatrix();
        }

    }

    @Override
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean result = super.handleMouseClicked(mouseX, mouseY, mouseButton);

        if (recordConfigButton.isMouseOver(mouseX, mouseY)) {
            return recordConfigButton.handleMouseClick(mouseX, mouseY);
        }

        return result;
    }
}
