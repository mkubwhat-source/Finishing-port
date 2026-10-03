package alabaster.hearthandharvest.common.fd.mixin;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class NourishmentAlwaysEatMixin
{
	@Inject(
			method = "canEat",
			at = @At("HEAD"),
			cancellable = true)
	private void alwaysEatUnderNourishmentEffect(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
		if (((Player) (Object) this).hasEffect(HHModEffects.NOURISHMENT)) {
			cir.setReturnValue(true);
		}
	}
}
