package com.sidden.flavored.mixin;

import com.sidden.flavored.FlavoredEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Replaces NeoForge's LivingEntityUseItemEvent.Finish for spicy food (the stack is still intact at HEAD). */
@Mixin(Consumable.class)
public abstract class ConsumableMixin {
    @Inject(method = "onConsume", at = @At("HEAD"))
    private void flavored$spicyFood(Level level, LivingEntity entity, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        FlavoredEvents.onConsumed(entity, stack);
    }
}
