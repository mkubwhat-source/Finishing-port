package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * NeoForge's AlterGroundEvent: lets {@link HHEvents#ALTER_GROUND} change the block a tree's
 * ground decorator (podzol under mega spruces, etc.) places.
 */
@Mixin(AlterGroundDecorator.class)
public abstract class AlterGroundDecoratorMixin {
    @Redirect(method = "placeBlockAt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void hearthandharvest$alterGround(TreeDecorator.Context context, BlockPos pos, BlockState state) {
        context.setBlock(pos, HHEvents.ALTER_GROUND.invoker().modify(context.level(), context.random(), pos, state));
    }
}
