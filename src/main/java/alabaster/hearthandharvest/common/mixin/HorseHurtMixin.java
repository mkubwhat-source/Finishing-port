package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.common.registry.HHModAttachments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HorseHurtMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void horseshoeProtection(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof AbstractHorse horse)) return;
        if (horse.getAttachedOrGet(HHModAttachments.HORSESHOE_ITEM, HHModAttachments.HORSESHOE_ITEM.initializer()).isEmpty()) return;
        if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH)) {
            cir.setReturnValue(false);
        }
    }
}