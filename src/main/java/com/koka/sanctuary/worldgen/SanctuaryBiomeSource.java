package com.koka.sanctuary.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public final class SanctuaryBiomeSource extends BiomeSource {
	public static final MapCodec<SanctuaryBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		MultiNoiseBiomeSourceParameterList.REGISTRY_CODEC.fieldOf("fallback").forGetter(source -> source.fallback),
		Biome.REGISTRY_CODEC.fieldOf("meadow").forGetter(source -> source.meadow),
		Biome.REGISTRY_CODEC.fieldOf("birch_forest").forGetter(source -> source.birchForest),
		Biome.REGISTRY_CODEC.fieldOf("forest").forGetter(source -> source.forest),
		Biome.REGISTRY_CODEC.fieldOf("flower_forest").forGetter(source -> source.flowerForest),
		Biome.REGISTRY_CODEC.fieldOf("sunflower_plains").forGetter(source -> source.sunflowerPlains),
		Biome.REGISTRY_CODEC.fieldOf("windswept_hills").forGetter(source -> source.windsweptHills),
		SanctuaryWorldgenOptions.CODEC.optionalFieldOf("options").forGetter(source -> Optional.of(source.options)),
		Codec.DOUBLE.optionalFieldOf("fallback_scale", 0.42D).forGetter(source -> source.fallbackScale),
		Codec.DOUBLE.optionalFieldOf("inner_radius", SanctuaryWorldgenOptions.DEFAULT_INNER_RADIUS).forGetter(source -> source.innerRadius),
		Codec.DOUBLE.optionalFieldOf("outer_radius", SanctuaryWorldgenOptions.DEFAULT_OUTER_RADIUS).forGetter(source -> source.outerRadius)
	).apply(instance, (
		fallback,
		meadow,
		birchForest,
		forest,
		flowerForest,
		sunflowerPlains,
		windsweptHills,
		options,
		fallbackScale,
		legacyInnerRadius,
		legacyOuterRadius
	) -> new SanctuaryBiomeSource(
		fallback,
		meadow,
		birchForest,
		forest,
		flowerForest,
		sunflowerPlains,
		windsweptHills,
		options.orElse(SanctuaryWorldgenOptions.fromLegacy(meadow, legacyInnerRadius, legacyOuterRadius)),
		fallbackScale
	)));

	private final RegistryEntry<MultiNoiseBiomeSourceParameterList> fallback;
	private final RegistryEntry<Biome> meadow;
	private final RegistryEntry<Biome> birchForest;
	private final RegistryEntry<Biome> forest;
	private final RegistryEntry<Biome> flowerForest;
	private final RegistryEntry<Biome> sunflowerPlains;
	private final RegistryEntry<Biome> windsweptHills;
	private final SanctuaryWorldgenOptions options;
	private final double innerRadius;
	private final double outerRadius;
	private final double fallbackScale;
	private final MultiNoiseBiomeSource fallbackSource;

	public SanctuaryBiomeSource(
		RegistryEntry<MultiNoiseBiomeSourceParameterList> fallback,
		RegistryEntry<Biome> meadow,
		RegistryEntry<Biome> birchForest,
		RegistryEntry<Biome> forest,
		RegistryEntry<Biome> flowerForest,
		RegistryEntry<Biome> sunflowerPlains,
		RegistryEntry<Biome> windsweptHills,
		SanctuaryWorldgenOptions options,
		double fallbackScale
	) {
		this.fallback = fallback;
		this.meadow = meadow;
		this.birchForest = birchForest;
		this.forest = forest;
		this.flowerForest = flowerForest;
		this.sunflowerPlains = sunflowerPlains;
		this.windsweptHills = windsweptHills;
		this.options = options.sanitized(meadow);
		this.innerRadius = this.options.innerRadius();
		this.outerRadius = Math.max(this.options.outerRadius(), this.innerRadius + 32.0D);
		this.fallbackScale = Math.max(0.1D, Math.min(1.0D, fallbackScale));
		this.fallbackSource = MultiNoiseBiomeSource.create(fallback);
	}

	@Override
	protected MapCodec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	protected Stream<RegistryEntry<Biome>> biomeStream() {
		return Stream.concat(
			Stream.of(this.options.starterBiome(), this.meadow, this.birchForest, this.forest, this.flowerForest, this.sunflowerPlains, this.windsweptHills),
			this.fallbackSource.getBiomes().stream()
		).distinct();
	}

	@Override
	public RegistryEntry<Biome> getBiome(int quartX, int quartY, int quartZ, MultiNoiseUtil.MultiNoiseSampler sampler) {
		int blockX = quartX << 2;
		int blockZ = quartZ << 2;
		return this.sampleSanctuaryBiome(
			quartX,
			quartY,
			quartZ,
			sampler,
			blockX,
			blockZ,
			this.options,
			this.innerRadius,
			this.outerRadius
		);
	}

	private RegistryEntry<Biome> sampleSanctuaryBiome(
		int quartX,
		int quartY,
		int quartZ,
		MultiNoiseUtil.MultiNoiseSampler sampler,
		double localBlockX,
		double localBlockZ,
		SanctuaryWorldgenOptions activeOptions,
		double activeInnerRadius,
		double activeOuterRadius
	) {
		double distance = SanctuaryWorldgen.noisyDistanceToOrigin(localBlockX, localBlockZ);
		double archipelagoSize = activeOptions.archipelagoSize();
		double starterIslandScale = activeOptions.starterIslandScale();
		double archipelagoSpacing = activeOptions.archipelagoSpacing();
		double outerRadiusScale = clamp(
			1.0D - archipelagoSpacing * 0.45D,
			0.72D,
			1.90D
		);
		double activeSanctuaryOuterRadius = Math.max(
			activeOuterRadius * outerRadiusScale,
			activeOptions.effectiveVanillaFloatingIslandsStartRadius() * SanctuaryWorldgenOptions.BIOME_BOUNDARY_FROM_GAP_RATIO
		);
		double activeSanctuaryInnerRadius = Math.min(
			activeSanctuaryOuterRadius - 24.0D,
			activeInnerRadius * clamp(1.0D - archipelagoSpacing * 0.20D, 0.82D, 1.18D)
		);
		double patchFrequencyScale = 1.0D / Math.max(0.35D, archipelagoSize);
		double sampleX = localBlockX * patchFrequencyScale;
		double sampleZ = localBlockZ * patchFrequencyScale;
		double edgeFallbackThreshold = clamp(0.18D + archipelagoSpacing * 0.18D, 0.05D, 0.32D);
		if (distance >= activeSanctuaryOuterRadius) {
			return this.sampleFallbackBiome(quartX, quartY, quartZ, sampler, activeOptions);
		}

		MultiNoiseUtil.NoiseValuePoint point = sampler.sample(quartX, quartY, quartZ);
		double temperature = MultiNoiseUtil.toFloat(point.temperatureNoise());
		double humidity = MultiNoiseUtil.toFloat(point.humidityNoise());
		double sanctuaryBias = 1.0D - smoothstep(activeSanctuaryInnerRadius, activeSanctuaryOuterRadius, distance);

		if (sanctuaryBias <= 0.0D) {
			return this.sampleFallbackBiome(quartX, quartY, quartZ, sampler, activeOptions);
		}

		if (sanctuaryBias < edgeFallbackThreshold) {
			return this.sampleFallbackBiome(quartX, quartY, quartZ, sampler, activeOptions);
		}

		if (!this.usesCuratedSanctuaryPalette(activeOptions.starterBiome())) {
			double fullStarterBiomeRadius = Math.max(
				72.0D,
				activeSanctuaryInnerRadius * clamp(0.88D + (starterIslandScale - 1.0D) * 0.55D, 0.58D, 1.35D)
			);
			if (distance < fullStarterBiomeRadius || sanctuaryBias >= edgeFallbackThreshold + 0.10D) {
				return activeOptions.starterBiome();
			}

			return this.sampleFallbackBiome(quartX, quartY, quartZ, sampler, activeOptions);
		}

		double centralMeadowRadius = activeSanctuaryInnerRadius * starterIslandScale * (
			0.28D
				+ 0.06D * Math.sin(sampleX * 0.008D)
				+ 0.04D * Math.cos(sampleZ * 0.007D)
		);
		centralMeadowRadius = Math.max(40.0D, centralMeadowRadius);
		if (distance < centralMeadowRadius) {
			return activeOptions.starterBiome();
		}

		double patchA = 0.50D
			+ 0.22D * Math.sin(sampleX * 0.010D + humidity * 2.6D)
			+ 0.17D * Math.cos(sampleZ * 0.012D - temperature * 1.7D)
			+ 0.10D * Math.sin((sampleX - sampleZ) * 0.006D);
		double patchB = 0.49D
			+ 0.18D * Math.cos(sampleX * 0.009D - humidity * 1.9D)
			+ 0.15D * Math.sin(sampleZ * 0.010D + temperature * 2.3D)
			+ 0.08D * Math.cos((sampleX + sampleZ) * 0.005D);
		double ridgeBand = 0.45D
			+ 0.24D * Math.sin((sampleX + sampleZ) * 0.008D)
			+ 0.16D * Math.cos((sampleX - sampleZ) * 0.011D)
			- 0.10D * humidity;
		double blossomBand = 0.43D
			+ 0.19D * Math.sin(sampleX * 0.013D)
			+ 0.17D * Math.cos(sampleZ * 0.014D)
			+ 0.08D * humidity;
		double sunflowerBand = 0.42D
			+ 0.20D * Math.cos(sampleX * 0.011D + 0.8D)
			+ 0.14D * Math.sin(sampleZ * 0.010D - 0.6D)
			+ 0.09D * temperature;
		double edgeBias = smoothstep(
			activeSanctuaryInnerRadius * 0.50D,
			activeSanctuaryOuterRadius * (0.96D + Math.max(0.0D, archipelagoSpacing) * 0.16D),
			distance
		);

		if (edgeBias > 0.42D && ridgeBand > 0.58D) {
			return this.windsweptHills;
		}

		if (blossomBand > 0.60D && humidity > -0.22D) {
			return this.flowerForest;
		}

		if (sunflowerBand > 0.59D && temperature > -0.15D && edgeBias < 0.62D) {
			return this.sunflowerPlains;
		}

		if (patchB > 0.53D || temperature > 0.14D) {
			return this.forest;
		}

		if (patchA > 0.44D || humidity > 0.10D) {
			return this.birchForest;
		}

		return this.meadow;
	}

	private RegistryEntry<Biome> sampleFallbackBiome(int quartX, int quartY, int quartZ, MultiNoiseUtil.MultiNoiseSampler sampler) {
		return this.sampleFallbackBiome(quartX, quartY, quartZ, sampler, this.options);
	}

	private RegistryEntry<Biome> sampleFallbackBiome(
		int quartX,
		int quartY,
		int quartZ,
		MultiNoiseUtil.MultiNoiseSampler sampler,
		SanctuaryWorldgenOptions activeOptions
	) {
		double sizeScale = 1.0D / Math.max(0.35D, activeOptions.archipelagoSize());
		double spacingScale = 1.0D - activeOptions.archipelagoSpacing() * 0.35D;
		double effectiveFallbackScale = clamp(this.fallbackScale * sizeScale * spacingScale, 0.12D, 1.40D);
		int scaledQuartX = scaleQuartCoord(quartX, effectiveFallbackScale);
		int scaledQuartZ = scaleQuartCoord(quartZ, effectiveFallbackScale);
		return this.fallbackSource.getBiome(scaledQuartX, quartY, scaledQuartZ, sampler);
	}

	public SanctuaryBiomeSource withOptions(SanctuaryWorldgenOptions updatedOptions) {
		return new SanctuaryBiomeSource(
			this.fallback,
			this.meadow,
			this.birchForest,
			this.forest,
			this.flowerForest,
			this.sunflowerPlains,
			this.windsweptHills,
			updatedOptions,
			this.fallbackScale
		);
	}

	public SanctuaryWorldgenOptions options() {
		return this.options;
	}

	public RegistryEntry<MultiNoiseBiomeSourceParameterList> fallback() {
		return this.fallback;
	}

	public RegistryEntry<Biome> meadow() {
		return this.meadow;
	}

	public RegistryEntry<Biome> birchForest() {
		return this.birchForest;
	}

	public RegistryEntry<Biome> forest() {
		return this.forest;
	}

	public RegistryEntry<Biome> flowerForest() {
		return this.flowerForest;
	}

	public RegistryEntry<Biome> sunflowerPlains() {
		return this.sunflowerPlains;
	}

	public RegistryEntry<Biome> windsweptHills() {
		return this.windsweptHills;
	}

	public double fallbackScale() {
		return this.fallbackScale;
	}

	public List<RegistryEntry<Biome>> getStartingBiomeCandidates() {
		LinkedHashMap<String, RegistryEntry<Biome>> candidatesByKey = new LinkedHashMap<>();
		addCandidate(candidatesByKey, this.options.starterBiome());
		addCandidate(candidatesByKey, this.meadow);
		addCandidate(candidatesByKey, this.birchForest);
		addCandidate(candidatesByKey, this.forest);
		addCandidate(candidatesByKey, this.flowerForest);
		addCandidate(candidatesByKey, this.sunflowerPlains);
		addCandidate(candidatesByKey, this.windsweptHills);

		for (RegistryEntry<Biome> biome : this.fallbackSource.getBiomes()) {
			addCandidate(candidatesByKey, biome);
		}

		List<RegistryEntry<Biome>> candidates = new ArrayList<>(candidatesByKey.values());
		RegistryEntry<Biome> selectedStarterBiome = this.options.starterBiome();
		candidates.sort(Comparator.comparing(SanctuaryBiomeSource::biomeSortKey));
		if (candidates.remove(selectedStarterBiome)) {
			candidates.add(0, selectedStarterBiome);
		}

		return candidates;
	}

	public static boolean isSurfaceSafeStartBiome(RegistryEntry<Biome> biome) {
		if (!biome.isIn(BiomeTags.IS_OVERWORLD)
			|| biome.isIn(BiomeTags.IS_NETHER)
			|| biome.isIn(BiomeTags.IS_END)
			|| biome.isIn(BiomeTags.IS_OCEAN)
			|| biome.isIn(BiomeTags.IS_DEEP_OCEAN)
			|| biome.isIn(BiomeTags.IS_RIVER)
			|| biome.isIn(BiomeTags.IS_BEACH)) {
			return false;
		}

		String path = biomePath(biome);
		return !path.contains("cave")
			&& !path.contains("void")
			&& !path.contains("underground")
			&& !path.contains("basalt")
			&& !path.contains("delta");
	}

	public static boolean supportsStarterTrees(RegistryEntry<Biome> biome) {
		if (!isSurfaceSafeStartBiome(biome)) {
			return false;
		}

		String path = biomePath(biome);
		return biome.isIn(BiomeTags.IS_FOREST)
			|| biome.isIn(BiomeTags.IS_TAIGA)
			|| biome.isIn(BiomeTags.IS_JUNGLE)
			|| biome.isIn(BiomeTags.IS_SAVANNA)
			|| path.contains("plains")
			|| path.contains("meadow")
			|| path.contains("grove");
	}

	public static boolean isLushStarterBiome(RegistryEntry<Biome> biome) {
		if (!supportsStarterTrees(biome)) {
			return false;
		}

		String path = biomePath(biome);
		return !path.contains("snow")
			&& !path.contains("frozen")
			&& !path.contains("desert")
			&& !path.contains("badlands")
			&& !path.contains("stony");
	}

	public static Text getBiomeDisplayName(RegistryEntry<Biome> biome) {
		return biome
			.getKey()
			.map(key -> {
				String translationKey = Util.createTranslationKey("biome", key.getValue());
				String fallbackName = humanizePath(key.getValue().getPath());
				return Text.translatableWithFallback(translationKey, fallbackName);
			})
			.orElse(Text.literal("Unknown biome"));
	}

	private static int scaleQuartCoord(int coord, double scale) {
		return (int) Math.floor(coord * scale);
	}

	private static double smoothstep(double edge0, double edge1, double value) {
		double t = Math.max(0.0D, Math.min(1.0D, (value - edge0) / (edge1 - edge0)));
		return t * t * (3.0D - 2.0D * t);
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	private static void addCandidate(LinkedHashMap<String, RegistryEntry<Biome>> candidatesByKey, RegistryEntry<Biome> biome) {
		if (biome == null || !isSurfaceSafeStartBiome(biome)) {
			return;
		}

		candidatesByKey.putIfAbsent(biomeKey(biome), biome);
	}

	private static String biomeSortKey(RegistryEntry<Biome> biome) {
		return biome.getKey().map(key -> key.getValue().toString()).orElse("unknown");
	}

	private boolean usesCuratedSanctuaryPalette(RegistryEntry<Biome> biome) {
		return biome != null && biome.matches(this.meadow);
	}

	private static String biomeKey(RegistryEntry<Biome> biome) {
		return biome.getKey().map(key -> key.getValue().toString()).orElse("unknown:" + biome.hashCode());
	}

	private static String biomePath(RegistryEntry<Biome> biome) {
		return biome.getKey().map(key -> key.getValue().getPath()).orElse("");
	}

	private static String humanizePath(String path) {
		String[] parts = path.split("_");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isEmpty()) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append(' ');
			}

			builder.append(Character.toUpperCase(parts[i].charAt(0)));
			if (parts[i].length() > 1) {
				builder.append(parts[i].substring(1));
			}
		}

		return builder.isEmpty() ? path : builder.toString();
	}
}
