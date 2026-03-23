# Changelog — All In MC: Configs

## v0.1.2 — 2026-03-23

- Added search/filter box to the mod list screen (filters by mod name or mod ID)
- Shows "No mods match your search." when filter yields no results
- Scroll resets to top when search text changes
- Added `gui.allinmc_configs.search` lang key

## v0.1.1 — 2026-03-22

- Added mod descriptions below each entry in the mod list screen (pulled from neoforge.mods.toml)
- Added config presets system: save, load, and delete named config profiles per mod
  - Presets stored in `config/allinmc_configs_presets/<modId>/`
  - New PresetsScreen accessible via "Presets" button next to each mod
- Increased entry height in mod list to accommodate description text
- Added ConfigPresets utility class for preset file operations
- Added PresetsScreen for preset management UI

## v0.1.0 — 2026-03-22

Initial release.

- In-game config menu opened via K keybind
- Auto-detects all loaded All In MC mods (allinmc_ prefix)
- Opens NeoForge's built-in ConfigurationScreen for editing/saving
- Scrollable mod list with support for many mods
- Accessible from Mods menu via IConfigScreenFactory
- Lang file with keybind and category translations
- NeoForge 21.4.156 / Minecraft 1.21.4
