package com.koka.sanctuary.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record OriginDistanceRampDensityFunction(double startRadius, double endRadius) implements DensityFunction.Base {
	public static final double MAX_DISTANCE = OriginDistanceDensityFunction.MAX_DISTANCE;
	public static final MapCodec<OriginDistanceRampDensityFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("start_radius").forGetter(OriginDistanceRampDensityFunction::startRadius),
		Codec.DOUBLE.fieldOf("end_radius").forGetter(OriginDistanceRampDensityFunction::endRadius)
	).apply(instance, OriginDistanceRampDensityFunction::new));

	@SuppressWarnings("unchecked")
	public static final CodecHolder<DensityFunction> CODEC_HOLDER = CodecHolder.of((MapCodec<DensityFunction>) (MapCodec<?>) MAP_CODEC);

	public OriginDistanceRampDensityFunction {
		startRadius = Math.max(0.0D, startRadius);
		endRadius = Math.max(startRadius + 1.0D, endRadius);
	}

	@Override
	public double sample(DensityFunction.NoisePos context) {
		double distance = SanctuaryWorldgen.noisyDistanceToOrigin(context.blockX(), context.blockZ());
		if (distance <= this.startRadius) {
			return 0.0D;
		}

		if (distance >= this.endRadius) {
			return 1.0D;
		}

		double t = (distance - this.startRadius) / (this.endRadius - this.startRadius);
		return t * t * (3.0D - 2.0D * t);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}

	@Override
	public CodecHolder<? extends DensityFunction> getCodecHolder() {
		return CODEC_HOLDER;
	}
}
