package com.github.vfyjxf.jeiutilities.config;

import com.github.vfyjxf.jeiutilities.JEIUtilities;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@Mod.EventBusSubscriber(modid = JEIUtilities.MODID)
public class JeiUtilitiesConfig {

    private static final String CATEGORY_HISTORY = "history";
    private static final String CATEGORY_BOOKMARK = "bookmark";

    private static Configuration config;
    private static File modConfigFile;

    private static boolean enableHistory = true;
    private static boolean matchesNBTs = true;
    private static SplittingMode splittingMode = SplittingMode.DOTTED_LINE;
    private static int backgroundColour = 0xee555555;

    private static boolean recordRecipes = true;
    private static boolean showRecipeBookmarkReminders = true;
    private static RecordMode recordMode = RecordMode.ENABLE;


    public static void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), JEIUtilities.MODID);

        modConfigFile = new File(configDir, JEIUtilities.MODID + ".cfg");
        config = new Configuration(modConfigFile);

        loadConfig();

    }

    private static void loadConfig() {

        if (config == null) {
            return;
        }

        config.load();

        {
            enableHistory = config.getBoolean("enableHistory",
                    CATEGORY_HISTORY,
                    enableHistory,
                    "Enable browsing history function"
            );
            matchesNBTs = config.getBoolean("matchesNBTs",
                    CATEGORY_HISTORY,
                    matchesNBTs,
                    "Add item with different nbt to the browsing history"
            );
            try {
                splittingMode = SplittingMode.valueOf(
                        config.getString("splittingMode",
                                CATEGORY_HISTORY,
                                SplittingMode.DOTTED_LINE.name(),
                                "Splitting mode for the browsing history.\n" +
                                        "Mode : BACKGROUND, DOTTED_LINE"
                        )
                );
            } catch (IllegalArgumentException | NullPointerException e) {
                //set default mode
                splittingMode = SplittingMode.DOTTED_LINE;
            }

            backgroundColour = config.getInt("backgroundColour",
                    CATEGORY_HISTORY,
                    backgroundColour,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE,
                    "Color of the history area display"
            );
        }
        {
            recordRecipes = config.getBoolean("recordRecipes",
                    CATEGORY_BOOKMARK,
                    recordRecipes,
                    "Record current recipe when add ingredient to bookmark in recipe screen"
            );
            showRecipeBookmarkReminders = config.getBoolean("showRecipeBookmarkReminders",
                    CATEGORY_BOOKMARK,
                    showRecipeBookmarkReminders,
                    "Display a letter \"R\" in the upper left corner of the recipe bookmark."
            );
            recordMode = RecordMode.valueOf(config.getString(
                    "recordMode", CATEGORY_BOOKMARK, recordMode.name(),
                    "Current mode of recording recipes." + "\n"
                            + "Enable: The opposite of RESTRICTED mode" + "\n"
                            + "Disable: Don't record any recipes" + "\n"
                            + "RESTRICTED: You need to hold down Shift to view the recorded recipe or record recipe."));
        }


        if (config.hasChanged()) {
            config.save();
        }
    }

    public static Configuration getConfig() {
        return config;
    }

    public static File getModConfigFile() {
        return modConfigFile;
    }

    public static boolean isEnableHistory() {
        return enableHistory;
    }

    public static boolean isMatchesNBTs() {
        return matchesNBTs;
    }

    public static boolean getRecordRecipes() {
        return recordRecipes;
    }

    public static RecordMode getRecordMode() {
        return recordMode;
    }

    public static int getBackgroundColour() {
        return backgroundColour;
    }

    public static void setRecordMode(RecordMode mode) {
        JeiUtilitiesConfig.recordMode = mode;
        config.get(CATEGORY_BOOKMARK, "recordMode", recordMode.name(), "Current mode of recording recipes").set(mode.name());
        config.save();
    }

    public static boolean isShowRecipeBookmarkReminders() {
        return showRecipeBookmarkReminders;
    }

    public static SplittingMode getSplittingMode() {
        return splittingMode;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(JEIUtilities.MODID)) {
            if (config.hasChanged()) {
                config.save();
            }
            loadConfig();
        }
    }

}
