package net.hecco.bountifulfares.definition.block.custom;

import net.hecco.bountifulfares.registry.content.BFParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class GoldenAppleLeavesBlock extends LeavesBlock {

    // LeavesBlock's constructor gained a leading AmbientLeavesBlockSoundPlayer param in 26.3
    // (same fix as FruitLeavesBlock).
    public GoldenAppleLeavesBlock(Properties settings) {
        super(AmbientLeavesBlockSoundPlayer.noAmbientSound(), settings);
    }

    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (random.nextInt(16) == 0) {
            if (!isFaceFull(world.getBlockState(pos.below()).getCollisionShape(world, pos.below()), Direction.UP)) {
                ParticleUtils.spawnParticleBelow(world, pos, random, BFParticles.GOLDEN_PETAL.get());
            }
        }
    }

}
