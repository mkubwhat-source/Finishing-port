package net.hecco.bountifulfares.mixin.gameplay;

import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin {
    @Shadow
    private EquipmentSlot getClickedSlot(Vec3 vector) { return EquipmentSlot.HEAD; }

    // 26.3: ArmorStand.interactAt(Player, Vec3, InteractionHand) became
    // interact(Player, InteractionHand, Vec3) (same body; still calls getEquipmentSlotForItem right
    // after the client-side early return) - confirmed via javap.
    @Inject(method = "interact",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/entity/decoration/ArmorStand;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"),
            cancellable = true)
    private void bountifulfares$artisanBrushInteraction(Player player, InteractionHand hand, Vec3 vec, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(BFItems.ARTISAN_BRUSH.get())) {
            EquipmentSlot equipmentslot1 = this.getClickedSlot(vec);
            ItemStack slotStack = ((ArmorStand)(Object)this).getItemBySlot(equipmentslot1);
            // `ArmorItem`/`ArmorMaterials` no longer exist in 26.3 and there is no generic "dyeable"
            // item tag anymore (armor dyeing is one data recipe per item). The vanilla tag that
            // lists exactly the dyeable leather items is `cauldron_can_remove_dye` (leather armor
            // pieces + leather horse armor + wolf armor), which stands in for the old
            // `getMaterial() == ArmorMaterials.LEATHER` check.
            if (itemstack.has(DataComponents.DYED_COLOR) && (slotStack.has(DataComponents.DYED_COLOR) || slotStack.is(ItemTags.CAULDRON_CAN_REMOVE_DYE))) {
                slotStack.set(DataComponents.DYED_COLOR, itemstack.get(DataComponents.DYED_COLOR));
                player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                cir.setReturnValue(InteractionResult.SUCCESS);
                if (!player.level().isClientSide()) {
                    CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger((ServerPlayer) player, itemstack, ((ArmorStand) (Object) this));
                }
                cir.cancel();
            }
        }
    }
}
