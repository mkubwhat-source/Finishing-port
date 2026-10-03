package alabaster.hearthandharvest.common.block.entity;

import alabaster.hearthandharvest.common.item.JugBlockItem;
import alabaster.hearthandharvest.common.registry.HHModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import alabaster.hearthandharvest.platform.fluid.IFluidHandler;
import alabaster.hearthandharvest.platform.fluid.FluidTank;

public class JugBlockEntity extends HHSyncedBlockEntity {

    private final FluidTank fluidTank;

    public JugBlockEntity(BlockPos pos, BlockState state) {
        super(HHModBlockEntities.JUG.get(), pos, state);
        this.fluidTank = createFluidTank();
    }

    public FluidStack getOutput() {
        return fluidTank.getFluid();
    }

    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    public int getFluidCapacity() {
        return fluidTank.getCapacity();
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidTank.deserialize(input.childOrEmpty("FluidTank"));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fluidTank.serialize(output.child("FluidTank"));
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return fluidTank.fill(resource, action);
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return fluidTank.drain(maxDrain, action);
    }

    private FluidTank createFluidTank() {
        return new FluidTank(JugBlockItem.JUG_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
                syncToClient();
                if (level != null && !level.isClientSide()) {
                    level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
                }
            }
        };
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return writeTag(provider, this::saveAdditional);
    }

    public void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}