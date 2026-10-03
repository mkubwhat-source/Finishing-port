package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;

public class ItemWithInfo extends Item {
    public ItemWithInfo(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        //        if (BountifulFares.CONFIG.isEnableItemGuideTooltips()) {
//            if (Screen.hasShiftDown()) {
//                writeInfo(tooltip);
//            } else {
//                tooltip.accept(Text.literal("§8Hold Shift for More Info..."));
//            }
//        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
    }

    public void writeInfo(List<Component> tooltip) {
        if (this == BFBlocks.FERMENTATION_VESSEL.get().asItem()) {
            tooltip.add(Component.literal("§7"+"Can be used to ferment ingredients"));
            tooltip.add(Component.literal("§7"+"into new ones."));
            tooltip.add(Component.literal("§7"+"Fill the vessel with a Water Bottle"));
            tooltip.add(Component.literal("§7"+"and an item to ferment something."));
        }
    }
}
