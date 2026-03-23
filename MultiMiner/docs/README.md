# All In MC: Multiminer

Advanced mining and tree-chopping mechanics for faster and smoother gameplay.  
Part of the **All In MC** modpack by Sqersters.

## Features

- **Vein Mining** — Mine an ore block and all connected ores of the same type break automatically. Awards XP orbs.
- **Tree Felling** — Chop the base of a tree and all logs above come down with it.
- **Configurable** — Toggle features on/off, set max block limits, and choose activation mode (sneak, keybind, or always).
- **Keybind Toggle** — Rebindable key (default: V) to toggle multi-mining on/off, as an alternative to sneaking.

## Requirements

- Minecraft 1.21.4
- NeoForge 21.4.156+
- Java 21

## Configuration

Config file is generated at `config/allinmc_multiminer-common.toml` on first run.

| Option | Default | Description |
|--------|---------|-------------|
| `veinMining.enabled` | `true` | Enable/disable vein mining |
| `veinMining.maxBlocks` | `64` | Max blocks per vein mine |
| `veinMining.activation` | `SNEAK` | Activation mode: SNEAK, KEYBIND, or ALWAYS |
| `treeFelling.enabled` | `true` | Enable/disable tree felling |
| `treeFelling.maxBlocks` | `128` | Max blocks per tree fell |
| `treeFelling.activation` | `SNEAK` | Activation mode: SNEAK, KEYBIND, or ALWAYS |

### Activation Modes

| Mode | Behavior |
|------|----------|
| `SNEAK` | Hold sneak (shift) to activate |
| `KEYBIND` | Press the toggle key (default: V) to turn on/off |
| `ALWAYS` | Always active, no key required |

## Modded Block Support

Multiminer uses custom block tags so other mods can add their blocks:

| Tag | Purpose | Default contents |
|-----|---------|-----------------|
| `allinmc_multiminer:vein_mineable` | Blocks that can be vein mined | All vanilla ore tags, `#minecraft:logs`, `#c:ores` |
| `allinmc_multiminer:tree_fellable` | Blocks that can be tree felled | `minecraft:logs` |

To add your mod's blocks, create a tag file at:
```
data/allinmc_multiminer/tags/block/vein_mineable.json
```
```json
{
  "replace": false,
  "values": ["mymod:my_custom_ore"]
}
```

## Building

```bash
cd MultiMiner
./gradlew build
```

Output JAR will be in `build/libs/`.

## Version

0.1.3
