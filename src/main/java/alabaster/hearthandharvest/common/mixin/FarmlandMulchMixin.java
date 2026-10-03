package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmlandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import alabaster.hearthandharvest.common.fd.block.RichSoilFarmlandBlock;

/**
 * Mulch keeps nearby farmland moist (within 2 blocks). In this bundle that is every block in
 * {@code #hearthandharvest:mulch}: HH's mulch and Bountiful Fares' walnut/palm mulch (user decision).
 */
@Mixin(value = {FarmlandBlock.class, RichSoilFarmlandBlock.class})
public class FarmlandMulchMixin {
    @Inject(method = "isNearWater", at = @At("RETURN"), cancellable = true)
    private static void checkForMulch(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        for (BlockPos check : BlockPos.betweenClosed(pos.offset(-2, 0, -2), pos.offset(2, 0, 2))) {
            if (level.getBlockState(check).is(HHModTags.MULCH)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}