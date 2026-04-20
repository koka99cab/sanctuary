package com.koka.sanctuary.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record OriginIslandDensityFunction(
	double centerY,
	double radialOffset,
	double coreRadius,
	double plateauRadius,
	double upperDepth,
	double lowerDepth,
	double shardRadius,
	double shardStrength,
	double erosionStrength,
	double cavernCenterOffset,
	double cavernRadius,
	double cavernHeight,
	double cavernStrength,
	double monolithGapScale,
	boolean satelliteIslands,
	VerticalityProfile verticalityProfile,
	MainIslandPreset mainIslandPreset,
	double presetPrimary,
	double presetSecondary
) implements DensityFunction.Base {
	public static final double DEFAULT_CENTER_Y = 128.0D;
	public static final double DEFAULT_RADIAL_OFFSET = 0.0D;
	public static final double DEFAULT_CORE_RADIUS = 66.0D;
	public static final double DEFAULT_PLATEAU_RADIUS = 30.0D;
	public static final double DEFAULT_UPPER_DEPTH = 24.0D;
	public static final double DEFAULT_LOWER_DEPTH = 186.0D;
	public static final double DEFAULT_SHARD_RADIUS = 36.0D;
	public static final double DEFAULT_SHARD_STRENGTH = 0.52D;
	public static final double DEFAULT_EROSION_STRENGTH = 0.54D;
	public static final double DEFAULT_CAVERN_CENTER_OFFSET = -10.0D;
	public static final double DEFAULT_CAVERN_RADIUS = 14.0D;
	public static final double DEFAULT_CAVERN_HEIGHT = 9.0D;
	public static final double DEFAULT_CAVERN_STRENGTH = 0.72D;
	public static final double DEFAULT_PRESET_PRIMARY = 1.0D;
	public static final double DEFAULT_PRESET_SECONDARY = 1.0D;

	public static final MapCodec<OriginIslandDensityFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("center_y", DEFAULT_CENTER_Y).forGetter(OriginIslandDensityFunction::centerY),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("radial_offset", DEFAULT_RADIAL_OFFSET).forGetter(OriginIslandDensityFunction::radialOffset),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("core_radius", DEFAULT_CORE_RADIUS).forGetter(OriginIslandDensityFunction::coreRadius),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("plateau_radius", DEFAULT_PLATEAU_RADIUS).forGetter(OriginIslandDensityFunction::plateauRadius),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("upper_depth", DEFAULT_UPPER_DEPTH).forGetter(OriginIslandDensityFunction::upperDepth),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("lower_depth", DEFAULT_LOWER_DEPTH).forGetter(OriginIslandDensityFunction::lowerDepth),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("shard_radius", DEFAULT_SHARD_RADIUS).forGetter(OriginIslandDensityFunction::shardRadius),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("shard_strength", DEFAULT_SHARD_STRENGTH).forGetter(OriginIslandDensityFunction::shardStrength),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("erosion_strength", DEFAULT_EROSION_STRENGTH).forGetter(OriginIslandDensityFunction::erosionStrength),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("cavern_center_offset", DEFAULT_CAVERN_CENTER_OFFSET).forGetter(OriginIslandDensityFunction::cavernCenterOffset),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("cavern_radius", DEFAULT_CAVERN_RADIUS).forGetter(OriginIslandDensityFunction::cavernRadius),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("cavern_height", DEFAULT_CAVERN_HEIGHT).forGetter(OriginIslandDensityFunction::cavernHeight),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("cavern_strength", DEFAULT_CAVERN_STRENGTH).forGetter(OriginIslandDensityFunction::cavernStrength),
		com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("monolith_gap_scale", SanctuaryWorldgenOptions.DEFAULT_MONOLITH_GAP_SCALE).forGetter(OriginIslandDensityFunction::monolithGapScale),
		StyleData.CODEC.optionalFieldOf("style_data", StyleData.DEFAULT).forGetter(OriginIslandDensityFunction::styleData),
		PresetData.CODEC.optionalFieldOf("preset_data", PresetData.DEFAULT).forGetter(OriginIslandDensityFunction::presetData)
	).apply(instance, (
		centerY,
		radialOffset,
		coreRadius,
		plateauRadius,
		upperDepth,
		lowerDepth,
		shardRadius,
		shardStrength,
		erosionStrength,
		cavernCenterOffset,
		cavernRadius,
		cavernHeight,
		cavernStrength,
		monolithGapScale,
		styleData,
		presetData
	) -> new OriginIslandDensityFunction(
		centerY,
		radialOffset,
		coreRadius,
		plateauRadius,
		upperDepth,
		lowerDepth,
		shardRadius,
		shardStrength,
		erosionStrength,
		cavernCenterOffset,
		cavernRadius,
		cavernHeight,
		cavernStrength,
		monolithGapScale,
		styleData.satelliteIslands(),
		styleData.verticalityProfile(),
		presetData.mainIslandPreset(),
		presetData.presetPrimary(),
		presetData.presetSecondary()
	)));

	@SuppressWarnings("unchecked")
	public static final CodecHolder<DensityFunction> CODEC_HOLDER = CodecHolder.of((MapCodec<DensityFunction>) (MapCodec<?>) MAP_CODEC);

	public static OriginIslandDensityFunction createScaled(double scale) {
		return createTuned(
			DEFAULT_RADIAL_OFFSET,
			scale,
			SanctuaryWorldgenOptions.DEFAULT_ARCHIPELAGO_SIZE,
			SanctuaryWorldgenOptions.DEFAULT_ARCHIPELAGO_SPACING,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_GAP_SCALE,
			SanctuaryWorldgenOptions.DEFAULT_SATELLITE_ISLANDS,
			SanctuaryWorldgenOptions.DEFAULT_VERTICALITY_PROFILE,
			SanctuaryWorldgenOptions.DEFAULT_MAIN_ISLAND_PRESET,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_MASS_HEIGHT,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_SHOULDER_WIDTH
		);
	}

	public static OriginIslandDensityFunction createShellScaled(double radialOffset, double scale) {
		return createTuned(
			radialOffset,
			scale,
			SanctuaryWorldgenOptions.DEFAULT_ARCHIPELAGO_SIZE,
			SanctuaryWorldgenOptions.DEFAULT_ARCHIPELAGO_SPACING,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_GAP_SCALE,
			SanctuaryWorldgenOptions.DEFAULT_SATELLITE_ISLANDS,
			SanctuaryWorldgenOptions.DEFAULT_VERTICALITY_PROFILE,
			SanctuaryWorldgenOptions.DEFAULT_MAIN_ISLAND_PRESET,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_MASS_HEIGHT,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_SHOULDER_WIDTH
		);
	}

	public static OriginIslandDensityFunction createTuned(SanctuaryWorldgenOptions options) {
		return createShellTuned(DEFAULT_RADIAL_OFFSET, options);
	}

	public static OriginIslandDensityFunction createShellTuned(double radialOffset, SanctuaryWorldgenOptions options) {
		return createTuned(
			radialOffset,
			options.starterIslandScale(),
			options.archipelagoSize(),
			options.archipelagoSpacing(),
			options.monolithGapScale(),
			options.satelliteIslands(),
			options.verticalityProfile(),
			options.mainIslandPreset(),
			resolvePresetPrimary(options),
			resolvePresetSecondary(options)
		);
	}

	private static OriginIslandDensityFunction createTuned(
		double radialOffset,
		double scale,
		double archipelagoSize,
		double archipelagoSpacing,
		double monolithGapScale,
		boolean satelliteIslands,
		VerticalityProfile verticalityProfile,
		MainIslandPreset mainIslandPreset,
		double presetPrimary,
		double presetSecondary
	) {
		double clampedScale = clamp(scale, SanctuaryWorldgenOptions.MIN_STARTER_ISLAND_SCALE, SanctuaryWorldgenOptions.MAX_STARTER_ISLAND_SCALE);
		double clampedArchipelagoSize = clamp(archipelagoSize, SanctuaryWorldgenOptions.MIN_ARCHIPELAGO_SIZE, SanctuaryWorldgenOptions.MAX_ARCHIPELAGO_SIZE);
		double clampedArchipelagoSpacing = clamp(
			archipelagoSpacing,
			SanctuaryWorldgenOptions.MIN_ARCHIPELAGO_SPACING,
			SanctuaryWorldgenOptions.MAX_ARCHIPELAGO_SPACING
		);
		double clampedMonolithGapScale = clamp(
			monolithGapScale,
			SanctuaryWorldgenOptions.MIN_MONOLITH_GAP_SCALE,
			SanctuaryWorldgenOptions.MAX_MONOLITH_GAP_SCALE
		);
		double bulkScale = clamp(clampedScale * (0.92D + (clampedArchipelagoSize - 1.0D) * 0.38D - clampedArchipelagoSpacing * 0.10D), 0.55D, 2.10D);
		double shoulderScale = clamp(clampedScale * (1.00D + (clampedArchipelagoSize - 1.0D) * 0.45D + clampedArchipelagoSpacing * 0.18D), 0.55D, 2.20D);
		double shardScale = clamp(clampedScale * (0.84D + (clampedArchipelagoSize - 1.0D) * 0.95D - clampedArchipelagoSpacing * 0.40D), 0.45D, 2.10D);
		double verticalScale = clamp(0.92D + (clampedArchipelagoSize - 1.0D) * 0.70D - clampedArchipelagoSpacing * 0.12D, 0.65D, 1.95D);
		double depthScale = clamp(0.92D + (clampedArchipelagoSize - 1.0D) * 0.55D + Math.max(0.0D, clampedArchipelagoSpacing) * 0.20D, 0.65D, 1.90D);
		double carveScale = clamp(0.72D + clampedArchipelagoSpacing * 0.95D + (clampedArchipelagoSize - 1.0D) * 0.35D, 0.18D, 1.40D);
		double spikeScale = clamp(0.88D + (clampedArchipelagoSize - 1.0D) * 0.70D - clampedArchipelagoSpacing * 0.15D, 0.70D, 2.20D);
		return new OriginIslandDensityFunction(
			DEFAULT_CENTER_Y,
			radialOffset,
			DEFAULT_CORE_RADIUS * bulkScale,
			DEFAULT_PLATEAU_RADIUS * shoulderScale,
			DEFAULT_UPPER_DEPTH * verticalScale,
			DEFAULT_LOWER_DEPTH * depthScale,
			DEFAULT_SHARD_RADIUS * shardScale,
			clamp(DEFAULT_SHARD_STRENGTH + (clampedArchipelagoSize - 1.0D) * 0.42D - clampedArchipelagoSpacing * 0.15D, 0.28D, 1.55D),
			clamp(DEFAULT_EROSION_STRENGTH * carveScale, 0.18D, 1.55D),
			DEFAULT_CAVERN_CENTER_OFFSET,
			DEFAULT_CAVERN_RADIUS * clamp(0.88D + (clampedArchipelagoSize - 1.0D) * 0.50D, 0.65D, 1.70D),
			DEFAULT_CAVERN_HEIGHT * spikeScale,
			clamp(DEFAULT_CAVERN_STRENGTH + (clampedArchipelagoSize - 1.0D) * 0.28D - clampedArchipelagoSpacing * 0.08D, 0.32D, 1.65D),
			clampedMonolithGapScale,
			satelliteIslands,
			verticalityProfile == null ? SanctuaryWorldgenOptions.DEFAULT_VERTICALITY_PROFILE : verticalityProfile,
			mainIslandPreset == null ? SanctuaryWorldgenOptions.DEFAULT_MAIN_ISLAND_PRESET : mainIslandPreset,
			presetPrimary,
			presetSecondary
		);
	}

	@Override
	public double sample(DensityFunction.NoisePos context) {
		double x = context.blockX();
		double y = context.blockY();
		double z = context.blockZ();
		double worldRadius = Math.sqrt(x * x + z * z);
		double noisyWorldRadius = SanctuaryWorldgen.noisyDistanceToOrigin(x, z);
		double angle = Math.atan2(z, x);
		double originRadius = this.radialOffset > 0.0D ? Math.max(0.0D, worldRadius - this.radialOffset) : worldRadius;
		double influenceRadiusDistance = this.radialOffset > 0.0D
			? Math.max(0.0D, noisyWorldRadius - this.radialOffset)
			: noisyWorldRadius;
		double localX = Math.cos(angle) * originRadius;
		double localZ = Math.sin(angle) * originRadius;
		double influenceRadius = this.getInfluenceRadius();
		double influence = 1.0D - smoothstep(
			influenceRadius * 0.88D,
			influenceRadius * 1.30D,
			influenceRadiusDistance
		);
		if (influence <= 0.0D) {
			return 0.0D;
		}

		double field = switch (this.mainIslandPreset) {
			case MONOLITH -> this.sampleMonolith(localX, y, localZ);
			case FRACTURED_ARCHIPELAGO -> this.sampleFracturedArchipelago(localX, y, localZ);
			case HOLLOW_CROWN -> this.sampleHollowCrown(localX, y, localZ);
			case SPIRE_GARDEN -> this.sampleSpireGarden(localX, y, localZ);
		};
		field -= this.sampleRavineCarve(localX, y, localZ) * (this.erosionStrength * 0.22D);
		double undersideTaper = smoothstep(this.centerY - 14.0D, this.centerY - this.lowerDepth * 0.92D, y);
		field -= undersideTaper * 0.06D * Math.cos((localX + localZ) * 0.03D);
		field *= influence;
		return clamp(field, -2.25D, 2.25D);
	}

	@Override
	public double minValue() {
		return -2.25D;
	}

	@Override
	public double maxValue() {
		return 2.25D;
	}

	@Override
	public CodecHolder<? extends DensityFunction> getCodecHolder() {
		return CODEC_HOLDER;
	}

	private double sampleMonolith(double x, double y, double z) {
		double verticalScale = this.verticalScale();
		double verticalVariance = this.verticalVariance();
		double massHeight = clamp(
			this.presetPrimary,
			SanctuaryWorldgenOptions.MIN_MONOLITH_MASS_HEIGHT,
			SanctuaryWorldgenOptions.MAX_MONOLITH_MASS_HEIGHT
		);
		double shoulderWidth = clamp(
			this.presetSecondary,
			SanctuaryWorldgenOptions.MIN_MONOLITH_SHOULDER_WIDTH,
			SanctuaryWorldgenOptions.MAX_MONOLITH_SHOULDER_WIDTH
		);
		double field = sampleIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY + 6.0D,
			0.0D,
			this.coreRadius * (0.72D + 0.16D * shoulderWidth),
			this.upperDepth * (1.10D + 0.25D * massHeight) * verticalScale,
			this.lowerDepth * (0.82D + 0.10D * massHeight),
			0.08D
		);
		field = Math.max(field, sampleIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY - 6.0D,
			0.0D,
			this.coreRadius * (1.12D * shoulderWidth) + this.plateauRadius * 0.55D,
			this.upperDepth * (0.68D * massHeight) * clamp(0.92D + verticalVariance * 0.10D, 0.70D, 1.25D),
			this.lowerDepth * (0.74D + 0.08D * shoulderWidth),
			0.05D
		));
		field = Math.max(field, sampleIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY - 26.0D,
			0.0D,
			this.coreRadius * (0.50D + 0.10D * shoulderWidth),
			this.upperDepth * (0.34D + 0.06D * massHeight),
			this.lowerDepth * (1.18D + 0.18D * massHeight),
			0.03D
		));
		field -= this.sampleCentralBasin(x, y, z) * (this.erosionStrength * 0.55D);
		field -= samplePocketNoise(x, y - this.centerY, z, 0.45D / Math.max(12.0D, this.coreRadius)) * 0.16D;
		return field;
	}

	private double sampleFracturedArchipelago(double x, double y, double z) {
		double verticalVariance = this.verticalVariance();
		int islandCount = clampToInt(
			(int) Math.round(this.presetPrimary),
			SanctuaryWorldgenOptions.MIN_FRACTURED_ISLAND_COUNT,
			SanctuaryWorldgenOptions.MAX_FRACTURED_ISLAND_COUNT
		);
		double ringRadius = clamp(
			this.presetSecondary,
			SanctuaryWorldgenOptions.MIN_FRACTURED_RING_RADIUS,
			SanctuaryWorldgenOptions.MAX_FRACTURED_RING_RADIUS
		);
		double field = sampleIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY + 3.0D,
			0.0D,
			this.coreRadius * 0.42D,
			this.upperDepth * 0.82D,
			this.lowerDepth * 0.40D,
			0.04D
		);
		field = Math.max(field, this.sampleIslandRing(
			x,
			y,
			z,
			islandCount,
			(this.coreRadius * 0.70D + this.plateauRadius * 1.30D) * ringRadius,
			this.coreRadius * 0.42D,
			0.10D,
			0.95D,
			0.54D,
			2.0D * verticalVariance
		));
		field -= this.sampleRadialVoid(x, y, z, this.coreRadius * (0.26D + 0.12D * ringRadius), this.upperDepth * 1.10D, this.lowerDepth * 0.32D) * 0.45D;
		return field;
	}

	private double sampleHollowCrown(double x, double y, double z) {
		double verticalScale = this.verticalScale();
		double voidSize = clamp(
			this.presetPrimary,
			SanctuaryWorldgenOptions.MIN_HOLLOW_VOID_SIZE,
			SanctuaryWorldgenOptions.MAX_HOLLOW_VOID_SIZE
		);
		double ringThickness = clamp(
			this.presetSecondary,
			SanctuaryWorldgenOptions.MIN_HOLLOW_RING_THICKNESS,
			SanctuaryWorldgenOptions.MAX_HOLLOW_RING_THICKNESS
		);
		double crownRadius = this.coreRadius * (0.58D + 0.38D * voidSize) + this.plateauRadius * (0.34D + 0.24D * ringThickness);
		double ringHalfWidth = this.plateauRadius * (0.28D + 0.26D * ringThickness);
		double field = sampleRingIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY + 4.0D,
			0.0D,
			crownRadius,
			ringHalfWidth,
			this.upperDepth * (1.04D + 0.14D * ringThickness) * verticalScale,
			this.lowerDepth * (0.76D + 0.12D * ringThickness),
			0.06D
		);
		field = Math.max(field, sampleRingIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY - 8.0D,
			0.0D,
			crownRadius * 0.94D,
			ringHalfWidth * 0.88D,
			this.upperDepth * (1.08D + 0.18D * ringThickness) * verticalScale,
			this.lowerDepth * (0.82D + 0.10D * ringThickness),
			0.06D
		) * 0.96D);
		field = Math.max(field, this.sampleIslandRing(
			x,
			y,
			z,
			5,
			crownRadius * 1.02D,
			this.plateauRadius * (0.30D + 0.18D * ringThickness),
			0.24D,
			0.74D,
			0.42D,
			6.0D
		));
		field -= this.sampleRadialVoid(
			x,
			y,
			z,
			Math.max(12.0D, crownRadius - ringHalfWidth * (1.10D + 0.18D * voidSize)),
			this.upperDepth * (1.52D + 0.18D * voidSize),
			this.lowerDepth * 0.72D
		) * 1.85D;
		field -= this.sampleCentralBasin(x, y, z) * (this.erosionStrength * 0.18D);
		return field;
	}

	private double sampleSpireGarden(double x, double y, double z) {
		double verticalScale = this.verticalScale();
		int spireCount = clampToInt(
			(int) Math.round(this.presetPrimary),
			SanctuaryWorldgenOptions.MIN_SPIRE_COUNT,
			SanctuaryWorldgenOptions.MAX_SPIRE_COUNT
		);
		double spireHeight = clamp(
			this.presetSecondary,
			SanctuaryWorldgenOptions.MIN_SPIRE_HEIGHT,
			SanctuaryWorldgenOptions.MAX_SPIRE_HEIGHT
		);
		double field = sampleIsland(
			x,
			y,
			z,
			0.0D,
			this.centerY + 2.0D,
			0.0D,
			this.coreRadius * 0.36D,
			this.upperDepth * 0.70D,
			this.lowerDepth * 0.34D,
			0.04D
		);
		field = Math.max(field, this.sampleIslandRing(
			x,
			y,
			z,
			3,
			this.coreRadius * 0.65D,
			this.plateauRadius * 0.64D,
			0.18D,
			0.78D,
			0.40D,
			-2.0D
		));
		double baseSpireHeight = (36.0D + 28.0D * spireHeight) * clamp(0.80D + verticalScale * 0.30D, 0.70D, 1.55D);
		field = Math.max(field, this.sampleSpireRing(
			x,
			y,
			z,
			spireCount,
			(this.coreRadius * 0.92D + this.plateauRadius * 1.22D) * 0.86D,
			Math.max(4.2D, this.cavernRadius * 0.34D),
			baseSpireHeight,
			this.lowerDepth * 0.30D,
			0.14D
		));
		field -= this.sampleRadialVoid(x, y, z, this.coreRadius * 0.22D, this.upperDepth * 0.85D, this.lowerDepth * 0.20D) * 0.18D;
		return field;
	}

	private double sampleIslandRing(
		double x,
		double y,
		double z,
		int islandCount,
		double ringDistance,
		double islandRadius,
		double phase,
		double topScale,
		double bottomScale,
		double verticalOffset
	) {
		double field = -2.25D;
		for (int i = 0; i < islandCount; i++) {
			double islandAngle = phase + i * (Math.PI * 2.0D / islandCount) + 0.16D * Math.sin(i * 1.37D);
			double islandDistance = ringDistance * (0.88D + 0.08D * Math.cos(i * 1.81D));
			double islandX = Math.cos(islandAngle) * islandDistance;
			double islandZ = Math.sin(islandAngle) * islandDistance;
			double islandY = this.centerY
				+ verticalOffset
				+ (8.0D * this.verticalVariance()) * Math.sin(islandAngle * 2.0D)
				+ (4.0D * this.verticalVariance()) * Math.cos(i * 1.29D);
			double radius = islandRadius * (0.88D + 0.14D * ((i & 1) == 0 ? 1.0D : 0.0D));
			double islandField = sampleIsland(
				x,
				y,
				z,
				islandX,
				islandY,
				islandZ,
				radius,
				this.upperDepth * topScale * this.verticalScale() * (0.92D + 0.08D * (i % 3)),
				this.lowerDepth * bottomScale * (0.90D + 0.08D * ((i + 1) % 2)),
				0.08D
			);
			field = Math.max(field, islandField);
		}

		return field;
	}

	private double sampleSpireRing(
		double x,
		double y,
		double z,
		int spikeCount,
		double ringDistance,
		double radius,
		double height,
		double rootDepth,
		double phase
	) {
		double field = -2.25D;
		for (int i = 0; i < spikeCount; i++) {
			double spikeAngle = phase + i * (Math.PI * 2.0D / spikeCount) + 0.12D * Math.cos(i * 1.93D);
			double spikeDistance = ringDistance * (0.84D + 0.12D * Math.sin(i * 1.21D));
			double spikeX = Math.cos(spikeAngle) * spikeDistance;
			double spikeZ = Math.sin(spikeAngle) * spikeDistance;
			double spikeRadius = Math.max(4.0D, radius * (0.84D + 0.12D * (i % 3)));
			double spikeHeight = height * this.verticalScale() * (0.90D + 0.10D * ((i + 1) % 4));
			double spikeField = sampleSpire(
				x,
				y,
				z,
				spikeX,
				this.centerY + 14.0D + (5.0D * this.verticalVariance()) * Math.sin(i * 1.17D),
				spikeZ,
				spikeRadius,
				spikeHeight,
				rootDepth * (0.92D + 0.08D * (i % 2))
			);
			field = Math.max(field, spikeField);
		}

		return field * this.cavernStrength;
	}

	private double sampleCentralBasin(double x, double y, double z) {
		double radial = Math.sqrt(x * x + z * z);
		double horizontal = 1.0D - radial / Math.max(12.0D, this.plateauRadius * 1.25D);
		if (horizontal <= 0.0D) {
			return 0.0D;
		}

		double vertical = 1.0D - Math.abs(y - (this.centerY + this.upperDepth * 0.14D)) / Math.max(18.0D, this.upperDepth * 1.45D);
		return Math.max(0.0D, Math.min(horizontal, vertical));
	}

	private double sampleRadialVoid(double x, double y, double z, double radius, double topDepth, double bottomDepth) {
		double radial = Math.sqrt(x * x + z * z);
		double horizontal = 1.0D - radial / Math.max(8.0D, radius);
		if (horizontal <= 0.0D) {
			return 0.0D;
		}

		double center = this.centerY + this.cavernCenterOffset + this.upperDepth * 0.12D;
		double above = 1.0D - Math.max(0.0D, y - center) / Math.max(12.0D, topDepth);
		double below = 1.0D - Math.max(0.0D, center - y) / Math.max(16.0D, bottomDepth);
		return Math.max(0.0D, Math.min(horizontal, Math.min(above, below)));
	}

	private double sampleRavineCarve(double x, double y, double z) {
		double vertical = 1.0D - Math.abs(y - (this.centerY + this.upperDepth * 0.04D)) / Math.max(20.0D, this.upperDepth * 1.15D);
		if (vertical <= 0.0D) {
			return 0.0D;
		}

		double channelWidth = Math.max(12.0D, this.cavernRadius * 1.20D);
		double channelA = 1.0D - Math.abs(x * 0.55D + z) / channelWidth;
		double channelB = 1.0D - Math.abs(x - z * 0.58D) / channelWidth;
		return Math.max(0.0D, Math.max(channelA, channelB) * vertical);
	}

	private double getInfluenceRadius() {
		double presetInfluence = switch (this.mainIslandPreset) {
			case MONOLITH -> this.coreRadius * 1.65D + this.plateauRadius * 1.90D;
			case FRACTURED_ARCHIPELAGO -> {
				double ringRadius = clamp(
					this.presetSecondary,
					SanctuaryWorldgenOptions.MIN_FRACTURED_RING_RADIUS,
					SanctuaryWorldgenOptions.MAX_FRACTURED_RING_RADIUS
				);
				yield (this.coreRadius * 0.70D + this.plateauRadius * 1.30D) * ringRadius + this.coreRadius * 0.60D;
			}
			case HOLLOW_CROWN -> {
				double voidSize = clamp(
					this.presetPrimary,
					SanctuaryWorldgenOptions.MIN_HOLLOW_VOID_SIZE,
					SanctuaryWorldgenOptions.MAX_HOLLOW_VOID_SIZE
				);
				double ringThickness = clamp(
					this.presetSecondary,
					SanctuaryWorldgenOptions.MIN_HOLLOW_RING_THICKNESS,
					SanctuaryWorldgenOptions.MAX_HOLLOW_RING_THICKNESS
				);
				double crownRadius = this.coreRadius * (0.82D + 0.46D * voidSize) + this.plateauRadius * (0.72D + 0.30D * ringThickness);
				yield crownRadius + this.plateauRadius * (0.72D + 0.34D * ringThickness);
			}
			case SPIRE_GARDEN -> (this.coreRadius * 0.92D + this.plateauRadius * 1.22D) * 0.86D + Math.max(6.0D, this.cavernRadius * 0.40D);
		};
		return presetInfluence + this.shardRadius * 0.90D;
	}

	private static double sampleIsland(
		double x,
		double y,
		double z,
		double centerX,
		double centerY,
		double centerZ,
		double radius,
		double topDepth,
		double bottomDepth,
		double rippleStrength
	) {
		double dx = x - centerX;
		double dz = z - centerZ;
		double dy = y - centerY;
		double effectiveRadius = Math.max(6.0D, radius);
		double radial = Math.sqrt(dx * dx + dz * dz);
		double radialRatio = radial / effectiveRadius;
		if (radialRatio >= 1.0D) {
			return -2.25D;
		}

		double horizontal = 1.0D - radialRatio;
		double shoulder = Math.pow(horizontal, 0.72D);
		double underside = Math.pow(horizontal, 1.44D);
		double ripple = rippleStrength * 0.46D * (Math.sin(dx * 0.045D) + Math.cos(dz * 0.043D));
		double contour = sampleOrganicDetail(dx, dy, dz);
		double erosion = sampleErosionNoise(dx, dy, dz);
		double pocketCarve = samplePocketNoise(dx, dy, dz, 0.40D / effectiveRadius) * (0.08D + (1.0D - horizontal) * 0.18D);
		double roofHeight = topDepth * (0.18D + shoulder * (0.92D + contour * 0.18D)) + ripple * topDepth;
		double floorDepth = bottomDepth * (0.07D + underside * (1.18D - contour * 0.12D)) - ripple * bottomDepth * 0.10D;
		double roof = (centerY + roofHeight - y) / Math.max(10.0D, topDepth);
		double floor = (y - (centerY - floorDepth)) / Math.max(16.0D, bottomDepth);
		double side = horizontal * (0.88D + 0.14D * shoulder + contour * 0.08D) - radialRatio * 0.10D + ripple * 0.08D;
		side -= Math.max(0.0D, erosion - 0.58D) * (0.08D + (1.0D - horizontal) * 0.18D);
		return Math.min(side, Math.min(roof, floor)) - pocketCarve;
	}

	private static double sampleRingIsland(
		double x,
		double y,
		double z,
		double centerX,
		double centerY,
		double centerZ,
		double ringRadius,
		double ringHalfWidth,
		double topDepth,
		double bottomDepth,
		double rippleStrength
	) {
		double dx = x - centerX;
		double dz = z - centerZ;
		double dy = y - centerY;
		double radial = Math.sqrt(dx * dx + dz * dz);
		double normalized = Math.abs(radial - ringRadius) / Math.max(4.0D, ringHalfWidth);
		if (normalized >= 1.0D) {
			return -2.25D;
		}

		double horizontal = 1.0D - normalized;
		double shoulder = Math.pow(horizontal, 0.72D);
		double underside = Math.pow(horizontal, 1.34D);
		double ripple = rippleStrength * 0.42D * (
			Math.sin(dx * 0.040D + dz * 0.014D)
				+ Math.cos(dz * 0.038D - dx * 0.010D)
		);
		double contour = sampleOrganicDetail(dx, dy, dz);
		double erosion = sampleErosionNoise(dx, dy, dz);
		double roofHeight = topDepth * (0.18D + shoulder * (0.86D + contour * 0.16D)) + ripple * topDepth;
		double floorDepth = bottomDepth * (0.10D + underside * (1.12D - contour * 0.08D)) - ripple * bottomDepth * 0.08D;
		double roof = (centerY + roofHeight - y) / Math.max(10.0D, topDepth);
		double floor = (y - (centerY - floorDepth)) / Math.max(16.0D, bottomDepth);
		double side = horizontal * (0.90D + 0.14D * shoulder + contour * 0.06D) - normalized * 0.12D;
		side -= Math.max(0.0D, erosion - 0.62D) * 0.14D;
		return Math.min(side, Math.min(roof, floor));
	}

	private static double sampleSpire(
		double x,
		double y,
		double z,
		double centerX,
		double baseY,
		double centerZ,
		double radius,
		double height,
		double rootDepth
	) {
		double dx = x - centerX;
		double dz = z - centerZ;
		double radial = Math.sqrt(dx * dx + dz * dz);
		double horizontal = 1.0D - radial / Math.max(4.0D, radius);
		if (horizontal <= 0.0D) {
			return -2.25D;
		}

		double positiveHorizontal = Math.max(0.0D, horizontal);
		double roof = (baseY + height * (0.35D + positiveHorizontal * 1.05D) - y) / Math.max(18.0D, height);
		double floor = (y - (baseY - rootDepth * (0.55D + positiveHorizontal * 0.35D))) / Math.max(12.0D, rootDepth);
		return Math.min(horizontal * 1.18D, Math.min(roof, floor));
	}

	private static double sampleDirectionalGroove(
		double x,
		double y,
		double z,
		double centerY,
		double verticalRange,
		double width,
		double dirX,
		double dirZ,
		double offset
	) {
		double length = Math.sqrt(dirX * dirX + dirZ * dirZ);
		if (length <= 1.0E-6D) {
			return 0.0D;
		}

		double normX = dirX / length;
		double normZ = dirZ / length;
		double lateral = (-normZ * x + normX * z) + offset * width;
		double along = normX * x + normZ * z;
		double horizontal = 1.0D - Math.abs(lateral - 10.0D * Math.sin(along * 0.015D)) / Math.max(8.0D, width);
		if (horizontal <= 0.0D) {
			return 0.0D;
		}

		double vertical = 1.0D - Math.abs(y - centerY) / Math.max(18.0D, verticalRange);
		return Math.max(0.0D, horizontal * vertical);
	}

	private static double sampleOrganicDetail(double dx, double dy, double dz) {
		return 0.48D * Math.sin(dx * 0.019D + dy * 0.031D)
			+ 0.32D * Math.cos(dz * 0.023D - dy * 0.027D)
			+ 0.20D * Math.sin((dx - dz) * 0.014D + dy * 0.017D);
	}

	private static double sampleErosionNoise(double dx, double dy, double dz) {
		return 0.5D + 0.5D * (
			0.56D * Math.sin(dx * 0.010D + dz * 0.007D)
				+ 0.29D * Math.cos(dz * 0.013D - dy * 0.022D)
				+ 0.15D * Math.sin((dx + dz) * 0.021D)
		);
	}

	private static double samplePocketNoise(double dx, double dy, double dz, double scale) {
		double sample = 0.52D * Math.sin((dx + dz) * scale)
			+ 0.31D * Math.cos((dx - dz) * scale * 1.6D - dy * scale * 1.9D)
			+ 0.17D * Math.sin(dy * scale * 2.3D + dz * scale * 0.8D);
		return Math.max(0.0D, sample - 0.58D);
	}

	private static double resolvePresetPrimary(SanctuaryWorldgenOptions options) {
		return switch (options.mainIslandPreset()) {
			case MONOLITH -> options.monolithMassHeight();
			case FRACTURED_ARCHIPELAGO -> options.fracturedIslandCount();
			case HOLLOW_CROWN -> options.hollowVoidSize();
			case SPIRE_GARDEN -> options.spireCount();
		};
	}

	private static double resolvePresetSecondary(SanctuaryWorldgenOptions options) {
		return switch (options.mainIslandPreset()) {
			case MONOLITH -> options.monolithShoulderWidth();
			case FRACTURED_ARCHIPELAGO -> options.fracturedRingRadius();
			case HOLLOW_CROWN -> options.hollowRingThickness();
			case SPIRE_GARDEN -> options.spireHeight();
		};
	}

	private static double smoothstep(double edge0, double edge1, double value) {
		double t = clamp((value - edge0) / (edge1 - edge0), 0.0D, 1.0D);
		return t * t * (3.0D - 2.0D * t);
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	private static int clampToInt(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	private double verticalScale() {
		return switch (this.verticalityProfile) {
			case PLATEAU -> 0.78D;
			case BALANCED -> 1.0D;
			case VARIED -> 1.28D;
		};
	}

	private double verticalVariance() {
		return switch (this.verticalityProfile) {
			case PLATEAU -> 0.35D;
			case BALANCED -> 1.0D;
			case VARIED -> 1.50D;
		};
	}

	private PresetData presetData() {
		return new PresetData(this.mainIslandPreset, this.presetPrimary, this.presetSecondary);
	}

	private StyleData styleData() {
		return new StyleData(this.satelliteIslands, this.verticalityProfile);
	}

	private record PresetData(MainIslandPreset mainIslandPreset, double presetPrimary, double presetSecondary) {
		private static final PresetData DEFAULT = new PresetData(
			SanctuaryWorldgenOptions.DEFAULT_MAIN_ISLAND_PRESET,
			DEFAULT_PRESET_PRIMARY,
			DEFAULT_PRESET_SECONDARY
		);

		private static final com.mojang.serialization.Codec<PresetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MainIslandPreset.CODEC.optionalFieldOf("main_island_preset", SanctuaryWorldgenOptions.DEFAULT_MAIN_ISLAND_PRESET).forGetter(PresetData::mainIslandPreset),
			com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("preset_primary", DEFAULT_PRESET_PRIMARY).forGetter(PresetData::presetPrimary),
			com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("preset_secondary", DEFAULT_PRESET_SECONDARY).forGetter(PresetData::presetSecondary)
		).apply(instance, PresetData::new));
	}

	private record StyleData(boolean satelliteIslands, VerticalityProfile verticalityProfile) {
		private static final StyleData DEFAULT = new StyleData(
			SanctuaryWorldgenOptions.DEFAULT_SATELLITE_ISLANDS,
			SanctuaryWorldgenOptions.DEFAULT_VERTICALITY_PROFILE
		);

		private static final com.mojang.serialization.Codec<StyleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			com.mojang.serialization.Codec.BOOL.optionalFieldOf("satellite_islands", SanctuaryWorldgenOptions.DEFAULT_SATELLITE_ISLANDS).forGetter(StyleData::satelliteIslands),
			VerticalityProfile.CODEC.optionalFieldOf("verticality_profile", SanctuaryWorldgenOptions.DEFAULT_VERTICALITY_PROFILE).forGetter(StyleData::verticalityProfile)
		).apply(instance, StyleData::new));
	}
}
