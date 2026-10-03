package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Milking goats gives goat milk (bucket) or a goat milk bottle (glass bottle) instead of cow milk. */
public class GoatMilking {
    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> hit == null ? onRightClickEntity(player, hand, target) : InteractionResult.PASS);
    }

    private static InteractionResult onRightClickEntity(Player player, InteractionHand hand, Entity target) {
        if (!(target instanceof Goat goat) || goat.isBaby()) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.BUCKET)) {
            return milk(player, hand, goat, HHModItems.GOAT_MILK_BUCKET.get());
        }
        if (held.is(Items.GLASS_BOTTLE) && !Config.DISABLE_BOTTLE_MILKING.get()) {
            return milk(player, hand, goat, HHModItems.GOAT_MILK_BOTTLE.get());
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult milk(Player player, InteractionHand hand, Goat goat, Item result) {
        Level level = player.level();
        if (!level.isClientSide()) {
            goat.playSound(SoundEvents.GOAT_MILK, 1.0F, 1.0F);
            player.setItemInHand(hand, ItemUtils.createFilledResult(player.getItemInHand(hand), player, new ItemStack(result)));
            HHSimpleTrigger.trigger(HHModTriggers.MILKED_GOAT, player);
        }
        return InteractionResult.SUCCESS;
    }
}
