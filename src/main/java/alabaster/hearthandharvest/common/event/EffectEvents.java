package alabaster.hearthandharvest.common.event;

import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EffectEvents {

    public static void register() {
        ServerMobEffectEvents.ALLOW_ADD.register((incoming, entity, context) -> {
            // Clarity
            if ((incoming.getEffect().is(MobEffects.BLINDNESS) || incoming.getEffect().is(MobEffects.DARKNESS))
                    && entity.hasEffect(HHModEffects.CLARITY)) {
                return false;
            }
            // Drunk
            if (incoming.getEffect().is(HHModEffects.DRUNK)) return true;
            if (incoming.getEffect().value().getCategory() != MobEffectCategory.BENEFICIAL) return true;
            MobEffectInstance drunkInstance = entity.getEffect(HHModEffects.DRUNK);
            return drunkInstance == null || drunkInstance.getAmplifier() < 4;
        });
        ServerMobEffectEvents.AFTER_ADD.register((instance, entity, context) -> {
            if (instance.getEffect().is(HHModEffects.CLARITY)) {
                entity.removeEffect(MobEffects.BLINDNESS);
                entity.removeEffect(MobEffects.DARKNESS);
            }
        });
    }
}