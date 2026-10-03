package net.hecco.bountifulfares.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TerrainParticle.class)
public abstract class BlockDustParticleMixin {
    // 26.3: the particle tint is no longer BlockColors.getColor(state, level, pos, tintIndex) but
    // BlockColors.getTintSource(state, 0).colorAsTerrainParticle(state, level, pos) (javap); the
    // BlockPos is still argument index 2 and BlockAndTintGetter moved to
    // net.minecraft.client.renderer.block.
    @ModifyArg(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockTintSource;colorAsTerrainParticle(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"), index = 2)
    public @Nullable BlockPos bountifulfares$init(@Nullable BlockPos pos, @Local(argsOnly = true) ClientLevel world) {
        if (pos != null && world.getBlockState(pos).is(Blocks.AIR)) return pos.below();
        return pos;
    }
}
