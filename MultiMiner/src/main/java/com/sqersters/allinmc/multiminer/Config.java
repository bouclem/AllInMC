package com.sqersters.allinmc.multiminer;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Vein Mining config
    public static final ModConfigSpec.BooleanValue VEIN_MINING_ENABLED;
    public static final ModConfigSpec.IntValue VEIN_MINING_MAX_BLOCKS;
    public static final ModConfigSpec.EnumValue<ActivationMode> VEIN_MINING_ACTIVATION;

    // Tree Felling config
    public static final ModConfigSpec.BooleanValue TREE_FELLING_ENABLED;
    public static final ModConfigSpec.IntValue TREE_FELLING_MAX_BLOCKS;
    public static final ModConfigSpec.EnumValue<ActivationMode> TREE_FELLING_ACTIVATION;

    static {
        BUILDER.translation("allinmc_multiminer.config.veinMining").push("veinMining");

        VEIN_MINING_ENABLED = BUILDER
                .comment("Enable or disable vein mining")
                .translation("allinmc_multiminer.config.veinMining.enabled")
                .define("enabled", true);

        VEIN_MINING_MAX_BLOCKS = BUILDER
                .comment("Maximum number of blocks to vein mine at once")
                .translation("allinmc_multiminer.config.veinMining.maxBlocks")
                .defineInRange("maxBlocks", 64, 1, 256);

        VEIN_MINING_ACTIVATION = BUILDER
                .comment("How vein mining is activated: SNEAK = hold sneak, KEYBIND = toggle key (default V), ALWAYS = always active")
                .translation("allinmc_multiminer.config.veinMining.activation")
                .defineEnum("activation", ActivationMode.SNEAK);

        BUILDER.pop();

        BUILDER.translation("allinmc_multiminer.config.treeFelling").push("treeFelling");

        TREE_FELLING_ENABLED = BUILDER
                .comment("Enable or disable tree felling")
                .translation("allinmc_multiminer.config.treeFelling.enabled")
                .define("enabled", true);

        TREE_FELLING_MAX_BLOCKS = BUILDER
                .comment("Maximum number of blocks to fell at once")
                .translation("allinmc_multiminer.config.treeFelling.maxBlocks")
                .defineInRange("maxBlocks", 128, 1, 512);

        TREE_FELLING_ACTIVATION = BUILDER
                .comment("How tree felling is activated: SNEAK = hold sneak, KEYBIND = toggle key (default V), ALWAYS = always active")
                .translation("allinmc_multiminer.config.treeFelling.activation")
                .defineEnum("activation", ActivationMode.SNEAK);

        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();
}
