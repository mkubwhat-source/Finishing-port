package alabaster.hearthandharvest.common.block.entity.inventory;

import alabaster.hearthandharvest.common.block.entity.CaskBlockEntity;
import alabaster.hearthandharvest.platform.inventory.IItemHandler;
import alabaster.hearthandharvest.platform.inventory.SidedItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.BooleanSupplier;

/** Cask automation: top inserts into any input slot, each horizontal side into one input slot, bottom extracts results. Nothing moves while sealed except output. */
public class CaskItemHandler extends SidedItemHandler {
    private static final int SLOT_OUTPUT = CaskBlockEntity.FIRST_OUTPUT_SLOT;
    private static final int SLOTS_INPUT = CaskBlockEntity.INPUT_SLOTS;
    private final @Nullable Direction side;
    private final BooleanSupplier sealed;

    public CaskItemHandler(IItemHandler itemHandler, @Nullable Direction side, BooleanSupplier sealed) {
        super(itemHandler);
        this.side = side;
        this.sealed = sealed;
    }

    @Override
    protected boolean canExtract(int slot) {
        if (side == null || side == Direction.UP) {
            return slot < SLOTS_INPUT && !sealed.getAsBoolean();
        }
        return slot >= SLOT_OUTPUT;
    }

    @Override
    protected boolean canInsert(int slot, ItemStack stack) {
        if (sealed.getAsBoolean()) return false;
        if (side == null) return slot < SLOT_OUTPUT;
        return switch (side) {
            case UP -> slot < SLOT_OUTPUT;
            case NORTH -> slot == 0;
            case EAST -> slot == 1;
            case SOUTH -> slot == 2;
            case WEST -> slot == 3;
            default -> false;
        };
    }
}
