package net.hecco.bountifulfares.definition.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EbullienceEffect extends MobEffect {
    public EbullienceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // MobEffect.applyEffectTick gained a leading ServerLevel param in 26.3 (confirmed via javap).
    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
//        if (entity instanceof Player player) {
//            player.getFoodData().setExhaustion(0f);
//        }
        return super.applyEffectTick(level, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}