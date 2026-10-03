package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.entity.ManureDropHelper;
import alabaster.hearthandharvest.common.registry.HHModAttachments;
import alabaster.hearthandharvest.common.registry.HHModParticleTypes;
import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ManureEvents {

    // Player hand-feeds an animal. Fires before the interaction resolves, so we use
    // isFood() + !isInLove() as a proxy for "this will set love mode". Stays Animal-only:
    // love mode / isFood() don't exist for arbitrary mobs, so this can't generalize to CAN_POOP.
    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> {
            if (hit == null && !level.isClientSide() && target instanceof Animal animal) {
                ItemStack item = player.getItemInHand(hand);
                if (!animal.isInLove() && animal.isFood(item)) {
                    ManureDropHelper.schedulePoop(animal);
                }
            }
            return InteractionResult.PASS;
        });
        HHEvents.LIVING_TICK_POST.register(ManureEvents::onEntityTick);
        HHEvents.LIVING_TICK_POST.register(ManureEvents::onLivingTick);
    }

    // Fed-poop countdown and random chance. Fires every tick per entity.
    // Eligible entities are Animals (default) or anything explicitly tagged CAN_POOP.
    private static void onEntityTick(LivingEntity self) {
        if (self.level().isClientSide()) return;
        if (!(self instanceof Animal) && !self.typeHolder().is(HHModTags.CAN_POOP)) return;
        if (!ManureDropHelper.canPoop(self)) return;

        if (Config.MANURE_FED_POOP_ENABLED.get()) {
            int timer = self.getAttachedOrGet(HHModAttachments.MANURE_POOP_TIMER, HHModAttachments.MANURE_POOP_TIMER.initializer());
            if (timer > 0) {
                self.setAttached(HHModAttachments.MANURE_POOP_TIMER, timer - 1);
                if (timer == 1) ManureDropHelper.dropPoop(self);
            }
        }

        if (Config.MANURE_RANDOM_POOP_ENABLED.get()
                && self.tickCount % 20 == 0
                && self.getRandom().nextInt(Config.MANURE_RANDOM_POOP_CHANCE.get()) == 0) {
            ManureDropHelper.dropPoop(self);
        }
    }

    // Ticks down MANURE_FLY_TICKS on any living entity hit by a manure projectile
    private static void onLivingTick(LivingEntity living) {
        if (living.level().isClientSide()) return;
        int flyTicks = living.getAttachedOrGet(HHModAttachments.MANURE_FLY_TICKS, HHModAttachments.MANURE_FLY_TICKS.initializer());
        if (flyTicks <= 0) return;
        living.setAttached(HHModAttachments.MANURE_FLY_TICKS, flyTicks - 1);
        if (flyTicks % 4 != 0) return;
        if (!(living.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(HHModParticleTypes.FLIES.get(),
                living.getX(),
                living.getY() + living.getBbHeight() * 0.5,
                living.getZ(),
                2,
                living.getBbWidth() * 0.5,
                living.getBbHeight() * 0.4,
                living.getBbWidth() * 0.5,
                0.02);
    }
}