package com.github.vfyjxf.jeiutilities;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = JEIUtilities.MODID,
        name = JEIUtilities.NAME,
        version = JEIUtilities.VERSION,
        dependencies = JEIUtilities.DEPENDENCIES,
        guiFactory = JEIUtilities.GUI_FACTORY,
        clientSideOnly = true
)
public class JEIUtilities {
    public static final String MODID = "jeiutilities";
    public static final String NAME = "JEI Utilities";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:jei";
    public static final String GUI_FACTORY = "com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfigGuiFactory";

    public static final Logger logger = LogManager.getLogger(JEIUtilities.NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JeiUtilitiesConfig.preInit(event);
    }
}
