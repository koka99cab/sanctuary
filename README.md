# Sanctuary

Sanctuary is a Fabric world preset for Minecraft `1.21.11` that builds a floating-island overworld around a handcrafted central survival zone.

The mod mixes three layers:

- a custom central island generator,
- a curated Sanctuary biome zone near the origin,
- a fallback overworld biome source so the world can open back into broader procedural floating islands.

## Requirements

- Minecraft `1.21.11`
- Fabric Loader `0.19.1+`
- Fabric API
- Java `21+`

## Installation

Place the mod jar in your `mods` folder.

For singleplayer, install it like a normal Fabric mod.

For dedicated servers, install it on the server and also on clients.

## Is Sanctuary Server-Side Only?

No.

The terrain generation itself is server-authoritative, but the mod is not packaged as a server-side-only mod:

- it registers custom worldgen codecs and density functions,
- it defines a custom biome source,
- it ships a client entrypoint for the world customization screen,
- it includes client mixins for the Create World flow.

In practice, treat Sanctuary as a mod that should be present on both the server and the client.

## How the Preset Works

The main entrypoints are:

- [`src/main/resources/data/sanctuary/worldgen/world_preset/sanctuary.json`](/sanctuary/src/main/resources/data/sanctuary/worldgen/world_preset/sanctuary.json)
  This is the actual world preset definition.
- [`src/main/resources/data/sanctuary/worldgen/noise_settings/sanctuary_overworld.json`](/sanctuary/src/main/resources/data/sanctuary/worldgen/noise_settings/sanctuary_overworld.json)
  This is the terrain graph. It combines the Sanctuary central field with vanilla End-style floating island noise.
- [`src/main/java/com/koka/sanctuary/worldgen/OriginIslandDensityFunction.java`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/OriginIslandDensityFunction.java)
  This is the custom central island geometry.
- [`src/main/java/com/koka/sanctuary/worldgen/SanctuaryBiomeSource.java`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/SanctuaryBiomeSource.java)
  This controls the curated Sanctuary biome zone and the fallback biome behavior outside it.
- [`src/main/java/com/koka/sanctuary/worldgen/SanctuaryChunkGeneratorSettingsFactory.java`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/SanctuaryChunkGeneratorSettingsFactory.java)
  This is where the customization screen is translated into actual worldgen changes.

## Customization Reference

The customization screen is implemented in [`SanctuaryCustomizeScreen.java`](/sanctuary/src/main/java/com/koka/sanctuary/client/gui/SanctuaryCustomizeScreen.java).

### Shared settings

- `Main island preset`
  Chooses the overall central landform family.
- `Starting biome`
  Picks the biome used at spawn. The dropdown is built from safe surface biomes collected from the fallback overworld biome list.
- `Deepslate core`
  Replaces lower interior strata with deepslate. It does not repaint the entire island surface.
- `Vertical profile`
  Pushes the preset toward flatter plateaus or more dramatic vertical relief.
- `Sanctuary zone scale`
  Expands or shrinks the curated central Sanctuary biome zone. This is mainly a biome-zone control, not a full terrain-mass control.
- `Main island scale`
  Scales the main island body used by the custom density function.
- `Archipelago size`
  Makes the floating island field read larger or smaller overall.
- `Archipelago spacing`
  Pushes the island field toward denser clustering or more open separation.
- `Sanctuary gap (blocks)`
  Controls when the distant vanilla End-style floating islands begin to fade in, in block units. The same value also influences the outer Sanctuary biome boundary.

### Main island presets

- `Monolith`
  A single central mass.
  Extra controls:
  `Mass height`, `Shoulder width`
- `Fractured Archipelago`
  A broken central formation made of multiple nearby islands.
  Extra controls:
  `Island count`, `Ring radius`
- `Hollow Crown`
  A ring-like central formation with a hollow center.
  Extra controls:
  `Void size`, `Ring thickness`
- `Spire Garden`
  A smaller core surrounded by tall spires.
  Extra controls:
  `Spire count`, `Spire height`

## Biome Mod Compatibility

Sanctuary is intentionally friendlier to biome mods than to full worldgen overhauls.

Why:

- the Sanctuary preset uses a custom biome source,
- but its fallback is the vanilla overworld multi-noise preset: [`"fallback": "minecraft:overworld"`](/sanctuary/src/main/resources/data/sanctuary/worldgen/world_preset/sanctuary.json:9),
- outside the curated Sanctuary zone, biome selection comes from that fallback source in [`SanctuaryBiomeSource.java`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/SanctuaryBiomeSource.java:247),
- the starting-biome dropdown also harvests candidates from the fallback biome list in [`getStartingBiomeCandidates()`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/SanctuaryBiomeSource.java:316).

### What usually works well

- Mods or datapacks that add new biomes to the vanilla overworld multi-noise setup.
- Biome packs whose biomes are tagged as normal overworld surface biomes.

Those biomes can:

- appear outside the central Sanctuary zone,
- appear in the starting biome selector if they pass the safe-surface filter.

The starting biome filter excludes obvious non-surface starts such as:

- oceans,
- deep oceans,
- rivers,
- beaches,
- nether biomes,
- end biomes,
- cave-like or void-like biomes.

That filtering lives in [`isSurfaceSafeStartBiome()`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/SanctuaryBiomeSource.java:340).

### What is less likely to merge cleanly

- Mods that replace the entire overworld generator with their own custom chunk generator.
- Mods that expect you to choose their own separate world preset.
- Large-scale worldgen overhauls that do not integrate through the vanilla overworld multi-noise fallback.

In short:

- biome-expansion mods are the best fit,
- full overworld replacement mods are not the main compatibility target.

## Structure Generation

Sanctuary includes structure placement rules to reduce structures spawning in open void.

That logic lives in [`SanctuaryStructureRules.java`](/sanctuary/src/main/java/com/koka/sanctuary/worldgen/SanctuaryStructureRules.java).

Current behavior:

- structures are sampled against local terrain support before being accepted,
- underground structures such as mineshafts and strongholds are biased toward interior placement inside actual mass,
- weakly supported starts can be rejected entirely.

This mostly targets vanilla structures. Mods with their own structure systems may behave differently depending on how they hook into structure starts.

## Current Notes and Limitations

- The central gap / outer floating island transition is still under active tuning.
- `Sanctuary gap (blocks)` controls when the outer End-style islands begin, but it does not guarantee a solid connection between the main island and the outer field.
- Worldgen changes do not retroactively update already-generated chunks.
- Ore distribution is not yet exposed as a customization setting.

## Version

Current project version: `0.3.0`
