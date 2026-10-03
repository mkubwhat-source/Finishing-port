package com.sidden.flavored.block;

import com.sidden.flavored.registry.FlavoredBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/** A stripped cinnamon stalk slowly regrows its bark (unless waxed - the waxed variant is a plain pillar). */
public class StrippedCinnamonStalkBlock extends RotatedPillarBlock {
    public StrippedCinnamonStalkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(0, 7) == 0) {
            // 1.21.1 set the stalk's default state (axis reset to Y); the axis is kept now so a
            // horizontal stalk does not visibly flip upright when it regrows.
            level.setBlock(pos, FlavoredBlocks.CINNAMON_STALK.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)), 2);
        }
        super.randomTick(state, level, pos, random);
    }
}
