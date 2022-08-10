package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.gui.recipe.RecipeLayoutLite;
import com.github.vfyjxf.jeiutilities.jei.recipe.IRecipeInfo;
import com.github.vfyjxf.jeiutilities.mixin.accessor.BookmarkOverlayAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.core.config.IClientConfig;
import mezz.jei.core.config.IWorldConfig;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.textures.Textures;
import mezz.jei.input.mouse.IUserInputHandler;
import mezz.jei.input.mouse.handlers.CombinedInputHandler;
import mezz.jei.input.mouse.handlers.ProxyInputHandler;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

import static com.github.vfyjxf.jeiutilities.config.KeyBindings.displayPreview;
import static com.github.vfyjxf.jeiutilities.config.KeyBindings.isKeyDown;
import static mezz.jei.gui.overlay.IngredientGrid.INGREDIENT_HEIGHT;
import static mezz.jei.gui.overlay.IngredientGrid.INGREDIENT_WIDTH;


@SuppressWarnings({"unused", "rawtypes"})
public class AdvancedBookmarkOverlay extends BookmarkOverlay {

    private static final int INNER_PADDING = 2;
    private static final int BUTTON_SIZE = 20;

    private final BookmarkOverlayAccessor accessor = (BookmarkOverlayAccessor) this;

    private final GuiIconToggleButton recordConfigButton;
    private final IngredientGridWithNavigation contents;
    @Nullable
    private RecipeLayoutLite recipeLayout;
    private IRecipeInfo infoUnderMouse;
    private boolean showError;

    public static BookmarkOverlay create(
            BookmarkList bookmarkList,
            Textures textures,
            IngredientGridWithNavigation contents,
            IClientConfig clientConfig,
            IWorldConfig worldConfig,
            GuiScreenHelper guiScreenHelper,
            IConnectionToServer serverConnection
    ) {
        if (JeiUtilitiesConfig.getRecordRecipes()) {
            return new AdvancedBookmarkOverlay(bookmarkList, textures, contents, clientConfig, worldConfig, guiScreenHelper, serverConnection);
        } else {
            return new BookmarkOverlay(bookmarkList, textures, contents, clientConfig, worldConfig, guiScreenHelper, serverConnection);
        }
    }

    public AdvancedBookmarkOverlay(
            BookmarkList bookmarkList,
            Textures textures,
            IngredientGridWithNavigation contents,
            IClientConfig clientConfig,
            IWorldConfig worldConfig,
            GuiScreenHelper guiScreenHelper,
            IConnectionToServer serverConnection
    ) {
        super(bookmarkList,
                textures,
                contents,
                clientConfig,
                worldConfig,
                guiScreenHelper,
                serverConnection
        );
        this.recordConfigButton = RecordConfigButton.create(this);
        this.contents = accessor.getContents();
    }

    @Override
    public boolean updateBounds(@NotNull Set<ImmutableRect2i> guiExclusionAreas) {
        ImmutableRect2i parentArea = accessor.getParentArea();
        ImmutableRect2i availableContentsArea = parentArea.cropBottom(BUTTON_SIZE + INNER_PADDING);
        boolean contentsHasRoom = contents.updateBounds(availableContentsArea, guiExclusionAreas);

        ImmutableRect2i contentsArea = contents.getBackgroundArea();

        ImmutableRect2i bookmarkButtonArea = parentArea
                .matchWidthAndX(contentsArea)
                .keepBottom(BUTTON_SIZE)
                .keepLeft(BUTTON_SIZE);

        ImmutableRect2i recordConfigArea = parentArea
                .matchWidthAndX(contentsArea)
                .keepBottom(BUTTON_SIZE)
                .keepLeft(BUTTON_SIZE)
                .addOffset(BUTTON_SIZE + INNER_PADDING, 0);


        accessor.getBookmarkButton().updateBounds(bookmarkButtonArea);
        this.recordConfigButton.updateBounds(recordConfigArea);

        if (contentsHasRoom) {
            contents.updateLayout(false);
        }
        return contentsHasRoom;
    }

    @Override
    public void drawScreen(@NotNull Minecraft minecraft, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(minecraft, poseStack, mouseX, mouseY, partialTicks);
        this.recordConfigButton.draw(poseStack, mouseX, mouseY, partialTicks);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drawTooltips(@NotNull Minecraft minecraft, @NotNull PoseStack poseStack, int mouseX, int mouseY) {
        boolean renderRecipe = false;
        boolean displayRecipe = isKeyDown(displayPreview, false);
        if (displayRecipe) {
            Optional<ITypedIngredient<?>> ingredient = getIngredientUnderMouse();
            if (ingredient.isPresent() && ingredient.get().getIngredient() instanceof IRecipeInfo info) {
                RecipeLayoutLite recipeLayout;
                if (this.infoUnderMouse == info) {
                    recipeLayout = this.recipeLayout;
                } else {
                    this.infoUnderMouse = info;
                    recipeLayout = RecipeLayoutLite.create(info.getRecipeCategory(), info.getRecipe(), info.getFocusGroup(), mouseX, mouseY);
                    this.recipeLayout = recipeLayout;
                }
                if (recipeLayout != null) {
                    updatePosition(mouseX, mouseY);
                    recipeLayout.drawRecipe(poseStack, mouseX, mouseY);
                    recipeLayout.showError(poseStack, mouseX, mouseY);
                    renderRecipe = true;
                }
            }
        }
        if (!renderRecipe) {
            super.drawTooltips(minecraft, poseStack, mouseX, mouseY);
            this.recordConfigButton.drawTooltips(poseStack, mouseX, mouseY);
        }
    }

    private void updatePosition(int mouseX, int mouseY) {
        if (this.recipeLayout != null) {
            int x = this.recipeLayout.getPosX();
            int y = this.recipeLayout.getPosY();
            ImmutableRect2i area = new ImmutableRect2i(x - INGREDIENT_WIDTH, y - INGREDIENT_WIDTH, INGREDIENT_WIDTH * 2, INGREDIENT_HEIGHT * 2);
            if (!area.contains(mouseX, mouseY)) {
                this.recipeLayout.setPosition(mouseX, mouseY);
            }
        }
    }

    @Override
    public @NotNull IUserInputHandler createInputHandler() {
        final IUserInputHandler bookmarkButtonInputHandler = accessor.getBookmarkButton().createInputHandler();
        final IUserInputHandler recordConfigButtonInputHandler = this.recordConfigButton.createInputHandler();

        final IUserInputHandler displayedInputHandler = new CombinedInputHandler(
                accessor.getCheatInputHandler(),
                accessor.getContents().createInputHandler(),
                bookmarkButtonInputHandler,
                recordConfigButtonInputHandler
        );


        return new ProxyInputHandler(() -> {
            if (isListDisplayed()) {
                return displayedInputHandler;
            }
            return new CombinedInputHandler(bookmarkButtonInputHandler, recordConfigButtonInputHandler);
        });
    }

    public @Nullable RecipeLayoutLite getRecipeLayout() {
        return recipeLayout;
    }

    public IRecipeInfo getInfoUnderMouse() {
        return infoUnderMouse;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    public void setInfoUnderMouse(IRecipeInfo infoUnderMouse) {
        this.infoUnderMouse = infoUnderMouse;
    }

    public void setRecipeLayout(@Nullable RecipeLayoutLite recipeLayout) {
        this.recipeLayout = recipeLayout;
    }

}

