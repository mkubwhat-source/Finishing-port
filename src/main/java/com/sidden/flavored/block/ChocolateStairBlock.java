package com.sidden.flavored.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChocolateStairBlock extends StairBlock {
    public ChocolateStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        ChocolateDrips.animateTick(state, level, pos, random);
    }
}
