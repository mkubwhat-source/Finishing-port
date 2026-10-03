package net.hecco.bountifulfares.mixin.gameplay;

import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushMixin {

    // getCloneItemStack gained a trailing boolean parameter in 26.3 (confirmed via javap on
    // BlockBehaviour) - the mixin's injected method signature must match the new descriptor
    // exactly for Mixin to find the target.
    @Inject(method = "getCloneItemStack", at = @At("HEAD"), cancellable = true)
    private void bountifulfares$replacePickstack(LevelReader world, BlockPos pos, BlockState state, boolean includeData, CallbackInfoReturnable<ItemStack> cir) {
        if (Services.PLATFORM.get().getBoolConfigValue("enableSweetBerryPips")) {
            cir.setReturnValue(BFItems.SWEET_BERRY_PIPS.get().getDefaultInstance());
            cir.cancel();
        }
    }
}
