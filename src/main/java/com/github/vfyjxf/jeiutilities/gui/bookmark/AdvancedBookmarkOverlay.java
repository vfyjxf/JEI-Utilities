package com.github.vfyjxf.jeiutilities.gui.bookmark;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.mixin.accessor.BookmarkOverlayAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
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

import java.util.Set;


@SuppressWarnings("unused")
public class AdvancedBookmarkOverlay extends BookmarkOverlay {

    private static final int INNER_PADDING = 2;
    private static final int BUTTON_SIZE = 20;

    private final GuiIconToggleButton recordConfigButton;

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
    }

    @Override
    public boolean updateBounds(@NotNull Set<ImmutableRect2i> guiExclusionAreas) {
        BookmarkOverlayAccessor accessor = (BookmarkOverlayAccessor) this;
        ImmutableRect2i parentArea = accessor.getParentArea();
        ImmutableRect2i availableContentsArea = parentArea.cropBottom(BUTTON_SIZE + INNER_PADDING);
        boolean contentsHasRoom = accessor.getContents().updateBounds(availableContentsArea, guiExclusionAreas);

        ImmutableRect2i contentsArea = accessor.getContents().getBackgroundArea();

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
            accessor.getContents().updateLayout(false);
        }
        return contentsHasRoom;
    }

    @Override
    public void drawScreen(@NotNull Minecraft minecraft, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(minecraft, poseStack, mouseX, mouseY, partialTicks);
        this.recordConfigButton.draw(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawTooltips(@NotNull Minecraft minecraft, @NotNull PoseStack poseStack, int mouseX, int mouseY) {
        super.drawTooltips(minecraft, poseStack, mouseX, mouseY);
        this.recordConfigButton.drawTooltips(poseStack, mouseX, mouseY);
    }

    @Override
    public @NotNull IUserInputHandler createInputHandler() {
        BookmarkOverlayAccessor accessor = (BookmarkOverlayAccessor) this;
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

}

