package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link HHEvents#ITEM_PICKUP} (NeoForge ItemEntityPickupEvent.Post) after a successful pickup. */
@Mixin(ItemEntity.class)
public abstract class ItemEntityPickupMixin {
    @Unique
    private ItemStack hearthandharvest$beforePickup = ItemStack.EMPTY;

    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void hearthandharvest$capture(Player player, CallbackInfo ci) {
        hearthandharvest$beforePickup = ((ItemEntity) (Object) this).getItem().copy();
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V", shift = At.Shift.AFTER))
    private void hearthandharvest$picked(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        HHEvents.ITEM_PICKUP.invoker().onPickup(player, self, hearthandharvest$beforePickup, self.getItem().copy());
    }
}
