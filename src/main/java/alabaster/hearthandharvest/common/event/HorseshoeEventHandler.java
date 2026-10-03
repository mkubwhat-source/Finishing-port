package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModAttachments;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class HorseshoeEventHandler {

    public static final Identifier SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "horseshoe_speed");

    public static void register() {
        HHEvents.LIVING_TICK_POST.register(HorseshoeEventHandler::onEntityTick);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> onLivingDeath(entity));
    }

    private static void onEntityTick(LivingEntity entity) {
        if (!(entity instanceof AbstractHorse horse)) return;
        if (horse.level().isClientSide()) return;

        AttributeInstance speedAttr = horse.getAttribute(Attributes.MOVEMENT_SPEED);
        boolean shod = !horse.getAttachedOrGet(HHModAttachments.HORSESHOE_ITEM, HHModAttachments.HORSESHOE_ITEM.initializer()).isEmpty();

        if (!shod) {
            if (speedAttr != null && speedAttr.hasModifier(SPEED_MODIFIER_ID))
                speedAttr.removeModifier(SPEED_MODIFIER_ID);
            return;
        }

        if (!horse.hasEffect(MobEffects.FIRE_RESISTANCE))
            horse.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false));

        if (speedAttr != null) {
            BlockState below = horse.level().getBlockState(horse.blockPosition().below());
            float factor = below.getBlock().getSpeedFactor();
            if (factor < 1.0f && horse.onGround()) {
                speedAttr.addOrUpdateTransientModifier(new AttributeModifier(
                        SPEED_MODIFIER_ID,
                        (1.0f / factor) - 1.0f,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            } else {
                if (speedAttr.hasModifier(SPEED_MODIFIER_ID))
                    speedAttr.removeModifier(SPEED_MODIFIER_ID);
            }
        }
    }

    private static void onLivingDeath(LivingEntity entity) {
        if (!(entity instanceof AbstractHorse horse)) return;
        ItemStack shoe = horse.getAttachedOrGet(HHModAttachments.HORSESHOE_ITEM, HHModAttachments.HORSESHOE_ITEM.initializer());
        if (shoe.isEmpty()) return;
        if (!(horse.level() instanceof ServerLevel serverLevel)) return;
        horse.spawnAtLocation(serverLevel, shoe.copy());
        horse.setAttached(HHModAttachments.HORSESHOE_ITEM, ItemStack.EMPTY);
    }
}