package com.koka.sanctuary.mixin;

import com.koka.sanctuary.worldgen.SanctuaryStructureRules;
import java.util.Optional;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Structure.class)
abstract class StructureMixin {
	@Inject(method = "getValidStructurePosition", at = @At("RETURN"), cancellable = true)
	private void sanctuary$adaptStructuresToFloatingIslands(
		Structure.Context context,
		CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir
	) {
		Optional<Structure.StructurePosition> result = cir.getReturnValue();
		if (result.isEmpty()) {
			return;
		}

		Structure self = (Structure) (Object) this;
		cir.setReturnValue(SanctuaryStructureRules.adapt(self, context, result.get()));
	}
}
