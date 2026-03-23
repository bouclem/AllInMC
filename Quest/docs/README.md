# All In MC: Quest

Introduces quests, skills, and progression systems to the AllInMC ecosystem.

## Requirements

- Minecraft 1.21.4
- NeoForge 21.4.156+
- All In MC: Configs 0.1.0+

## Skills

Five skill types that level up through gameplay actions:

| Skill       | XP Trigger                  | XP per Action |
|-------------|-----------------------------|---------------|
| Combat      | Hitting hostile mobs        | 5             |
| Mining      | Breaking stone/ore blocks   | 3             |
| Woodcutting | Breaking log blocks         | 4             |
| Farming     | Harvesting mature crops     | 5             |
| Healing     | Healing                     | 10            |

- Max level: 50
- XP per level: 100 × level + 2 × level² (scales harder at higher levels)
- Skill XP is separate from vanilla Minecraft XP
- Skills persist through death

## Skill Bonuses

Each skill grants a gradual passive bonus that increases with every level:

| Skill       | Bonus                    | Rate per Level |
|-------------|--------------------------|----------------|
| Combat      | +% damage to hostiles    | +2% per level  |
| Mining      | +% mining speed          | +2% per level  |
| Woodcutting | +% chopping speed        | +2% per level  |
| Farming     | % chance extra crop drops| +1% per level  |
| Healing     | +% heal amount           | +2% per level  |

Bonuses start at level 1 (0%) and scale up to level 50 (98% for most, 49% for farming).

## Controls

- Press **J** to open the Skills menu (configurable in Controls settings)

## Installation

1. Copy `gradle/wrapper/`, `gradlew`, and `gradlew.bat` from an existing AllInMC mod (e.g., MultiMiner)
2. Run `./gradlew build`
3. Place the built jar in your mods folder alongside All In MC: Configs

## Version

0.1.1
