package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.HHModItems;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Milking a cow with a glass bottle gives a milk bottle (the Farmer's Delight item, ported in). */
public class CowMilking {
    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> hit == null ? onRightClickEntity(player, level, hand, target) : InteractionResult.PASS);
    }

    private static InteractionResult onRightClickEntity(Player player, Level level, InteractionHand hand, Entity target) {
        if (Config.DISABLE_BOTTLE_MILKING.get()) return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);
        if (target instanceof Cow cow && heldItem.is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide()) {
                heldItem.shrink(1);
                cow.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                ItemStack cowMilk = new ItemStack(HHModItems.MILK_BOTTLE.get());
                boolean added = player.addItem(cowMilk);
                if (!added) {
                    player.drop(cowMilk, false, net.minecraft.util.Prediction.SERVER_ONLY);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
