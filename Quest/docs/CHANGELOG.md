# All In MC: Quest — Changelog

## v0.1.1 — 2026-03-23
- Added gradual passive bonuses per skill level
  - Combat: +2% damage to hostile mobs per level
  - Mining: +2% mining speed on stone/ore per level
  - Woodcutting: +2% chopping speed on logs per level
  - Farming: +1% chance for extra crop drops per level
  - Healing: +2% heal amount per level
- Added SkillBonuses utility class for bonus calculations
- Bonus percentages now displayed in the Skills menu screen
- Updated XP curve for difficulty scaling: 100×level + 2×level² (was 100×level)
- Added bonus translation keys to lang file

## v0.1.0 — 2026-03-22
- Initial release
- Added skill system with 5 skills: Combat, Mining, Woodcutting, Farming, Healing
- XP awarded per gameplay action (separate from vanilla XP)
- Leveling system: max level 50, XP curve = 100 × level
- Skills menu screen (default keybind: J)
- Player skill data persists through death
- Client-server data sync via NeoForge networking
- Depends on All In MC: Configs
