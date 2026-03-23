package com.sqersters.allinmc.quest.skill;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores all skill levels and XP for a single player.
 * Persisted via NeoForge data attachments using a Codec.
 */
public class PlayerSkillData {

    /**
     * Codec for a single skill entry (level + xp pair).
     */
    private static final Codec<int[]> SKILL_ENTRY_CODEC = Codec.INT_STREAM
            .xmap(
                    stream -> stream.toArray(),
                    arr -> java.util.Arrays.stream(arr)
            );

    /**
     * Codec for the full PlayerSkillData.
     * Serializes as a map of skill name -> [level, xp].
     */
    public static final Codec<PlayerSkillData> CODEC = Codec.unboundedMap(Codec.STRING, SKILL_ENTRY_CODEC)
            .xmap(
                    map -> {
                        PlayerSkillData data = new PlayerSkillData();
                        map.forEach((name, arr) -> {
                            try {
                                SkillType type = SkillType.fromName(name);
                                if (arr.length >= 2) {
                                    data.levels.put(type, arr[0]);
                                    data.xp.put(type, arr[1]);
                                }
                            } catch (IllegalArgumentException ignored) {
                                // Skip unknown skill types (forward compat)
                            }
                        });
                        return data;
                    },
                    data -> {
                        Map<String, int[]> map = new HashMap<>();
                        for (SkillType type : SkillType.values()) {
                            map.put(type.getSerializedName(), new int[]{
                                    data.getLevel(type),
                                    data.getXp(type)
                            });
                        }
                        return map;
                    }
            );

    private final EnumMap<SkillType, Integer> levels = new EnumMap<>(SkillType.class);
    private final EnumMap<SkillType, Integer> xp = new EnumMap<>(SkillType.class);

    public PlayerSkillData() {
        for (SkillType type : SkillType.values()) {
            levels.put(type, 1);
            xp.put(type, 0);
        }
    }

    public int getLevel(SkillType type) {
        return levels.getOrDefault(type, 1);
    }

    public int getXp(SkillType type) {
        return xp.getOrDefault(type, 0);
    }

    /**
     * Adds XP to the given skill and handles level-ups.
     * Returns true if a level-up occurred.
     */
    public boolean addXp(SkillType type, int amount) {
        int currentLevel = getLevel(type);
        if (currentLevel >= SkillUtils.MAX_LEVEL) return false;

        int currentXp = getXp(type) + amount;
        boolean leveledUp = false;

        while (currentLevel < SkillUtils.MAX_LEVEL && currentXp >= SkillUtils.xpRequiredForLevel(currentLevel)) {
            currentXp -= SkillUtils.xpRequiredForLevel(currentLevel);
            currentLevel++;
            leveledUp = true;
        }

        // Clamp XP at max level
        if (currentLevel >= SkillUtils.MAX_LEVEL) {
            currentXp = 0;
        }

        levels.put(type, currentLevel);
        xp.put(type, currentXp);
        return leveledUp;
    }

    /**
     * Copies all data from another PlayerSkillData instance.
     */
    public void copyFrom(PlayerSkillData other) {
        for (SkillType type : SkillType.values()) {
            levels.put(type, other.getLevel(type));
            xp.put(type, other.getXp(type));
        }
    }

    /**
     * Writes this data to a CompoundTag for network sync.
     */
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        for (SkillType type : SkillType.values()) {
            CompoundTag skillTag = new CompoundTag();
            skillTag.putInt("level", getLevel(type));
            skillTag.putInt("xp", getXp(type));
            tag.put(type.getSerializedName(), skillTag);
        }
        return tag;
    }

    /**
     * Reads data from a CompoundTag received over the network.
     */
    public static PlayerSkillData fromTag(CompoundTag tag) {
        PlayerSkillData data = new PlayerSkillData();
        for (SkillType type : SkillType.values()) {
            String key = type.getSerializedName();
            if (tag.contains(key)) {
                CompoundTag skillTag = tag.getCompound(key);
                data.levels.put(type, skillTag.getInt("level"));
                data.xp.put(type, skillTag.getInt("xp"));
            }
        }
        return data;
    }
}
