package alabaster.hearthandharvest.common.block.entity.inventory;

import alabaster.hearthandharvest.common.block.entity.KegBlockEntity;
import alabaster.hearthandharvest.platform.inventory.IItemHandler;
import alabaster.hearthandharvest.platform.inventory.SidedItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/** Keg automation: top inserts containers, sides insert ingredients, outputs can be extracted from any side. */
public class KegItemHandler extends SidedItemHandler {
    private final @Nullable Direction side;

    public KegItemHandler(IItemHandler inventory, @Nullable Direction side) {
        super(inventory);
        this.side = side;
    }

    private static boolean isOutput(int slot) {
        return slot == KegBlockEntity.OUTPUT_SLOT_ONE
                || slot == KegBlockEntity.OUTPUT_SLOT_TWO
                || slot == KegBlockEntity.CONTAINER_OUTPUT_SLOT;
    }

    @Override
    protected boolean canInsert(int slot, ItemStack stack) {
        if (isOutput(slot)) return false;
        if (side == null) return true;
        return switch (side) {
            case UP -> slot == KegBlockEntity.CONTAINER_INPUT_SLOT;
            case DOWN -> false;
            default -> slot == KegBlockEntity.INPUT_SLOT_ONE || slot == KegBlockEntity.INPUT_SLOT_TWO;
        };
    }

    @Override
    protected boolean canExtract(int slot) {
        return isOutput(slot);
    }
}
