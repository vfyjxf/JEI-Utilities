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

    private static Configuration config;
    private static File modConfigFile;

    private static boolean enableHistory = true;
    private static int useRows = 2;
    private static boolean matchesNBTs = true;
    private static SplittingMode splittingMode = SplittingMode.DOTTED_LINE;
    private static int backgroundColour = 0xee555555;


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
                    "Enable browsing history feature"
            );

            useRows = config.getInt("useRows",
                    CATEGORY_HISTORY,
                    useRows,
                    1,
                    6,
                    "Number of rows to use for history"
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

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static Configuration getConfig() {
        return config;
    }

    public static boolean isEnableHistory() {
        return enableHistory;
    }

    public static int getUseRows() {
        return useRows;
    }

    public static boolean isMatchesNBTs() {
        return matchesNBTs;
    }

    public static int getBackgroundColour() {
        return backgroundColour;
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
