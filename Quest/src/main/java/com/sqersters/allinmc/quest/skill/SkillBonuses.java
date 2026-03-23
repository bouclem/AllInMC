package com.sqersters.allinmc.quest.skill;

/**
 * Calculates gradual passive bonuses based on skill levels.
 * Each skill grants a small bonus per level.
 */
public final class SkillBonuses {

    private SkillBonuses() {}

    /** Bonus rate per level (2% per level for most skills). */
    private static final float RATE_PER_LEVEL = 0.02f;

    /** Farming uses 1% per level for extra drop chance. */
    private static final float FARMING_RATE_PER_LEVEL = 0.01f;

    /**
     * Returns the damage multiplier for the given combat level.
     * Level 1 = 1.0 (no bonus), Level 50 = 1.98 (+98%).
     */
    public static float getDamageMultiplier(int level) {
        return 1.0f + RATE_PER_LEVEL * (level - 1);
    }

    /**
     * Returns the mining speed multiplier for the given mining level.
     */
    public static float getMiningSpeedMultiplier(int level) {
        return 1.0f + RATE_PER_LEVEL * (level - 1);
    }

    /**
     * Returns the woodcutting speed multiplier for the given woodcutting level.
     */
    public static float getWoodcuttingSpeedMultiplier(int level) {
        return 1.0f + RATE_PER_LEVEL * (level - 1);
    }

    /**
     * Returns the chance (0.0 to 1.0) for an extra crop drop at the given farming level.
     * Level 1 = 0% chance, Level 50 = 49% chance.
     */
    public static float getExtraCropDropChance(int level) {
        return FARMING_RATE_PER_LEVEL * (level - 1);
    }

    /**
     * Returns the healing multiplier for the given healing level.
     */
    public static float getHealingMultiplier(int level) {
        return 1.0f + RATE_PER_LEVEL * (level - 1);
    }

    /**
     * Returns the bonus percentage for display purposes (e.g., 49 for 49%).
     */
    public static int getBonusPercent(SkillType type, int level) {
        return switch (type) {
            case COMBAT, MINING, WOODCUTTING, HEALING -> Math.round(RATE_PER_LEVEL * (level - 1) * 100);
            case FARMING -> Math.round(FARMING_RATE_PER_LEVEL * (level - 1) * 100);
        };
    }

    /**
     * Returns the bonus description key suffix for the given skill type.
     */
    public static String getBonusDescription(SkillType type) {
        return "skill.allinmc_quest.bonus." + type.getSerializedName();
    }
}
