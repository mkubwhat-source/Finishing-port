package com.sidden.flavored.effect;

import com.sidden.flavored.registry.FlavoredEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

/**
 * Heat: an initial burst of fire damage when the effect starts (for players, scaled by a fixed
 * per-player "spice sensitivity" derived from their UUID), removed by entering water. Attacking
 * while heated sets the target on fire (FlavoredEvents).
 */
public class HeatEffect extends MobEffect {
    public static final int MAX_DAMAGE = 9;

    public HeatEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        if (livingEntity.isInWater()) {
            livingEntity.removeEffect(FlavoredEffects.HEAT);
        }
        return super.applyEffectTick(level, livingEntity, amplifier);
    }

    @Override
    public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
        super.onEffectStarted(livingEntity, amplifier);
        if (!livingEntity.fireImmune() && livingEntity.level() instanceof ServerLevel serverLevel) {
            float damage = amplifier;
            if (livingEntity instanceof Player player) {
                Random seed = new Random(player.getUUID().getLeastSignificantBits());
                float spiceSensitivity = seed.nextFloat() * MAX_DAMAGE;
                float variance = (livingEntity.getRandom().nextFloat() * 2.0F) - 1.0F;
                damage = spiceSensitivity + variance;
            } else {
                damage += 2.0F;
            }
            livingEntity.hurtServer(serverLevel, livingEntity.damageSources().onFire(), Math.max(0, damage));
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
