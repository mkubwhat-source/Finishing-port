package alabaster.hearthandharvest.common.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Supplier;

/**
 * One of Hearth and Harvest's fluids (juices, wines, ciders, sap, goat milk, ...).
 * <p>
 * 1.21.1 built these on NeoForge's {@code BaseFlowingFluid} with no fluid block, so they only exist
 * inside the keg, jug, stomping basin and tree tapper and can't be placed in the world. Same here:
 * a plain {@link FlowingFluid} pair (source/flowing) whose legacy block is air.
 */
public class HHFluidType extends FlowingFluid {
    private final boolean source;
    private final Supplier<? extends Fluid> sourceFluid;
    private final Supplier<? extends Fluid> flowingFluid;
    private final Supplier<? extends Item> bucket;

    public HHFluidType(boolean source, Supplier<? extends Fluid> sourceFluid, Supplier<? extends Fluid> flowingFluid, Supplier<? extends Item> bucket) {
        this.source = source;
        this.sourceFluid = sourceFluid;
        this.flowingFluid = flowingFluid;
        this.bucket = bucket;
        if (!source) registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        builder.add(LEVEL);
    }

    @Override
    public Fluid getSource() {
        return sourceFluid.get();
    }

    @Override
    public Fluid getFlowing() {
        return flowingFluid.get();
    }

    @Override
    public Item getBucket() {
        return bucket == null ? Items.AIR : bucket.get();
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    public int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 5;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid other, Direction direction) {
        return false;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSame(Fluid other) {
        return other == getSource() || other == getFlowing();
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return source;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return source ? 8 : fluidState.getValue(LEVEL);
    }
}
