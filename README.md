# MineHelper

A client-side Fabric mod for Minecraft 26.1.2 with quality-of-life features: **Block Finder** (scans and highlights target blocks through walls), **Player ESP** (highlights players through walls), **Auto Sprint** (automatic sprinting), and **Auto Clicker** (humanized clicking automation).

> **README на русском:** [README_RU.md](README_RU.md)

**[⬇ Download the latest release](https://github.com/markolockwood/MineHelper/releases/latest)**

## Warning

Some features of this mod may be prohibited on certain servers. Either avoid using them or don't advertise their use.

## Features

### Block Finder

A powerful tool for finding structures when you know which block is prevalent in them!

- Scans chunks around the player using the chunk palette — skips sections that don't contain the target block entirely, making rare-block searches (ores, clay, ancient debris) orders of magnitude faster than naive per-block iteration
- Highlights found blocks with configurable-color bounding boxes **visible through walls**, using a custom `ALWAYS_PASS` depth-test pipeline
- Scanning radiates outward from the player, so nearby blocks are highlighted first
- Caches scanned chunks; only new areas are scanned as you move
- Client-side only — no server modifications required; works in singleplayer and on any server

### Player ESP

Player highlighting system with multiple render modes and team color support:

- **Two render modes:**
  - **AABB** — lightweight box outline around player hitbox (12 lines, ~5-10x faster than Glow)
  - **Glow** — full vanilla entity outline with blur effect, shows actual player model and animations through walls
- **Two color modes:**
  - **Solid** — single customizable color for all players
  - **Team** — uses player's team color from scoreboard (e.g., red vs blue teams on servers like Hypixel)
- Configurable radius (16-256 blocks)
- Both modes render through walls with no depth test

### Auto Sprint

Automatic sprinting when moving forward — a standard accessibility feature:

- Automatically enables sprint when pressing forward
- Checks conditions: not in water (unless Dolphin's Grace), sufficient hunger, not using items, not sneaking
- More reliable than vanilla toggle sprint (doesn't reset after death/teleports)
- **100% legal** on all servers — this is vanilla functionality made more consistent
- Enabled by default, persists between game sessions

### Auto Clicker

Humanized auto-clicking for PvP and mining:

- Clicks automatically while left mouse button is held down
- **Humanized timing:** configurable CPS (1-20, default 10) with ±30% randomization
- Uses vanilla attack logic with cooldown bypass for consistent performance
- Works in both singleplayer and multiplayer
- Disabled by default
- **Note:** Use responsibly — aggressive CPS settings may trigger anti-cheat on some servers

## Installation

1. Install [Fabric Loader 0.19.3+](https://fabricmc.net/use/)
2. Install [Fabric API 0.155.2+26.1.2](https://modrinth.com/mod/fabric-api)
3. Download `minehelper-1.0.0.jar` from the [latest release](https://github.com/markolockwood/MineHelper/releases/latest) and place it in `.minecraft/mods/`

## GUI

Access via `/minehelper` command. Three tabs:
- **General** — language selection, Auto Sprint toggle, Auto Clicker toggle with CPS control
- **Block Finder** — toggle, target block, radius, color, line width
- **Player ESP** — toggle, render mode (AABB/Glow), color mode (Solid/Team), color picker, radius

## Commands

All commands are client-side only.

### General

| Command | Description |
|---|---|
| `/minehelper` | Open GUI with all settings |
| `/minehelper lang <en\|ru>` | Switch language |

### Block Finder

| Command | Description |
|---|---|
| `/blockfinder set <block_id>` | Set target block. Accepts both `stone` and `minecraft:stone`. Tab completion works for all blocks. |
| `/blockfinder check` | Display the name of the block you're looking at |
| `/blockfinder toggle` | Enable / disable scanning |
| `/blockfinder clear` | Clear cache and rescan everything |
| `/blockfinder radius <16–128>` | Set scan radius in blocks (default: 64) |
| `/blockfinder color <r> <g> <b>` | Set highlight color (0–255 per channel) |
| `/blockfinder status` | Show current state: enabled, target block, blocks found, radius |

**Keybind:** Assign a key in `Controls → Misc → Toggle Block Finder`

**Examples:**
```
/blockfinder set ancient_debris
/blockfinder toggle
/blockfinder color 255 165 0
/blockfinder radius 96
```

### Player ESP

| Command | Description |
|---|---|
| `/mhesp` or `/mhesp toggle` | Enable / disable Player ESP |
| `/mhesp mode <aabb\|glow>` | Set render mode (AABB: lightweight boxes, Glow: vanilla outline with blur) |
| `/mhesp colormode <solid\|team>` | Set color mode (Solid: one color, Team: team-based colors) |
| `/mhesp color <r> <g> <b>` | Set solid color (0–255 per channel) |
| `/mhesp radius <16–256>` | Set detection radius in blocks (default: 64) |
| `/mhesp debug` | Debug command: displays all available color sources for nearby players |

**Examples:**
```
/mhesp toggle
/mhesp mode glow
/mhesp colormode team
/mhesp radius 128
```

### Auto Sprint

| Command | Description |
|---|---|
| `/mhsprint` | Toggle Auto Sprint on/off |

### Auto Clicker

| Command | Description |
|---|---|
| `/mhclick` | Toggle Auto Clicker on/off |

**Note:** CPS is adjusted via GUI in the General tab (±/− buttons).

## Configuration

Settings are saved automatically to `.minecraft/config/`:

**`mh_blockfinder.json`:**
```json
{
  "scanRadius": 64,
  "highlightColor": [1.0, 1.0, 0.0, 1.0],
  "lineWidth": 3.0,
  "ticksPerScan": 2,
  "chunksPerScan": 8
}
```

| Field | Description |
|---|---|
| `scanRadius` | Search radius in blocks (16–128) |
| `highlightColor` | RGBA, each value 0.0–1.0 |
| `lineWidth` | Bounding box line thickness |
| `ticksPerScan` | Ticks between scan steps (increase to reduce CPU load) |
| `chunksPerScan` | Chunks processed per scan step (decrease to reduce CPU load) |

**`mh_playeresp.json`:**
```json
{
  "radius": 64,
  "lineWidth": 2.0,
  "renderMode": "AABB",
  "colorMode": "SOLID",
  "solidColor": [1.0, 0.0, 0.0, 1.0]
}
```

| Field | Description |
|---|---|
| `radius` | Detection radius in blocks (16–256) |
| `lineWidth` | Line thickness for AABB mode |
| `renderMode` | `"AABB"` or `"GLOW"` |
| `colorMode` | `"SOLID"` or `"TEAM"` |
| `solidColor` | RGBA for solid mode, each value 0.0–1.0 |

**`mh_autosprint.json`:**
```json
{
  "enabled": true
}
```

| Field | Description |
|---|---|
| `enabled` | Auto Sprint state (true/false) |

**`mh_autoclicker.json`:**
```json
{
  "enabled": false,
  "cps": 10
}
```

| Field | Description |
|---|---|
| `enabled` | Auto Clicker state (true/false) |
| `cps` | Clicks per second (1–20, with ±30% randomization) |

## Building from source

```bash
./gradlew build
```

Output: `build/libs/minehelper-1.0.0.jar`

## Requirements

- Minecraft 26.1.2
- Fabric Loader 0.19.3+
- Fabric API 0.155.2+26.1.2
- Java 25+

## License

CC0-1.0
