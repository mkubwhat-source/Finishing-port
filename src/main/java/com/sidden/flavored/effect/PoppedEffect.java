package com.sidden.flavored.effect;

import com.sidden.flavored.registry.FlavoredBlockTags;
import com.sidden.flavored.registry.FlavoredParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Popped (from corn): while burning or standing on a heat source, the entity "pops" - launched
 * upwards, extinguished, with popcorn particles.
 * <p>
 * 26.3: effect ticks only run on the server ({@code applyEffectTick} takes a ServerLevel), so the
 * particles are sent from the server, and the launch is flagged ({@code needsSync}) so the
 * velocity reaches the client of a player (whose movement is client-authoritative).
 */
public class PoppedEffect extends MobEffect {
    public PoppedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        if ((livingEntity.isOnFire() || livingEntity.getBlockStateOn().is(FlavoredBlockTags.HEAT_SOURCES)) && !livingEntity.getBlockStateOn().isAir()) {
            pop(level, livingEntity, 1);
        }
        return super.applyEffectTick(level, livingEntity, amplifier);
    }

    public void pop(ServerLevel level, LivingEntity entity, int amount) {
        Vec3 vec3 = entity.getDeltaMovement();
        entity.setDeltaMovement(vec3.x, amount, vec3.z);
        entity.needsSync = true;
        entity.extinguishFire();
        Vec3 pos = entity.position();
        RandomSource random = level.getRandom();
        for (int i = 0; i < 5; i++) {
            // count 0 = one particle with an exact velocity, like 1.21.1's addParticle call
            level.sendParticles(FlavoredParticles.POPCORN_POPS.get(),
                    pos.x + (double) random.nextInt(5) / 10, pos.y + 1.0, pos.z + (double) random.nextInt(5) / 10,
                    0, random.nextInt(-5, 5), 0, random.nextInt(-5, 5), 1.0);
        }
        entity.playSound(SoundEvents.WIND_CHARGE_THROW);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
