package net.hecco.bountifulfares.definition.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class FruitLeavesBlock extends LeavesBlock implements BonemealableBlock {

    public final BlockState hangingFruitBlockstate;

    // LeavesBlock's constructor gained a leading AmbientLeavesBlockSoundPlayer param in 26.3
    // (confirmed via javap); noAmbientSound() is the direct no-op default for a leaves block with
    // no special ambient ("wind through leaves") sound of its own.
    public FruitLeavesBlock(BlockState hangingFruitBlockstate, Properties settings) {
        super(AmbientLeavesBlockSoundPlayer.noAmbientSound(), settings);
        this.hangingFruitBlockstate = hangingFruitBlockstate;
    }
    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, BonemealSource source) {
        return world.getBlockState(pos.below()).isAir();
    }
    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }
    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        world.setBlock(pos.below(), hangingFruitBlockstate, 2);
    }
}
