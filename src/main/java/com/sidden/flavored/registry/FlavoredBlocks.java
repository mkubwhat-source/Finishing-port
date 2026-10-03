package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.block.*;
import net.hecco.bountifulfares.platform.BFProperties;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

/**
 * Flavored's blocks, registered under the {@code flavored} namespace inside the Bountiful Fares jar.
 * <p>
 * Flavored's corn bush is not ported: the bundle uses Bountiful Fares' maize crop for corn (old
 * {@code flavored:corn_bush} blocks become {@code bountifulfares:maize_crop} via a registry alias,
 * see {@link FlavoredMerges}).
 */
public final class FlavoredBlocks {
    // Workstations
    public static final Supplier<Block> KEG = register("keg", () -> new KegBlock(BFProperties.blockCopy(Blocks.COPPER_BLOCK.weathering().pick(net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED))));
    public static final Supplier<Block> MIXING_BOWL = register("mixing_bowl", () -> new MixingBowlBlock(BFProperties.blockCopy(Blocks.CAULDRON)));
    public static final Supplier<Block> OVEN = register("oven", () -> new OvenBlock(BFProperties.block().mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F)
            .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 13 : 0)));

    // Chocolate blocks
    public static final Supplier<Block> CHOCOLATE_BLOCK = register("chocolate_block", () -> new ChocolateBlock(BFProperties.blockCopy(Blocks.PACKED_MUD)));
    public static final Supplier<Block> CHOCOLATE_TILES = register("chocolate_tiles", () -> new ChocolateBlock(BFProperties.blockCopy(Blocks.PACKED_MUD)));
    public static final Supplier<Block> CHOCOLATE_TILE_STAIRS = register("chocolate_tile_stairs", () -> new ChocolateStairBlock(Blocks.MUD_BRICK_STAIRS.defaultBlockState(), BFProperties.blockCopy(Blocks.MUD_BRICK_STAIRS)));
    public static final Supplier<Block> CHOCOLATE_TILE_SLAB = register("chocolate_tile_slab", () -> new ChocolateSlabBlock(BFProperties.blockCopy(Blocks.MUD_BRICK_SLAB)));

    // Cinnamon blocks
    public static final Supplier<Block> CINNAMON_STALK = register("cinnamon_stalk", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.JUNGLE_LOG)));
    public static final Supplier<Block> STRIPPED_CINNAMON_STALK = register("stripped_cinnamon_stalk", () -> new StrippedCinnamonStalkBlock(BFProperties.blockCopy(Blocks.STRIPPED_JUNGLE_LOG).randomTicks()));
    public static final Supplier<Block> WAXED_STRIPPED_CINNAMON_STALK = register("waxed_stripped_cinnamon_stalk", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.STRIPPED_JUNGLE_LOG)));
    public static final Supplier<Block> CINNAMON_SPROUT = register("cinnamon_sprout", () -> new CinnamonSproutBlock(BFProperties.blockCopy(Blocks.SHORT_GRASS)));

    // Crop plants. 1.21.1 also registered plain block items for these (not in the creative tab,
    // but with item models); they are kept so existing ids stay valid. Planting uses the seeds.
    public static final Supplier<Block> TOMATO_BUSH = register("tomato_bush", () -> new TomatoBushBlock(BFProperties.blockCopy(Blocks.SWEET_BERRY_BUSH)));
    public static final Supplier<Block> PEPPER_BUSH = register("pepper_bush", () -> new PepperBushBlock(BFProperties.blockCopy(Blocks.SWEET_BERRY_BUSH)));
    public static final Supplier<Block> SPINACH_BUSH = register("spinach_bush", () -> new SpinachBushBlock(BFProperties.blockCopy(Blocks.SWEET_BERRY_BUSH)));
    public static final Supplier<Block> GARLICS = register("garlics", () -> new CropBlock(BFProperties.blockCopy(Blocks.CARROTS)));

    // Food / item blocks
    public static final Supplier<Block> SOFT_CHEESE = register("soft_cheese", () -> new SoftCheeseBlock(BFProperties.blockCopy(Blocks.CAKE).randomTicks().strength(0.3f)));
    public static final Supplier<Block> AGED_CHEESE = register("aged_cheese", () -> new AgedCheeseBlock(BFProperties.blockCopy(SOFT_CHEESE.get()).randomTicks()));
    public static final Supplier<Block> PUDDING = BFRegistryHelper.registerBlock(Flavored.MOD_ID, "pudding", () -> new PuddingBlock(BFProperties.blockCopy(Blocks.CAKE)), new Item.Properties().stacksTo(1));
    public static final Supplier<Block> PIZZA = BFRegistryHelper.registerBlock(Flavored.MOD_ID, "pizza", () -> new PizzaBlock(BFProperties.blockCopy(Blocks.CAKE)), new Item.Properties().stacksTo(1));

    private static Supplier<Block> register(String name, Supplier<Block> block) {
        return BFRegistryHelper.registerBlock(Flavored.MOD_ID, name, block);
    }

    private static Supplier<Block> registerNoItem(String name, Supplier<Block> block) {
        return BFRegistryHelper.registerBlockNoItem(Flavored.MOD_ID, name, block);
    }

    public static void init() {
    }

    private FlavoredBlocks() {
    }
}
