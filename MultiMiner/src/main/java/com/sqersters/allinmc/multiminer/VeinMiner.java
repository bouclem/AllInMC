package com.sqersters.allinmc.multiminer;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Handles vein mining: when a player breaks an ore block,
 * all connected ore blocks of the same type are also broken.
 * Awards experience orbs for each block broken.
 */
public class VeinMiner {

    // Flag to prevent recursive event firing when we break blocks programmatically
    private static boolean isVeinMining = false;

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isVeinMining) return;
        if (!Config.VEIN_MINING_ENABLED.getAsBoolean()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ServerPlayer player = (ServerPlayer) event.getPlayer();

        // Check activation mode
        ActivationMode mode = Config.VEIN_MINING_ACTIVATION.get();
        if (!ActivationManager.shouldActivate(player, mode)) return;

        BlockPos origin = event.getPos();
        BlockState originState = level.getBlockState(origin);

        // Only activate on blocks in the vein_mineable tag
        if (!originState.is(ModTags.VEIN_MINEABLE)) return;

        // Player must be holding a tool
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        Block targetBlock = originState.getBlock();
        int maxBlocks = Config.VEIN_MINING_MAX_BLOCKS.getAsInt();

        // BFS to find all connected blocks of the same type
        Set<BlockPos> toBreak = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(origin);
        toBreak.add(origin);

        while (!queue.isEmpty() && toBreak.size() < maxBlocks) {
            BlockPos current = queue.poll();
            // Check all 26 neighbors (3x3x3 cube minus center)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos neighbor = current.offset(dx, dy, dz);
                        if (toBreak.contains(neighbor)) continue;
                        if (toBreak.size() >= maxBlocks) break;

                        BlockState neighborState = level.getBlockState(neighbor);
                        if (neighborState.is(targetBlock)) {
                            toBreak.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        // Remove the origin block since the game already handles that one
        toBreak.remove(origin);

        if (toBreak.isEmpty()) return;

        // Break all connected blocks
        isVeinMining = true;
        try {
            for (BlockPos pos : toBreak) {
                BlockState state = level.getBlockState(pos);
                if (state.isAir()) continue;

                // Drop items with proper tool context (respects fortune, silk touch)
                Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, tool);

                // Award experience orbs
                int xp = state.getExpDrop(level, pos, level.getBlockEntity(pos), player, tool);
                if (xp > 0) {
                    ExperienceOrb.award(level, Vec3.atCenterOf(pos), xp);
                }

                level.destroyBlock(pos, false); // false = don't drop again

                // Damage the tool
                tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                // Stop if tool breaks
                if (tool.isEmpty()) break;
            }
        } finally {
            isVeinMining = false;
        }
    }
}
