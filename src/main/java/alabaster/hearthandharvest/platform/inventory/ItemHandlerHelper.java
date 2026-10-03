package alabaster.hearthandharvest.platform.inventory;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/** The parts of NeoForge's {@code ItemHandlerHelper} (1.21.1) that Hearth and Harvest uses, same logic. */
public final class ItemHandlerHelper {
    private ItemHandlerHelper() {
    }

    public static boolean canItemStacksStack(ItemStack a, ItemStack b) {
        if (a.isEmpty() || !ItemStack.isSameItemSameComponents(a, b)) return false;
        return a.isStackable();
    }

    public static ItemStack insertItem(@Nullable IItemHandler dest, ItemStack stack, boolean simulate) {
        if (dest == null || stack.isEmpty()) return stack;
        for (int i = 0; i < dest.getSlotCount(); i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    /** Inserts into slots already holding the same item first, then into empty slots. */
    public static ItemStack insertItemStacked(@Nullable IItemHandler inventory, ItemStack stack, boolean simulate) {
        if (inventory == null || stack.isEmpty()) return stack;
        if (!stack.isStackable()) return insertItem(inventory, stack, simulate);
        int size = inventory.getSlotCount();
        for (int i = 0; i < size; i++) {
            if (canItemStacksStack(inventory.getStackInSlot(i), stack)) {
                stack = inventory.insertItem(i, stack, simulate);
                if (stack.isEmpty()) break;
            }
        }
        if (!stack.isEmpty()) {
            for (int i = 0; i < size; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty()) break;
                }
            }
        }
        return stack;
    }

    /** Puts the stack in the player's inventory, dropping what doesn't fit at their feet. */
    public static void giveItemToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!player.getInventory().add(stack) && !player.level().isClientSide()) {
            ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY() + 0.5, player.getZ(), stack);
            entity.setPickUpDelay(40);
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0, 1, 0));
            player.level().addFreshEntity(entity);
        }
    }

    /** Comparator signal for an item handler, the same formula as vanilla containers. */
    public static int calcRedstoneFromInventory(@Nullable IItemHandler inv) {
        if (inv == null) return 0;
        int itemsFound = 0;
        float proportion = 0.0F;
        for (int j = 0; j < inv.getSlotCount(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                proportion += (float) itemstack.getCount() / (float) Math.min(inv.getSlotLimit(j), itemstack.getMaxStackSize());
                ++itemsFound;
            }
        }
        proportion = proportion / (float) inv.getSlotCount();
        return Mth.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
    }
}
