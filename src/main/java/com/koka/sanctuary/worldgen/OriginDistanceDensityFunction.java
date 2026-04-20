package com.koka.sanctuary.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record OriginDistanceDensityFunction() implements DensityFunction.Base {
	public static final OriginDistanceDensityFunction INSTANCE = new OriginDistanceDensityFunction();
	public static final double MAX_DISTANCE = 43_000_000.0D;
	public static final MapCodec<OriginDistanceDensityFunction> MAP_CODEC = MapCodec.unit(INSTANCE);

	@SuppressWarnings("unchecked")
	public static final CodecHolder<DensityFunction> CODEC_HOLDER = CodecHolder.of((MapCodec<DensityFunction>) (MapCodec<?>) MAP_CODEC);

	@Override
	public double sample(DensityFunction.NoisePos context) {
		double x = context.blockX();
		double z = context.blockZ();
		return Math.sqrt(x * x + z * z);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return MAX_DISTANCE;
	}

	@Override
	public CodecHolder<? extends DensityFunction> getCodecHolder() {
		return CODEC_HOLDER;
	}
}
