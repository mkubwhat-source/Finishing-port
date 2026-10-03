package alabaster.hearthandharvest.platform.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hearth and Harvest: a side-restricted view of another {@link IItemHandler} (NeoForge's pattern of
 * returning a different handler per side). Subclasses decide which slots accept insertion and allow
 * extraction; the rules apply both to the NeoForge-style methods and to Fabric's transfer API
 * (hoppers, pipes), whose transactions go through the backing handler's slot storages.
 */
public abstract class SidedItemHandler implements IItemHandler {
    protected final IItemHandler backing;
    private final List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();

    protected SidedItemHandler(IItemHandler backing) {
        this.backing = backing;
        for (int i = 0; i < backing.getSlotCount(); i++) slots.add(new Slot(i));
    }

    /** Whether {@code stack} may be inserted into {@code slot} from this side. */
    protected abstract boolean canInsert(int slot, ItemStack stack);

    /** Whether items may be taken out of {@code slot} from this side. */
    protected abstract boolean canExtract(int slot);

    @Override public int getSlotCount() { return backing.getSlotCount(); }
    @Override public int getSlotLimit(int slot) { return backing.getSlotLimit(slot); }
    @Override public ItemStack getStackInSlot(int slot) { return backing.getStackInSlot(slot); }
    @Override public void setStackInSlot(int slot, ItemStack stack) { backing.setStackInSlot(slot, stack); }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return canInsert(slot, stack) ? backing.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract(slot) ? backing.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return canInsert(slot, stack) && backing.isItemValid(slot, stack);
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return slots.get(slot);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long inserted = 0;
        for (SingleSlotStorage<ItemVariant> slot : slots) {
            inserted += slot.insert(resource, maxAmount - inserted, transaction);
            if (inserted >= maxAmount) break;
        }
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long extracted = 0;
        for (SingleSlotStorage<ItemVariant> slot : slots) {
            extracted += slot.extract(resource, maxAmount - extracted, transaction);
            if (extracted >= maxAmount) break;
        }
        return extracted;
    }

    @Override
    public @NonNull Iterator<StorageView<ItemVariant>> iterator() {
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            if (canExtract(i)) views.add(slots.get(i));
        }
        return views.iterator();
    }

    private class Slot implements SingleSlotStorage<ItemVariant> {
        private final int index;

        Slot(int index) {
            this.index = index;
        }

        private SingleSlotStorage<ItemVariant> delegate() {
            return backing.getSlot(index);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (resource.isBlank() || !isItemValid(index, resource.toStack())) return 0;
            return delegate().insert(resource, maxAmount, transaction);
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (!canExtract(index)) return 0;
            return delegate().extract(resource, maxAmount, transaction);
        }

        @Override public boolean supportsInsertion() { return true; }
        @Override public boolean supportsExtraction() { return canExtract(index); }
        @Override public boolean isResourceBlank() { return delegate().isResourceBlank(); }
        @Override public ItemVariant getResource() { return delegate().getResource(); }
        @Override public long getAmount() { return delegate().getAmount(); }
        @Override public long getCapacity() { return delegate().getCapacity(); }
    }
}
