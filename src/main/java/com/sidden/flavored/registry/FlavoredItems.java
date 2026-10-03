package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.item.*;
import net.hecco.bountifulfares.platform.BFProperties;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Weapon;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Flavored's items. {@code flavored:flour}, {@code flavored:corn} and {@code flavored:corn_seeds}
 * are not registered: the bundle merges them into Bountiful Fares' flour, maize and maize seeds
 * (registry aliases in {@link FlavoredMerges} convert existing stacks).
 * <p>
 * 26.3: {@code ItemNameBlockItem} is gone - a plain {@code BlockItem} uses the item's own name by
 * default (see BFProperties). Enchantability, tool and weapon behavior are components.
 */
public final class FlavoredItems {
    // Ingredients
    public static final Supplier<Item> DOUGH = item("dough", Item::new, p -> p);
    public static final Supplier<Item> PASTRY_DOUGH = item("pastry_dough", Item::new, p -> p);
    public static final Supplier<Item> COOKIE_DOUGH = item("cookie_dough", Item::new, FlavoredFoods.COOKIE_DOUGH::apply);
    public static final Supplier<Item> BATTER = item("batter", Item::new, p -> p.stacksTo(1));
    public static final Supplier<Item> PASTA = item("pasta", Item::new, p -> p);
    public static final Supplier<Item> BUTTER = item("butter", Item::new, p -> p);
    public static final Supplier<Item> CHOCOLATE = item("chocolate", Item::new, FlavoredFoods.CHOCOLATE::apply);
    public static final Supplier<Item> SOFT_CHEESE_SLICE = item("soft_cheese_slice", SoftCheesyItem::new, FlavoredFoods.SOFT_CHEESE_SLICE::apply);
    public static final Supplier<Item> AGED_CHEESE_SLICE = item("aged_cheese_slice", AgedCheesyItem::new, FlavoredFoods.AGED_CHEESE_SLICE::apply);

    // Crops
    public static final Supplier<Item> GREEN_TOMATO = item("green_tomato", TomatoItem::new, FlavoredFoods.GREEN_TOMATO::apply);
    public static final Supplier<Item> YELLOW_TOMATO = item("yellow_tomato", TomatoItem::new, FlavoredFoods.YELLOW_TOMATO::apply);
    public static final Supplier<Item> RED_TOMATO = item("red_tomato", TomatoItem::new, FlavoredFoods.RED_TOMATO::apply);
    public static final Supplier<Item> GARLIC = item("garlic", p -> new BlockItem(FlavoredBlocks.GARLICS.get(), p), FlavoredFoods.GARLIC::apply);
    public static final Supplier<Item> PEPPER = item("pepper", Item::new, FlavoredFoods.PEPPER::apply);
    public static final Supplier<Item> SPINACH = item("spinach", Item::new, FlavoredFoods.SPINACH::apply);
    public static final Supplier<Item> CINNAMON = item("cinnamon", Item::new, p -> p);

    // Seeds
    public static final Supplier<Item> TOMATO_SEEDS = item("tomato_seeds", p -> new BlockItem(FlavoredBlocks.TOMATO_BUSH.get(), p), p -> p);
    public static final Supplier<Item> PEPPER_SEEDS = item("pepper_seeds", p -> new BlockItem(FlavoredBlocks.PEPPER_BUSH.get(), p), p -> p);
    public static final Supplier<Item> SPINACH_SEEDS = item("spinach_seeds", p -> new BlockItem(FlavoredBlocks.SPINACH_BUSH.get(), p), p -> p);

    // Raw cuts
    public static final Supplier<Item> GROUND_BEEF = item("ground_beef", Item::new, FlavoredFoods.GROUND_BEEF::apply);
    public static final Supplier<Item> CHICKEN_DRUMSTICK = item("chicken_drumstick", Item::new, FlavoredFoods.CHICKEN_DRUMSTICK::apply);
    public static final Supplier<Item> MUTTON_SHANK = item("mutton_shank", Item::new, FlavoredFoods.MUTTON_SHANK::apply);
    public static final Supplier<Item> PORK_JOWL = item("pork_jowl", Item::new, FlavoredFoods.PORK_JOWL::apply);

    // Cooked cuts
    public static final Supplier<Item> COOKED_GROUND_BEEF = item("cooked_ground_beef", Item::new, FlavoredFoods.COOKED_GROUND_BEEF::apply);
    public static final Supplier<Item> COOKED_CHICKEN_DRUMSTICK = item("cooked_chicken_drumstick", Item::new, FlavoredFoods.COOKED_CHICKEN_DRUMSTICK::apply);
    public static final Supplier<Item> COOKED_MUTTON_SHANK = item("cooked_mutton_shank", Item::new, FlavoredFoods.COOKED_MUTTON_SHANK::apply);
    public static final Supplier<Item> COOKED_PORK_JOWL = item("cooked_pork_jowl", Item::new, FlavoredFoods.COOKED_PORK_JOWL::apply);

    // Normal foodstuffs
    public static final Supplier<Item> PIZZA_SLICE = item("pizza_slice", SoftCheesyItem::new, FlavoredFoods.PIZZA_SLICE::apply);
    public static final Supplier<Item> HAMBURGER = item("hamburger", AgedCheesyItem::new, FlavoredFoods.HAMBURGER::apply);
    public static final Supplier<Item> HAM_SANDWICH = item("ham_sandwich", Item::new, FlavoredFoods.HAM_SANDWICH::apply);
    public static final Supplier<Item> CHICKEN_SANDWICH = item("chicken_sandwich", Item::new, FlavoredFoods.CHICKEN_SANDWICH::apply);
    public static final Supplier<Item> SHAWARMA = item("shawarma", Item::new, FlavoredFoods.SHAWARMA::apply);
    public static final Supplier<Item> CHEESE_SANDWICH = item("cheese_sandwich", Item::new, FlavoredFoods.CHEESE_SANDWICH::apply);
    public static final Supplier<Item> GRILLED_CORN = item("grilled_corn", Item::new, FlavoredFoods.GRILLED_CORN::apply);
    public static final Supplier<Item> BUTTER_PASTRY = item("butter_pastry", Item::new, FlavoredFoods.BUTTER_PASTRY::apply);
    public static final Supplier<Item> HONEY_PASTRY = item("honey_pastry", Item::new, FlavoredFoods.HONEY_PASTRY::apply);
    public static final Supplier<Item> CHOCOLATE_PASTRY = item("chocolate_pastry", Item::new, FlavoredFoods.CHOCOLATE_PASTRY::apply);
    public static final Supplier<Item> CINNAMON_PASTRY = item("cinnamon_pastry", Item::new, FlavoredFoods.CINNAMON_PASTRY::apply);
    public static final Supplier<Item> GARLIC_BREAD = item("garlic_bread", Item::new, FlavoredFoods.GARLIC_BREAD::apply);

    // Dishes
    public static final Supplier<Item> PORRIDGE = item("porridge", Item::new, p -> FlavoredFoods.PORRIDGE.apply(p).stacksTo(1));
    public static final Supplier<Item> SHAKSHOUKA = item("shakshouka", Item::new, p -> FlavoredFoods.SHAKSHOUKA.apply(p).stacksTo(1));
    public static final Supplier<Item> TOMATO_PASTA = item("tomato_pasta", Item::new, p -> FlavoredFoods.TOMATO_PASTA.apply(p).stacksTo(1));
    public static final Supplier<Item> PESTO_PASTA = item("pesto_pasta", Item::new, p -> FlavoredFoods.PESTO_PASTA.apply(p).stacksTo(1));
    public static final Supplier<Item> CREAM_PASTA = item("cream_pasta", AgedCheesyItem::new, p -> FlavoredFoods.CREAM_PASTA.apply(p).stacksTo(1));
    public static final Supplier<Item> CARBONARA_PASTA = item("carbonara_pasta", AgedCheesyItem::new, p -> FlavoredFoods.CARBONARA_PASTA.apply(p).stacksTo(1));
    public static final Supplier<Item> RAGU_PASTA = item("ragu_pasta", Item::new, p -> FlavoredFoods.RAGU_PASTA.apply(p).stacksTo(1));
    public static final Supplier<Item> OSSOBUCO = item("ossobuco", Item::new, p -> FlavoredFoods.OSSOBUCO.apply(p).stacksTo(1));
    public static final Supplier<Item> SALAD = item("salad", Item::new, p -> FlavoredFoods.SALAD.apply(p).stacksTo(1));
    public static final Supplier<Item> CEREAL = item("cereal", Item::new, p -> FlavoredFoods.CEREAL.apply(p).stacksTo(1));
    public static final Supplier<Item> POLENTA = item("polenta", Item::new, p -> FlavoredFoods.POLENTA.apply(p).stacksTo(1));

    // Drinks (1.21.1 DrinkItem: drink animation/sound, 40 ticks, returns a glass bottle - all
    // expressed by the consumable + use_remainder components now)
    public static final Supplier<Item> SWEET_BERRY_JUICE = item("sweet_berry_juice", Item::new, FlavoredFoods.SWEET_BERRY_JUICE::apply);
    public static final Supplier<Item> GLOW_BERRY_JUICE = item("glow_berry_juice", Item::new, FlavoredFoods.GLOW_BERRY_JUICE::apply);
    public static final Supplier<Item> WORT = item("wort", Item::new, FlavoredFoods.WORT::apply);
    public static final Supplier<Item> APPLE_JUICE = item("apple_juice", Item::new, FlavoredFoods.APPLE_JUICE::apply);

    // Beverages
    // Hearth and Harvest merge: Flavored's berry wines are also HH's sweet/glow berry wines, so they are
    // HH wine bottles (3 glasses, cask aging into vintages, Drunk scaled by vintage) with HH's effects.
    public static final Supplier<Item> SWEET_BERRY_WINE = item("sweet_berry_wine",
            p -> new alabaster.hearthandharvest.common.item.WineBottleItem(() -> alabaster.hearthandharvest.common.registry.HHModFluids.SWEET_BERRY_WINE.source().get(), p, true, false).glasses(3),
            p -> alabaster.hearthandharvest.common.item.WineBottleItem.properties(p, alabaster.hearthandharvest.common.HHFoodValues.SWEET_BERRY_WINE));
    public static final Supplier<Item> GLOW_BERRY_WINE = item("glow_berry_wine",
            p -> new alabaster.hearthandharvest.common.item.WineBottleItem(() -> alabaster.hearthandharvest.common.registry.HHModFluids.GLOW_BERRY_WINE.source().get(), p, true, false).glasses(3),
            p -> alabaster.hearthandharvest.common.item.WineBottleItem.properties(p, alabaster.hearthandharvest.common.HHFoodValues.GLOW_BERRY_WINE));
    public static final Supplier<Item> BEER = item("beer", Item::new, p -> FlavoredFoods.BEER.apply(p).stacksTo(1));
    public static final Supplier<Item> CIDER = item("cider", Item::new, p -> FlavoredFoods.CIDER.apply(p).stacksTo(1));

    // Other drinkables
    public static final Supplier<Item> HOT_SAUCE = item("hot_sauce", HotSauceItem::new, p -> p.stacksTo(1).durability(16));

    // Tools
    public static final Supplier<Item> KNIFE = item("knife", KnifeItem::new, p -> p.durability(200).enchantable(14)
            .component(DataComponents.TOOL, KnifeItem.createToolProperties())
            .component(DataComponents.WEAPON, new Weapon(1))
            .attributes(KnifeItem.createAttributes()));
    public static final Supplier<Item> WHISK = item("whisk", Item::new, p -> p.durability(2000).enchantable(14)
            .component(DataComponents.TOOL, WhiskItem.createToolProperties()));

    // Spawn eggs (26.3: the entity type is the spawn_egg item's entity_data component; the egg's
    // colors are its texture - see assets/flavored/items/chocken_spawn_egg.json)
    public static final Supplier<Item> CHOCKEN_SPAWN_EGG = item("chocken_spawn_egg", net.minecraft.world.item.SpawnEggItem::new, p -> p.spawnEgg(FlavoredEntities.CHOCKEN.get()));

    // Other
    public static final Supplier<Item> CHOCOLATE_EGG = item("chocolate_egg", ChocolateEggItem::new, p -> FlavoredFoods.CHOCOLATE_EGG.apply(p).stacksTo(16));
    public static final Supplier<Item> DRIED_PEPPER = item("dried_pepper", Item::new, FlavoredFoods.DRIED_PEPPER::apply);

    private static Supplier<Item> item(String name, Function<Item.Properties, Item> factory, Function<Item.Properties, Item.Properties> properties) {
        return BFRegistryHelper.registerItem(Flavored.MOD_ID, name, () -> factory.apply(properties.apply(BFProperties.item())));
    }

    public static void init() {
    }

    private FlavoredItems() {
    }
}
