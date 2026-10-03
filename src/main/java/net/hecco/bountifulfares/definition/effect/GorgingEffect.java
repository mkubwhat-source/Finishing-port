package net.hecco.bountifulfares.definition.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class GorgingEffect extends MobEffect {
    protected GorgingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity instanceof  Player) {
            ((Player) entity).getFoodData().setFoodLevel(20);
        }
        super.onEffectStarted(entity, amplifier);
    }

    // MobEffect.applyEffectTick gained a leading ServerLevel param in 26.3 (confirmed via javap).
    // FoodData.setExhaustion(float) is also gone (only addExhaustion(float), which has no floor
    // and only clamps at 40, remains), so the private exhaustionLevel field is set directly via
    // the access-widener entry added for this in bountifulfares.accesswidener.
    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        super.applyEffectTick(level, entity, amplifier);
        if (entity instanceof Player) {
            ((Player) entity).getFoodData().exhaustionLevel = 0f;
        }
        return true;
    }

//    @Override
//    public void onRemoved(AttributeContainer attributeContainer) {
//        if (entity instanceof  PlayerEntity) {
//            ((PlayerEntity) entity).getHungerManager().setFoodLevel(1 + Random.create().nextBetween(0, 5));
//        }
//        super.onRemoved(attributeContainer);
//    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
