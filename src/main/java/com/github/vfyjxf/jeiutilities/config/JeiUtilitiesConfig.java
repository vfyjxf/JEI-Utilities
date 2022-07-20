package com.github.vfyjxf.jeiutilities.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JeiUtilitiesConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT_CONFIG;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT_CONFIG = specPair.getLeft();
    }

    public final static class ClientConfig {

        private final ForgeConfigSpec.BooleanValue enableHistory;
        private final ForgeConfigSpec.BooleanValue matchesTags;
        private final ForgeConfigSpec.EnumValue<SplittingMode> splittingMode;
        private final ForgeConfigSpec.IntValue usedRows;
        private final ForgeConfigSpec.IntValue backgroundColour;

        private final ForgeConfigSpec.BooleanValue recordRecipes;
        private final ForgeConfigSpec.BooleanValue keepOutputCount;
        private final ForgeConfigSpec.BooleanValue showRecipeBookmarkReminders;
        private final ForgeConfigSpec.EnumValue<RecordMode> recordMode;

        private final ForgeConfigSpec.BooleanValue adaptiveRecipePreview;
        private final ForgeConfigSpec.DoubleValue recipePreviewScaling;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("history-settings");
            {
                enableHistory = builder.comment("Enable browsing history function").define("EnableHistory", true);
                matchesTags = builder.comment("Add item with different nbt to the browsing history").define("MatchesNBTs", true);
                splittingMode = builder.comment("Splitting mode for the view history.\n" + "Mode : BACKGROUND, DOTTED_LINE").defineEnum("SplittingMode", SplittingMode.DOTTED_LINE);
                usedRows = builder.comment("Number of rows used for the view history").defineInRange("UsedRows", 2, 1, 5);
                backgroundColour = builder.comment("Color of the history area display").defineInRange("BackgroundColour", 0xee555555, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
            builder.pop();

            builder.push("bookmark-settings");
            {
                recordRecipes = builder.comment("Record current recipe when add ingredient to bookmark in recipe screen").define("RecordRecipes", true);
                keepOutputCount = builder.comment("Keep output count when add recipe to bookmark").define("KeepOutputCount", false);
                showRecipeBookmarkReminders = builder.comment("Display a letter \"R\" in the upper left corner of the recipe bookmark.").define("ShowRecipeBookmarkReminders", true);
                recordMode = builder.comment("""
                                Current mode of recording recipes.
                                Enable: The opposite of RESTRICTED mode
                                Disable: Don't record any recipes
                                RESTRICTED: You need to hold down Shift to view the recorded recipe or record recipe.""")
                        .defineEnum("RecordMode", RecordMode.ENABLE);
            }
            builder.pop();

            builder.push("render-settings");
            {
                adaptiveRecipePreview = builder.comment("If true, then the recipe preview will automatically select the appropriate scaling based on the screen size.").define("AdaptiveRecipePreview", true);
                recipePreviewScaling = builder.comment("The scaling of the recipe preview.It is only used when adaptiveRecipePreview is false.").defineInRange("RecipePreviewScaling", 0.8F, 0.0F, 5.0F);
            }
            builder.pop();
        }

    }

    public static boolean getEnableHistory() {
        return CLIENT_CONFIG.enableHistory.get();
    }

    public static boolean getMatchesTags() {
        return CLIENT_CONFIG.matchesTags.get();
    }

    public static SplittingMode getSplittingMode() {
        return CLIENT_CONFIG.splittingMode.get();
    }

    public static int getUsedRows() {
        return CLIENT_CONFIG.usedRows.get();
    }

    public static int getBackgroundColour() {
        return CLIENT_CONFIG.backgroundColour.get();
    }

    public static boolean getRecordRecipes() {
        return CLIENT_CONFIG.recordRecipes.get();
    }

    public static boolean getKeepOutputCount() {
        return CLIENT_CONFIG.keepOutputCount.get();
    }

    public static boolean getShowRecipeBookmarkReminders() {
        return CLIENT_CONFIG.showRecipeBookmarkReminders.get();
    }

    public static RecordMode getRecordMode() {
        return CLIENT_CONFIG.recordMode.get();
    }

    public static boolean isEnableMode(){
        return getRecordMode() == RecordMode.ENABLE;
    }

    public static boolean isDisableMode(){
        return getRecordMode() == RecordMode.DISABLE;
    }

    public static boolean isRestrictedMode(){
        return getRecordMode() == RecordMode.RESTRICTED;
    }

    public static boolean getAdaptiveRecipePreview() {
        return CLIENT_CONFIG.adaptiveRecipePreview.get();
    }

    public static double getRecipePreviewScaling() {
        return CLIENT_CONFIG.recipePreviewScaling.get();
    }

    public static void setRecordMode(RecordMode mode) {
        CLIENT_CONFIG.recordMode.set(mode);
    }

}
