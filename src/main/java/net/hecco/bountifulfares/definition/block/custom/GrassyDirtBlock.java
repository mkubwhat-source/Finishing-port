package net.hecco.bountifulfares.definition.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

public class GrassyDirtBlock extends Block implements BonemealableBlock {
    public GrassyDirtBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, BonemealSource source) {
        return world.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
    }

    public boolean canStayAsGrass(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.is(Blocks.SNOW) && blockState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            // LightEngine.getLightBlockInto(world, state, pos, blockState, blockPos, dir, opacity) was
            // removed in 26.3; the replacement getLightDampeningInto(BlockState, BlockState, Direction, int)
            // drops the world/pos args and BlockState.getLightBlock(world, pos) is now the no-arg
            // getLightDampening() (confirmed via javap/bytecode on vanilla's own SpreadingSnowyBlock,
            // the grass-spreading analog of this block). world.getMaxLightLevel() is likewise gone;
            // LightEngine.MAX_LEVEL (15) is the direct replacement constant.
            int i = LightEngine.getLightDampeningInto(state, blockState, Direction.UP, blockState.getLightDampening());
            return i < LightEngine.MAX_LEVEL;
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canStayAsGrass(state, world, pos)) {
                world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        }
    }
}
