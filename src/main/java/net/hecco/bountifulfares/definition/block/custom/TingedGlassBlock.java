package net.hecco.bountifulfares.definition.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

public class TingedGlassBlock extends TransparentBlock {
    public TingedGlassBlock(Properties settings) {
        super(settings);
    }

    // BlockGetter.getMaxLightLevel() was removed in 26.3; LightEngine.MAX_LEVEL (15) is the direct
    // replacement constant (same fix already applied to GrassyDirtBlock).
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return LightEngine.MAX_LEVEL / 5;
    }
}
