package net.hecco.bountifulfares.definition.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class FruitBlock extends FallingBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty SLICES = IntegerProperty.create("slices", 0, 3);
    public FruitBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SLICES, 0).setValue(FACING, Direction.NORTH));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLICES, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (!world.getBlockState(pos.above()).is(BlockTags.LEAVES)) {
            scheduledTickAccess.scheduleTick(pos, this, this.getDelayAfterPlace());
        }
        return super.updateShape(state, world, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(SLICES) != 3 && player.canEat(false)) {
            world.setBlock(pos, state.cycle(SLICES), Block.UPDATE_CLIENTS);
            player.getFoodData().eat(4, 0.1f);
            world.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        } else if (state.getValue(SLICES) == 3 && player.canEat(false)) {
            world.removeBlock(pos, false);
            player.getFoodData().eat(4, 0.1f);
            world.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.getBlockState(pos.above()).is(BlockTags.LEAVES)) {
            world.scheduleTick(pos, this, this.getDelayAfterPlace());
        }
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        if (entity.getBlockState().getValue(SLICES) != 0) {
            entity.dropItem = false;
        }
        super.falling(entity);
    }

    @Override
    public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!world.isClientSide()) {
            world.destroyBlock(hit.getBlockPos(), false);
            world.playSound(null, hit.getBlockPos(), SoundEvents.BAMBOO_WOOD_FALL, SoundSource.BLOCKS, 1.0F, 1.0F);
            int stackCount = 9;
            if (state.getValue(SLICES) == 1) {
                stackCount = 4;
            } else if (state.getValue(SLICES) == 2) {
                stackCount = 2;
            } else if (state.getValue(SLICES) == 3) {
                stackCount = 1;
            }
            world.addFreshEntity(new ItemEntity(world, hit.getBlockPos().getX() + 0.5, hit.getBlockPos().getY() + 0.5, hit.getBlockPos().getZ() + 0.5, new ItemStack(getFruitItem(), stackCount)));
        }
    }

    public Item getFruitItem() {
        return null;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {

    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (isFree(world.getBlockState(pos.below())) && pos.getY() >= world.getMinY() && !world.getBlockState(pos.above()).is(BlockTags.LEAVES)) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(world, pos, state);
            this.falling(fallingBlockEntity);
        }
    }

    // FallingBlock.getDustColor(BlockState, BlockGetter, BlockPos) is a newly-added abstract
    // method in 26.3 (confirmed via javap - it wasn't there before, so plain FallingBlock
    // subclasses that never needed to implement it now must). Vanilla's own ConcretePowderBlock
    // implements it by deriving the color from the block's map color (confirmed via javap/
    // bytecode), which is the sensible generic default for a block with no dedicated dust-color
    // field.
    @Override
    public int getDustColor(BlockState state, net.minecraft.world.level.BlockGetter world, BlockPos pos) {
        return state.getMapColor(world, pos).col;
    }
}
