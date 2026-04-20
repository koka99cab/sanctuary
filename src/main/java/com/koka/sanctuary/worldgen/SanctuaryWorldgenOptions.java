package com.koka.sanctuary.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public record SanctuaryWorldgenOptions(
	RegistryEntry<Biome> starterBiome,
	double sanctuaryZoneScale,
	double starterIslandScale,
	double archipelagoSize,
	double archipelagoSpacing,
	double monolithGapScale,
	int vanillaFloatingIslandsStartRadius,
	boolean deepslateCore,
	boolean satelliteIslands,
	VerticalityProfile verticalityProfile,
	MainIslandPreset mainIslandPreset,
	double monolithMassHeight,
	double monolithShoulderWidth,
	int fracturedIslandCount,
	double fracturedRingRadius,
	double hollowVoidSize,
	double hollowRingThickness,
	int spireCount,
	double spireHeight
) {
	private static final double LEGACY_GAP_EPSILON = 1.0E-4D;
	public static final double BIOME_BOUNDARY_FROM_GAP_RATIO = 0.30D;
	public static final double DEFAULT_SANCTUARY_ZONE_SCALE = 1.0D;
	public static final double DEFAULT_STARTER_ISLAND_SCALE = 1.0D;
	public static final double DEFAULT_ARCHIPELAGO_SIZE = 1.0D;
	public static final double DEFAULT_ARCHIPELAGO_SPACING = 0.0D;
	public static final double DEFAULT_MONOLITH_GAP_SCALE = 1.0D;
	public static final int DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS = 768;
	public static final boolean DEFAULT_DEEPSLATE_CORE = false;
	public static final boolean DEFAULT_SATELLITE_ISLANDS = false;
	public static final VerticalityProfile DEFAULT_VERTICALITY_PROFILE = VerticalityProfile.BALANCED;
	public static final MainIslandPreset DEFAULT_MAIN_ISLAND_PRESET = MainIslandPreset.MONOLITH;
	public static final double DEFAULT_MONOLITH_MASS_HEIGHT = 1.0D;
	public static final double DEFAULT_MONOLITH_SHOULDER_WIDTH = 1.0D;
	public static final int DEFAULT_FRACTURED_ISLAND_COUNT = 4;
	public static final double DEFAULT_FRACTURED_RING_RADIUS = 1.0D;
	public static final double DEFAULT_HOLLOW_VOID_SIZE = 1.0D;
	public static final double DEFAULT_HOLLOW_RING_THICKNESS = 1.0D;
	public static final int DEFAULT_SPIRE_COUNT = 7;
	public static final double DEFAULT_SPIRE_HEIGHT = 1.0D;
	public static final double DEFAULT_INNER_RADIUS = 96.0D;
	public static final double DEFAULT_OUTER_RADIUS = 224.0D;
	public static final double MIN_SANCTUARY_ZONE_SCALE = 0.60D;
	public static final double MAX_SANCTUARY_ZONE_SCALE = 1.80D;
	public static final double MIN_STARTER_ISLAND_SCALE = 0.60D;
	public static final double MAX_STARTER_ISLAND_SCALE = 1.80D;
	public static final double MIN_ARCHIPELAGO_SIZE = 0.60D;
	public static final double MAX_ARCHIPELAGO_SIZE = 1.60D;
	public static final double MIN_ARCHIPELAGO_SPACING = -0.40D;
	public static final double MAX_ARCHIPELAGO_SPACING = 0.40D;
	public static final double MIN_MONOLITH_GAP_SCALE = 0.70D;
	public static final double MAX_MONOLITH_GAP_SCALE = 2.60D;
	public static final int MIN_VANILLA_FLOATING_ISLANDS_START_RADIUS = 0;
	public static final int MAX_VANILLA_FLOATING_ISLANDS_START_RADIUS = 8192;
	public static final double MIN_MONOLITH_MASS_HEIGHT = 0.80D;
	public static final double MAX_MONOLITH_MASS_HEIGHT = 2.40D;
	public static final double MIN_MONOLITH_SHOULDER_WIDTH = 0.60D;
	public static final double MAX_MONOLITH_SHOULDER_WIDTH = 2.00D;
	public static final int MIN_FRACTURED_ISLAND_COUNT = 3;
	public static final int MAX_FRACTURED_ISLAND_COUNT = 8;
	public static final double MIN_FRACTURED_RING_RADIUS = 0.80D;
	public static final double MAX_FRACTURED_RING_RADIUS = 2.40D;
	public static final double MIN_HOLLOW_VOID_SIZE = 0.70D;
	public static final double MAX_HOLLOW_VOID_SIZE = 2.00D;
	public static final double MIN_HOLLOW_RING_THICKNESS = 0.60D;
	public static final double MAX_HOLLOW_RING_THICKNESS = 1.80D;
	public static final int MIN_SPIRE_COUNT = 5;
	public static final int MAX_SPIRE_COUNT = 14;
	public static final double MIN_SPIRE_HEIGHT = 0.80D;
	public static final double MAX_SPIRE_HEIGHT = 2.60D;

	public static final Codec<SanctuaryWorldgenOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Biome.REGISTRY_CODEC.fieldOf("starter_biome").forGetter(SanctuaryWorldgenOptions::starterBiome),
		Codec.DOUBLE.optionalFieldOf("sanctuary_zone_scale", DEFAULT_SANCTUARY_ZONE_SCALE).forGetter(SanctuaryWorldgenOptions::sanctuaryZoneScale),
		Codec.DOUBLE.optionalFieldOf("starter_island_scale", DEFAULT_STARTER_ISLAND_SCALE).forGetter(SanctuaryWorldgenOptions::starterIslandScale),
		Codec.DOUBLE.optionalFieldOf("archipelago_size", DEFAULT_ARCHIPELAGO_SIZE).forGetter(SanctuaryWorldgenOptions::archipelagoSize),
		Codec.DOUBLE.optionalFieldOf("archipelago_spacing", DEFAULT_ARCHIPELAGO_SPACING).forGetter(SanctuaryWorldgenOptions::archipelagoSpacing),
		Codec.DOUBLE.optionalFieldOf("monolith_gap_scale", DEFAULT_MONOLITH_GAP_SCALE).forGetter(SanctuaryWorldgenOptions::monolithGapScale),
		Codec.INT.optionalFieldOf(
			"vanilla_floating_islands_start_radius",
			DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS
		).forGetter(SanctuaryWorldgenOptions::vanillaFloatingIslandsStartRadius),
		Codec.BOOL.optionalFieldOf("deepslate_core", DEFAULT_DEEPSLATE_CORE).forGetter(SanctuaryWorldgenOptions::deepslateCore),
		Codec.BOOL.optionalFieldOf("satellite_islands", DEFAULT_SATELLITE_ISLANDS).forGetter(SanctuaryWorldgenOptions::satelliteIslands),
		VerticalityProfile.CODEC.optionalFieldOf("verticality_profile", DEFAULT_VERTICALITY_PROFILE).forGetter(SanctuaryWorldgenOptions::verticalityProfile),
		PresetTuning.CODEC.optionalFieldOf("preset_tuning", PresetTuning.DEFAULT).forGetter(SanctuaryWorldgenOptions::presetTuning)
	).apply(instance, (
		starterBiome,
		sanctuaryZoneScale,
		starterIslandScale,
		archipelagoSize,
		archipelagoSpacing,
		monolithGapScale,
		vanillaFloatingIslandsStartRadius,
		deepslateCore,
		satelliteIslands,
		verticalityProfile,
		presetTuning
	) -> new SanctuaryWorldgenOptions(
		starterBiome,
		sanctuaryZoneScale,
		starterIslandScale,
		archipelagoSize,
		archipelagoSpacing,
		monolithGapScale,
		vanillaFloatingIslandsStartRadius,
		deepslateCore,
		satelliteIslands,
		verticalityProfile,
		presetTuning.mainIslandPreset(),
		presetTuning.monolithMassHeight(),
		presetTuning.monolithShoulderWidth(),
		presetTuning.fracturedIslandCount(),
		presetTuning.fracturedRingRadius(),
		presetTuning.hollowVoidSize(),
		presetTuning.hollowRingThickness(),
		presetTuning.spireCount(),
		presetTuning.spireHeight()
	)));

	public SanctuaryWorldgenOptions {
		sanctuaryZoneScale = clamp(sanctuaryZoneScale, MIN_SANCTUARY_ZONE_SCALE, MAX_SANCTUARY_ZONE_SCALE);
		starterIslandScale = clamp(starterIslandScale, MIN_STARTER_ISLAND_SCALE, MAX_STARTER_ISLAND_SCALE);
		archipelagoSize = clamp(archipelagoSize, MIN_ARCHIPELAGO_SIZE, MAX_ARCHIPELAGO_SIZE);
		archipelagoSpacing = clamp(archipelagoSpacing, MIN_ARCHIPELAGO_SPACING, MAX_ARCHIPELAGO_SPACING);
		monolithGapScale = clamp(monolithGapScale, MIN_MONOLITH_GAP_SCALE, MAX_MONOLITH_GAP_SCALE);
		vanillaFloatingIslandsStartRadius = clamp(
			vanillaFloatingIslandsStartRadius,
			MIN_VANILLA_FLOATING_ISLANDS_START_RADIUS,
			MAX_VANILLA_FLOATING_ISLANDS_START_RADIUS
		);
		verticalityProfile = verticalityProfile == null ? DEFAULT_VERTICALITY_PROFILE : verticalityProfile;
		mainIslandPreset = mainIslandPreset == null ? DEFAULT_MAIN_ISLAND_PRESET : mainIslandPreset;
		monolithMassHeight = clamp(monolithMassHeight, MIN_MONOLITH_MASS_HEIGHT, MAX_MONOLITH_MASS_HEIGHT);
		monolithShoulderWidth = clamp(monolithShoulderWidth, MIN_MONOLITH_SHOULDER_WIDTH, MAX_MONOLITH_SHOULDER_WIDTH);
		fracturedIslandCount = clamp(fracturedIslandCount, MIN_FRACTURED_ISLAND_COUNT, MAX_FRACTURED_ISLAND_COUNT);
		fracturedRingRadius = clamp(fracturedRingRadius, MIN_FRACTURED_RING_RADIUS, MAX_FRACTURED_RING_RADIUS);
		hollowVoidSize = clamp(hollowVoidSize, MIN_HOLLOW_VOID_SIZE, MAX_HOLLOW_VOID_SIZE);
		hollowRingThickness = clamp(hollowRingThickness, MIN_HOLLOW_RING_THICKNESS, MAX_HOLLOW_RING_THICKNESS);
		spireCount = clamp(spireCount, MIN_SPIRE_COUNT, MAX_SPIRE_COUNT);
		spireHeight = clamp(spireHeight, MIN_SPIRE_HEIGHT, MAX_SPIRE_HEIGHT);
	}

	public static SanctuaryWorldgenOptions defaults(RegistryEntry<Biome> starterBiome) {
		return new SanctuaryWorldgenOptions(
			starterBiome,
			DEFAULT_SANCTUARY_ZONE_SCALE,
			DEFAULT_STARTER_ISLAND_SCALE,
			DEFAULT_ARCHIPELAGO_SIZE,
			DEFAULT_ARCHIPELAGO_SPACING,
			DEFAULT_MONOLITH_GAP_SCALE,
			DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS,
			DEFAULT_DEEPSLATE_CORE,
			DEFAULT_SATELLITE_ISLANDS,
			DEFAULT_VERTICALITY_PROFILE,
			DEFAULT_MAIN_ISLAND_PRESET,
			DEFAULT_MONOLITH_MASS_HEIGHT,
			DEFAULT_MONOLITH_SHOULDER_WIDTH,
			DEFAULT_FRACTURED_ISLAND_COUNT,
			DEFAULT_FRACTURED_RING_RADIUS,
			DEFAULT_HOLLOW_VOID_SIZE,
			DEFAULT_HOLLOW_RING_THICKNESS,
			DEFAULT_SPIRE_COUNT,
			DEFAULT_SPIRE_HEIGHT
		);
	}

	public static SanctuaryWorldgenOptions fromLegacy(RegistryEntry<Biome> starterBiome, double innerRadius, double outerRadius) {
		double scaleFromInner = innerRadius / DEFAULT_INNER_RADIUS;
		double scaleFromOuter = outerRadius / DEFAULT_OUTER_RADIUS;
		double averagedScale = (scaleFromInner + scaleFromOuter) * 0.5D;
		return new SanctuaryWorldgenOptions(
			starterBiome,
			averagedScale,
			DEFAULT_STARTER_ISLAND_SCALE,
			DEFAULT_ARCHIPELAGO_SIZE,
			DEFAULT_ARCHIPELAGO_SPACING,
			DEFAULT_MONOLITH_GAP_SCALE,
			DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS,
			DEFAULT_DEEPSLATE_CORE,
			DEFAULT_SATELLITE_ISLANDS,
			DEFAULT_VERTICALITY_PROFILE,
			DEFAULT_MAIN_ISLAND_PRESET,
			DEFAULT_MONOLITH_MASS_HEIGHT,
			DEFAULT_MONOLITH_SHOULDER_WIDTH,
			DEFAULT_FRACTURED_ISLAND_COUNT,
			DEFAULT_FRACTURED_RING_RADIUS,
			DEFAULT_HOLLOW_VOID_SIZE,
			DEFAULT_HOLLOW_RING_THICKNESS,
			DEFAULT_SPIRE_COUNT,
			DEFAULT_SPIRE_HEIGHT
		);
	}

	public SanctuaryWorldgenOptions sanitized(RegistryEntry<Biome> defaultStarterBiome) {
		return new SanctuaryWorldgenOptions(
			this.starterBiome != null ? this.starterBiome : defaultStarterBiome,
			this.sanctuaryZoneScale,
			this.starterIslandScale,
			this.archipelagoSize,
			this.archipelagoSpacing,
			this.monolithGapScale,
			this.vanillaFloatingIslandsStartRadius,
			this.deepslateCore,
			false,
			this.verticalityProfile,
			this.mainIslandPreset,
			this.monolithMassHeight,
			this.monolithShoulderWidth,
			this.fracturedIslandCount,
			this.fracturedRingRadius,
			this.hollowVoidSize,
			this.hollowRingThickness,
			this.spireCount,
			this.spireHeight
		);
	}

	public double innerRadius() {
		return DEFAULT_INNER_RADIUS * this.sanctuaryZoneScale;
	}

	public double outerRadius() {
		return DEFAULT_OUTER_RADIUS * this.sanctuaryZoneScale;
	}

	public int effectiveVanillaFloatingIslandsStartRadius() {
		if (this.vanillaFloatingIslandsStartRadius != DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS) {
			return this.vanillaFloatingIslandsStartRadius;
		}

		if (Math.abs(this.monolithGapScale - DEFAULT_MONOLITH_GAP_SCALE) > LEGACY_GAP_EPSILON) {
			return clamp(
				(int) Math.round(DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS * this.monolithGapScale),
				MIN_VANILLA_FLOATING_ISLANDS_START_RADIUS,
				MAX_VANILLA_FLOATING_ISLANDS_START_RADIUS
			);
		}

		return this.vanillaFloatingIslandsStartRadius;
	}

	private PresetTuning presetTuning() {
		return new PresetTuning(
			this.mainIslandPreset,
			this.monolithMassHeight,
			this.monolithShoulderWidth,
			this.fracturedIslandCount,
			this.fracturedRingRadius,
			this.hollowVoidSize,
			this.hollowRingThickness,
			this.spireCount,
			this.spireHeight
		);
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private record PresetTuning(
		MainIslandPreset mainIslandPreset,
		double monolithMassHeight,
		double monolithShoulderWidth,
		int fracturedIslandCount,
		double fracturedRingRadius,
		double hollowVoidSize,
		double hollowRingThickness,
		int spireCount,
		double spireHeight
	) {
		private static final PresetTuning DEFAULT = new PresetTuning(
			DEFAULT_MAIN_ISLAND_PRESET,
			DEFAULT_MONOLITH_MASS_HEIGHT,
			DEFAULT_MONOLITH_SHOULDER_WIDTH,
			DEFAULT_FRACTURED_ISLAND_COUNT,
			DEFAULT_FRACTURED_RING_RADIUS,
			DEFAULT_HOLLOW_VOID_SIZE,
			DEFAULT_HOLLOW_RING_THICKNESS,
			DEFAULT_SPIRE_COUNT,
			DEFAULT_SPIRE_HEIGHT
		);

		private static final Codec<PresetTuning> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MainIslandPreset.CODEC.optionalFieldOf("main_island_preset", DEFAULT_MAIN_ISLAND_PRESET).forGetter(PresetTuning::mainIslandPreset),
			Codec.DOUBLE.optionalFieldOf("monolith_mass_height", DEFAULT_MONOLITH_MASS_HEIGHT).forGetter(PresetTuning::monolithMassHeight),
			Codec.DOUBLE.optionalFieldOf("monolith_shoulder_width", DEFAULT_MONOLITH_SHOULDER_WIDTH).forGetter(PresetTuning::monolithShoulderWidth),
			Codec.INT.optionalFieldOf("fractured_island_count", DEFAULT_FRACTURED_ISLAND_COUNT).forGetter(PresetTuning::fracturedIslandCount),
			Codec.DOUBLE.optionalFieldOf("fractured_ring_radius", DEFAULT_FRACTURED_RING_RADIUS).forGetter(PresetTuning::fracturedRingRadius),
			Codec.DOUBLE.optionalFieldOf("hollow_void_size", DEFAULT_HOLLOW_VOID_SIZE).forGetter(PresetTuning::hollowVoidSize),
			Codec.DOUBLE.optionalFieldOf("hollow_ring_thickness", DEFAULT_HOLLOW_RING_THICKNESS).forGetter(PresetTuning::hollowRingThickness),
			Codec.INT.optionalFieldOf("spire_count", DEFAULT_SPIRE_COUNT).forGetter(PresetTuning::spireCount),
			Codec.DOUBLE.optionalFieldOf("spire_height", DEFAULT_SPIRE_HEIGHT).forGetter(PresetTuning::spireHeight)
		).apply(instance, PresetTuning::new));
	}
}
