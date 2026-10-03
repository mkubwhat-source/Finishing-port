package alabaster.hearthandharvest.platform.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IFluidHandlerItem} over an item's Fabric fluid storage ({@code FluidStorage.ITEM}):
 * buckets and other mods' fluid containers. The container lives in a private one-slot storage, so
 * exchanges (bucket -> empty bucket) end up in {@link #getContainer()}, as with NeoForge.
 */
final class FabricItemFluidHandler implements IFluidHandlerItem {
    private ItemStack current;
    private final SingleStackStorage slot = new SingleStackStorage() {
        @Override
        protected ItemStack getStack() {
            return current;
        }

        @Override
        protected void setStack(ItemStack stack) {
            current = stack;
        }
    };

    private FabricItemFluidHandler(ItemStack stack) {
        this.current = stack;
    }

    @Nullable
    static FabricItemFluidHandler of(ItemStack stack) {
        if (stack.isEmpty()) return null;
        FabricItemFluidHandler handler = new FabricItemFluidHandler(stack);
        return handler.storage() == null ? null : handler;
    }

    @Nullable
    private Storage<FluidVariant> storage() {
        return ContainerItemContext.ofSingleSlot(slot).find(FluidStorage.ITEM);
    }

    private List<StorageView<FluidVariant>> views() {
        Storage<FluidVariant> storage = storage();
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        if (storage != null) storage.iterator().forEachRemaining(views::add);
        return views;
    }

    @Override
    public ItemStack getContainer() {
        return current;
    }

    @Override
    public int getTanks() {
        return Math.max(1, views().size());
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
        return fill(stack, FluidAction.SIMULATE) > 0;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        Storage<FluidVariant> storage = storage();
        if (storage == null || resource.isEmpty()) return 0;
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = storage.insert(FluidVariant.of(resource.getFluid()), resource.getAmount() * FluidStack.DROPLETS_PER_MB, tx);
            if (action.execute()) tx.commit();
            return (int) (inserted / FluidStack.DROPLETS_PER_MB);
        }
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        Storage<FluidVariant> storage = storage();
        if (storage == null || resource.isEmpty()) return FluidStack.EMPTY;
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = storage.extract(FluidVariant.of(resource.getFluid()), resource.getAmount() * FluidStack.DROPLETS_PER_MB, tx);
            if (action.execute()) tx.commit();
            return resource.copyWithAmount((int) (extracted / FluidStack.DROPLETS_PER_MB));
        }
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (StorageView<FluidVariant> view : views()) {
            if (!view.isResourceBlank()) {
                return drain(new FluidStack(view.getResource().getFluid(), maxDrain), action);
            }
        }
        return FluidStack.EMPTY;
    }
}
