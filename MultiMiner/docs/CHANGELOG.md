# Changelog — All In MC: Multiminer

## v0.1.3 — 2026-03-23

- Added experience orb drops for vein-mined blocks (uses NeoForge `getExpDrop` API)
- Added configurable activation mode per feature: SNEAK, KEYBIND, or ALWAYS
  - Replaces the old `requireSneak` boolean config option
  - KEYBIND mode uses a rebindable toggle key (default: V), synced client→server via custom payload
- Added `#minecraft:logs` and `#c:ores` to the `vein_mineable` tag so vein mining works on logs and common ore tags
- New files: ActivationMode, ActivationManager, ActivationPayload, ModNetwork, ModKeybinds
- New lang keys for activation mode config and keybind

## v0.1.2 — 2026-03-22

- Added custom block tags for modded ore and wood support
  - `allinmc_multiminer:vein_mineable` — includes all vanilla ore tags; other mods can add blocks to this tag
  - `allinmc_multiminer:tree_fellable` — includes `minecraft:logs`; other mods can add blocks to this tag
- Replaced hardcoded ore checks in VeinMiner with `vein_mineable` tag lookup
- Replaced `minecraft:logs` check in TreeFeller with `tree_fellable` tag lookup
- Added ModTags.java for centralized tag key definitions

## v0.1.1 — 2026-03-22

- Added lang file (en_us.json) with translations for all config entries
- Refactored Config.java to use push/pop sections with .translation() keys
- Config screen now shows readable labels instead of raw key paths

## v0.1.0 — 2026-03-22

Initial release.

- Vein mining: breaks connected ore blocks of the same type (BFS, 26-neighbor)
- Tree felling: breaks connected log blocks upward from the chopped block
- Config: enable/disable each feature, max blocks, sneak requirement
- Respects tool durability, fortune, and silk touch
- NeoForge 21.4.156 / Minecraft 1.21.4
