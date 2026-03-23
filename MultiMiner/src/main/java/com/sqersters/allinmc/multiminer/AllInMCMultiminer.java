package com.sqersters.allinmc.multiminer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AllInMCMultiminer.MODID)
public class AllInMCMultiminer {
    public static final String MODID = "allinmc_multiminer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AllInMCMultiminer(IEventBus modEventBus, ModContainer modContainer) {
        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register network payloads (mod event bus)
        modEventBus.register(ModNetwork.class);

        // Register client-side keybinds (mod event bus, client only)
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.register(ModKeybinds.class);
            NeoForge.EVENT_BUS.register(ModKeybinds.class);
        }

        // Register event handlers on the game event bus
        NeoForge.EVENT_BUS.register(new VeinMiner());
        NeoForge.EVENT_BUS.register(new TreeFeller());
        NeoForge.EVENT_BUS.register(new ActivationManager());

        LOGGER.info("All In MC: Multiminer loaded!");
    }
}
