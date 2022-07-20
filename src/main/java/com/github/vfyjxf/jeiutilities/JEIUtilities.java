package com.github.vfyjxf.jeiutilities;

import com.github.vfyjxf.jeiutilities.config.JeiUtilitiesConfig;
import com.github.vfyjxf.jeiutilities.config.KeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JeiUtilities.MODE_ID)
public class JeiUtilities {

    public static final String MODE_ID = "jeiutilities";
    public static final Logger logger = LogManager.getLogger();

    public JeiUtilities() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, JeiUtilitiesConfig.CLIENT_SPEC);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> KeyBindings::init);
    }

}
