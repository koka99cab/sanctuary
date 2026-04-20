package com.koka.sanctuary.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public final class SanctuaryStructureRules {
	private static final int[][] LOCAL_SUPPORT_SAMPLE_OFFSETS = {
		{4, 4},
		{12, 4},
		{4, 12},
		{12, 12},
		{8, 8}
	};
	private static final int[][] BROAD_SUPPORT_SAMPLE_OFFSETS = {
		{-8, -8},
		{8, -8},
		{24, -8},
		{-8, 8},
		{8, 8},
		{24, 8},
		{-8, 24},
		{8, 24},
		{24, 24}
	};
	private static final int MIN_TERRAIN_HEIGHT_ABOVE_BOTTOM = 16;
	private static final int SUPPORT_SCAN_DEPTH = 48;
	private static final StructureProfile DEFAULT_PROFILE = new StructureProfile(
		SampleLayout.LOCAL,
		AnchorMode.KEEP,
		2,
		1,
		24,
		6,
		14,
		0,
		4,
		0,
		2,
		6,
		64
	);
	private static final StructureProfile SETTLEMENT_PROFILE = new StructureProfile(
		SampleLayout.BROAD,
		AnchorMode.KEEP,
		6,
		0,
		56,
		8,
		12,
		0,
		4,
		0,
		1,
		8,
		28
	);
	private static final StructureProfile DEEP_JIGSAW_PROFILE = new StructureProfile(
		SampleLayout.BROAD,
		AnchorMode.KEEP,
		7,
		4,
		120,
		14,
		24,
		0,
		8,
		0,
		1,
		14,
		52
	);
	private static final StructureProfile MONUMENT_PROFILE = new StructureProfile(
		SampleLayout.BROAD,
		AnchorMode.KEEP,
		5,
		1,
		60,
		8,
		14,
		0,
		5,
		0,
		1,
		9,
		40
	);
	private static final StructureProfile PORTAL_PROFILE = new StructureProfile(
		SampleLayout.LOCAL,
		AnchorMode.KEEP,
		3,
		0,
		32,
		7,
		12,
		0,
		4,
		0,
		1,
		8,
		40
	);
	private static final StructureProfile MINESHAFT_PROFILE = new StructureProfile(
		SampleLayout.BROAD,
		AnchorMode.INTERIOR,
		5,
		4,
		110,
		14,
		24,
		20,
		10,
		96,
		0,
		14,
		48
	);
	private static final StructureProfile STRONGHOLD_PROFILE = new StructureProfile(
		SampleLayout.BROAD,
		AnchorMode.INTERIOR,
		4,
		3,
		96,
		12,
		20,
		18,
		8,
		72,
		0,
		12,
		52
	);
	private static final StructureProfile BURIED_PROFILE = new StructureProfile(
		SampleLayout.LOCAL,
		AnchorMode.INTERIOR,
		3,
		0,
		20,
		6,
		10,
		4,
		3,
		20,
		0,
		6,
		24
	);

	private SanctuaryStructureRules() {
	}

	public static Optional<Structure.StructurePosition> adapt(Structure structure, Structure.Context context, Structure.StructurePosition position) {
		if (!SanctuaryWorldgen.isSanctuaryGenerator(context.chunkGenerator())) {
			return Optional.of(position);
		}

		StructureProfile profile = profileFor(structure, position);
		TerrainReport terrain = analyzeTerrain(context, profile);
		if (!terrain.isSupported(profile)) {
			return Optional.empty();
		}

		BlockPos originalPos = position.position();
		int adjustedY = switch (profile.anchorMode()) {
			case KEEP -> originalPos.getY();
			case INTERIOR -> computeInteriorAnchorY(profile, terrain, originalPos.getY());
		};
		if (adjustedY == originalPos.getY()) {
			return Optional.of(position);
		}

		BlockPos adjustedPos = new BlockPos(originalPos.getX(), adjustedY, originalPos.getZ());
		return Optional.of(new Structure.StructurePosition(adjustedPos, position.generator()));
	}

	private static StructureProfile profileFor(Structure structure, Structure.StructurePosition position) {
		StructureType<?> type = structure.getType();
		if (type == StructureType.MINESHAFT) {
			return MINESHAFT_PROFILE;
		}
		if (type == StructureType.STRONGHOLD) {
			return STRONGHOLD_PROFILE;
		}
		if (type == StructureType.BURIED_TREASURE) {
			return BURIED_PROFILE;
		}
		if (type == StructureType.JIGSAW) {
			if (position.position().getY() < 48) {
				return DEEP_JIGSAW_PROFILE;
			}
			return SETTLEMENT_PROFILE;
		}
		if (
			type == StructureType.DESERT_PYRAMID
				|| type == StructureType.JUNGLE_TEMPLE
				|| type == StructureType.IGLOO
				|| type == StructureType.SWAMP_HUT
				|| type == StructureType.WOODLAND_MANSION
				|| type == StructureType.OCEAN_MONUMENT
				|| type == StructureType.OCEAN_RUIN
				|| type == StructureType.SHIPWRECK
		) {
			return MONUMENT_PROFILE;
		}
		if (type == StructureType.RUINED_PORTAL) {
			return PORTAL_PROFILE;
		}

		return DEFAULT_PROFILE;
	}

	private static TerrainReport analyzeTerrain(Structure.Context context, StructureProfile profile) {
		ChunkGenerator chunkGenerator = context.chunkGenerator();
		ChunkPos chunkPos = context.chunkPos();
		HeightLimitView world = context.world();
		NoiseConfig noiseConfig = context.noiseConfig();
		int[][] sampleOffsets = offsetsFor(profile.sampleLayout());
		int bottomY = world.getBottomY();
		List<TerrainSample> samples = new ArrayList<>(sampleOffsets.length);
		int supportedColumns = 0;
		int deepColumns = 0;
		int totalThickness = 0;
		int surfaceYSum = 0;
		int surfaceYCount = 0;
		int voidColumns = 0;
		int minSurfaceY = Integer.MAX_VALUE;
		int maxSurfaceY = Integer.MIN_VALUE;
		TerrainSample bestSurfaceSample = null;
		TerrainSample bestDeepSample = null;

		for (int[] sampleOffset : sampleOffsets) {
			int sampleX = chunkPos.getStartX() + sampleOffset[0];
			int sampleZ = chunkPos.getStartZ() + sampleOffset[1];
			int topY = chunkGenerator.getHeightInGround(sampleX, sampleZ, Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
			if (topY <= bottomY + MIN_TERRAIN_HEIGHT_ABOVE_BOTTOM) {
				voidColumns++;
				continue;
			}

			int thickness = measureSurfaceThickness(chunkGenerator.getColumnSample(sampleX, sampleZ, world, noiseConfig), topY - 1, bottomY);
			TerrainSample sample = new TerrainSample(sampleX, sampleZ, topY, thickness);
			samples.add(sample);
			surfaceYSum += topY;
			surfaceYCount++;
			minSurfaceY = Math.min(minSurfaceY, topY);
			maxSurfaceY = Math.max(maxSurfaceY, topY);

			if (thickness >= profile.minSurfaceThickness()) {
				supportedColumns++;
				if (bestSurfaceSample == null || thickness > bestSurfaceSample.thickness()) {
					bestSurfaceSample = sample;
				}
			}

			if (thickness >= profile.minDeepSurfaceThickness()) {
				deepColumns++;
				if (bestDeepSample == null || thickness > bestDeepSample.thickness()) {
					bestDeepSample = sample;
				}
			}

			totalThickness += thickness;
		}

		int averageSurfaceY = surfaceYCount == 0 ? bottomY : Math.round((float) surfaceYSum / surfaceYCount);
		int averageThickness = samples.isEmpty() ? 0 : Math.round((float) totalThickness / samples.size());
		int surfaceDelta = samples.size() <= 1 ? 0 : maxSurfaceY - minSurfaceY;
		return new TerrainReport(
			bottomY,
			supportedColumns,
			deepColumns,
			totalThickness,
			averageSurfaceY,
			averageThickness,
			voidColumns,
			surfaceDelta,
			bestSurfaceSample,
			bestDeepSample,
			samples
		);
	}

	private static int[][] offsetsFor(SampleLayout layout) {
		return layout == SampleLayout.BROAD ? BROAD_SUPPORT_SAMPLE_OFFSETS : LOCAL_SUPPORT_SAMPLE_OFFSETS;
	}

	private static int computeInteriorAnchorY(StructureProfile profile, TerrainReport terrain, int originalY) {
		TerrainSample anchorSample = terrain.bestDeepSample() != null ? terrain.bestDeepSample() : terrain.bestSurfaceSample();
		if (anchorSample == null) {
			return originalY;
		}

		int maxInset = Math.max(profile.surfaceCover(), anchorSample.thickness() - profile.surfaceCover());
		int inset = Math.min(profile.targetInset(), maxInset);
		int targetY = anchorSample.topY() - inset;
		int maxRaisedY = originalY + profile.maxRaise();
		return Math.min(Math.max(originalY, targetY), maxRaisedY);
	}

	private static int measureSurfaceThickness(VerticalBlockSample sample, int topBlockY, int bottomY) {
		int minY = Math.max(bottomY, topBlockY - SUPPORT_SCAN_DEPTH);
		int thickness = 0;

		for (int y = topBlockY; y >= minY; y--) {
			if (!sample.getState(y).blocksMovement()) {
				break;
			}

			thickness++;
		}

		return thickness;
	}

	private enum SampleLayout {
		LOCAL,
		BROAD
	}

	private enum AnchorMode {
		KEEP,
		INTERIOR
	}

	private record StructureProfile(
		SampleLayout sampleLayout,
		AnchorMode anchorMode,
		int minSupportedColumns,
		int minDeepColumns,
		int minTotalThickness,
		int minSurfaceThickness,
		int minDeepSurfaceThickness,
		int targetInset,
		int surfaceCover,
		int maxRaise,
		int maxVoidColumns,
		int minAverageThickness,
		int maxSurfaceDelta
	) {
	}

	private record TerrainSample(int x, int z, int topY, int thickness) {
	}

	private record TerrainReport(
		int bottomY,
		int supportedColumns,
		int deepColumns,
		int totalThickness,
		int averageSurfaceY,
		int averageThickness,
		int voidColumns,
		int surfaceDelta,
		TerrainSample bestSurfaceSample,
		TerrainSample bestDeepSample,
		List<TerrainSample> samples
	) {
		private boolean isSupported(StructureProfile profile) {
			return supportedColumns >= profile.minSupportedColumns()
				&& (deepColumns >= profile.minDeepColumns() || totalThickness >= profile.minTotalThickness())
				&& voidColumns <= profile.maxVoidColumns()
				&& averageThickness >= profile.minAverageThickness()
				&& surfaceDelta <= profile.maxSurfaceDelta();
		}
	}
}
