// From FarmersDelightRefabricated 26.3 (MIT, vectorwing / MehVahdJukaar): its Fabric version of NeoForge's item-handler API.
package alabaster.hearthandharvest.platform.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public class ItemHandlerStackWrapper extends SingleStackStorage {
    private final IItemHandler handler;
    private final int slot;

    public ItemHandlerStackWrapper(IItemHandler handler, int slot) {
        this.handler = handler;
        this.slot = slot;
    }

    @Override
    protected ItemStack getStack() {
        return handler.getStackInSlot(slot);
    }

    @Override
    protected void setStack(ItemStack stack) {
        handler.setStackInSlot(slot, stack);
    }

    @Override
    protected int getCapacity(ItemVariant itemVariant) {
        if (itemVariant.isBlank())
            return handler.getSlotLimit(slot);

        return Math.min(handler.getSlotLimit(slot), itemVariant.toStack().getMaxStackSize());
    }
}
