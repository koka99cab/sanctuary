package com.koka.sanctuary.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum MainIslandPreset implements StringIdentifiable {
	MONOLITH("monolith"),
	FRACTURED_ARCHIPELAGO("fractured_archipelago"),
	HOLLOW_CROWN("hollow_crown"),
	SPIRE_GARDEN("spire_garden");

	public static final Codec<MainIslandPreset> CODEC = StringIdentifiable.createCodec(MainIslandPreset::values);

	private final String id;

	MainIslandPreset(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
