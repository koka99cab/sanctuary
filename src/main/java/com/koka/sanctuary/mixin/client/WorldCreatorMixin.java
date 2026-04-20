package com.koka.sanctuary.mixin.client;

import com.koka.sanctuary.SanctuaryMod;
import com.koka.sanctuary.client.SanctuaryClient;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreator.class)
abstract class WorldCreatorMixin {
	@Inject(method = "getLevelScreenProvider", at = @At("HEAD"), cancellable = true)
	private void sanctuary$provideCustomScreen(CallbackInfoReturnable<LevelScreenProvider> cir) {
		WorldCreator worldCreator = (WorldCreator) (Object) this;
		if (worldCreator.getWorldType().preset() != null
			&& worldCreator.getWorldType().preset().matchesKey(SanctuaryMod.SANCTUARY_WORLD_PRESET)) {
			cir.setReturnValue(SanctuaryClient.SANCTUARY_LEVEL_SCREEN_PROVIDER);
		}
	}

	@Redirect(
		method = "updateWorldTypeLists",
		at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
	)
	private Object sanctuary$keepSanctuaryPresetStable(
		Map<Optional<net.minecraft.registry.RegistryKey<WorldPreset>>, LevelScreenProvider> providers,
		Object key
	) {
		Object provider = providers.get(key);
		if (provider != null) {
			return provider;
		}

		return isSanctuaryWorldPresetKey(key) ? SanctuaryClient.SANCTUARY_LEVEL_SCREEN_PROVIDER : null;
	}

	private static boolean isSanctuaryWorldPresetKey(Object key) {
		if (!(key instanceof Optional<?> optionalKey) || optionalKey.isEmpty()) {
			return false;
		}

		return SanctuaryMod.SANCTUARY_WORLD_PRESET.equals(optionalKey.get());
	}
}
