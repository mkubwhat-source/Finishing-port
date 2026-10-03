package net.hecco.bountifulfares.definition.block.integration;

import com.mojang.serialization.MapCodec;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.registry.content.BFBlockEntities;
import net.hecco.bountifulfares.registry.integration.FarmersDelightIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FDCabinetBlock extends BaseEntityBlock {

    // 26.3 removed the BlockBehaviour.codec()/simpleCodec() data-driven-block-type system entirely
    // (see the identical note on FermentationVesselBlock), so no per-block MapCodec is needed.

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public FDCabinetBlock(Properties settings) {
        super(settings);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof CabinetBlockEntity cabinetBlockEntity) {
            player.openMenu(cabinetBlockEntity);
        }

        return InteractionResult.SUCCESS;
    }

    // BlockBehaviour.onRemove(BlockState,Level,BlockPos,BlockState,boolean) is gone in 26.3
    // (confirmed via javap - no such method on Block/BlockBehaviour anymore). The container-drop
    // side effect it used to trigger is now handled automatically by BlockEntity itself: the base
    // BlockEntity.preRemoveSideEffects(BlockPos, BlockState) already drops a Container's contents
    // via Containers.dropContents when the block entity is removed (confirmed via javap/bytecode
    // on BlockEntity), and CabinetBlockEntity extends RandomizableContainerBlockEntity, which
    // implements Container and does not override that method - so nothing needs to be done here
    // for the drop itself. The redstone-comparator neighbor update that onRemove used to also
    // perform is now the renamed affectNeighborsAfterRemoval hook (confirmed via javap on
    // BlockBehaviour), using Containers.updateNeighboursAfterDestroy as vanilla's ChestBlock does.
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return FarmersDelightIntegration.CABINET_BLOCK_ENTITY.get().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getBlockEntity(pos) instanceof CabinetBlockEntity cabinetBlockEntity) {
            cabinetBlockEntity.tick();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, OPEN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    // getAnalogOutputSignal gained a trailing Direction parameter in 26.3 (confirmed via javap on
    // BlockBehaviour); the direction isn't needed for this block's comparator signal.
    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}