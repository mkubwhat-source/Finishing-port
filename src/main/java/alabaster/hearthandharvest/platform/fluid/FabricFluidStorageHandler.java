package alabaster.hearthandharvest.platform.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IFluidHandler} over another mod's Fabric fluid storage (block side). Only whole
 * millibuckets move: a partial-droplet result is redone for exactly the whole mB so nothing is lost.
 */
final class FabricFluidStorageHandler implements IFluidHandler {
    private final Storage<FluidVariant> storage;

    FabricFluidStorageHandler(Storage<FluidVariant> storage) {
        this.storage = storage;
    }

    private List<StorageView<FluidVariant>> views() {
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        storage.iterator().forEachRemaining(views::add);
        return views;
    }

    @Override
    public int getTanks() {
        return views().size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        List<StorageView<FluidVariant>> views = views();
        if (tank >= views.size() || views.get(tank).isResourceBlank()) return FluidStack.EMPTY;
        StorageView<FluidVariant> view = views.get(tank);
        return new FluidStack(view.getResource().getFluid(), (int) (view.getAmount() / FluidStack.DROPLETS_PER_MB));
    }

    @Override
    public int getTankCapacity(int tank) {
        List<StorageView<FluidVariant>> views = views();
        if (tank >= views.size()) return 0;
        return (int) Math.min(Integer.MAX_VALUE, views.get(tank).getCapacity() / FluidStack.DROPLETS_PER_MB);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    private long move(FluidStack resource, boolean insert, FluidAction action) {
        FluidVariant variant = FluidVariant.of(resource.getFluid());
        long wholeMb;
        try (Transaction tx = Transaction.openOuter()) {
            long droplets = resource.getAmount() * FluidStack.DROPLETS_PER_MB;
            long moved = insert ? storage.insert(variant, droplets, tx) : storage.extract(variant, droplets, tx);
            wholeMb = moved / FluidStack.DROPLETS_PER_MB;
            if (moved % FluidStack.DROPLETS_PER_MB == 0) {
                if (action.execute()) tx.commit();
                return wholeMb;
            }
        }
        // The other storage moved a fraction of a millibucket; redo it for the whole mB only.
        if (wholeMb == 0 || action.simulate()) return wholeMb;
        try (Transaction exact = Transaction.openOuter()) {
            long target = wholeMb * FluidStack.DROPLETS_PER_MB;
            long moved = insert ? storage.insert(variant, target, exact) : storage.extract(variant, target, exact);
            if (moved != target) return 0;
            exact.commit();
            return wholeMb;
        }
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !storage.supportsInsertion()) return 0;
        return (int) move(resource, true, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !storage.supportsExtraction()) return FluidStack.EMPTY;
        return resource.copyWithAmount((int) move(resource, false, action));
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (StorageView<FluidVariant> view : views()) {
            if (!view.isResourceBlank() && view.getAmount() >= FluidStack.DROPLETS_PER_MB) {
                return drain(new FluidStack(view.getResource().getFluid(), maxDrain), action);
            }
        }
        return FluidStack.EMPTY;
    }
}
