# All In MC: Configs

In-game configuration menu for all All In MC mods.  
Browse, edit, and save config options without leaving the game.

## Features

- Press **K** (default keybind) to open the config menu in-game
- Lists all loaded All In MC mods automatically with descriptions
- Select a mod to view and edit its config values
- Uses NeoForge's built-in config editor for reliable editing and saving
- Config presets: save, load, and delete named config profiles per mod
- Search/filter bar to quickly find mods when many are installed
- Also accessible from the Mods menu via the Config button

## Requirements

- Minecraft 1.21.4
- NeoForge 21.4.156+
- Java 21
- At least one other All In MC mod installed (e.g. Multiminer)

## Usage

1. In-game, press `K` to open the All In MC config menu
2. Select a mod from the list
3. Edit any config values
4. Changes are saved automatically by NeoForge's config system

## Building

```bash
cd Configs
./gradlew build
```

Output JAR will be in `build/libs/`.

## Version

0.1.2
