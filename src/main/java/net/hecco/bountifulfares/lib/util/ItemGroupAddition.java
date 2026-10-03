package net.hecco.bountifulfares.lib.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

/**
 * Describes one entry to be spliced into a vanilla creative-mode tab.
 * Formerly {@code net.hecco.nexuslib.lib.util.ItemGroupAddition}; ported in-tree
 * as part of removing the NexusLib dependency.
 */
public record ItemGroupAddition(ItemGroupAdditionType type, Optional<ItemLike> origin, ItemStack stack) {
    public static ItemGroupAddition create(ItemLike origin, ItemStack stack) {
        return new ItemGroupAddition(ItemGroupAdditionType.ADD_AFTER, Optional.of(origin), stack);
    }
    public static ItemGroupAddition create(ItemStack stack) {
        return new ItemGroupAddition(ItemGroupAdditionType.ADD_LAST, Optional.empty(), stack);
    }
    public static ItemGroupAddition create(ItemGroupAdditionType type, ItemLike origin, ItemStack stack) {
        return new ItemGroupAddition(type, Optional.of(origin), stack);
    }
}
