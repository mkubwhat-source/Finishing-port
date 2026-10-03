package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.common.HHFood;
import alabaster.hearthandharvest.common.fd.item.*;
import com.sidden.flavored.registry.FlavoredBlocks;
import com.sidden.flavored.registry.FlavoredItems;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.VillagerFood;

import net.hecco.bountifulfares.platform.BFProperties;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.HHFoodValues;
import alabaster.hearthandharvest.common.item.GoatMilkBucketItem;
import alabaster.hearthandharvest.common.item.*;
import alabaster.hearthandharvest.common.block.trellis.TrellisMaterial;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.fabricmc.loader.api.FabricLoader;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class HHModItems {
    public static LinkedHashSet<Supplier<Item>> CREATIVE_TAB_ITEMS = Sets.newLinkedHashSet();
    public static LinkedHashSet<Supplier<Item>> CREATIVE_TAB_BLOCKS = Sets.newLinkedHashSet();

    public static Supplier<Item> registerWithTab(String name, Supplier<Item> supplier) {
        Supplier<Item> item = BFRegistryHelper.registerItem(HearthAndHarvest.MODID, name, supplier);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    public static Supplier<Item> registerWithBlockTab(String name, Supplier<Item> supplier) {
        Supplier<Item> item = BFRegistryHelper.registerItem(HearthAndHarvest.MODID, name, supplier);
        CREATIVE_TAB_BLOCKS.add(item);
        return item;
    }

    public static Supplier<Item> registerWithNoTab(String name, Supplier<Item> supplier) {
        return BFRegistryHelper.registerItem(HearthAndHarvest.MODID, name, supplier);
    }

    /**
     * An item of another mod in this bundle that replaced a Hearth and Harvest item (see the merge
     * list in HHRegistryAliases). HH code keeps referring to it by its old field name.
     */
    private static Supplier<Item> merged(Supplier<? extends net.minecraft.world.level.ItemLike> item) {
        return () -> item.get().asItem();
    }

    // Helper methods (26.3: properties must carry the registry id, see BFProperties)
    public static Item.Properties basicItem() {
        return BFProperties.item();
    }

    /** Block items named after their block (1.21.1 BlockItem used the block's translation key). */
    public static Item.Properties blockItem() {
        return BFProperties.item().useBlockDescriptionPrefix();
    }

    /** 1.21.1: FD KnifeItem(tier) + CleaverItem.createAttributes(tier, 2.0, -3.0). */
    public static Item.Properties cleaverItem(ToolMaterial material) {
        return KnifeItem.knifeProperties(BFProperties.item(), material, 2.0F, -3.0F);
    }

    /** 1.21.1 TrellisBlockItem.getBurnTime: 300 ticks. */
    public static Item.Properties trellisItem() {
        return BFProperties.item().component(DataComponents.COOKING_FUEL, new net.minecraft.world.item.component.CookingFuel(
                new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(300), new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant(1.0F)));
    }

    public static Item.Properties foodItem(HHFood food) {
        return food.eat(BFProperties.item());
    }

    public static Item.Properties bowlFoodItem(HHFood food) {
        return food.eat(BFProperties.item()).craftRemainder(Items.BOWL).stacksTo(16);
    }

    public static Item.Properties drinkItem(HHFood food) {
        return food.drink(BFProperties.item()).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
    }

    public static Item.Properties jarItem(HHFood food) {
        return food.eat(BFProperties.item()).craftRemainder(HHModItems.JAR.get()).stacksTo(16);
    }


    // Tools
    public static final Supplier<Item> FLINT_CLEAVER = registerWithTab("flint_cleaver",
            () -> new CleaverItem(cleaverItem(KnifeItem.FLINT)));
    public static final Supplier<Item> IRON_CLEAVER = registerWithTab("iron_cleaver",
            () -> new CleaverItem(cleaverItem(ToolMaterial.IRON)));
    public static final Supplier<Item> DIAMOND_CLEAVER = registerWithTab("diamond_cleaver",
            () -> new CleaverItem(cleaverItem(ToolMaterial.DIAMOND)));
    public static final Supplier<Item> NETHERITE_CLEAVER = registerWithTab("netherite_cleaver",
            () -> new CleaverItem(cleaverItem(ToolMaterial.NETHERITE).fireResistant()));
    public static final Supplier<Item> GOLDEN_CLEAVER = registerWithTab("golden_cleaver",
            () -> new CleaverItem(cleaverItem(ToolMaterial.GOLD)));

    public static final Supplier<Item> PITCHFORK = registerWithTab("pitchfork",
            () -> new PitchforkItem(BFProperties.item()
                    .durability(250)
                    .attributes(PitchforkItem.createAttributes())));

    public static final Supplier<Item> WATERING_CAN = registerWithTab("watering_can",
            () -> new WateringCanItem(basicItem()));
    public static final Supplier<Item> FERTILIZER_BAG = registerWithTab("fertilizer_bag",
            () -> new FertilizerBagItem(basicItem().stacksTo(1).durability(8)));
    public static final Supplier<Item> UNIVERSAL_FEED = registerWithTab("universal_feed",
            () -> new UniversalFeedItem(basicItem()));
    public static final Supplier<Item> SEED_POUCH = registerWithTab("seed_pouch",
            () -> new SeedPouchItem(basicItem().stacksTo(1)));

    public static final Supplier<Item> FARMERS_HAT = registerWithTab("farmers_hat",
            () -> new FarmersHatItem(basicItem().stacksTo(1)));

    // Spawn Eggs
    public static final Supplier<Item> CROW_SPAWN_EGG = registerWithTab("crow_spawn_egg",
            () -> new SpawnEggItem(BFProperties.item().spawnEgg(HHModEntities.CROW.get())));
    public static final Supplier<Item> CROW_FEATHER = registerWithTab("crow_feather",
            () -> new Item(basicItem()));

    // Workstations
    public static final Supplier<Item> TREE_TAPPER = registerWithTab("tree_tapper",
            () -> new BlockItem(HHModBlocks.TREE_TAPPER.get(), blockItem()));
    public static final Supplier<Item> CASK = registerWithTab("cask",
            () -> new BlockItem(HHModBlocks.CASK.get(), blockItem()));
    public static final Supplier<Item> KEG = registerWithTab("keg",
            () -> new BlockItem(HHModBlocks.KEG.get(), blockItem()));
    public static final Supplier<Item> STOMPING_BASIN = registerWithTab("stomping_basin",
            () -> new BlockItem(HHModBlocks.STOMPING_BASIN.get(), blockItem()));
    public static final Supplier<Item> JUG = registerWithTab("jug",
            () -> new JugBlockItem(HHModBlocks.JUG.get(), blockItem()));
    public static final Supplier<Item> TROUGH = registerWithTab("trough",
            () -> new BlockItem(HHModBlocks.TROUGH.get(), blockItem()));
    public static final Supplier<Item> SPRINKLER = registerWithTab("sprinkler",
            () -> new BlockItem(HHModBlocks.SPRINKLER.get(), blockItem()));

    public static final Supplier<Item> COUNTER = registerWithBlockTab("counter",
            () -> new BlockItem(HHModBlocks.COUNTER.get(), blockItem()));
    public static final Supplier<Item> DRAWER = registerWithBlockTab("drawer",
            () -> new BlockItem(HHModBlocks.DRAWER.get(), blockItem()));
    public static final Supplier<Item> BASIN = registerWithBlockTab("basin",
            () -> new BlockItem(HHModBlocks.BASIN.get(), blockItem()));

    public static final Supplier<Item> NEST = registerWithBlockTab("nest",
            () -> new BlockItem(HHModBlocks.NEST.get(), blockItem()));

    public static final Supplier<Item> HAY_RUG = registerWithBlockTab("hay_rug",
            () -> new BlockItem(HHModBlocks.HAY_RUG.get(), blockItem()));
    public static final Supplier<Item> STRAW_RUG = registerWithBlockTab("straw_rug",
            () -> new BlockItem(HHModBlocks.STRAW_RUG.get(), blockItem()));

    public static final Supplier<Item> SCARECROW = registerWithBlockTab("scarecrow",
            () -> new BlockItem(HHModBlocks.SCARECROW.get(), basicItem()));

    public static final Supplier<Item> TRELLIS = registerWithBlockTab("trellis",
            () -> new TrellisBlockItem(HHModBlocks.TRELLIS.get(), TrellisMaterial.STICK, trellisItem()));
    public static final Supplier<Item> BAMBOO_TRELLIS = registerWithBlockTab("bamboo_trellis",
            () -> new TrellisBlockItem(HHModBlocks.TRELLIS.get(), TrellisMaterial.BAMBOO, trellisItem()));
    public static final Supplier<Item> STRIPPED_BAMBOO_TRELLIS = registerWithBlockTab("stripped_bamboo_trellis",
            () -> new TrellisBlockItem(HHModBlocks.TRELLIS.get(), TrellisMaterial.STRIPPED_BAMBOO, trellisItem()));

    public static final Supplier<Item> HORSESHOE = registerWithTab("horseshoe",
            () -> new HorseshoeItem (basicItem()));

    // Half-Cabinets
    public static final Supplier<Item> OAK_HALF_CABINET = registerWithBlockTab("oak_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.OAK_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> SPRUCE_HALF_CABINET = registerWithBlockTab("spruce_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.SPRUCE_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> BIRCH_HALF_CABINET = registerWithBlockTab("birch_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.BIRCH_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> JUNGLE_HALF_CABINET = registerWithBlockTab("jungle_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.JUNGLE_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> ACACIA_HALF_CABINET = registerWithBlockTab("acacia_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.ACACIA_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> DARK_OAK_HALF_CABINET = registerWithBlockTab("dark_oak_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.DARK_OAK_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> MANGROVE_HALF_CABINET = registerWithBlockTab("mangrove_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.MANGROVE_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> CHERRY_HALF_CABINET = registerWithBlockTab("cherry_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.CHERRY_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> BAMBOO_HALF_CABINET = registerWithBlockTab("bamboo_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.BAMBOO_HALF_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> CRIMSON_HALF_CABINET = registerWithBlockTab("crimson_half_cabinet",
            () -> new BlockItem(HHModBlocks.CRIMSON_HALF_CABINET.get(), blockItem()));
    public static final Supplier<Item> WARPED_HALF_CABINET = registerWithBlockTab("warped_half_cabinet",
            () -> new BlockItem(HHModBlocks.WARPED_HALF_CABINET.get(), blockItem()));

    // Crabbers Delight Compat
    public static final Supplier<Item> PALM_HALF_CABINET = FabricLoader.getInstance().isModLoaded("crabbersdelight")
            ? registerWithBlockTab("palm_half_cabinet",
            () -> new FuelBlockItem(HHModBlocks.PALM_HALF_CABINET.get(), blockItem(), 300))
            : null;

    public static final Supplier<Item> CRATE = registerWithBlockTab("crate",
            () -> new CrateBlockItem(HHModBlocks.CRATE.get(), blockItem().stacksTo(16)));

    // Bottle Racks
    public static final Supplier<Item> OAK_BOTTLE_RACK = registerWithBlockTab("oak_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.OAK_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> SPRUCE_BOTTLE_RACK = registerWithBlockTab("spruce_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.SPRUCE_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> BIRCH_BOTTLE_RACK = registerWithBlockTab("birch_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.BIRCH_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> JUNGLE_BOTTLE_RACK = registerWithBlockTab("jungle_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.JUNGLE_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> ACACIA_BOTTLE_RACK = registerWithBlockTab("acacia_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.ACACIA_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> DARK_OAK_BOTTLE_RACK = registerWithBlockTab("dark_oak_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.DARK_OAK_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> MANGROVE_BOTTLE_RACK = registerWithBlockTab("mangrove_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.MANGROVE_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> CHERRY_BOTTLE_RACK = registerWithBlockTab("cherry_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.CHERRY_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> BAMBOO_BOTTLE_RACK = registerWithBlockTab("bamboo_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.BAMBOO_BOTTLE_RACK.get(), blockItem(), 300));
    public static final Supplier<Item> CRIMSON_BOTTLE_RACK = registerWithBlockTab("crimson_bottle_rack",
            () -> new BlockItem(HHModBlocks.CRIMSON_BOTTLE_RACK.get(), blockItem()));
    public static final Supplier<Item> WARPED_BOTTLE_RACK = registerWithBlockTab("warped_bottle_rack",
            () -> new BlockItem(HHModBlocks.WARPED_BOTTLE_RACK.get(), blockItem()));

    // Crabbers Delight Compat
    public static final Supplier<Item> PALM_BOTTLE_RACK = FabricLoader.getInstance().isModLoaded("crabbersdelight")
            ? registerWithBlockTab("palm_bottle_rack",
            () -> new FuelBlockItem(HHModBlocks.PALM_BOTTLE_RACK.get(), blockItem(), 300))
            : null;

    // Crops
    public static final Supplier<Item> BLUEBERRIES = FabricLoader.getInstance().isModLoaded("berry_good")
            ? registerWithTab("blueberries", () -> new Item(foodItem(HHFoodValues.BLUEBERRIES)))
            : registerWithTab("blueberries", () -> new BlockItem(HHModBlocks.BLUEBERRY_BUSH.get(), foodItem(HHFoodValues.BLUEBERRIES)));
    public static final Supplier<Item> CHERRY = registerWithTab("cherry",
            () -> new Item(foodItem(HHFoodValues.CHERRY)));
    public static final Supplier<Item> RASPBERRY = FabricLoader.getInstance().isModLoaded("berry_good")
            ? registerWithTab("raspberry", () -> new Item(foodItem(HHFoodValues.RASPBERRY)))
            :  registerWithTab("raspberry", () -> new BlockItem(HHModBlocks.RASPBERRY_BUSH.get(), foodItem(HHFoodValues.RASPBERRY)));
    public static final Supplier<Item> RED_GRAPES = registerWithTab("red_grapes",
            () -> new Item(foodItem(HHFoodValues.GRAPES)));
    public static final Supplier<Item> GREEN_GRAPES = registerWithTab("green_grapes",
            () -> new Item(foodItem(HHFoodValues.GRAPES)));
    public static final Supplier<Item> PEANUT = registerWithTab("peanut",
            () -> new BlockItem(HHModBlocks.PEANUT_CROP.get(), foodItem(HHFoodValues.PEANUT)));
    public static final Supplier<Item> COTTON_SEEDS = registerWithTab("cotton_seeds",
            () -> new BlockItem(HHModBlocks.COTTON_CROP.get(), basicItem()));
    public static final Supplier<Item> COTTON = registerWithTab("cotton",
            () -> new Item(basicItem()));
    public static final Supplier<Item> CORN = registerWithTab("corn",
            () -> new ConsumableItem(foodItem(HHFoodValues.CORN)));
    public static final Supplier<Item> CORN_KERNELS = registerWithTab("corn_kernels",
            () -> new BlockItem(HHModBlocks.CORN_STALK.get(), basicItem()));
    public static final Supplier<Item> CORN_HUSK = registerWithTab("corn_husk",
            () -> new Item(basicItem()));

    // Berry Good Pips
    public static final Supplier<Item> BLUEBERRY_PIPS = FabricLoader.getInstance().isModLoaded("berry_good")
            ? registerWithTab("blueberry_pips",
            () -> new BlockItem(HHModBlocks.BLUEBERRY_BUSH.get(), basicItem()))
            : null;
    public static final Supplier<Item> RASPBERRY_PIPS = FabricLoader.getInstance().isModLoaded("berry_good")
            ? registerWithTab("raspberry_pips",
            () -> new BlockItem(HHModBlocks.RASPBERRY_BUSH.get(), basicItem()))
            : null;

    // Wild Crops
    public static final Supplier<Item> WILD_RED_GRAPES = registerWithTab("wild_red_grapes",
            () -> new BlockItem(HHModBlocks.WILD_RED_GRAPES.get(), basicItem()));
    public static final Supplier<Item> WILD_GREEN_GRAPES = registerWithTab("wild_green_grapes",
            () -> new BlockItem(HHModBlocks.WILD_GREEN_GRAPES.get(), blockItem()));
    public static final Supplier<Item> WILD_COTTON = registerWithTab("wild_cotton",
            () -> new BlockItem(HHModBlocks.WILD_COTTON.get(), blockItem()));
    public static final Supplier<Item> WILD_PEANUTS = registerWithTab("wild_peanuts",
            () -> new BlockItem(HHModBlocks.WILD_PEANUTS.get(), blockItem()));

    // Flowers
    public static final Supplier<Item> YELLOW_MUM = registerWithTab("yellow_mum",
            () -> new BlockItem(HHModBlocks.YELLOW_MUM.get(), blockItem()));
    public static final Supplier<Item> ORANGE_MUM = registerWithTab("orange_mum",
            () -> new BlockItem(HHModBlocks.ORANGE_MUM.get(), blockItem()));
    public static final Supplier<Item> RED_MUM = registerWithTab("red_mum",
            () -> new BlockItem(HHModBlocks.RED_MUM.get(), blockItem()));
    public static final Supplier<Item> BLUE_MUM = registerWithTab("blue_mum",
            () -> new BlockItem(HHModBlocks.BLUE_MUM.get(), blockItem()));
    public static final Supplier<Item> LIGHT_BLUE_MUM = registerWithTab("light_blue_mum",
            () -> new BlockItem(HHModBlocks.LIGHT_BLUE_MUM.get(), blockItem()));
    public static final Supplier<Item> PURPLE_MUM = registerWithTab("purple_mum",
            () -> new BlockItem(HHModBlocks.PURPLE_MUM.get(), blockItem()));
    public static final Supplier<Item> PINK_MUM = registerWithTab("pink_mum",
            () -> new BlockItem(HHModBlocks.PINK_MUM.get(), blockItem()));
    public static final Supplier<Item> WHITE_MUM = registerWithTab("white_mum",
            () -> new BlockItem(HHModBlocks.WHITE_MUM.get(), blockItem()));

    // Crates
    public static final Supplier<Item> BLUEBERRY_CRATE = registerWithBlockTab("blueberry_crate",
            () -> new BlockItem(HHModBlocks.BLUEBERRY_CRATE.get(), blockItem()));
    public static final Supplier<Item> CHERRY_CRATE = registerWithBlockTab("cherry_crate",
            () -> new BlockItem(HHModBlocks.CHERRY_CRATE.get(), blockItem()));
    public static final Supplier<Item> RASPBERRY_CRATE = registerWithBlockTab("raspberry_crate",
            () -> new BlockItem(HHModBlocks.RASPBERRY_CRATE.get(), blockItem()));
    public static final Supplier<Item> RED_GRAPE_CRATE = registerWithBlockTab("red_grape_crate",
            () -> new BlockItem(HHModBlocks.RED_GRAPE_CRATE.get(), blockItem()));
    public static final Supplier<Item> GREEN_GRAPE_CRATE = registerWithBlockTab("green_grape_crate",
            () -> new BlockItem(HHModBlocks.GREEN_GRAPE_CRATE.get(), blockItem()));
    public static final Supplier<Item> PEANUT_CRATE = registerWithBlockTab("peanut_crate",
            () -> new BlockItem(HHModBlocks.PEANUT_CRATE.get(), blockItem()));
    public static final Supplier<Item> CORN_CRATE = registerWithBlockTab("corn_crate",
            () -> new BlockItem(HHModBlocks.CORN_CRATE.get(), blockItem()));
    public static final Supplier<Item> APPLE_CRATE = registerWithBlockTab("apple_crate",
            () -> new BlockItem(HHModBlocks.APPLE_CRATE.get(), blockItem()));
    public static final Supplier<Item> GOLDEN_APPLE_CRATE = registerWithBlockTab("golden_apple_crate",
            () -> new BlockItem(HHModBlocks.GOLDEN_APPLE_CRATE.get(), blockItem()));
    public static final Supplier<Item> GOLDEN_CARROT_CRATE = registerWithBlockTab("golden_carrot_crate",
            () -> new BlockItem(HHModBlocks.GOLDEN_CARROT_CRATE.get(), blockItem()));
    public static final Supplier<Item> GLISTERING_MELON_CRATE = registerWithBlockTab("glistering_melon_crate",
            () -> new BlockItem(HHModBlocks.GLISTERING_MELON_CRATE.get(), blockItem()));
    public static final Supplier<Item> POISONOUS_POTATO_CRATE = registerWithBlockTab("poisonous_potato_crate",
            () -> new BlockItem(HHModBlocks.POISONOUS_POTATO_CRATE.get(), blockItem()));
    public static final Supplier<Item> GLOW_BERRY_CRATE = registerWithBlockTab("glow_berry_crate",
            () -> new BlockItem(HHModBlocks.GLOW_BERRY_CRATE.get(), blockItem()));
    public static final Supplier<Item> SWEET_BERRY_CRATE = registerWithBlockTab("sweet_berry_crate",
            () -> new BlockItem(HHModBlocks.SWEET_BERRY_CRATE.get(), blockItem()));

    // Bags
    public static final Supplier<Item> SALT_BAG = registerWithBlockTab("salt_bag",
            () -> new BlockItem(HHModBlocks.SALT_BAG.get(), blockItem()));
    public static final Supplier<Item> SUGAR_BAG = registerWithBlockTab("sugar_bag",
            () -> new BlockItem(HHModBlocks.SUGAR_BAG.get(), blockItem()));
    public static final Supplier<Item> COCOA_BEAN_BAG = registerWithBlockTab("cocoa_bean_bag",
            () -> new BlockItem(HHModBlocks.COCOA_BEAN_BAG.get(), blockItem()));
    public static final Supplier<Item> GUNPOWDER_BAG = registerWithBlockTab("gunpowder_bag",
            () -> new BlockItem(HHModBlocks.GUNPOWDER_BAG.get(), blockItem()));
    public static final Supplier<Item> CORN_KERNEL_BAG = registerWithBlockTab("corn_kernel_bag",
            () -> new BlockItem(HHModBlocks.CORN_KERNEL_BAG.get(), blockItem()));
    public static final Supplier<Item> FLOUR_BAG = merged(BFBlocks.FLOUR_BLOCK);
    public static final Supplier<Item> MANURE_BAG = registerWithBlockTab("manure_bag",
            () -> new BlockItem(HHModBlocks.MANURE_BAG.get(), blockItem()));
    public static final Supplier<Item> FEATHER_BAG = registerWithBlockTab("feather_bag",
            () -> new BlockItem(HHModBlocks.FEATHER_BAG.get(), blockItem()));

    // Misc
    public static final Supplier<Item> COTTON_BALE = registerWithBlockTab("cotton_bale",
            () -> new BlockItem(HHModBlocks.COTTON_BALE.get(), blockItem()));
    public static final Supplier<Item> SUGAR_CANE_BUNDLE = registerWithBlockTab("sugar_cane_bundle",
            () -> new BlockItem(HHModBlocks.SUGAR_CANE_BUNDLE.get(), blockItem()));
    public static final Supplier<Item> SPOOL = registerWithBlockTab("spool",
            () -> new BlockItem(HHModBlocks.SPOOL.get(), blockItem()));
    public static final Supplier<Item> ROPE_COIL = registerWithBlockTab("rope_coil",
            () -> new BlockItem(HHModBlocks.ROPE_COIL.get(), blockItem()));
    public static final Supplier<Item> CORN_HUSK_BUNDLE = registerWithBlockTab("corn_husk_bundle",
            () -> new FuelBlockItem(HHModBlocks.CORN_HUSK_BUNDLE.get(), blockItem(), 4000 ));
    public static final Supplier<Item> CHARCOAL_BLOCK = registerWithBlockTab("charcoal_block",
            () -> new FuelBlockItem(HHModBlocks.CHARCOAL_BLOCK.get(), blockItem(), 16000));
    public static final Supplier<Item> STICK_BRUSH = registerWithBlockTab("stick_brush",
            () -> new FuelBlockItem(HHModBlocks.STICK_BRUSH.get(), blockItem(), 1000));
    public static final Supplier<Item> MULCH = registerWithBlockTab("mulch",
            () -> new FuelBlockItem(HHModBlocks.MULCH.get(), blockItem(), 1000));

    // Salt
    public static final Supplier<Item> SALT_BLOCK = registerWithBlockTab("salt_block",
            () -> new SaltBlockItem(HHModBlocks.SALT_BLOCK.get(), blockItem()));
    public static final Supplier<Item> LIGHTLY_LICKED_SALT_BLOCK = registerWithBlockTab("lightly_licked_salt_block",
            () -> new SaltBlockItem(HHModBlocks.LIGHTLY_LICKED_SALT_BLOCK.get(), blockItem()));
    public static final Supplier<Item> WELL_LICKED_SALT_BLOCK = registerWithBlockTab("well_licked_salt_block",
            () -> new SaltBlockItem(HHModBlocks.WELL_LICKED_SALT_BLOCK.get(), blockItem()));
    public static final Supplier<Item> HEAVILY_LICKED_SALT_BLOCK = registerWithBlockTab("heavily_licked_salt_block",
            () -> new SaltBlockItem(HHModBlocks.HEAVILY_LICKED_SALT_BLOCK.get(), blockItem()));
    public static final Supplier<Item> POLISHED_SALT_BLOCK = registerWithBlockTab("polished_salt_block",
            () -> new BlockItem(HHModBlocks.POLISHED_SALT_BLOCK.get(), blockItem()));
    public static final Supplier<Item> SALT_STAIRS = registerWithBlockTab("salt_stairs",
            () -> new BlockItem(HHModBlocks.SALT_STAIRS.get(), blockItem()));
    public static final Supplier<Item> POLISHED_SALT_STAIRS = registerWithBlockTab("polished_salt_stairs",
            () -> new BlockItem(HHModBlocks.POLISHED_SALT_STAIRS.get(), blockItem()));
    public static final Supplier<Item> SALT_SLAB = registerWithBlockTab("salt_slab",
            () -> new BlockItem(HHModBlocks.SALT_SLAB.get(), blockItem()));
    public static final Supplier<Item> POLISHED_SALT_SLAB = registerWithBlockTab("polished_salt_slab",
            () -> new BlockItem(HHModBlocks.POLISHED_SALT_SLAB.get(), blockItem()));
    public static final Supplier<Item> SALT_WALL = registerWithBlockTab("salt_wall",
            () -> new BlockItem(HHModBlocks.SALT_WALL.get(), blockItem()));
    public static final Supplier<Item> POLISHED_SALT_WALL = registerWithBlockTab("polished_salt_wall",
            () -> new BlockItem(HHModBlocks.POLISHED_SALT_WALL.get(), blockItem()));
    public static final Supplier<Item> SALT_DRIP = registerWithBlockTab("salt_drip",
            () -> new BlockItem(HHModBlocks.SALT_DRIP.get(), blockItem()));
    public static final Supplier<Item> SALT_LAMP = registerWithBlockTab("salt_lamp",
            () -> new BlockItem(HHModBlocks.SALT_LAMP.get(), blockItem()));

    // Manure
    public static final Supplier<Item> MANURE = registerWithTab("manure",
            () -> new ManureItem(basicItem()));
    public static final Supplier<Item> WET_MANURE_BRICK = registerWithTab("wet_manure_brick",
            () -> new Item(basicItem()));
    public static final Supplier<Item> MANURE_BRICK = registerWithTab("manure_brick",
            () -> new Item(basicItem()));
    public static final Supplier<Item> MANURE_BLOCK = registerWithBlockTab("manure_block",
            () -> new FuelBlockItem(HHModBlocks.MANURE_BLOCK.get(), blockItem(), 1000));
    public static final Supplier<Item> MANURE_BRICK_BLOCK = registerWithBlockTab("manure_bricks",
            () -> new BlockItem(HHModBlocks.MANURE_BRICKS_BLOCK.get(), blockItem()));
    public static final Supplier<Item> POLISHED_MANURE = registerWithBlockTab("polished_manure",
            () -> new BlockItem(HHModBlocks.POLISHED_MANURE.get(), blockItem()));
    public static final Supplier<Item> MANURE_BRICK_STAIRS = registerWithBlockTab("manure_brick_stairs",
            () -> new BlockItem(HHModBlocks.MANURE_BRICK_STAIRS.get(), blockItem()));
    public static final Supplier<Item> POLISHED_MANURE_STAIRS = registerWithBlockTab("polished_manure_stairs",
            () -> new BlockItem(HHModBlocks.POLISHED_MANURE_STAIRS.get(), blockItem()));
    public static final Supplier<Item> MANURE_BRICK_SLAB = registerWithBlockTab("manure_brick_slab",
            () -> new BlockItem(HHModBlocks.MANURE_BRICK_SLAB.get(), blockItem()));
    public static final Supplier<Item> POLISHED_MANURE_SLAB = registerWithBlockTab("polished_manure_slab",
            () -> new BlockItem(HHModBlocks.POLISHED_MANURE_SLAB.get(), blockItem()));
    public static final Supplier<Item> MANURE_BRICK_WALL = registerWithBlockTab("manure_brick_wall",
            () -> new BlockItem(HHModBlocks.MANURE_BRICK_WALL.get(), blockItem()));
    public static final Supplier<Item> POLISHED_MANURE_WALL = registerWithBlockTab("polished_manure_wall",
            () -> new BlockItem(HHModBlocks.POLISHED_MANURE_WALL.get(), blockItem()));

    // Half-Slab Crates
    public static final Supplier<Item> BROWN_MUSHROOM_CRATE = registerWithBlockTab("brown_mushroom_crate",
            () -> new BlockItem(HHModBlocks.BROWN_MUSHROOM_CRATE.get(), blockItem()));
    public static final Supplier<Item> RED_MUSHROOM_CRATE = registerWithBlockTab("red_mushroom_crate",
            () -> new BlockItem(HHModBlocks.RED_MUSHROOM_CRATE.get(), blockItem()));
    public static final Supplier<Item> CRIMSON_FUNGUS_CRATE = registerWithBlockTab("crimson_fungus_crate",
            () -> new BlockItem(HHModBlocks.CRIMSON_FUNGUS_CRATE.get(), blockItem()));
    public static final Supplier<Item> WARPED_FUNGUS_CRATE = registerWithBlockTab("warped_fungus_crate",
            () -> new BlockItem(HHModBlocks.WARPED_FUNGUS_CRATE.get(), blockItem()));

    // Drinks
    public static final Supplier<Item> MEAD = merged(BFItems.MEAD_BOTTLE);
    public static final Supplier<Item> HARD_CIDER = registerWithTab("hard_cider",
            () -> new WineBottleItem(HHModFluids.HARD_CIDER.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.HARD_CIDER), true, false).glasses(3));
    public static final Supplier<Item> ROOT_BEER = registerWithTab("root_beer",
            () -> new WineBottleItem(HHModFluids.ROOT_BEER.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.ROOT_BEER), true, false).glasses(3));
    public static final Supplier<Item> BLUEBERRY_WINE = registerWithTab("blueberry_wine",
            () -> new WineBottleItem(HHModFluids.BLUEBERRY_WINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.BLUEBERRY_WINE), true, false).glasses(3));
    public static final Supplier<Item> CHERRY_WINE = registerWithTab("cherry_wine",
            () -> new WineBottleItem(HHModFluids.CHERRY_WINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.CHERRY_WINE), true, false).glasses(3));
    public static final Supplier<Item> RASPBERRY_WINE = registerWithTab("raspberry_wine",
            () -> new WineBottleItem(HHModFluids.RASPBERRY_WINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.RASPBERRY_WINE), true, false).glasses(3));
    public static final Supplier<Item> RED_GRAPE_WINE = registerWithTab("red_grape_wine",
            () -> new WineBottleItem(HHModFluids.RED_GRAPE_WINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.RED_GRAPE_WINE), true, false).glasses(3));
    public static final Supplier<Item> GREEN_GRAPE_WINE = registerWithTab("green_grape_wine",
            () -> new WineBottleItem(HHModFluids.GREEN_GRAPE_WINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.GREEN_GRAPE_WINE), true, false).glasses(3));
    public static final Supplier<Item> SWEET_BERRY_WINE = merged(FlavoredItems.SWEET_BERRY_WINE);
    public static final Supplier<Item> GLOW_BERRY_WINE = merged(FlavoredItems.GLOW_BERRY_WINE);
    public static final Supplier<Item> MELON_WINE = registerWithTab("melon_wine",
            () -> new WineBottleItem(HHModFluids.MELON_WINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.MELON_WINE), true, false).glasses(3));
    public static final Supplier<Item> MOONSHINE = registerWithTab("moonshine",
            () -> new WineBottleItem(HHModFluids.MOONSHINE.source()::get, WineBottleItem.properties(BFProperties.item(), HHFoodValues.MOONSHINE), true, false).glasses(3));
    public static final Supplier<Item> CHOCOLATE_MILK_BOTTLE = registerWithTab("chocolate_milk_bottle",
            () -> new ConsumableItem(drinkItem(HHFoodValues.CHOCOLATE_MILK_BOTTLE.milk()), false, true));
    public static final Supplier<Item> GOAT_MILK_BOTTLE = registerWithTab("goat_milk_bottle",
            () -> new ConsumableItem(drinkItem(HHFoodValues.GOAT_MILK_BOTTLE.milk()), false, true));
    public static final Supplier<Item> GOAT_MILK_BUCKET = registerWithTab("goat_milk_bucket",
            () -> new GoatMilkBucketItem(() -> HHModFluids.GOAT_MILK.source().get(), basicItem().craftRemainder(Items.BUCKET).stacksTo(1).usingConvertsTo(Items.BUCKET)));
    public static final Supplier<Item> BLUEBERRY_JUICE = registerWithTab("blueberry_juice",
            () -> new ConsumableItem(drinkItem(HHFoodValues.BLUEBERRY_JUICE), true, false));
    public static final Supplier<Item> CHERRY_JUICE = registerWithTab("cherry_juice",
            () -> new ConsumableItem(drinkItem(HHFoodValues.CHERRY_JUICE), true, false));
    public static final Supplier<Item> RASPBERRY_JUICE = registerWithTab("raspberry_juice",
            () -> new ConsumableItem(drinkItem(HHFoodValues.RASPBERRY_JUICE), true, false));
    public static final Supplier<Item> RED_GRAPE_JUICE = registerWithTab("red_grape_juice",
            () -> new ConsumableItem(drinkItem(HHFoodValues.RED_GRAPE_JUICE), true, false));
    public static final Supplier<Item> GREEN_GRAPE_JUICE = registerWithTab("green_grape_juice",
            () -> new ConsumableItem(drinkItem(HHFoodValues.GREEN_GRAPE_JUICE), true, false));
    public static final Supplier<Item> SWEET_BERRY_JUICE = merged(FlavoredItems.SWEET_BERRY_JUICE);
    public static final Supplier<Item> GLOW_BERRY_JUICE = merged(FlavoredItems.GLOW_BERRY_JUICE);

    // Jar Items
    public static final Supplier<Item> JAR = registerWithTab("jar",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.JAR.get(), basicItem()));
    public static final Supplier<Item> BLUEBERRY_JAM = registerWithTab("blueberry_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.BLUEBERRY_JAM.get(), jarItem(HHFoodValues.BLUEBERRY_JAM)));
    public static final Supplier<Item> CHERRY_JAM = registerWithTab("cherry_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.CHERRY_JAM.get(), jarItem(HHFoodValues.CHERRY_JAM)));
    public static final Supplier<Item> RASPBERRY_JAM = registerWithTab("raspberry_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.RASPBERRY_JAM.get(), jarItem(HHFoodValues.RASPBERRY_JAM)));
    public static final Supplier<Item> GRAPE_JAM = registerWithTab("grape_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.GRAPE_JAM.get(), jarItem(HHFoodValues.GRAPE_JAM)));
    public static final Supplier<Item> APPLE_JAM = registerWithTab("apple_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.APPLE_JAM.get(), jarItem(HHFoodValues.APPLE_JAM)));
    public static final Supplier<Item> SWEET_BERRY_JAM = registerWithTab("sweet_berry_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.SWEET_BERRY_JAM.get(), jarItem(HHFoodValues.SWEET_BERRY_JAM)));
    public static final Supplier<Item> GLOW_BERRY_JAM = registerWithTab("glow_berry_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.GLOW_BERRY_JAM.get(), jarItem(HHFoodValues.GLOW_BERRY_JAM)));
    public static final Supplier<Item> MELON_JAM = registerWithTab("melon_jam",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.MELON_JAM.get(), jarItem(HHFoodValues.MELON_JAM)));
    public static final Supplier<Item> PEANUT_BUTTER = registerWithTab("peanut_butter",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.PEANUT_BUTTER.get(), jarItem(HHFoodValues.PEANUT_BUTTER)));
    public static final Supplier<Item> PICKLED_BEETROOTS = merged(BFItems.PICKLED_BEETROOT);
    public static final Supplier<Item> PICKLED_CABBAGE = registerWithTab("pickled_cabbage",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.PICKLED_CABBAGE.get(), jarItem(HHFoodValues.PICKLED_CABBAGE)));
    public static final Supplier<Item> PICKLED_CARROTS = registerWithTab("pickled_carrots",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.PICKLED_CARROTS.get(), jarItem(HHFoodValues.PICKLED_CARROTS)));
    public static final Supplier<Item> PICKLED_ONIONS = registerWithTab("pickled_onions",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.PICKLED_ONIONS.get(), jarItem(HHFoodValues.PICKLED_ONIONS)));
    public static final Supplier<Item> PICKLED_POTATOES = registerWithTab("pickled_potatoes",
            () -> new JarBlockItem(HHModBlocks.EMPTY_JAR_DISPLAY.get(), HHModBlocks.PICKLED_POTATOES.get(), jarItem(HHFoodValues.PICKLED_POTATOES)));

    // Sweets
    public static final Supplier<Item> CARAMEL = registerWithTab("caramel",
            () -> new ConsumableItem(foodItem(HHFoodValues.CARAMEL)));
    public static final Supplier<Item> CARAMEL_APPLE = registerWithTab("caramel_apple",
            () -> new ConsumableItem(foodItem(HHFoodValues.CARAMEL_APPLE)));
    public static final Supplier<Item> CHOCOLATE_BAR = merged(FlavoredItems.CHOCOLATE);
    public static final Supplier<Item> COTTON_CANDY = registerWithTab("cotton_candy",
            () -> new ConsumableItem(foodItem(HHFoodValues.COTTON_CANDY)));
    public static final Supplier<Item> BLUEBERRY_MUFFIN = registerWithTab("blueberry_muffin",
            () -> new ConsumableItem(foodItem(HHFoodValues.BLUEBERRY_MUFFIN)));
    public static final Supplier<Item> RASPBERRY_SCONE = registerWithTab("raspberry_scone",
            () -> new ConsumableItem(foodItem(HHFoodValues.BLUEBERRY_MUFFIN)));
    public static final Supplier<Item> PEANUT_BUTTER_COOKIE = registerWithTab("peanut_butter_cookie",
            () -> new ConsumableItem(foodItem(HHFoodValues.PEANUT_BUTTER_COOKIE)));
    public static final Supplier<Item> TRAIL_MIX = registerWithTab("trail_mix",
            () -> new ConsumableItem(foodItem(HHFoodValues.TRAIL_MIX)));
    public static final Supplier<Item> ROASTED_PEANUTS = registerWithTab("roasted_peanuts",
            () -> new ConsumableItem(foodItem(HHFoodValues.ROASTED_PEANUTS)));
    public static final Supplier<Item> MARSHMALLOW_STICK = registerWithTab("marshmallow_stick",
            () -> new RoastableItem(
                    foodItem(HHFoodValues.MARSHMALLOW_STICK).craftRemainder(Items.STICK),
                    HHModItems.ROASTED_MARSHMALLOW_STICK,
                    5,
                    Component.translatable("tooltip.hearthandharvest.roastable").withStyle(ChatFormatting.GRAY))
    );
    public static final Supplier<Item> ROASTED_MARSHMALLOW_STICK = registerWithTab("roasted_marshmallow_stick",
            () -> new RoastableItem(foodItem(HHFoodValues.ROASTED_MARSHMALLOW_STICK).craftRemainder(Items.STICK),
                    HHModItems.CHARRED_MARSHMALLOW_STICK,
                    10,
                    Component.translatable("tooltip.hearthandharvest.roasted_marshmallow_stick").withStyle(ChatFormatting.GOLD))
    );
    public static final Supplier<Item> CHARRED_MARSHMALLOW_STICK = registerWithTab("charred_marshmallow_stick",
            () -> new RoastableItem(
                    foodItem(HHFoodValues.CHARRED_MARSHMALLOW_STICK).craftRemainder(Items.STICK),
                    null,
                    5,
                    Component.translatable("tooltip.hearthandharvest.charred_marshmallow_stick").withStyle(ChatFormatting.DARK_GRAY))
    );
    public static final Supplier<Item> SMORE = registerWithTab("smore",
            () -> new ConsumableItem(foodItem(HHFoodValues.SMORE)));
    public static final Supplier<Item> SUGAR_CUBES = registerWithTab("sugar_cubes",
            () -> new SugarCubesItem(foodItem(HHFoodValues.SUGAR_CUBES)));

    // Sap and Syrup
    public static final Supplier<Item> SAP_BUCKET = registerWithTab("sap_bucket",
            () -> new HHBucketItem(() -> HHModFluids.SAP.source().get(), basicItem().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Supplier<Item> SYRUP_BOTTLE = registerWithTab("syrup_bottle",
            () -> new Item(basicItem().craftRemainder(Items.GLASS_BOTTLE)));
    public static final Supplier<Item> MAPLE_COOKIE = registerWithTab("maple_cookie",
            () -> new ConsumableItem(foodItem(HHFoodValues.MAPLE_COOKIE)));

    // Pies
    public static final Supplier<Item> BLUEBERRY_PIE = registerWithTab("blueberry_pie",
            () -> new BlockItem(HHModBlocks.BLUEBERRY_PIE.get(), blockItem()));
    public static final Supplier<Item> BLUEBERRY_PIE_SLICE = registerWithTab("blueberry_pie_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.BLUEBERRY_PIE_SLICE)));
    public static final Supplier<Item> CHERRY_PIE = registerWithTab("cherry_pie",
            () -> new BlockItem(HHModBlocks.CHERRY_PIE.get(), blockItem()));
    public static final Supplier<Item> CHERRY_PIE_SLICE = registerWithTab("cherry_pie_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.CHERRY_PIE_SLICE)));
    public static final Supplier<Item> RASPBERRY_PIE = registerWithTab("raspberry_pie",
            () -> new BlockItem(HHModBlocks.RASPBERRY_PIE.get(), blockItem()));
    public static final Supplier<Item> RASPBERRY_PIE_SLICE = registerWithTab("raspberry_pie_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.RASPBERRY_PIE_SLICE)));
    public static final Supplier<Item> GRAPE_PIE = registerWithTab("grape_pie",
            () -> new BlockItem(HHModBlocks.GRAPE_PIE.get(), blockItem()));
    public static final Supplier<Item> GRAPE_PIE_SLICE = registerWithTab("grape_pie_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.GRAPE_PIE_SLICE)));
    public static final Supplier<Item> PEANUT_BUTTER_PIE = registerWithTab("peanut_butter_pie",
            () -> new BlockItem(HHModBlocks.PEANUT_BUTTER_PIE.get(), blockItem()));
    public static final Supplier<Item> PEANUT_BUTTER_PIE_SLICE = registerWithTab("peanut_butter_pie_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.PEANUT_BUTTER_PIE_SLICE)));
    public static final Supplier<Item> CHICKEN_POT_PIE = registerWithTab("chicken_pot_pie",
            () -> new BlockItem(HHModBlocks.CHICKEN_POT_PIE.get(), blockItem()));
    public static final Supplier<Item> CHICKEN_POT_PIE_SLICE = registerWithTab("chicken_pot_pie_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.CHICKEN_POT_PIE_SLICE)));
    public static final Supplier<Item> CARROT_CAKE = registerWithTab("carrot_cake",
            () -> new BlockItem(HHModBlocks.CARROT_CAKE.get(), blockItem()));
    public static final Supplier<Item> CARROT_CAKE_SLICE = registerWithTab("carrot_cake_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.CARROT_CAKE_SLICE)));
    public static final Supplier<Item> CHOCOLATE_CAKE = registerWithTab("chocolate_cake",
            () -> new BlockItem(HHModBlocks.CHOCOLATE_CAKE.get(), blockItem()));
    public static final Supplier<Item> CHOCOLATE_CAKE_SLICE = registerWithTab("chocolate_cake_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.CHOCOLATE_CAKE_SLICE)));

    // Pizzas
    public static final Supplier<Item> PIZZA = merged(FlavoredBlocks.PIZZA);
    public static final Supplier<Item> PIZZA_SLICE = merged(FlavoredItems.PIZZA_SLICE);

    public static final Supplier<Item> MEAT_PIZZA = registerWithTab("meat_pizza",
            () -> new BlockItem(HHModBlocks.MEAT_PIZZA.get(), blockItem()));
    public static final Supplier<Item> MEAT_PIZZA_SLICE = registerWithTab("meat_pizza_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.MEAT_PIZZA_SLICE)));

    public static final Supplier<Item> VEGGIE_PIZZA = registerWithTab("veggie_pizza",
            () -> new BlockItem(HHModBlocks.VEGGIE_PIZZA.get(), blockItem()));
    public static final Supplier<Item> VEGGIE_PIZZA_SLICE = registerWithTab("veggie_pizza_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.VEGGIE_PIZZA_SLICE)));

    public static final Supplier<Item> CHEESE_PIZZA = registerWithTab("cheese_pizza",
            () -> new BlockItem(HHModBlocks.CHEESE_PIZZA.get(), blockItem()));
    public static final Supplier<Item> CHEESE_PIZZA_SLICE = registerWithTab("cheese_pizza_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.CHEESE_PIZZA_SLICE)));

    // Ingredients
    public static final Supplier<Item> COOKING_OIL = registerWithTab("cooking_oil",
            () -> new Item(basicItem().craftRemainder(Items.GLASS_BOTTLE)));
    public static final Supplier<Item> BUTTER = merged(FlavoredItems.BUTTER);
    public static final Supplier<Item> FLOUR = merged(BFItems.FLOUR);
    public static final Supplier<Item> CORN_MEAL = registerWithTab("corn_meal",
            () -> new Item(basicItem()));
    public static final Supplier<Item> BATTER = merged(FlavoredItems.BATTER);
    public static final Supplier<Item> SALT = registerWithTab("salt",
            () -> new Item(basicItem()));
    public static final Supplier<Item> TORTILLA = registerWithTab("tortilla",
            () -> new ConsumableItem(foodItem(HHFoodValues.TORTILLA)));

    public static final Supplier<Item> UNRIPE_CHEESE_WHEEL = registerWithTab("unripe_cheese_wheel",
            () -> new BlockItem(HHModBlocks.UNRIPE_CHEESE_WHEEL.get(), blockItem()));
    public static final Supplier<Item> CHEESE_WHEEL = registerWithTab("cheese_wheel",
            () -> new BlockItem(HHModBlocks.CHEESE_WHEEL.get(), blockItem()));
    public static final Supplier<Item> CHEESE_SLICE = registerWithTab("cheese_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.CHEESE_SLICE)));
    public static final Supplier<Item> UNRIPE_GOAT_CHEESE_WHEEL = registerWithTab("unripe_goat_cheese_wheel",
            () -> new BlockItem(HHModBlocks.UNRIPE_GOAT_CHEESE_WHEEL.get(), blockItem()));
    public static final Supplier<Item> GOAT_CHEESE_WHEEL = registerWithTab("goat_cheese_wheel",
            () -> new BlockItem(HHModBlocks.GOAT_CHEESE_WHEEL.get(), blockItem()));
    public static final Supplier<Item> GOAT_CHEESE_SLICE = registerWithTab("goat_cheese_slice",
            () -> new ConsumableItem(foodItem(HHFoodValues.GOAT_CHEESE_SLICE)));

    // Foods
    public static final Supplier<Item> RAW_SAUSAGE = registerWithTab("raw_sausage",
            () -> new ConsumableItem(foodItem(HHFoodValues.RAW_SAUSAGE)));
    public static final Supplier<Item> COOKED_SAUSAGE = registerWithTab("cooked_sausage",
            () -> new ConsumableItem(foodItem(HHFoodValues.COOKED_SAUSAGE)));
    public static final Supplier<Item> RAW_SKEWERED_SAUSAGE = registerWithTab("raw_skewered_sausage",
            () -> new RoastableItem(foodItem(HHFoodValues.RAW_SKEWERED_SAUSAGE).craftRemainder(Items.STICK),
                    HHModItems.SKEWERED_SAUSAGE,
                    10,
                    Component.translatable("tooltip.hearthandharvest.roastable").withStyle(ChatFormatting.GRAY))
    );
    public static final Supplier<Item> SKEWERED_SAUSAGE = registerWithTab("skewered_sausage",
            () -> new ConsumableItem(foodItem(HHFoodValues.SKEWERED_SAUSAGE).craftRemainder(Items.STICK)));
    public static final Supplier<Item> HOT_DOG = registerWithTab("hot_dog",
            () -> new ConsumableItem(foodItem(HHFoodValues.HOT_DOG)));
    public static final Supplier<Item> JERKY = registerWithTab("jerky",
            () -> new ConsumableItem(foodItem(HHFoodValues.JERKY)));
    public static final Supplier<Item> TACO = registerWithTab("taco",
            () -> new ConsumableItem(foodItem(HHFoodValues.TACO)));
    public static final Supplier<Item> RAISINS = registerWithTab("raisins",
            () -> new ConsumableItem(foodItem(HHFoodValues.RAISINS)));
    public static final Supplier<Item> RAISIN_COOKIE = registerWithTab("raisin_cookie",
            () -> new ConsumableItem(foodItem(HHFoodValues.RAISIN_COOKIE)));
    public static final Supplier<Item> SUNFLOWER_SEEDS = registerWithTab("sunflower_seeds",
            () -> new SunflowerSeedItem(foodItem(HHFoodValues.SUNFLOWER_SEEDS)));
    public static final Supplier<Item> POPCORN = registerWithTab("popcorn",
            () -> new ConsumableItem(foodItem(HHFoodValues.POPCORN)));
    public static final Supplier<Item> UNCOOKED_CORN_ON_THE_COB = registerWithTab("uncooked_corn_on_the_cob",
            () -> new RoastableItem(foodItem(HHFoodValues.UNCOOKED_CORN_ON_THE_COB).craftRemainder(Items.STICK),
                    HHModItems.COOKED_CORN_ON_THE_COB,
                    10,
                    Component.translatable("tooltip.hearthandharvest.roastable").withStyle(ChatFormatting.GRAY))
    );
    public static final Supplier<Item> COOKED_CORN_ON_THE_COB = registerWithTab("cooked_corn_on_the_cob",
            () -> new RoastableItem(
                    foodItem(HHFoodValues.COOKED_CORN_ON_THE_COB).craftRemainder(Items.STICK),
                    null,
                    0,
                    Component.translatable("tooltip.hearthandharvest.cooked_corn_on_the_cob").withStyle(ChatFormatting.GOLD))
    );
    public static final Supplier<Item> BAKED_APPLE = registerWithTab("baked_apple",
            () -> new ConsumableItem(foodItem(HHFoodValues.CIDER_DONUT)));
    public static final Supplier<Item> CIDER_DONUT = registerWithTab("cider_donut",
            () -> new ConsumableItem(foodItem(HHFoodValues.CIDER_DONUT)));
    public static final Supplier<Item> CANDY_CORN = registerWithTab("candy_corn",
            () -> new ConsumableItem(foodItem(HHFoodValues.CANDY_CORN)));
    public static final Supplier<Item> CORN_BREAD = registerWithTab("corn_bread",
            () -> new ConsumableItem(foodItem(HHFoodValues.CORN_BREAD)));
    public static final Supplier<Item> CORN_STEW = registerWithTab("corn_stew",
            () -> new ConsumableItem(bowlFoodItem(HHFoodValues.CORN_STEW)));
    public static final Supplier<Item> TAMALE = registerWithTab("tamale",
            () -> new ConsumableItem(foodItem(HHFoodValues.TAMALE)));
    public static final Supplier<Item> STREET_CORN = registerWithTab("street_corn",
            () -> new ConsumableItem(foodItem(HHFoodValues.STREET_CORN).stacksTo(1).craftRemainder(Items.STICK)));

    // Meals
    public static final Supplier<Item> MACARONI_AND_CHEESE = registerWithTab("macaroni_and_cheese",
            () -> new ConsumableItem(bowlFoodItem(HHFoodValues.MACARONI_AND_CHEESE)));
    public static final Supplier<Item> MASHED_POTATOES = registerWithTab("mashed_potatoes",
            () -> new ConsumableItem(bowlFoodItem(HHFoodValues.MASHED_POTATOES)));
    public static final Supplier<Item> PEANUT_BUTTER_AND_JELLY_SANDWICH = registerWithTab("peanut_butter_and_jelly_sandwich",
            () -> new ConsumableItem(foodItem(HHFoodValues.PEANUT_BUTTER_AND_JELLY_SANDWICH)));
    public static final Supplier<Item> WAFFLE = registerWithTab("waffle",
            () -> new BlockItem(HHModBlocks.WAFFLE.get(), foodItem(HHFoodValues.WAFFLE)));
    public static final Supplier<Item> PANCAKE = registerWithTab("pancake",
            () -> new BlockItem(HHModBlocks.PANCAKE.get(), foodItem(HHFoodValues.PANCAKE)));
    public static final Supplier<Item> BISCUITS_AND_GRAVY = registerWithTab("biscuits_and_gravy",
            () -> new ConsumableItem(bowlFoodItem(HHFoodValues.BISCUITS_AND_GRAVY)));
    public static final Supplier<Item> GLAZED_CARROTS = registerWithTab("glazed_carrots",
            () -> new ConsumableItem(bowlFoodItem(HHFoodValues.GLAZED_CARROTS)));

    // ---- From Farmer's Delight (FarmersDelightRefabricated 26.3, MIT, vectorwing) ----
    // Items HH's recipes and loot used. FD items that already exist in this bundle are mapped
    // instead (see HHRegistryAliases): tomato -> flavored:red_tomato, beef patty -> flavored:ground_beef,
    // chicken cuts -> flavored:chicken_drumstick, raw pasta -> flavored:pasta, apple cider ->
    // flavored:apple_juice, wheat dough -> flavored:dough, knives -> flavored:knife.
    public static final Supplier<Item> COOKING_POT = registerWithTab("cooking_pot",
            () -> new CookingPotItem(HHModBlocks.COOKING_POT.get(), blockItem().stacksTo(1)));
    public static final Supplier<Item> CUTTING_BOARD = registerWithTab("cutting_board",
            () -> new FuelBlockItem(HHModBlocks.CUTTING_BOARD.get(), blockItem(), 200));

    public static final Supplier<Item> OAK_CABINET = registerWithBlockTab("oak_cabinet", () -> new FuelBlockItem(HHModBlocks.OAK_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> SPRUCE_CABINET = registerWithBlockTab("spruce_cabinet", () -> new FuelBlockItem(HHModBlocks.SPRUCE_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> BIRCH_CABINET = registerWithBlockTab("birch_cabinet", () -> new FuelBlockItem(HHModBlocks.BIRCH_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> JUNGLE_CABINET = registerWithBlockTab("jungle_cabinet", () -> new FuelBlockItem(HHModBlocks.JUNGLE_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> ACACIA_CABINET = registerWithBlockTab("acacia_cabinet", () -> new FuelBlockItem(HHModBlocks.ACACIA_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> DARK_OAK_CABINET = registerWithBlockTab("dark_oak_cabinet", () -> new FuelBlockItem(HHModBlocks.DARK_OAK_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> MANGROVE_CABINET = registerWithBlockTab("mangrove_cabinet", () -> new FuelBlockItem(HHModBlocks.MANGROVE_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> CHERRY_CABINET = registerWithBlockTab("cherry_cabinet", () -> new FuelBlockItem(HHModBlocks.CHERRY_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> BAMBOO_CABINET = registerWithBlockTab("bamboo_cabinet", () -> new FuelBlockItem(HHModBlocks.BAMBOO_CABINET.get(), blockItem(), 300));
    public static final Supplier<Item> CRIMSON_CABINET = registerWithBlockTab("crimson_cabinet", () -> new BlockItem(HHModBlocks.CRIMSON_CABINET.get(), blockItem()));
    public static final Supplier<Item> WARPED_CABINET = registerWithBlockTab("warped_cabinet", () -> new BlockItem(HHModBlocks.WARPED_CABINET.get(), blockItem()));

    public static final Supplier<Item> STRAW_BALE = registerWithBlockTab("straw_bale", () -> new BlockItem(HHModBlocks.STRAW_BALE.get(), blockItem()));
    public static final Supplier<Item> ROPE = registerWithTab("rope", () -> new RopeItem(HHModBlocks.ROPE.get(), blockItem()));
    public static final Supplier<Item> ORGANIC_COMPOST = registerWithBlockTab("organic_compost", () -> new BlockItem(HHModBlocks.ORGANIC_COMPOST.get(), blockItem()));
    public static final Supplier<Item> RICH_SOIL = registerWithBlockTab("rich_soil", () -> new BlockItem(HHModBlocks.RICH_SOIL.get(), blockItem()));
    public static final Supplier<Item> RICH_SOIL_FARMLAND = registerWithBlockTab("rich_soil_farmland", () -> new BlockItem(HHModBlocks.RICH_SOIL_FARMLAND.get(), blockItem()));
    public static final Supplier<Item> WILD_CABBAGES = registerWithTab("wild_cabbages", () -> new BlockItem(HHModBlocks.WILD_CABBAGES.get(), blockItem()));
    public static final Supplier<Item> WILD_ONIONS = registerWithTab("wild_onions", () -> new BlockItem(HHModBlocks.WILD_ONIONS.get(), blockItem()));

    public static final Supplier<Item> STRAW = registerWithTab("straw", () -> new FuelItem(basicItem(), 100));
    public static final Supplier<Item> CANVAS = registerWithTab("canvas", () -> new FuelItem(basicItem(), 400));
    public static final Supplier<Item> TREE_BARK = registerWithTab("tree_bark", () -> new FuelItem(basicItem(), 200));

    public static final Supplier<Item> CABBAGE = registerWithTab("cabbage",
            () -> new Item(foodItem(HHFoodValues.CABBAGE).component(DataComponents.VILLAGER_FOOD, new VillagerFood(1))));
    public static final Supplier<Item> CABBAGE_SEEDS = registerWithTab("cabbage_seeds",
            () -> new BlockItem(HHModBlocks.CABBAGE_CROP.get(), basicItem()));
    public static final Supplier<Item> CABBAGE_LEAF = registerWithTab("cabbage_leaf",
            () -> new Item(foodItem(HHFoodValues.CABBAGE_LEAF)));
    public static final Supplier<Item> ONION = registerWithTab("onion",
            () -> new BlockItem(HHModBlocks.ONION_CROP.get(), foodItem(HHFoodValues.ONION).component(DataComponents.VILLAGER_FOOD, new VillagerFood(1))));
    public static final Supplier<Item> PIE_CRUST = registerWithTab("pie_crust",
            () -> new Item(foodItem(HHFoodValues.PIE_CRUST)));
    public static final Supplier<Item> TOMATO_SAUCE = registerWithTab("tomato_sauce",
            () -> new ConsumableItem(bowlFoodItem(HHFoodValues.TOMATO_SAUCE)));
    public static final Supplier<Item> MILK_BOTTLE = registerWithTab("milk_bottle",
            () -> new ConsumableItem(drinkItem(HHFoodValues.MILK_BOTTLE.milk()), false, true));
    public static final Supplier<Item> HOT_COCOA = registerWithTab("hot_cocoa",
            () -> new ConsumableItem(drinkItem(HHFoodValues.HOT_COCOA.hotCocoa()), false, true));
    public static final Supplier<Item> MELON_JUICE = registerWithTab("melon_juice",
            () -> new ConsumableItem(drinkItem(HHFoodValues.MELON_JUICE.heal(2.0F)), false, true));

    public static void init() {
    }
}
