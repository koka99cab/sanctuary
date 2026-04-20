package com.koka.sanctuary.worldgen;

import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public final class SanctuaryWorldgen {
	public static final double ADVANCED_WORLDGEN_START_DISTANCE = 10_000_000.0D;
	public static final double ADVANCED_WORLDGEN_TRANSITION_WIDTH = 4_096.0D;
	private static final double GAP_NOISE_MIN_DISTANCE = 96.0D;
	private static final double GAP_NOISE_FULL_DISTANCE = 640.0D;
	private static final double GAP_NOISE_MIN_AMPLITUDE = 26.0D;
	private static final double GAP_NOISE_MAX_AMPLITUDE = 118.0D;

	private SanctuaryWorldgen() {
	}

	public static boolean isSanctuaryDimensions(DimensionOptionsRegistryHolder dimensions) {
		return isSanctuaryGenerator(dimensions.getChunkGenerator());
	}

	public static boolean isSanctuaryGenerator(ChunkGenerator generator) {
		return generator instanceof NoiseChunkGenerator noiseGenerator
			&& noiseGenerator.getBiomeSource() instanceof SanctuaryBiomeSource;
	}

	public static SanctuaryBiomeSource getSanctuaryBiomeSource(ChunkGenerator generator) {
		if (generator instanceof NoiseChunkGenerator noiseGenerator
			&& noiseGenerator.getBiomeSource() instanceof SanctuaryBiomeSource sanctuaryBiomeSource) {
			return sanctuaryBiomeSource;
		}

		throw new IllegalArgumentException("Chunk generator is not a Sanctuary generator");
	}

	public static boolean usesAdvancedWorldgen(double distanceFromOrigin) {
		return distanceFromOrigin >= ADVANCED_WORLDGEN_START_DISTANCE;
	}

	public static double distanceToOrigin(double blockX, double blockZ) {
		return Math.sqrt(blockX * blockX + blockZ * blockZ);
	}

	public static double noisyDistanceToOrigin(double blockX, double blockZ) {
		double distance = distanceToOrigin(blockX, blockZ);
		if (distance <= GAP_NOISE_MIN_DISTANCE) {
			return distance;
		}

		double envelope = smoothstep(GAP_NOISE_MIN_DISTANCE, GAP_NOISE_FULL_DISTANCE, distance);
		double amplitude = lerp(GAP_NOISE_MIN_AMPLITUDE, GAP_NOISE_MAX_AMPLITUDE, envelope);
		double noise = sampleGapNoise(blockX, blockZ);
		return Math.max(0.0D, distance + amplitude * noise);
	}

	private static double sampleGapNoise(double blockX, double blockZ) {
		double primary = Math.sin(blockX * 0.0068D + blockZ * 0.0047D);
		double secondary = Math.cos(blockZ * 0.0059D - blockX * 0.0042D);
		double tertiary = Math.sin((blockX - blockZ) * 0.0031D + Math.cos((blockX + blockZ) * 0.0018D));
		return primary * 0.55D + secondary * 0.30D + tertiary * 0.15D;
	}

	private static double smoothstep(double edge0, double edge1, double value) {
		double t = clamp((value - edge0) / (edge1 - edge0), 0.0D, 1.0D);
		return t * t * (3.0D - 2.0D * t);
	}

	private static double lerp(double start, double end, double t) {
		return start + (end - start) * t;
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	public static SanctuaryShellCoordinates projectToAdvancedShell(double blockX, double blockZ) {
		double worldDistance = distanceToOrigin(blockX, blockZ);
		double angle = Math.atan2(blockZ, blockX);
		double localDistance = Math.max(0.0D, worldDistance - ADVANCED_WORLDGEN_START_DISTANCE);
		double localBlockX = Math.cos(angle) * localDistance;
		double localBlockZ = Math.sin(angle) * localDistance;
		return new SanctuaryShellCoordinates(worldDistance, localDistance, localBlockX, localBlockZ);
	}
}
