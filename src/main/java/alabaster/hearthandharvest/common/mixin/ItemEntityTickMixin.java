package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.common.item.ManureItem;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.1 used NeoForge's {@code IItemExtension#onEntityItemUpdate}: manure items on the ground attract flies. */
@Mixin(ItemEntity.class)
public abstract class ItemEntityTickMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void hearthandharvest$onItemUpdate(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.getItem().getItem() instanceof ManureItem) {
            ManureItem.onEntityItemUpdate(self.getItem(), self);
        }
    }
}
