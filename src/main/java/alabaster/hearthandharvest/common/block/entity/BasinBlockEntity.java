package alabaster.hearthandharvest.common.block.entity;

import alabaster.hearthandharvest.common.registry.HHModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import alabaster.hearthandharvest.platform.fluid.FluidTank;

public class BasinBlockEntity extends HHSyncedBlockEntity {
    public static final int CAPACITY = 1000;
    public static final int BOTTLE_AMOUNT = 250;

    public final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
                if (tank.getFluidAmount() < CAPACITY) {
                    level.scheduleTick(worldPosition, getBlockState().getBlock(), 60);
                }
            }
        }
    };

    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(HHModBlockEntities.BASIN.get(), pos, state);
    }

    public boolean isFull()  { return tank.getFluidAmount() >= CAPACITY; }
    public boolean isEmpty() { return tank.getFluidAmount() == 0; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tank.serialize(output.child("Tank"));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tank.deserialize(input.childOrEmpty("Tank"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return writeTag(registries, output -> tank.serialize(output.child("Tank")));
    }

}