package alabaster.hearthandharvest.platform.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.TagValueInput;
import org.jspecify.annotations.Nullable;

/** Helpers for block entity data stored on items (26.3 {@code minecraft:block_entity_data}). */
public final class BlockEntityItems {
    /**
     * Registries for code that has no level at hand (e.g. {@code Item#getName}); set when a server
     * starts and when the client joins a world.
     */
    private static volatile HolderLookup.@Nullable Provider currentRegistries;

    private BlockEntityItems() {}

    public static void setCurrentRegistries(HolderLookup.@Nullable Provider registries) {
        currentRegistries = registries;
    }

    public static HolderLookup.@Nullable Provider currentRegistries() {
        return currentRegistries;
    }

    /** Replacement for 1.21.1's {@code BlockEntity#saveToItem}. */
    public static void saveToItem(BlockEntity blockEntity, ItemStack stack, HolderLookup.Provider registries) {
        stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(blockEntity.getType(), blockEntity.saveCustomOnly(registries)));
        stack.applyComponents(blockEntity.collectComponents());
    }

    /** Reads a container's "Items" list from an item's block entity data. Returns false if it has none. */
    public static boolean readItems(ItemStack stack, NonNullList<ItemStack> into, HolderLookup.@Nullable Provider registries) {
        TypedEntityData<BlockEntityType<?>> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null || registries == null) return false;
        ContainerHelper.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, registries, data.copyTagWithoutId()), into);
        return true;
    }
}
