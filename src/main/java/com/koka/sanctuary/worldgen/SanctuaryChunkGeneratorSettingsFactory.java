package com.koka.sanctuary.worldgen;

import com.koka.sanctuary.SanctuaryMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public final class SanctuaryChunkGeneratorSettingsFactory {
	private static final double EPSILON = 1.0E-4D;
	private static final double MIN_VANILLA_ISLANDS_BLEND_WIDTH = 256.0D;
	private static final double MAX_VANILLA_ISLANDS_BLEND_WIDTH = 896.0D;
	private static final YOffset DEEPSLATE_CORE_Y = YOffset.fixed(104);
	private static final RegistryKey<DensityFunction> END_BASE_3D_NOISE_KEY = RegistryKey.of(
		RegistryKeys.DENSITY_FUNCTION,
		Identifier.ofVanilla("end/base_3d_noise")
	);

	private SanctuaryChunkGeneratorSettingsFactory() {
	}

	public static NoiseChunkGenerator createGenerator(
		DynamicRegistryManager.Immutable registryManager,
		DimensionOptionsRegistryHolder selectedDimensions,
		SanctuaryWorldgenOptions options
	) {
		if (!(selectedDimensions.getChunkGenerator() instanceof NoiseChunkGenerator currentGenerator)) {
			throw new IllegalArgumentException("Selected dimensions do not use a noise chunk generator");
		}

		SanctuaryBiomeSource currentSource = SanctuaryWorldgen.getSanctuaryBiomeSource(currentGenerator);
		SanctuaryWorldgenOptions sanitizedOptions = options.sanitized(currentSource.meadow());
		RegistryEntry<ChunkGeneratorSettings> baseSettings = resolveBaseSettings(registryManager, currentGenerator);
		RegistryEntry<ChunkGeneratorSettings> effectiveSettings = hasCustomTerrainTuning(sanitizedOptions)
			? RegistryEntry.of(createCustomSettings(baseSettings.value(), sanitizedOptions))
			: baseSettings;
		return new NoiseChunkGenerator(currentSource.withOptions(sanitizedOptions), effectiveSettings);
	}

	private static RegistryEntry<ChunkGeneratorSettings> resolveBaseSettings(
		DynamicRegistryManager.Immutable registryManager,
		NoiseChunkGenerator currentGenerator
	) {
		Registry<ChunkGeneratorSettings> settingsRegistry = registryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
		return settingsRegistry
			.getOptional(SanctuaryMod.SANCTUARY_NOISE_SETTINGS)
			.map(entry -> (RegistryEntry<ChunkGeneratorSettings>) entry)
			.orElse(currentGenerator.getSettings());
	}

	private static ChunkGeneratorSettings createCustomSettings(
		ChunkGeneratorSettings baseSettings,
		SanctuaryWorldgenOptions options
	) {
		NoiseRouter adjustedRouter = createCustomNoiseRouter(baseSettings.noiseRouter(), options);
		BlockState defaultBlock = baseSettings.defaultBlock();
		MaterialRules.MaterialRule surfaceRule = createSurfaceRule(baseSettings.surfaceRule(), options);
		return new ChunkGeneratorSettings(
			baseSettings.generationShapeConfig(),
			defaultBlock,
			baseSettings.defaultFluid(),
			adjustedRouter,
			surfaceRule,
			baseSettings.spawnTarget(),
			baseSettings.seaLevel(),
			baseSettings.mobGenerationDisabled(),
			baseSettings.aquifers(),
			baseSettings.oreVeins(),
			baseSettings.usesLegacyRandom()
		);
	}

	private static MaterialRules.MaterialRule createSurfaceRule(MaterialRules.MaterialRule baseRule, SanctuaryWorldgenOptions options) {
		if (!options.deepslateCore()) {
			return baseRule;
		}

		return MaterialRules.sequence(
			MaterialRules.condition(
				MaterialRules.not(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH),
				MaterialRules.condition(
					MaterialRules.not(MaterialRules.aboveYWithStoneDepth(DEEPSLATE_CORE_Y, 0)),
					MaterialRules.block(Blocks.DEEPSLATE.getDefaultState())
				)
			),
			baseRule
		);
	}

	private static NoiseRouter createCustomNoiseRouter(NoiseRouter baseRouter, SanctuaryWorldgenOptions options) {
		NoiseRouter scaledRouter = baseRouter.apply(new DensityFunction.DensityFunctionVisitor() {
			@Override
			public DensityFunction apply(DensityFunction densityFunction) {
				if (densityFunction instanceof OriginIslandDensityFunction origin) {
					return origin.radialOffset() > 0.0D
						? OriginIslandDensityFunction.createShellTuned(origin.radialOffset(), options)
						: OriginIslandDensityFunction.createTuned(options);
				}

				return densityFunction;
			}
		});

		DensityFunction adjustedFinalDensity = scaledRouter.finalDensity().apply(new DensityFunction.DensityFunctionVisitor() {
			@Override
			public DensityFunction apply(DensityFunction densityFunction) {
				if (densityFunction instanceof DensityFunctionTypes.RegistryEntryHolder holder
					&& holder.function().matchesKey(END_BASE_3D_NOISE_KEY)) {
					return gateVanillaFloatingIslands(holder, options);
				}

				return densityFunction;
			}
		});
		double archipelagoMultiplier = clamp(
			1.0D + (options.archipelagoSize() - 1.0D) * 0.32D - options.archipelagoSpacing() * 0.12D,
			0.75D,
			1.35D
		);
		double archipelagoBias = clamp(
			(options.archipelagoSize() - 1.0D) * 0.12D - options.archipelagoSpacing() * 0.28D,
			-0.35D,
			0.28D
		);
		if (Math.abs(archipelagoMultiplier - 1.0D) > EPSILON) {
			adjustedFinalDensity = DensityFunctionTypes.mul(
				adjustedFinalDensity,
				DensityFunctionTypes.constant(archipelagoMultiplier)
			);
		}

		if (Math.abs(archipelagoBias) > EPSILON) {
			adjustedFinalDensity = DensityFunctionTypes.add(
				adjustedFinalDensity,
				DensityFunctionTypes.constant(archipelagoBias)
			);
		}

		return new NoiseRouter(
			scaledRouter.barrierNoise(),
			scaledRouter.fluidLevelFloodednessNoise(),
			scaledRouter.fluidLevelSpreadNoise(),
			scaledRouter.lavaNoise(),
			scaledRouter.temperature(),
			scaledRouter.vegetation(),
			scaledRouter.continents(),
			scaledRouter.erosion(),
			scaledRouter.depth(),
			scaledRouter.ridges(),
			scaledRouter.preliminarySurfaceLevel(),
			adjustedFinalDensity,
			scaledRouter.veinToggle(),
			scaledRouter.veinRidged(),
			scaledRouter.veinGap()
		);
	}

	private static boolean hasCustomTerrainTuning(SanctuaryWorldgenOptions options) {
		return Math.abs(options.starterIslandScale() - SanctuaryWorldgenOptions.DEFAULT_STARTER_ISLAND_SCALE) > EPSILON
			|| Math.abs(options.archipelagoSize() - SanctuaryWorldgenOptions.DEFAULT_ARCHIPELAGO_SIZE) > EPSILON
			|| Math.abs(options.archipelagoSpacing() - SanctuaryWorldgenOptions.DEFAULT_ARCHIPELAGO_SPACING) > EPSILON
			|| options.effectiveVanillaFloatingIslandsStartRadius() != SanctuaryWorldgenOptions.DEFAULT_VANILLA_FLOATING_ISLANDS_START_RADIUS
			|| options.satelliteIslands() != SanctuaryWorldgenOptions.DEFAULT_SATELLITE_ISLANDS
			|| options.verticalityProfile() != SanctuaryWorldgenOptions.DEFAULT_VERTICALITY_PROFILE
			|| options.mainIslandPreset() != SanctuaryWorldgenOptions.DEFAULT_MAIN_ISLAND_PRESET
			|| Math.abs(options.monolithMassHeight() - SanctuaryWorldgenOptions.DEFAULT_MONOLITH_MASS_HEIGHT) > EPSILON
			|| Math.abs(options.monolithShoulderWidth() - SanctuaryWorldgenOptions.DEFAULT_MONOLITH_SHOULDER_WIDTH) > EPSILON
			|| options.fracturedIslandCount() != SanctuaryWorldgenOptions.DEFAULT_FRACTURED_ISLAND_COUNT
			|| Math.abs(options.fracturedRingRadius() - SanctuaryWorldgenOptions.DEFAULT_FRACTURED_RING_RADIUS) > EPSILON
			|| Math.abs(options.hollowVoidSize() - SanctuaryWorldgenOptions.DEFAULT_HOLLOW_VOID_SIZE) > EPSILON
			|| Math.abs(options.hollowRingThickness() - SanctuaryWorldgenOptions.DEFAULT_HOLLOW_RING_THICKNESS) > EPSILON
			|| options.spireCount() != SanctuaryWorldgenOptions.DEFAULT_SPIRE_COUNT
			|| Math.abs(options.spireHeight() - SanctuaryWorldgenOptions.DEFAULT_SPIRE_HEIGHT) > EPSILON
			|| options.deepslateCore() != SanctuaryWorldgenOptions.DEFAULT_DEEPSLATE_CORE;
	}

	private static DensityFunction gateVanillaFloatingIslands(DensityFunction densityFunction, SanctuaryWorldgenOptions options) {
		int startRadius = options.effectiveVanillaFloatingIslandsStartRadius();
		if (startRadius <= 0) {
			return densityFunction;
		}

		double blendWidth = clamp(startRadius * 0.42D, MIN_VANILLA_ISLANDS_BLEND_WIDTH, MAX_VANILLA_ISLANDS_BLEND_WIDTH);
		return DensityFunctionTypes.mul(
			densityFunction,
			new OriginDistanceRampDensityFunction(startRadius, startRadius + blendWidth)
		);
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}
}
