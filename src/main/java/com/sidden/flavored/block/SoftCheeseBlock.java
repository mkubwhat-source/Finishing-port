package com.sidden.flavored.block;

import com.sidden.flavored.registry.FlavoredBlocks;
import com.sidden.flavored.registry.FlavoredParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Soft cheese ages (two stages) into aged cheese unless it is waxed with honeycomb; an axe removes the wax. */
public class SoftCheeseBlock extends Block {
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public SoftCheeseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WAXED, false).setValue(AGE, 0));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.HONEYCOMB) && !state.getValue(WAXED)) {
            level.setBlock(pos, state.setValue(WAXED, true), 2);
            // 1.21.1 shrank player.getUseItem() (the item being *used*, i.e. empty on a click), so
            // the honeycomb was never consumed; the held stack is consumed now, like vanilla waxing.
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        if (stack.is(ItemTags.AXES) && state.getValue(WAXED)) {
            level.setBlock(pos, state.setValue(WAXED, false), 2);
            level.playSound(null, pos, SoundEvents.AXE_WAX_OFF.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) return;

        if (random.nextInt(0, 10) == 0) {
            if (state.getValue(AGE) >= 1) {
                level.setBlock(pos, FlavoredBlocks.AGED_CHEESE.get().defaultBlockState(), 2);
            } else {
                level.setBlock(pos, state.setValue(AGE, 1), 2);
            }

            level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.sendParticles(FlavoredParticles.CHEESE_AGING.get(),
                    pos.getX() + (double) random.nextInt(5) / 10,
                    pos.getY() + 1.0,
                    pos.getZ() + (double) random.nextInt(5) / 10,
                    5, 0, 0, 0, 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WAXED, AGE);
    }
}
