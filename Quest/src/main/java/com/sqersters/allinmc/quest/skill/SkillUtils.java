package com.sqersters.allinmc.quest.skill;

/**
 * Utility class for skill XP and level calculations.
 */
public final class SkillUtils {

    public static final int MAX_LEVEL = 50;

    private SkillUtils() {}

    /**
     * Returns the total XP required to complete the given level and advance to the next.
     * Formula: 100 * level + 2 * level^2 (scales harder at higher levels).
     */
    public static int xpRequiredForLevel(int level) {
        return 100 * level + 2 * level * level;
    }

    /**
     * Returns the XP amount granted for a single action of the given skill type.
     */
    public static int xpPerAction(SkillType type) {
        return switch (type) {
            case COMBAT -> 5;
            case MINING -> 3;
            case WOODCUTTING -> 4;
            case FARMING -> 5;
            case HEALING -> 10;
        };
    }
}
