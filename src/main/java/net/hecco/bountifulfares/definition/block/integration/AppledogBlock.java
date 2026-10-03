package net.hecco.bountifulfares.definition.block.integration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class AppledogBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty JOY = BooleanProperty.create("joy");
    public AppledogBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(JOY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, JOY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!state.getValue(JOY)) {
            world.setBlockAndUpdate(pos, state.setValue(JOY, true));
            // SoundEvents.WOLF_AMBIENT was removed in 26.3 - wolf sounds are now the data-driven
            // WolfSoundVariant registry (confirmed via javap); the classic variant's adult ambient
            // sound is the direct replacement for the old plain constant.
            var wolfAmbientSound = world.registryAccess().lookupOrThrow(Registries.WOLF_SOUND_VARIANT)
                    .getOrThrow(WolfSoundVariants.CLASSIC).value().adultSounds().ambientSound();
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), wolfAmbientSound.value(), SoundSource.BLOCKS, 1.0f,1.0f + world.getRandom().nextFloat() / 3);
            world.scheduleTick(pos, this, 4);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        world.setBlockAndUpdate(pos, state.setValue(JOY, false));
        super.tick(state, world, pos, random);
    }
}
