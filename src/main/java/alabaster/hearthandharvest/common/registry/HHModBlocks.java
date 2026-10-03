package alabaster.hearthandharvest.common.registry;

import net.minecraft.world.level.material.PushReaction;
import net.hecco.bountifulfares.platform.BFProperties;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.*;
import alabaster.hearthandharvest.common.fd.block.*;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class HHModBlocks {


    // Workstations
    public static final Supplier<Block> TREE_TAPPER = register("tree_tapper",
            () -> new TreeTapperBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F).sound(SoundType.WOOD).randomTicks()));
    public static final Supplier<Block> CASK = register("cask",
            () -> new CaskBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> KEG = register("keg",
            () -> new KegBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> JUG = register("jug",
            () -> new JugBlock(BFProperties.blockCopy(Blocks.IRON_BARS).strength(2.0F, 3.0F).sound(SoundType.METAL)));
    public static final Supplier<Block> TROUGH = register("trough",
            () -> new TroughBlock(BFProperties.blockCopy(Blocks.CAULDRON)));
    public static final Supplier<Block> SPRINKLER = register("sprinkler",
            () -> new SprinklerBlock(BFProperties.blockCopy(Blocks.BARREL)));

    public static final Supplier<Block> SAP_CAULDRON = register("sap_cauldron",
            () -> new SapCauldronBlock(BFProperties.blockCopy(Blocks.CAULDRON).strength(2.0F, 3.0F).sound(SoundType.METAL).randomTicks()));

    public static final Supplier<Block> COUNTER = register("counter",
            () -> new Block(BFProperties.blockCopy(Blocks.BRICKS)));
    public static final Supplier<Block> DRAWER = register("drawer",
            () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BRICKS)));
    public static final Supplier<Block> BASIN = register("basin",
            () -> new BasinBlock(BFProperties.blockCopy(Blocks.BRICKS).randomTicks()));
    public static final Supplier<Block> NEST = register("nest",
            () -> new NestBlock(BFProperties.blockCopy(Blocks.HAY_BLOCK)));
    public static final Supplier<Block> HAY_RUG = register("hay_rug",
            () -> new CanvasRugBlock(BFProperties.blockCopy(Blocks.CARPET.white()).sound(SoundType.GRASS).strength(0.2F)));
    public static final Supplier<Block> STRAW_RUG = register("straw_rug",
            () -> new CanvasRugBlock(BFProperties.blockCopy(Blocks.CARPET.white()).sound(SoundType.GRASS).strength(0.2F)));

    public static final Supplier<Block> SCARECROW = register("scarecrow",
            () -> new ScarecrowBlock(BFProperties.blockCopy(Blocks.HAY_BLOCK)));

    public static final Supplier<Block> STOMPING_BASIN = register("stomping_basin",
            () -> new StompingBasinBlock(BFProperties.blockCopy(Blocks.BARREL)));




    // Half-Cabinets
    public static final Supplier<Block> OAK_HALF_CABINET = register("oak_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> SPRUCE_HALF_CABINET = register("spruce_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> BIRCH_HALF_CABINET = register("birch_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> JUNGLE_HALF_CABINET = register("jungle_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> ACACIA_HALF_CABINET = register("acacia_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> DARK_OAK_HALF_CABINET = register("dark_oak_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> MANGROVE_HALF_CABINET = register("mangrove_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> CHERRY_HALF_CABINET = register("cherry_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.CHERRY_WOOD)));
    public static final Supplier<Block> BAMBOO_HALF_CABINET = register("bamboo_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.BAMBOO_WOOD)));
    public static final Supplier<Block> CRIMSON_HALF_CABINET = register("crimson_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD)));
    public static final Supplier<Block> WARPED_HALF_CABINET = register("warped_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD)));

    // Crabbers Delight Compat
    public static final Supplier<Block> PALM_HALF_CABINET = FabricLoader.getInstance().isModLoaded("crabbersdelight")
            ? register("palm_half_cabinet",
            () -> new HalfCabinetBlock(BFProperties.blockCopy(Blocks.BARREL)))
            : null;

    public static final Supplier<Block> CRATE = register("crate",
            () -> new CrateBlock(BFProperties.blockCopy(Blocks.BARREL).noOcclusion().pushReaction(PushReaction.POPPED)));

    // Bottle Racks
    public static final Supplier<Block> OAK_BOTTLE_RACK = register("oak_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> SPRUCE_BOTTLE_RACK = register("spruce_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> BIRCH_BOTTLE_RACK = register("birch_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> JUNGLE_BOTTLE_RACK = register("jungle_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> ACACIA_BOTTLE_RACK = register("acacia_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> DARK_OAK_BOTTLE_RACK = register("dark_oak_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> MANGROVE_BOTTLE_RACK = register("mangrove_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> CHERRY_BOTTLE_RACK = register("cherry_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.CHERRY_WOOD)));
    public static final Supplier<Block> BAMBOO_BOTTLE_RACK = register("bamboo_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.BAMBOO_WOOD)));
    public static final Supplier<Block> CRIMSON_BOTTLE_RACK = register("crimson_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD)));
    public static final Supplier<Block> WARPED_BOTTLE_RACK = register("warped_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD)));

    // Crabbers Delight Compat
    public static final Supplier<Block> PALM_BOTTLE_RACK = FabricLoader.getInstance().isModLoaded("crabbersdelight")
            ? register("palm_bottle_rack",
            () -> new BottleRackBlock(BFProperties.blockCopy(Blocks.BARREL)))
            : null;

    // Wild Crops
    public static final Supplier<Block> WILD_RED_GRAPES = register("wild_red_grapes",
            () -> new WildCropBlock(MobEffects.SPEED, 10, BFProperties.blockCopy(Blocks.TALL_GRASS)));
    public static final Supplier<Block> WILD_GREEN_GRAPES = register("wild_green_grapes",
            () -> new WildCropBlock(MobEffects.SPEED, 10, BFProperties.blockCopy(Blocks.TALL_GRASS)));
    public static final Supplier<Block> WILD_COTTON = register("wild_cotton",
            () -> new WildCropBlock(MobEffects.SPEED, 10, BFProperties.blockCopy(Blocks.TALL_GRASS)));
    public static final Supplier<Block> WILD_PEANUTS = register("wild_peanuts",
            () -> new WildCropBlock(MobEffects.SPEED, 10, BFProperties.blockCopy(Blocks.TALL_GRASS)));

    // Flowers
    public static final Supplier<Block> YELLOW_MUM = register("yellow_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> ORANGE_MUM = register("orange_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> RED_MUM = register("red_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> BLUE_MUM = register("blue_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> LIGHT_BLUE_MUM = register("light_blue_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> PURPLE_MUM = register("purple_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> PINK_MUM = register("pink_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> WHITE_MUM = register("white_mum",
            () -> new FlowerBlock(MobEffects.INSTANT_HEALTH, 10, BFProperties.blockCopy(Blocks.POPPY)));

    // Potted Flowers
    public static final Supplier<Block> POTTED_YELLOW_MUM = register("potted_yellow_mum",
            () -> new FlowerPotBlock(HHModBlocks.YELLOW_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_ORANGE_MUM = register("potted_orange_mum",
            () -> new FlowerPotBlock(HHModBlocks.ORANGE_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_RED_MUM = register("potted_red_mum",
            () -> new FlowerPotBlock(HHModBlocks.RED_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_BLUE_MUM = register("potted_blue_mum",
            () -> new FlowerPotBlock(HHModBlocks.BLUE_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_LIGHT_BLUE_MUM = register("potted_light_blue_mum",
            () -> new FlowerPotBlock(HHModBlocks.LIGHT_BLUE_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_PURPLE_MUM = register("potted_purple_mum",
            () -> new FlowerPotBlock(HHModBlocks.PURPLE_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_PINK_MUM = register("potted_pink_mum",
            () -> new FlowerPotBlock(HHModBlocks.PINK_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> POTTED_WHITE_MUM = register("potted_white_mum",
            () -> new FlowerPotBlock(HHModBlocks.WHITE_MUM.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));

    // Crops
    public static final Supplier<Block> RASPBERRY_BUSH = register("raspberry_bush",
            () -> new RaspberryBushBlock(BFProperties.blockCopy(Blocks.SWEET_BERRY_BUSH)));
    public static final Supplier<Block> BLUEBERRY_BUSH = register("blueberry_bush",
            () -> new BlueberryBushBlock(BFProperties.blockCopy(Blocks.SWEET_BERRY_BUSH)));
    public static final Supplier<Block> PEANUT_CROP = register("peanuts",
            () -> new PeanutBlock(BFProperties.blockCopy(Blocks.WHEAT)));
    public static final Supplier<Block> COTTON_CROP = register("cotton",
            () -> new CottonBlock(BFProperties.blockCopy(Blocks.WHEAT)));
    public static final Supplier<Block> CORN_STALK = register("corn_stalk",
            () -> new CornStalkBlock());

    // Crates
    public static final Supplier<Block> BLUEBERRY_CRATE = register("blueberry_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> CHERRY_CRATE = register("cherry_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> RED_GRAPE_CRATE = register("red_grape_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> GREEN_GRAPE_CRATE = register("green_grape_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> RASPBERRY_CRATE = register("raspberry_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> PEANUT_CRATE = register("peanut_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> CORN_CRATE = register("corn_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> APPLE_CRATE = register("apple_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> GOLDEN_APPLE_CRATE = register("golden_apple_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> GOLDEN_CARROT_CRATE = register("golden_carrot_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> GLISTERING_MELON_CRATE = register("glistering_melon_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> POISONOUS_POTATO_CRATE = register("poisonous_potato_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> GLOW_BERRY_CRATE = register("glow_berry_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).lightLevel(state -> 10)));
    public static final Supplier<Block> SWEET_BERRY_CRATE = register("sweet_berry_crate",
            () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));

    // Bags
    public static final Supplier<Block> SALT_BAG = register("salt_bag",
            () -> new Block(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> SUGAR_BAG = register("sugar_bag",
            () -> new Block(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> COCOA_BEAN_BAG = register("cocoa_bean_bag",
            () -> new Block(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> GUNPOWDER_BAG = register("gunpowder_bag",
            () -> new Block(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> CORN_KERNEL_BAG = register("corn_kernel_bag",
            () -> new Block(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> MANURE_BAG = register("manure_bag",
            () -> new ManureBlock(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> FEATHER_BAG = register("feather_bag",
            () -> new FeatherBagBlock(BFProperties.blockCopy(Blocks.WOOL.white())));

    // Misc Storage Blocks
    public static final Supplier<Block> COTTON_BALE = register("cotton_bale",
            () -> new HayBlock(BFProperties.blockCopy(Blocks.WOOL.white())));
    public static final Supplier<Block> SUGAR_CANE_BUNDLE = register("sugar_cane_bundle",
            () -> new HayBlock(BFProperties.blockCopy(Blocks.MANGROVE_ROOTS)));
    public static final Supplier<Block> SPOOL = register("spool",
            () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.WOOL.white()).strength(2.0F, 3.0F).sound(SoundType.WOOL)));
    public static final Supplier<Block> ROPE_COIL = register("rope_coil",
            () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.WOOL.white()).strength(2.0F, 3.0F).sound(SoundType.WOOL)));
    public static final Supplier<Block> CORN_HUSK_BUNDLE = register("corn_husk_bundle",
            () -> new Block(BFProperties.blockCopy(Blocks.DRIED_KELP_BLOCK).strength(2.0F, 3.0F).sound(SoundType.CROP)));
    public static final Supplier<Block> CHARCOAL_BLOCK = register("charcoal_block",
            () -> new Block(BFProperties.blockCopy(Blocks.COAL_BLOCK)));
    public static final Supplier<Block> STICK_BRUSH = register("stick_brush",
            () -> new HayBlock(BFProperties.blockCopy(Blocks.MANGROVE_ROOTS)));
    public static final Supplier<Block> MULCH = register("mulch",
            () -> new MulchBlock(BFProperties.blockCopy(Blocks.MANGROVE_ROOTS).strength(1.0F, 1.5F).sound(SoundType.WET_GRASS).randomTicks()));

    // Half-Slab Crates
    public static final Supplier<Block> BROWN_MUSHROOM_CRATE = register("brown_mushroom_crate",
            () -> new SlabBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> RED_MUSHROOM_CRATE = register("red_mushroom_crate",
            () -> new SlabBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> CRIMSON_FUNGUS_CRATE = register("crimson_fungus_crate",
            () -> new SlabBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Supplier<Block> WARPED_FUNGUS_CRATE = register("warped_fungus_crate",
            () -> new SlabBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));

    // Pies
    public static final Supplier<Block> RASPBERRY_PIE = register("raspberry_pie",
            () -> new PieBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.RASPBERRY_PIE_SLICE));
    public static final Supplier<Block> BLUEBERRY_PIE = register("blueberry_pie",
            () -> new PieBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.BLUEBERRY_PIE_SLICE));
    public static final Supplier<Block> CHERRY_PIE = register("cherry_pie",
            () -> new PieBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.CHERRY_PIE_SLICE));
    public static final Supplier<Block> GRAPE_PIE = register("grape_pie",
            () -> new PieBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.GRAPE_PIE_SLICE));
    public static final Supplier<Block> PEANUT_BUTTER_PIE = register("peanut_butter_pie",
            () -> new PieBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.PEANUT_BUTTER_PIE_SLICE));
    public static final Supplier<Block> CHICKEN_POT_PIE = register("chicken_pot_pie",
            () -> new PieBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.CHICKEN_POT_PIE_SLICE));
    public static final Supplier<Block> CARROT_CAKE = register("carrot_cake",
            () -> new SliceableCakeBlock(BFProperties.blockCopy(Blocks.CAKE),  HHModItems.CARROT_CAKE_SLICE));
    public static final Supplier<Block> CHOCOLATE_CAKE = register("chocolate_cake",
            () -> new SliceableCakeBlock(BFProperties.blockCopy(Blocks.CAKE),  HHModItems.CHOCOLATE_CAKE_SLICE));

    // Pizzas
    public static final Supplier<Block> MEAT_PIZZA = register("meat_pizza",
            () -> new PizzaBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.MEAT_PIZZA_SLICE));
    public static final Supplier<Block> VEGGIE_PIZZA = register("veggie_pizza",
            () -> new PizzaBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.VEGGIE_PIZZA_SLICE));
    public static final Supplier<Block> CHEESE_PIZZA = register("cheese_pizza",
            () -> new PizzaBlock(BFProperties.blockCopy(Blocks.CAKE), HHModItems.CHEESE_PIZZA_SLICE));

    // Pancakes and Waffles
    public static final Supplier<Block> WAFFLE = register("waffle",
            () -> new FoodStackBlock(BFProperties.blockCopy(Blocks.CAKE)));
    public static final Supplier<Block> PANCAKE = register("pancake",
            () -> new FoodStackBlock(BFProperties.blockCopy(Blocks.CAKE)));

    // Jars
    public static final Supplier<Block> EMPTY_JAR_DISPLAY = register("empty_jar_display",
            () -> new JarBlock(BFProperties.block().noCollision().noOcclusion()));
    public static final Supplier<Block> JAR = register("jar",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> BLUEBERRY_JAM = register("blueberry_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> CHERRY_JAM = register("cherry_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> GRAPE_JAM = register("grape_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> RASPBERRY_JAM = register("raspberry_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> APPLE_JAM = register("apple_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> SWEET_BERRY_JAM = register("sweet_berry_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> GLOW_BERRY_JAM = register("glow_berry_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).lightLevel(state -> 8).noOcclusion()));
    public static final Supplier<Block> MELON_JAM = register("melon_jam",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> PEANUT_BUTTER = register("peanut_butter",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    // Merged into bountifulfares:pickled_beetroot as an item; the placed jar block stays so jars
    // placed in older worlds keep existing (they drop the merged item).
    public static final Supplier<Block> PICKLED_BEETROOTS = register("pickled_beetroots",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> PICKLED_CABBAGE = register("pickled_cabbage",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> PICKLED_CARROTS = register("pickled_carrots",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> PICKLED_ONIONS = register("pickled_onions",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));
    public static final Supplier<Block> PICKLED_POTATOES = register("pickled_potatoes",
            () -> new Block(BFProperties.blockCopy(Blocks.GLASS).strength(0.5F, 2.0F).sound(SoundType.GLASS).noOcclusion()));

    // Cheese
    public static final Supplier<Block> UNRIPE_CHEESE_WHEEL = register("unripe_cheese_wheel",
            () -> new UnripeCheeseWheelBlock(HHModBlocks.CHEESE_WHEEL, BFProperties.blockCopy(Blocks.CAKE)));
    public static final Supplier<Block> CHEESE_WHEEL = register("cheese_wheel",
            () -> new CheeseWheelBlock(HHModItems.CHEESE_SLICE, BFProperties.blockCopy(Blocks.CAKE)));

    public static final Supplier<Block> UNRIPE_GOAT_CHEESE_WHEEL = register("unripe_goat_cheese_wheel",
            () -> new UnripeCheeseWheelBlock(HHModBlocks.GOAT_CHEESE_WHEEL, BFProperties.blockCopy(Blocks.CAKE)));
    public static final Supplier<Block> GOAT_CHEESE_WHEEL = register("goat_cheese_wheel",
            () -> new CheeseWheelBlock(HHModItems.GOAT_CHEESE_SLICE, BFProperties.blockCopy(Blocks.CAKE)));

    // Salt Blocks
    public static final Supplier<Block> SALT_BLOCK = register("salt_block",
            () -> new SaltBlock(BFProperties.blockCopy(Blocks.GRAVEL).strength(2.0F, 3.0F).sound(SoundType.TUFF).randomTicks()));
    public static final Supplier<Block> LIGHTLY_LICKED_SALT_BLOCK = register("lightly_licked_salt_block",
            () -> new SaltBlock(BFProperties.blockCopy(Blocks.GRAVEL).strength(2.0F, 3.0F).sound(SoundType.TUFF).randomTicks()));
    public static final Supplier<Block> WELL_LICKED_SALT_BLOCK = register("well_licked_salt_block",
            () -> new SaltBlock(BFProperties.blockCopy(Blocks.GRAVEL).strength(2.0F, 3.0F).sound(SoundType.TUFF).randomTicks()));
    public static final Supplier<Block> HEAVILY_LICKED_SALT_BLOCK = register("heavily_licked_salt_block",
            () -> new SaltBlock(BFProperties.blockCopy(Blocks.GRAVEL).strength(2.0F, 3.0F).sound(SoundType.TUFF).randomTicks()));

    public static final Supplier<Block> POLISHED_SALT_BLOCK = register("polished_salt_block",
            () -> new Block(BFProperties.blockCopy(Blocks.GRAVEL).strength(2.5F, 3.0F).sound(SoundType.POLISHED_TUFF)));

    public static final Supplier<Block> SALT_STAIRS = register("salt_stairs",
            () -> new StairBlock(HHModBlocks.SALT_BLOCK.get().defaultBlockState(), BFProperties.blockCopy(HHModBlocks.SALT_BLOCK.get())));
    public static final Supplier<Block> POLISHED_SALT_STAIRS = register("polished_salt_stairs",
            () -> new StairBlock(HHModBlocks.POLISHED_SALT_BLOCK.get().defaultBlockState(), BFProperties.blockCopy(HHModBlocks.POLISHED_SALT_BLOCK.get())));

    public static final Supplier<Block> SALT_SLAB = register("salt_slab",
            () -> new SlabBlock(BFProperties.blockCopy(HHModBlocks.SALT_BLOCK.get())));
    public static final Supplier<Block> POLISHED_SALT_SLAB = register("polished_salt_slab",
            () -> new SlabBlock(BFProperties.blockCopy(HHModBlocks.POLISHED_SALT_BLOCK.get())));

    public static final Supplier<Block> SALT_WALL = register("salt_wall",
            () -> new WallBlock(BFProperties.blockCopy(HHModBlocks.SALT_BLOCK.get())));
    public static final Supplier<Block> POLISHED_SALT_WALL = register("polished_salt_wall",
            () -> new WallBlock(BFProperties.blockCopy(HHModBlocks.POLISHED_SALT_BLOCK.get())));

    public static final Supplier<Block> SALT_DRIP = register("salt_drip",
            () -> new SaltDripBlock(BFProperties.block()
                    .strength(1.0f, 1.0f).sound(SoundType.GRAVEL).noOcclusion().pushReaction(PushReaction.POPPED)));

    public static final Supplier<Block> SALT_LAMP = register("salt_lamp",
            () -> new SaltLampBlock(BFProperties.block()
                    .strength(1.5f).sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(SaltLampBlock.LIT) ? 15 : 0)
                    .noOcclusion()));

    // Manure
    public static final Supplier<Block> MANURE_BLOCK = register("manure_block",
            () -> new ManureBlock(BFProperties.blockCopy(Blocks.MUD).strength(1.0F, 2.0F).sound(SoundType.MUD).randomTicks()));

    public static final Supplier<Block> MANURE_BRICKS_BLOCK = register("manure_bricks",
            () -> new Block(BFProperties.blockCopy(Blocks.MUD_BRICKS).strength(2.5F, 3.0F).sound(SoundType.MUD_BRICKS).randomTicks()));
    public static final Supplier<Block> POLISHED_MANURE = register("polished_manure",
            () -> new Block(BFProperties.blockCopy(Blocks.MUD_BRICKS).strength(2.5F, 3.0F).sound(SoundType.MUD_BRICKS)));

    public static final Supplier<Block> MANURE_BRICK_STAIRS = register("manure_brick_stairs",
            () -> new StairBlock(HHModBlocks.MANURE_BRICKS_BLOCK.get().defaultBlockState(), BFProperties.blockCopy(HHModBlocks.MANURE_BRICKS_BLOCK.get())));
    public static final Supplier<Block> POLISHED_MANURE_STAIRS = register("polished_manure_stairs",
            () -> new StairBlock(HHModBlocks.POLISHED_MANURE.get().defaultBlockState(), BFProperties.blockCopy(HHModBlocks.POLISHED_MANURE.get())));

    public static final Supplier<Block> MANURE_BRICK_SLAB = register("manure_brick_slab",
            () -> new SlabBlock(BFProperties.blockCopy(HHModBlocks.MANURE_BRICKS_BLOCK.get())));
    public static final Supplier<Block> POLISHED_MANURE_SLAB = register("polished_manure_slab",
            () -> new SlabBlock(BFProperties.blockCopy(HHModBlocks.POLISHED_MANURE.get())));

    public static final Supplier<Block> MANURE_BRICK_WALL = register("manure_brick_wall",
            () -> new WallBlock(BFProperties.blockCopy(HHModBlocks.MANURE_BRICKS_BLOCK.get())));
    public static final Supplier<Block> POLISHED_MANURE_WALL = register("polished_manure_wall",
            () -> new WallBlock(BFProperties.blockCopy(HHModBlocks.POLISHED_MANURE.get())));

    // ---- From Farmer's Delight (FarmersDelightRefabricated 26.3, MIT, vectorwing) ----
    // Hearth and Harvest no longer depends on Farmer's Delight; the blocks it needed are part of
    // the mod now, under hearthandharvest: ids, with FDR's properties.
    public static final Supplier<Block> COOKING_POT = register("cooking_pot",
            () -> new CookingPotBlock(BFProperties.block().mapColor(MapColor.METAL).strength(0.5F, 6.0F).sound(SoundType.LANTERN)));
    public static final Supplier<Block> CUTTING_BOARD = register("cutting_board",
            () -> new CuttingBoardBlock(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0F).sound(SoundType.WOOD)));

    public static final Supplier<Block> OAK_CABINET = register("oak_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> SPRUCE_CABINET = register("spruce_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> BIRCH_CABINET = register("birch_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> JUNGLE_CABINET = register("jungle_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> ACACIA_CABINET = register("acacia_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> DARK_OAK_CABINET = register("dark_oak_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> MANGROVE_CABINET = register("mangrove_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL)));
    public static final Supplier<Block> CHERRY_CABINET = register("cherry_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.CHERRY_WOOD)));
    public static final Supplier<Block> BAMBOO_CABINET = register("bamboo_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.BAMBOO_WOOD)));
    public static final Supplier<Block> CRIMSON_CABINET = register("crimson_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD)));
    public static final Supplier<Block> WARPED_CABINET = register("warped_cabinet", () -> new CabinetBlock(BFProperties.blockCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD)));

    public static final Supplier<Block> STRAW_BALE = register("straw_bale",
            () -> new StrawBaleBlock(BFProperties.blockCopy(Blocks.HAY_BLOCK)));
    public static final Supplier<Block> ROPE = register("rope",
            () -> new RopeBlock(BFProperties.blockCopy(Blocks.CARPET.brown()).noCollision().noOcclusion().strength(0.2F).sound(SoundType.WOOL)));
    public static final Supplier<Block> ORGANIC_COMPOST = register("organic_compost",
            () -> new OrganicCompostBlock(BFProperties.blockCopy(Blocks.DIRT).strength(1.2F).sound(SoundType.CROP)));
    public static final Supplier<Block> RICH_SOIL = register("rich_soil",
            () -> new RichSoilBlock(BFProperties.blockCopy(Blocks.DIRT).randomTicks()));
    public static final Supplier<Block> RICH_SOIL_FARMLAND = register("rich_soil_farmland",
            () -> new RichSoilFarmlandBlock(BFProperties.blockCopy(Blocks.FARMLAND)));

    public static final Supplier<Block> CABBAGE_CROP = register("cabbages",
            () -> new CabbageBlock(BFProperties.blockCopy(Blocks.WHEAT)));
    public static final Supplier<Block> ONION_CROP = register("onions",
            () -> new OnionBlock(BFProperties.blockCopy(Blocks.WHEAT)));
    public static final Supplier<Block> WILD_CABBAGES = register("wild_cabbages",
            () -> new WildCropBlock(MobEffects.STRENGTH, 6, BFProperties.blockCopy(Blocks.TALL_GRASS)));
    public static final Supplier<Block> WILD_ONIONS = register("wild_onions",
            () -> new WildCropBlock(MobEffects.FIRE_RESISTANCE, 6, BFProperties.blockCopy(Blocks.TALL_GRASS)));

    /** Items are registered separately in {@link HHModItems} (1.21.1 split them the same way). */
    private static <T extends Block> Supplier<T> register(String name, Supplier<T> block) {
        return BFRegistryHelper.registerBlockNoItem(HearthAndHarvest.MODID, name, block);
    }

    public static void init() {
    }
}
