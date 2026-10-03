package alabaster.hearthandharvest.common.block;



import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.ScheduledTickAccess;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.block.entity.NestBlockEntity;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import alabaster.hearthandharvest.platform.inventory.ItemHandlerHelper;

public class NestBlock extends Block implements EntityBlock {

    public static final BooleanProperty GENERATED = BooleanProperty.create("generated");

    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D);
    protected static final VoxelShape INSIDE = Block.box(4.0D, 1.0D, 4.0D, 12.0D, 3.0D, 12.0D);
    protected static final VoxelShape FULLSHAPE = Shapes.join(SHAPE, INSIDE, BooleanOp.ONLY_FIRST);

    public NestBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(GENERATED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NestBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return FULLSHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GENERATED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return !level.getFluidState(below).isSource() && !level.getBlockState(below).is(Blocks.AIR);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource updateRandom) {
        if (!state.canSurvive(level, currentPos)) {
            scheduledTickAccess.scheduleTick(currentPos, this, 1);
        }
        return super.updateShape(state, level, scheduledTickAccess, currentPos, facing, facingPos, facingState, updateRandom);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean intersects) {
        if (level.isClientSide() || !(entity instanceof ItemEntity itemEntity)) return;
        if (!itemEntity.isAlive() || itemEntity.hasPickUpDelay() || itemEntity.getItem().isEmpty()) return;
        if (!(level.getBlockEntity(pos) instanceof NestBlockEntity nest)) return;

        ItemStack original = itemEntity.getItem();
        ItemStack remaining = nest.insert(original.copy());
        if (remaining.getCount() == original.getCount()) {
            nudgeAside(itemEntity, pos);
            return;
        }

        if (remaining.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(remaining);
        }
    }

    private static void nudgeAside(ItemEntity itemEntity, BlockPos pos) {
        double dx = itemEntity.getX() - (pos.getX() + 0.5D);
        double dz = itemEntity.getZ() - (pos.getZ() + 0.5D);
        if (dx * dx + dz * dz < 1.0E-4D) {
            dx = itemEntity.level().getRandom().nextDouble() - 0.5D;
            dz = itemEntity.level().getRandom().nextDouble() - 0.5D;
        }
        double length = Math.sqrt(dx * dx + dz * dz);
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(dx / length * 0.06D, 0.0D, dz / length * 0.06D));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !(level.getBlockEntity(pos) instanceof NestBlockEntity nest)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack remaining = nest.insert(stack.copy());
        if (remaining.getCount() == stack.getCount()) return InteractionResult.TRY_WITH_EMPTY_HAND;

        if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, remaining);
        }
        level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.6F, 1.2F);
        return InteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof NestBlockEntity nest) || nest.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        boolean fromCrowStash = nest.hasCrowStash();
        do {
            ItemStack taken = nest.takeLast();
            if (taken.isEmpty()) break;
            player.getInventory().placeItemBackInInventory(taken, net.minecraft.util.Prediction.SERVER_ONLY);
        } while (player.isShiftKeyDown());

        if (fromCrowStash) {
            HHSimpleTrigger.trigger(HHModTriggers.CROW_STASH_FOUND, player);
        }
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.3F, 1.4F);
        return InteractionResult.CONSUME;
    }


    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof NestBlockEntity nest
                ? ItemHandlerHelper.calcRedstoneFromInventory(nest.getInventory())
                : 0;
    }
}