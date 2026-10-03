package alabaster.hearthandharvest.platform.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Exposes one of Hearth and Harvest's {@link FluidTank}s to Fabric's transfer API (pipes, other
 * mods' tanks), as 1.21.1 exposed it through NeoForge's fluid capability. Moves whole millibuckets
 * only; transactions roll back with snapshots of the tank contents.
 */
public class FluidTankStorage extends SnapshotParticipant<FluidStack> implements SingleSlotStorage<FluidVariant> {
    private final FluidTank tank;

    public FluidTankStorage(FluidTank tank) {
        this.tank = tank;
    }

    @Override
    protected FluidStack createSnapshot() {
        return tank.getFluid().copy();
    }

    @Override
    protected void readSnapshot(FluidStack snapshot) {
        tank.setFluid(snapshot);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || resource.hasComponents()) return 0;
        int mb = (int) Math.min(Integer.MAX_VALUE, maxAmount / FluidStack.DROPLETS_PER_MB);
        if (mb <= 0) return 0;
        FluidStack offer = new FluidStack(resource.getFluid(), mb);
        int accepted = tank.fill(offer, IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) return 0;
        updateSnapshots(transaction);
        tank.fill(offer.copyWithAmount(accepted), IFluidHandler.FluidAction.EXECUTE);
        return accepted * FluidStack.DROPLETS_PER_MB;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || tank.getFluid().getFluid() != resource.getFluid()) return 0;
        int mb = (int) Math.min(Integer.MAX_VALUE, maxAmount / FluidStack.DROPLETS_PER_MB);
        if (mb <= 0) return 0;
        FluidStack available = tank.drain(mb, IFluidHandler.FluidAction.SIMULATE);
        if (available.isEmpty()) return 0;
        updateSnapshots(transaction);
        tank.drain(available.getAmount(), IFluidHandler.FluidAction.EXECUTE);
        return available.getAmount() * FluidStack.DROPLETS_PER_MB;
    }

    @Override
    public boolean isResourceBlank() {
        return tank.getFluid().isEmpty();
    }

    @Override
    public FluidVariant getResource() {
        return tank.getFluid().isEmpty() ? FluidVariant.blank() : FluidVariant.of(tank.getFluid().getFluid());
    }

    @Override
    public long getAmount() {
        return tank.getFluidAmount() * FluidStack.DROPLETS_PER_MB;
    }

    @Override
    public long getCapacity() {
        return tank.getCapacity() * FluidStack.DROPLETS_PER_MB;
    }
}
