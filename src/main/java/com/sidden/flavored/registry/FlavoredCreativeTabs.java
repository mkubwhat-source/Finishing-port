package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * The Flavored creative tab, in 1.21.1's order. Flour and corn/corn seeds are the merged
 * Bountiful Fares items (they stay in Bountiful Fares' tab too). 1.21.1 listed the ham sandwich
 * twice, which 26.3's tab builder rejects; it is listed once.
 */
public final class FlavoredCreativeTabs {
    public static final Supplier<CreativeModeTab> FLAVORED = BFRegistryHelper.register(Flavored.MOD_ID, "flavored", BuiltInRegistries.CREATIVE_MODE_TAB,
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(FlavoredItems.RED_TOMATO.get()))
                    .title(Component.translatable("creativetab.flavored.flavored"))
                    .displayItems((parameters, output) -> {
                        output.accept(FlavoredBlocks.CHOCOLATE_BLOCK.get());
                        output.accept(FlavoredBlocks.CHOCOLATE_TILES.get());
                        output.accept(FlavoredBlocks.CHOCOLATE_TILE_STAIRS.get());
                        output.accept(FlavoredBlocks.CHOCOLATE_TILE_SLAB.get());
                        output.accept(FlavoredBlocks.CINNAMON_STALK.get());
                        output.accept(FlavoredBlocks.STRIPPED_CINNAMON_STALK.get());
                        output.accept(FlavoredBlocks.WAXED_STRIPPED_CINNAMON_STALK.get());
                        output.accept(FlavoredBlocks.CINNAMON_SPROUT.get());
                        output.accept(FlavoredBlocks.KEG.get());
                        output.accept(FlavoredBlocks.OVEN.get());
                        output.accept(FlavoredBlocks.MIXING_BOWL.get());
                        output.accept(FlavoredItems.KNIFE.get());
                        output.accept(FlavoredItems.WHISK.get());
                        output.accept(FlavoredItems.GROUND_BEEF.get());
                        output.accept(FlavoredItems.COOKED_GROUND_BEEF.get());
                        output.accept(FlavoredItems.CHICKEN_DRUMSTICK.get());
                        output.accept(FlavoredItems.COOKED_CHICKEN_DRUMSTICK.get());
                        output.accept(FlavoredItems.MUTTON_SHANK.get());
                        output.accept(FlavoredItems.COOKED_MUTTON_SHANK.get());
                        output.accept(FlavoredItems.PORK_JOWL.get());
                        output.accept(FlavoredItems.COOKED_PORK_JOWL.get());
                        output.accept(FlavoredItems.GREEN_TOMATO.get());
                        output.accept(FlavoredItems.YELLOW_TOMATO.get());
                        output.accept(FlavoredItems.RED_TOMATO.get());
                        output.accept(FlavoredItems.GARLIC.get());
                        output.accept(BFItems.MAIZE.get());
                        output.accept(FlavoredItems.PEPPER.get());
                        output.accept(FlavoredItems.SPINACH.get());
                        output.accept(FlavoredItems.CINNAMON.get());
                        output.accept(BFItems.FLOUR.get());
                        output.accept(FlavoredItems.BUTTER.get());
                        output.accept(FlavoredItems.DOUGH.get());
                        output.accept(FlavoredItems.PASTRY_DOUGH.get());
                        output.accept(FlavoredItems.COOKIE_DOUGH.get());
                        output.accept(FlavoredItems.PASTA.get());
                        output.accept(FlavoredItems.BATTER.get());
                        output.accept(FlavoredItems.CHOCOLATE.get());
                        output.accept(FlavoredItems.TOMATO_SEEDS.get());
                        output.accept(BFItems.MAIZE_SEEDS.get());
                        output.accept(FlavoredItems.PEPPER_SEEDS.get());
                        output.accept(FlavoredItems.SPINACH_SEEDS.get());
                        output.accept(FlavoredItems.SOFT_CHEESE_SLICE.get());
                        output.accept(FlavoredItems.AGED_CHEESE_SLICE.get());
                        output.accept(FlavoredBlocks.SOFT_CHEESE.get());
                        output.accept(FlavoredBlocks.AGED_CHEESE.get());
                        output.accept(FlavoredItems.DRIED_PEPPER.get());
                        output.accept(FlavoredItems.CHOCOLATE_EGG.get());
                        output.accept(FlavoredItems.GRILLED_CORN.get());
                        output.accept(FlavoredItems.HAMBURGER.get());
                        output.accept(FlavoredItems.SHAWARMA.get());
                        output.accept(FlavoredItems.HAM_SANDWICH.get());
                        output.accept(FlavoredItems.CHICKEN_SANDWICH.get());
                        output.accept(FlavoredItems.CHEESE_SANDWICH.get());
                        output.accept(FlavoredItems.GARLIC_BREAD.get());
                        output.accept(FlavoredItems.BUTTER_PASTRY.get());
                        output.accept(FlavoredItems.CINNAMON_PASTRY.get());
                        output.accept(FlavoredItems.CHOCOLATE_PASTRY.get());
                        output.accept(FlavoredItems.HONEY_PASTRY.get());
                        output.accept(FlavoredItems.PORRIDGE.get());
                        output.accept(FlavoredItems.POLENTA.get());
                        output.accept(FlavoredItems.SALAD.get());
                        output.accept(FlavoredItems.TOMATO_PASTA.get());
                        output.accept(FlavoredItems.PESTO_PASTA.get());
                        output.accept(FlavoredItems.CREAM_PASTA.get());
                        output.accept(FlavoredItems.CARBONARA_PASTA.get());
                        output.accept(FlavoredItems.RAGU_PASTA.get());
                        output.accept(FlavoredItems.OSSOBUCO.get());
                        output.accept(FlavoredItems.SHAKSHOUKA.get());
                        output.accept(FlavoredItems.CEREAL.get());
                        output.accept(FlavoredBlocks.PIZZA.get());
                        output.accept(FlavoredItems.PIZZA_SLICE.get());
                        output.accept(FlavoredBlocks.PUDDING.get());
                        output.accept(FlavoredItems.SWEET_BERRY_JUICE.get());
                        output.accept(FlavoredItems.GLOW_BERRY_JUICE.get());
                        output.accept(FlavoredItems.APPLE_JUICE.get());
                        output.accept(FlavoredItems.WORT.get());
                        output.accept(FlavoredItems.SWEET_BERRY_WINE.get());
                        output.accept(FlavoredItems.GLOW_BERRY_WINE.get());
                        output.accept(FlavoredItems.CIDER.get());
                        output.accept(FlavoredItems.BEER.get());
                        output.accept(FlavoredItems.HOT_SAUCE.get());
                        output.accept(FlavoredItems.CHOCKEN_SPAWN_EGG.get());
                    }).build());

    public static void init() {
    }

    private FlavoredCreativeTabs() {
    }
}
