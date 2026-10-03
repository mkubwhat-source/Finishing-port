package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.platform.event.HHEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link HHEvents#LIVING_TICK_POST} (NeoForge EntityTickEvent.Post for living entities) and
 * {@link HHEvents#USE_ITEM_FINISH} (LivingEntityUseItemEvent.Finish, with a copy of the item from
 * before it was consumed, like NeoForge).
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityHooksMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void hearthandharvest$tickPost(CallbackInfo ci) {
        HHEvents.LIVING_TICK_POST.invoker().onTick((LivingEntity) (Object) this);
    }

    @WrapOperation(method = "completeUsingItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack hearthandharvest$useItemFinish(ItemStack stack, Level level, LivingEntity entity, Operation<ItemStack> original) {
        ItemStack before = stack.copy();
        ItemStack result = original.call(stack, level, entity);
        HHEvents.USE_ITEM_FINISH.invoker().onFinish(entity, before);
        return result;
    }
}
