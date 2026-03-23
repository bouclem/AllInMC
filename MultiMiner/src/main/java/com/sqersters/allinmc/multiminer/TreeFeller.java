package com.sqersters.allinmc.multiminer;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Handles tree felling: when a player breaks a log block,
 * all connected log blocks above are also broken.
 */
public class TreeFeller {

    private static boolean isFelling = false;

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isFelling) return;
        if (!Config.TREE_FELLING_ENABLED.getAsBoolean()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        ServerPlayer player = (ServerPlayer) event.getPlayer();

        // Check activation mode
        ActivationMode mode = Config.TREE_FELLING_ACTIVATION.get();
        if (!ActivationManager.shouldActivate(player, mode)) return;

        BlockPos origin = event.getPos();
        BlockState originState = level.getBlockState(origin);

        // Only activate on log blocks (uses custom tag for modded wood support)
        if (!originState.is(ModTags.TREE_FELLABLE)) return;

        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        int maxBlocks = Config.TREE_FELLING_MAX_BLOCKS.getAsInt();

        // BFS to find all connected log blocks (upward and sideways, not below origin)
        Set<BlockPos> toBreak = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(origin);
        toBreak.add(origin);

        while (!queue.isEmpty() && toBreak.size() < maxBlocks) {
            BlockPos current = queue.poll();
            // Check neighbors: up, and horizontal + diagonal at same/higher level
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) { // Only same level and above
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos neighbor = current.offset(dx, dy, dz);
                        if (toBreak.contains(neighbor)) continue;
                        if (toBreak.size() >= maxBlocks) break;
                        // Don't go below the origin
                        if (neighbor.getY() < origin.getY()) continue;

                        BlockState neighborState = level.getBlockState(neighbor);
                        if (neighborState.is(ModTags.TREE_FELLABLE)) {
                            toBreak.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        // Remove the origin since the game handles that break
        toBreak.remove(origin);

        if (toBreak.isEmpty()) return;

        // Break all connected logs
        isFelling = true;
        try {
            for (BlockPos pos : toBreak) {
                BlockState state = level.getBlockState(pos);
                if (state.isAir()) continue;

                Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, tool);
                level.destroyBlock(pos, false);

                tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                if (tool.isEmpty()) break;
            }
        } finally {
            isFelling = false;
        }
    }
}
