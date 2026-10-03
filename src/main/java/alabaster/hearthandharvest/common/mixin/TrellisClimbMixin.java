package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.common.block.trellis.TrellisBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * 1.21.1 TrellisBlock overrode NeoForge's {@code isLadder}: a trellis is climbable only when it has an
 * upright panel (or a neighbouring trellis has one on the shared face), not when it is only a flat
 * floor/ceiling piece. Fabric has no per-block ladder hook, so the trellis decides here before the
 * {@code #minecraft:climbable} tag check.
 */
@Mixin(LivingEntity.class)
public abstract class TrellisClimbMixin {
    @Shadow
    private Optional<BlockPos> lastClimbablePos;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void hearthandharvest$trellisLadder(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isSpectator()) return;
        BlockState state = self.getInBlockState();
        if (state.getBlock() instanceof TrellisBlock trellis) {
            BlockPos pos = self.blockPosition();
            boolean ladder = trellis.isLadder(state, self.level(), pos, self);
            if (ladder) lastClimbablePos = Optional.of(pos);
            cir.setReturnValue(ladder);
        }
    }
}
