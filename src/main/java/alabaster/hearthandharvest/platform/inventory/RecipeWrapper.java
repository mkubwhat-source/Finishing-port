// From FarmersDelightRefabricated 26.3 (MIT, vectorwing / MehVahdJukaar): its Fabric version of NeoForge's item-handler API.
package alabaster.hearthandharvest.platform.inventory;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Refabricated: Wrapper for ItemStackHandler.
 */
public class RecipeWrapper implements RecipeInput {

    private final IItemHandler handler;
    private final StackedItemContents stackedContents;
    private final int ingredientAmount;

    public RecipeWrapper(IItemHandler handler) {
        this.handler = handler;
        this.stackedContents = new StackedItemContents();
        int ingredientAmount = 0;
        for (int value : handler.getInputSlotIndexes()) {
            ItemStack itemstack = handler.getStackInSlot(value);
            if (!itemstack.isEmpty()) {
                ++ingredientAmount;
                stackedContents.accountStack(itemstack, 1);
            }
        }
        this.ingredientAmount = ingredientAmount;
    }

    public StackedItemContents stackedContents() {
        return stackedContents;
    }

    public int ingredientAmount() {
        return ingredientAmount;
    }

    @Override
    public ItemStack getItem(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public int size() {
        return handler.getSlotCount();
    }
}
