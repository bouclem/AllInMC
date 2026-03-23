package com.sqersters.allinmc.quest.skill;

import com.sqersters.allinmc.quest.AllInMCQuest;
import com.sqersters.allinmc.quest.network.SkillDataPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Listens for game events, awards skill XP, and applies skill bonuses.
 */
public class SkillEvents {

    // ── XP Awards ──

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockState state = event.getState();

        // Mining: stone and ore blocks
        if (isMineable(state)) {
            awardXp(player, SkillType.MINING);
        }

        // Woodcutting: log blocks
        if (state.is(BlockTags.LOGS)) {
            awardXp(player, SkillType.WOODCUTTING);
        }

        // Farming: mature crop blocks + extra drop bonus
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            awardXp(player, SkillType.FARMING);
            tryExtraCropDrop(player, state, event);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        // Award combat XP when a player damages a hostile mob
        if (event.getSource().getEntity() instanceof ServerPlayer player
                && event.getEntity() instanceof Monster) {
            awardXp(player, SkillType.COMBAT);
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        // Award healing XP when a player heals
        if (event.getEntity() instanceof ServerPlayer player && event.getAmount() > 0) {
            awardXp(player, SkillType.HEALING);

            // Apply healing bonus
            PlayerSkillData data = player.getData(SkillDataAttachment.PLAYER_SKILLS);
            float multiplier = SkillBonuses.getHealingMultiplier(data.getLevel(SkillType.HEALING));
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    // ── Bonus Application ──

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerSkillData data = player.getData(SkillDataAttachment.PLAYER_SKILLS);
        BlockState state = event.getState();

        // Mining speed bonus for stone/ore
        if (isMineable(state)) {
            float multiplier = SkillBonuses.getMiningSpeedMultiplier(data.getLevel(SkillType.MINING));
            event.setNewSpeed(event.getNewSpeed() * multiplier);
        }

        // Woodcutting speed bonus for logs
        if (state.is(BlockTags.LOGS)) {
            float multiplier = SkillBonuses.getWoodcuttingSpeedMultiplier(data.getLevel(SkillType.WOODCUTTING));
            event.setNewSpeed(event.getNewSpeed() * multiplier);
        }
    }

    @SubscribeEvent
    public void onIncomingDamage(LivingIncomingDamageEvent event) {
        // Apply combat damage bonus when a player attacks a hostile mob
        if (event.getSource().getEntity() instanceof ServerPlayer player
                && event.getEntity() instanceof Monster) {
            PlayerSkillData data = player.getData(SkillDataAttachment.PLAYER_SKILLS);
            float multiplier = SkillBonuses.getDamageMultiplier(data.getLevel(SkillType.COMBAT));
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    // ── Sync Events ──

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToClient(player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToClient(player);
        }
    }

    // ── Helpers ──

    private void awardXp(ServerPlayer player, SkillType type) {
        PlayerSkillData data = player.getData(SkillDataAttachment.PLAYER_SKILLS);
        int amount = SkillUtils.xpPerAction(type);
        boolean leveledUp = data.addXp(type, amount);

        player.setData(SkillDataAttachment.PLAYER_SKILLS, data);
        syncToClient(player);

        if (leveledUp) {
            AllInMCQuest.LOGGER.debug("Player {} leveled up {} to {}",
                    player.getName().getString(), type.getSerializedName(), data.getLevel(type));
        }
    }

    private void syncToClient(ServerPlayer player) {
        PlayerSkillData data = player.getData(SkillDataAttachment.PLAYER_SKILLS);
        PacketDistributor.sendToPlayer(player, new SkillDataPayload(data.toTag()));
    }

    /**
     * Rolls the farming bonus chance and spawns extra crop drops if successful.
     */
    private void tryExtraCropDrop(ServerPlayer player, BlockState state, BlockEvent.BreakEvent event) {
        PlayerSkillData data = player.getData(SkillDataAttachment.PLAYER_SKILLS);
        float chance = SkillBonuses.getExtraCropDropChance(data.getLevel(SkillType.FARMING));

        if (chance <= 0 || player.level().random.nextFloat() >= chance) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        // Get the block's normal drops and spawn an extra copy of each
        List<ItemStack> drops = Block.getDrops(
                state, serverLevel, event.getPos(), null, player, player.getMainHandItem()
        );
        for (ItemStack drop : drops) {
            ItemEntity itemEntity = new ItemEntity(
                    serverLevel,
                    event.getPos().getX() + 0.5,
                    event.getPos().getY() + 0.5,
                    event.getPos().getZ() + 0.5,
                    drop.copy()
            );
            itemEntity.setDefaultPickUpDelay();
            serverLevel.addFreshEntity(itemEntity);
        }
    }

    private boolean isMineable(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.BASE_STONE_NETHER)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.COPPER_ORES);
    }
}
