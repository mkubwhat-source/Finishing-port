package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class BlockItemWithInfo extends BlockItem {

    public BlockItemWithInfo(Block block, Properties settings) {
        super(block, settings);
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
            tooltip.add(Component.literal("§7"+"Can be used to ferment ingredients."));
            tooltip.add(Component.literal("§7"+"Fill the vessel with a Water Bottle"));
            tooltip.add(Component.literal("§7"+"and an item to ferment it."));
        }
//        if (this == ModBlocks.TRELLIS.asItem()) {
//            tooltip.accept(Text.literal("§7"+"Vine Crops and some plants can be"));
//            tooltip.accept(Text.literal("§7"+"grown on Trellises."));
//        }
    }
}
