package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * HH's two creative tabs, filled in registration order as in 1.21.1. Items merged into another
 * bundled mod (flour, butter, pizza, ...) are listed in that mod's tab instead.
 */
public class HHModCreativeTabs {
    public static final ResourceKey<CreativeModeTab> BLOCKS_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, HearthAndHarvest.MODID + "_blocks"));

    public static final Supplier<CreativeModeTab> TAB_HEARTH_AND_HARVEST = BFRegistryHelper.register(HearthAndHarvest.MODID, HearthAndHarvest.MODID, BuiltInRegistries.CREATIVE_MODE_TAB,
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.hearthandharvest"))
                    .icon(() -> new ItemStack(HHModItems.RED_GRAPES.get()))
                    .displayItems((parameters, output) -> HHModItems.CREATIVE_TAB_ITEMS.forEach(item -> output.accept(item.get())))
                    .build());
    public static final Supplier<CreativeModeTab> TAB_HEARTH_AND_HARVEST_BLOCKS = BFRegistryHelper.register(HearthAndHarvest.MODID, HearthAndHarvest.MODID + "_blocks", BuiltInRegistries.CREATIVE_MODE_TAB,
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.hearthandharvest_blocks"))
                    .icon(() -> new ItemStack(HHModItems.RED_GRAPE_CRATE.get()))
                    .displayItems((parameters, output) -> HHModItems.CREATIVE_TAB_BLOCKS.forEach(item -> output.accept(item.get())))
                    .build());

    public static void init() {
    }
}
