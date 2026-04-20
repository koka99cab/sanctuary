package com.koka.sanctuary.mixin.client;

import com.koka.sanctuary.SanctuaryMod;
import com.koka.sanctuary.worldgen.SanctuaryWorldgen;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldPresets.class)
abstract class WorldPresetsMixin {
	@Inject(method = "getWorldPreset", at = @At("HEAD"), cancellable = true)
	private static void sanctuary$recognizeCustomSanctuaryWorlds(
		DimensionOptionsRegistryHolder dimensions,
		CallbackInfoReturnable<Optional<RegistryKey<WorldPreset>>> cir
	) {
		if (SanctuaryWorldgen.isSanctuaryDimensions(dimensions)) {
			cir.setReturnValue(Optional.of(SanctuaryMod.SANCTUARY_WORLD_PRESET));
		}
	}
}
