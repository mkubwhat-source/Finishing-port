package alabaster.hearthandharvest.common.block.entity;

import alabaster.hearthandharvest.common.registry.HHModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import alabaster.hearthandharvest.platform.inventory.ItemStackHandler;

public class NestBlockEntity extends HHSyncedBlockEntity {
    public static final int SLOTS = 4;

    private final ItemStackHandler inventory = new ItemStackHandler(SLOTS) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (isEmpty()) crowStash = false;
            markUpdated();
        }
    };
    private boolean crowStash;

    public NestBlockEntity(BlockPos pos, BlockState state) {
        super(HHModBlockEntities.NEST.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack insert(ItemStack stack) {
        ItemStack remaining = stack;
        for (int slot = 0; slot < SLOTS && !remaining.isEmpty(); slot++) {
            remaining = inventory.insertItem(slot, remaining, false);
        }
        return remaining;
    }

    public ItemStack takeLast() {
        for (int slot = SLOTS - 1; slot >= 0; slot--) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                return inventory.extractItem(slot, stack.getCount(), false);
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean hasRoom() {
        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getCount() < Math.min(stack.getMaxStackSize(), inventory.getSlotLimit(slot))) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < SLOTS; slot++) {
            if (!inventory.getStackInSlot(slot).isEmpty()) return false;
        }
        return true;
    }

    public boolean hasCrowStash() {
        return crowStash;
    }

    public void markCrowStash() {
        crowStash = true;
        markUpdated();
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int slot = 0; slot < SLOTS; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, stack.copy());
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output.child("Inventory"));
        output.putBoolean("CrowStash", crowStash);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input.childOrEmpty("Inventory"));
        crowStash = input.getBooleanOr("CrowStash", false);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide()) dropContents(level, pos);
        super.preRemoveSideEffects(pos, state);
    }
}
