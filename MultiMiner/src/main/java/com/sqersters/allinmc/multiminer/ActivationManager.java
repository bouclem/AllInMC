package com.sqersters.allinmc.multiminer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Tracks which players have the multi-mining keybind toggled on.
 * State is synced from client via network packet.
 */
public class ActivationManager {

    private static final Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();

    public static void setActive(UUID playerId, boolean active) {
        if (active) {
            activePlayers.add(playerId);
        } else {
            activePlayers.remove(playerId);
        }
    }

    public static boolean isActive(UUID playerId) {
        return activePlayers.contains(playerId);
    }

    /**
     * Checks whether the given player should activate multi-mining,
     * based on the configured activation mode.
     */
    public static boolean shouldActivate(ServerPlayer player, ActivationMode mode) {
        return switch (mode) {
            case SNEAK -> player.isShiftKeyDown();
            case KEYBIND -> isActive(player.getUUID());
            case ALWAYS -> true;
        };
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        activePlayers.remove(event.getEntity().getUUID());
    }
}
