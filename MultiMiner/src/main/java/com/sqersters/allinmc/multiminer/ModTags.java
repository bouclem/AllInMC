package com.sqersters.allinmc.multiminer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Custom block tags for Multiminer.
 * Other mods can add blocks to these tags to enable vein mining / tree felling support.
 */
public class ModTags {
    public static final TagKey<Block> VEIN_MINEABLE = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(AllInMCMultiminer.MODID, "vein_mineable")
    );

    public static final TagKey<Block> TREE_FELLABLE = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(AllInMCMultiminer.MODID, "tree_fellable")
    );
}
