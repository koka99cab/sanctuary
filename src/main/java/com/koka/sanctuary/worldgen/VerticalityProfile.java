package com.koka.sanctuary.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum VerticalityProfile implements StringIdentifiable {
	PLATEAU("plateau"),
	BALANCED("balanced"),
	VARIED("varied");

	public static final Codec<VerticalityProfile> CODEC = StringIdentifiable.createCodec(VerticalityProfile::values);

	private final String id;

	VerticalityProfile(String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
