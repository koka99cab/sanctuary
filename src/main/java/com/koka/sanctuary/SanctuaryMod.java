package com.koka.sanctuary;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SanctuaryMod implements ModInitializer {
	public static final String MOD_ID = "sanctuary";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final RegistryKey<WorldPreset> SANCTUARY_WORLD_PRESET = RegistryKey.of(
		RegistryKeys.WORLD_PRESET,
		id("sanctuary")
	);
	public static final RegistryKey<ChunkGeneratorSettings> SANCTUARY_NOISE_SETTINGS = RegistryKey.of(
		RegistryKeys.CHUNK_GENERATOR_SETTINGS,
		id("sanctuary_overworld")
	);

	@Override
	public void onInitialize() {
		SanctuaryRegistries.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
