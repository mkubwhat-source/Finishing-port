package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import alabaster.hearthandharvest.common.event.FeatherParticles;
import alabaster.hearthandharvest.common.registry.HHModAttachments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ChickenPlucking {

    private static final int PLUCK_COOLDOWN_TICKS = 600;

    public static void register() {
        HHEvents.LIVING_TICK_POST.register(ChickenPlucking::onChickenTick);
        UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> hit == null ? onPlayerInteractEntity(player, hand, target) : InteractionResult.PASS);
    }

    private static void onChickenTick(LivingEntity entity) {
        if (!(entity instanceof Chicken chicken)) return;
        if (chicken.level().isClientSide()) return;
        int cooldown = chicken.getAttachedOrGet(HHModAttachments.PLUCK_COOLDOWN, HHModAttachments.PLUCK_COOLDOWN.initializer());
        if (cooldown > 0) chicken.setAttached(HHModAttachments.PLUCK_COOLDOWN, cooldown - 1);
    }

    private static InteractionResult onPlayerInteractEntity(Player player, InteractionHand hand, Entity target) {
        if (!(target instanceof Chicken chicken)) return InteractionResult.PASS;
        if (Config.DISABLE_CHICKEN_PLUCKING.get()) return InteractionResult.PASS;
        Level world = player.level();
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (world instanceof ServerLevel serverLevel) {
            if (chicken.getAttachedOrGet(HHModAttachments.PLUCK_COOLDOWN, HHModAttachments.PLUCK_COOLDOWN.initializer()) > 0) {
                return InteractionResult.FAIL;
            }
            chicken.setAttached(HHModAttachments.PLUCK_COOLDOWN, PLUCK_COOLDOWN_TICKS);
            chicken.spawnAtLocation(serverLevel, Items.FEATHER);
            FeatherParticles.burst(chicken, 8);
            HHSimpleTrigger.trigger(HHModTriggers.PLUCKED_CHICKEN, player);

            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.is(Items.SHEARS)) {
                heldItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                chicken.hurtServer(serverLevel, chicken.damageSources().playerAttack(player), 0.0F);
            } else {
                if (world.getRandom().nextDouble() < 0.25) {
                    chicken.hurtServer(serverLevel, chicken.damageSources().playerAttack(player), 1.0F);
                }
                else {
                    chicken.hurtServer(serverLevel, chicken.damageSources().playerAttack(player), 0.0F);
                }
            }
            if (chicken.getNavigation() != null) {
                chicken.getNavigation().moveTo(player.getX() + (world.getRandom().nextDouble() - 0.5) * 6.0,
                        player.getY(),
                        player.getZ() + (world.getRandom().nextDouble() - 0.5) * 6.0,
                        1.25);
            }
        }
        return InteractionResult.SUCCESS;
    }
}