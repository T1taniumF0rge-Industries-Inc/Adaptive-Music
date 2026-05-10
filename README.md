# Adaptive Music

Adaptive Music is a Fabric client mod for Minecraft 1.21 that starts a barebone emotional-memory music director. The first 1.0.0 build focuses on safe, configurable scaffolding that can be expanded into a richer soundtrack system.

## Current 1.0.0 features

- Client/server Fabric mod targeting Minecraft 1.21, with a server handshake that syncs the player respawn/home anchor to the client music director.
- Mod Menu integration with a config screen.
- `/adaptivemusic` and `/am` open the config screen.
- `/adaptivemusic status` and `/am status` show the current mood memory state.
- `/adaptivemusic home` and `/am home` mark your current position as a client-side home motif anchor; when installed server-side, the server also syncs your respawn anchor automatically.
- `/adaptivemusic settrack <mood> <sound_event_id>` maps a mood to any registered vanilla or modded sound event ID.
- Config file: `config/adaptivemusic.json`.
- Barebone adaptive moods:
  - `spawn_nostalgia` near the world-origin spawn area.
  - `danger` when visible hostile mobs are close.
  - `death_memory` after repeated deaths.
  - `home` near your saved home anchor.
  - `social` when another player appears nearby.
  - `default` fallback exploration music.

## Configuration notes

The config screen lets you toggle the mod, toggle debug toasts, and edit the sound event IDs used by each mood. Sound event IDs can come from Minecraft or from another mod loaded on the same client/server instance, as long as the sound event exists in the registry.

## Ideas for future versions

See the expanded roadmap in [`docs/ROADMAP.md`](docs/ROADMAP.md). The core creative goal is that **the world remembers you**, not just that each biome has different music.

- **Player leitmotifs:** assign a recurring sound palette per player UUID and weave it in when that player joins, trades, helps, or attacks.
- **Memory decay:** make deaths, discoveries, and social events fade over several in-game days instead of instantly resetting.
- **Biome emotional palettes:** let each biome contribute tags like lonely, warm, dangerous, ancient, or magical.
- **Home growth score:** make a base feel more peaceful as beds, pets, farms, storage, and villagers accumulate nearby.
- **Boss tension layers:** crossfade into stronger motifs as the Wither, Dragon, Warden, raids, or modded bosses become relevant.
- **Weather and time arcs:** add rainy-night melancholy, sunrise relief, storm danger, and sunset nostalgia.
- **Structure memories:** remember first village, first portal, death locations, ancient cities, and favorite mining routes.
- **Hardcore-style dread:** increase dissonance when the player carries valuable items or is far from home.
- **Dimension themes:** separate emotional memory banks for Overworld, Nether, End, and modded dimensions.
- **Data-driven packs:** allow resource packs/datapacks to define moods, rules, priorities, and music pools without code.
- **Smoother audio control:** replace this barebone trigger player with mixins/accessors for proper vanilla music cooldown and fade control.
- **Emotional scars:** permanently remember traumatic moments like lava deaths, betrayals, and first near-deaths.
- **Chunk memories:** let chunks become peaceful, cursed, nostalgic, tense, sacred, abandoned, or contested based on history.
- **Anti-repetition system:** track recently used motifs, rotate variants, and mutate melodies so music never feels stale.
- **The World Learns You:** adapt music around what scares the player, where they linger, what they avoid, and what they value.

## Building

```bash
gradle build --no-daemon
```
