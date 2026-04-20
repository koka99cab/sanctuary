package com.koka.sanctuary;

import com.koka.sanctuary.worldgen.OriginDistanceDensityFunction;
import com.koka.sanctuary.worldgen.OriginDistanceRampDensityFunction;
import com.koka.sanctuary.worldgen.OriginIslandDensityFunction;
import com.koka.sanctuary.worldgen.SanctuaryBiomeSource;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class SanctuaryRegistries {
	private static boolean registered;

	private SanctuaryRegistries() {
	}

	public static void register() {
		if (registered) {
			return;
		}

		registered = true;
		Registry.register(Registries.BIOME_SOURCE, SanctuaryMod.id("sanctuary_biome_source"), SanctuaryBiomeSource.CODEC);
		Registry.register(Registries.DENSITY_FUNCTION_TYPE, SanctuaryMod.id("origin_island_field"), OriginIslandDensityFunction.CODEC_HOLDER.codec());
		Registry.register(Registries.DENSITY_FUNCTION_TYPE, SanctuaryMod.id("origin_distance"), OriginDistanceDensityFunction.CODEC_HOLDER.codec());
		Registry.register(Registries.DENSITY_FUNCTION_TYPE, SanctuaryMod.id("origin_distance_ramp"), OriginDistanceRampDensityFunction.CODEC_HOLDER.codec());
	}
}
