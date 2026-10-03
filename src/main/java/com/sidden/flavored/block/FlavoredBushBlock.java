package com.sidden.flavored.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Shared growth/bonemeal behavior of Flavored's tomato, pepper and spinach bushes.
 * <p>
 * 1.21.1 had three near-identical {@code BushBlock} subclasses; {@code BushBlock} is
 * {@code VegetationBlock} in 26.3 and {@code BonemealableBlock} gained a {@code BonemealSource}
 * parameter. The NeoForge {@code CommonHooks.canCropGrow}/{@code fireCropGrowPost} hooks (no
 * listeners in Flavored) are dropped - their default is exactly the vanilla chance check, which is
 * kept ({@code random.nextInt(5) == 0}).
 */
public abstract class FlavoredBushBlock extends VegetationBlock implements BonemealableBlock {
    protected FlavoredBushBlock(Properties properties) {
        super(properties);
    }

    public abstract IntegerProperty ageProperty();

    public abstract int maxAge();

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(ageProperty()) < maxAge();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int i = state.getValue(ageProperty());
        if (i < maxAge() && level.getRawBrightness(pos.above(), 0) >= 9 && random.nextInt(5) == 0) {
            BlockState blockstate = state.setValue(ageProperty(), i + 1);
            level.setBlock(pos, blockstate, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockstate));
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof FarmlandBlock;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return state.getValue(ageProperty()) < maxAge();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        int i = Math.min(maxAge(), state.getValue(ageProperty()) + 1);
        level.setBlock(pos, state.setValue(ageProperty(), i), 2);
    }

    protected abstract ItemStack seeds();

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return seeds();
    }
}
