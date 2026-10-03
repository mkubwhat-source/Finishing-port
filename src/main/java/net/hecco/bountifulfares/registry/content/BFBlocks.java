package net.hecco.bountifulfares.registry.content;

import net.hecco.bountifulfares.platform.BFProperties;

import com.google.common.collect.Maps;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.BountifulFaresUtil;
import net.hecco.bountifulfares.definition.block.custom.*;
import net.hecco.bountifulfares.definition.item.custom.CeramicDishBlockItem;
import net.hecco.bountifulfares.definition.item.custom.DyeableCeramicBlockItem;
import net.hecco.bountifulfares.registry.misc.BFSaplingGenerators;
import net.hecco.bountifulfares.registry.util.BFBlockSetTypes;
import net.hecco.bountifulfares.registry.util.BFNoteBlockInstruments;
import net.hecco.bountifulfares.registry.util.BFWoodTypes;
import net.hecco.bountifulfares.lib.publicBlocks.PublicButtonBlock;
import net.hecco.bountifulfares.lib.publicBlocks.PublicDoorBlock;
import net.hecco.bountifulfares.lib.publicBlocks.PublicPressurePlateBlock;
import net.hecco.bountifulfares.lib.publicBlocks.PublicSaplingBlock;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class BFBlocks {
    public static final Map<Block, Block> CERAMIC_TO_CHECKERED_CERAMIC = Maps.newHashMap();
    public static final Map<Block, Block> REVERT_CHECKERED_CERAMIC = Maps.newHashMap();

    public static final Map<String, Supplier<Block>> TRELLISES = new HashMap<>();
    public static final Map<String, Supplier<Block>> PICKETS = new HashMap<>();
    public static final Map<DyeColor, Supplier<Block>> JACK_O_STRAWS = new HashMap<>();

    public static final Map<ItemLike, Integer> FUELS = new HashMap<>();
    public static final Map<TagKey<Item>, Integer> TAG_FUELS = new HashMap<>();

    public static final Supplier<Block> APPLE_LOG = registerBlock("apple_log", () -> new FruitLogBlock(BFProperties.blockCopy(Blocks.OAK_LOG).noOcclusion().forceSolidOff()));
    public static final Supplier<Block> APPLE_WOOD = registerBlock("apple_wood", () -> new FruitLogBlock(BFProperties.blockCopy(Blocks.OAK_WOOD).noOcclusion().forceSolidOff()));
    public static final Supplier<Block> STRIPPED_APPLE_LOG = registerBlock("stripped_apple_log", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(Blocks.STRIPPED_OAK_LOG).noOcclusion().forceSolidOff()));
    public static final Supplier<Block> STRIPPED_APPLE_WOOD = registerBlock("stripped_apple_wood", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(Blocks.STRIPPED_OAK_WOOD).noOcclusion().forceSolidOff()));
    public static final Supplier<Block> HANGING_APPLE = registerBlockNoItem("hanging_apple", () -> new HangingAppleBlock(BFProperties.block().mapColor(MapColor.COLOR_RED).noOcclusion().dynamicShape().sound(SoundType.AZALEA).pushReaction(PushReaction.POPPED).randomTicks().offsetType(BlockBehaviour.OffsetType.XZ)));
    public static final Supplier<Block> APPLE_LEAVES = registerBlock("apple_leaves", () -> new FruitLeavesBlock(HANGING_APPLE.get().defaultBlockState(), BFProperties.blockCopy(Blocks.OAK_LEAVES)));
    public static final Supplier<Block> FLOWERING_APPLE_LEAVES = registerBlock("flowering_apple_leaves", () -> new FruitLeavesBlock(HANGING_APPLE.get().defaultBlockState(), BFProperties.blockCopy(Blocks.OAK_LEAVES)));
    public static final Supplier<Block> APPLE_SAPLING = registerBlock("apple_sapling", () -> new PublicSaplingBlock(BFSaplingGenerators.APPLE_SAPLING_GENERATOR, BFProperties.blockCopy(Blocks.OAK_SAPLING).sound(SoundType.CHERRY_SAPLING)));
    public static final Supplier<Block> POTTED_APPLE_SAPLING = registerBlockNoItem("potted_apple_sapling", () -> new FlowerPotBlock(BFBlocks.APPLE_SAPLING.get(), BFProperties.blockCopy(Blocks.POTTED_OAK_SAPLING)));
    public static final Supplier<Block> ORANGE_LOG = registerBlock("orange_log", () -> new FruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_LOG.get())));
    public static final Supplier<Block> ORANGE_WOOD = registerBlock("orange_wood", () -> new FruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_WOOD.get())));
    public static final Supplier<Block> STRIPPED_ORANGE_LOG = registerBlock("stripped_orange_log", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_LOG.get())));
    public static final Supplier<Block> STRIPPED_ORANGE_WOOD = registerBlock("stripped_orange_wood", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_WOOD.get())));
    public static final Supplier<Block> HANGING_ORANGE = registerBlockNoItem("hanging_orange", () -> new HangingOrangeBlock(BFProperties.blockCopy(BFBlocks.HANGING_APPLE.get())));
    public static final Supplier<Block> ORANGE_LEAVES = registerBlock("orange_leaves", () -> new FruitLeavesBlock(HANGING_ORANGE.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.APPLE_LEAVES.get())));
    public static final Supplier<Block> FLOWERING_ORANGE_LEAVES = registerBlock("flowering_orange_leaves", () -> new FruitLeavesBlock(HANGING_ORANGE.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.FLOWERING_APPLE_LEAVES.get())));
    public static final Supplier<Block> ORANGE_SAPLING = registerBlock("orange_sapling", () -> new PublicSaplingBlock(BFSaplingGenerators.ORANGE_SAPLING_GENERATOR, BFProperties.blockCopy(BFBlocks.APPLE_SAPLING.get())));
    public static final Supplier<Block> POTTED_ORANGE_SAPLING = registerBlockNoItem("potted_orange_sapling", () -> new FlowerPotBlock(BFBlocks.ORANGE_SAPLING.get(), BFProperties.blockCopy(BFBlocks.POTTED_APPLE_SAPLING.get())));
    public static final Supplier<Block> LEMON_LOG = registerBlock("lemon_log", () -> new FruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_LOG.get())));
    public static final Supplier<Block> LEMON_WOOD = registerBlock("lemon_wood", () -> new FruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_WOOD.get())));
    public static final Supplier<Block> STRIPPED_LEMON_LOG = registerBlock("stripped_lemon_log", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_LOG.get())));
    public static final Supplier<Block> STRIPPED_LEMON_WOOD = registerBlock("stripped_lemon_wood", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_WOOD.get())));
    public static final Supplier<Block> HANGING_LEMON = registerBlockNoItem("hanging_lemon", () -> new HangingLemonBlock(BFProperties.blockCopy(BFBlocks.HANGING_APPLE.get())));
    public static final Supplier<Block> LEMON_LEAVES = registerBlock("lemon_leaves", () -> new FruitLeavesBlock(HANGING_LEMON.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.APPLE_LEAVES.get())));
    public static final Supplier<Block> FLOWERING_LEMON_LEAVES = registerBlock("flowering_lemon_leaves", () -> new FruitLeavesBlock(HANGING_LEMON.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.FLOWERING_APPLE_LEAVES.get())));
    public static final Supplier<Block> LEMON_SAPLING = registerBlock("lemon_sapling", () -> new PublicSaplingBlock(BFSaplingGenerators.LEMON_SAPLING_GENERATOR, BFProperties.blockCopy(BFBlocks.APPLE_SAPLING.get())));
    public static final Supplier<Block> POTTED_LEMON_SAPLING = registerBlockNoItem("potted_lemon_sapling", () -> new FlowerPotBlock(BFBlocks.LEMON_SAPLING.get(), BFProperties.blockCopy(BFBlocks.POTTED_APPLE_SAPLING.get())));
    public static final Supplier<Block> PLUM_LOG = registerBlock("plum_log", () -> new FruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_LOG.get())));
    public static final Supplier<Block> PLUM_WOOD = registerBlock("plum_wood", () -> new FruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_WOOD.get())));
    public static final Supplier<Block> STRIPPED_PLUM_LOG = registerBlock("stripped_plum_log", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_LOG.get())));
    public static final Supplier<Block> STRIPPED_PLUM_WOOD = registerBlock("stripped_plum_wood", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_WOOD.get())));
    public static final Supplier<Block> HANGING_PLUM = registerBlockNoItem("hanging_plum", () -> new HangingPlumBlock(BFProperties.blockCopy(BFBlocks.HANGING_APPLE.get())));
    public static final Supplier<Block> PLUM_LEAVES = registerBlock("plum_leaves", () -> new FruitLeavesBlock(HANGING_PLUM.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.APPLE_LEAVES.get())));
    public static final Supplier<Block> FLOWERING_PLUM_LEAVES = registerBlock("flowering_plum_leaves", () -> new FruitLeavesBlock(HANGING_PLUM.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.FLOWERING_APPLE_LEAVES.get())));
    public static final Supplier<Block> PLUM_SAPLING = registerBlock("plum_sapling", () -> new PublicSaplingBlock(BFSaplingGenerators.PLUM_SAPLING_GENERATOR, BFProperties.blockCopy(BFBlocks.APPLE_SAPLING.get())));
    public static final Supplier<Block> POTTED_PLUM_SAPLING = registerBlockNoItem("potted_plum_sapling", () -> new FlowerPotBlock(BFBlocks.PLUM_SAPLING.get(), BFProperties.blockCopy(BFBlocks.POTTED_APPLE_SAPLING.get())));
    public static final Supplier<Block> HOARY_APPLE_SAPLING_CROP = registerBlockNoItem("hoary_apple_sapling_crop", () -> new HoaryAppleSaplingCropBlock(BFProperties.block().mapColor(MapColor.STONE).randomTicks().noCollision().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> HOARY_APPLE_SAPLING = registerBlock("hoary_apple_sapling", () -> new PublicSaplingBlock(BFSaplingGenerators.HOARY_SAPLING_GENERATOR, BFProperties.block().mapColor(MapColor.STONE).randomTicks().noCollision().instabreak().sound(SoundType.CHERRY_SAPLING).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> POTTED_HOARY_APPLE_SAPLING = registerBlockNoItem("potted_hoary_apple_sapling", () -> new FlowerPotBlock(BFBlocks.HOARY_APPLE_SAPLING.get(), BFProperties.blockCopy(Blocks.POTTED_OAK_SAPLING)));
    public static final Supplier<Block> HOARY_LOG = registerBlock("hoary_log", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.OAK_LOG).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_WOOD = registerBlock("hoary_wood", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.OAK_WOOD).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> STRIPPED_HOARY_LOG = registerBlock("stripped_hoary_log", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> STRIPPED_HOARY_WOOD = registerBlock("stripped_hoary_wood", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.STRIPPED_OAK_WOOD).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_PLANKS = registerBlock("hoary_planks", () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_STAIRS = registerBlock("hoary_stairs", () -> new ModStairsBlock(BFBlocks.HOARY_PLANKS.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.HOARY_PLANKS.get())));
    public static final Supplier<Block> HOARY_SLAB = registerBlock("hoary_slab", () -> new SlabBlock(BFProperties.blockCopy(BFBlocks.HOARY_PLANKS.get())));
    public static final Supplier<Block> HOARY_FENCE = registerBlock("hoary_fence", () -> new FenceBlock(BFProperties.blockCopy(Blocks.OAK_FENCE).strength(2.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_FENCE_GATE = registerBlock("hoary_fence_gate", () -> new ModFenceGateBlock(BFProperties.blockCopy(Blocks.OAK_FENCE_GATE).strength(2.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY), BFWoodTypes.HOARY));
    public static final Supplier<Block> HOARY_DOOR = registerBlock("hoary_door", () -> new PublicDoorBlock(BFBlockSetTypes.HOARY, BFProperties.blockCopy(Blocks.OAK_DOOR).strength(2.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_TRAPDOOR = registerBlock("hoary_trapdoor", () -> new ModTrapdoorBlock(BFProperties.blockCopy(Blocks.OAK_TRAPDOOR).strength(2.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY), BFBlockSetTypes.HOARY));
    public static final Supplier<Block> HOARY_PRESSURE_PLATE = registerBlock("hoary_pressure_plate", () -> new PublicPressurePlateBlock(BFBlockSetTypes.HOARY, BFProperties.blockCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.TERRACOTTA_GRAY).strength(0.5f, 5.0f)));
    public static final Supplier<Block> HOARY_BUTTON = registerBlock("hoary_button", () -> new PublicButtonBlock(BFBlockSetTypes.HOARY, 30, BFProperties.blockCopy(BFBlocks.HOARY_PLANKS.get()).noCollision().strength(0.5f, 5f)));
    public static final Supplier<Block> HOARY_SIGN = registerBlockNoItem("hoary_sign", () -> new StandingSignBlock(BFWoodTypes.HOARY, BFProperties.blockCopy(Blocks.OAK_SIGN).strength(1.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_WALL_SIGN = registerBlockNoItem("hoary_wall_sign", () -> new WallSignBlock(BFWoodTypes.HOARY, BFProperties.blockCopy(Blocks.OAK_WALL_SIGN).overrideLootTable(HOARY_SIGN.get().getLootTable()).strength(1.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_HANGING_SIGN = registerBlockNoItem("hoary_hanging_sign", () -> new CeilingHangingSignBlock(BFWoodTypes.HOARY, BFProperties.blockCopy(Blocks.OAK_HANGING_SIGN).strength(1.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HOARY_WALL_HANGING_SIGN = registerBlockNoItem("hoary_wall_hanging_sign", () -> new WallHangingSignBlock(BFWoodTypes.HOARY, BFProperties.blockCopy(Blocks.OAK_WALL_HANGING_SIGN).overrideLootTable(HOARY_HANGING_SIGN.get().getLootTable()).strength(1.0f, 5.0f).mapColor(MapColor.TERRACOTTA_GRAY)));
    public static final Supplier<Block> HANGING_HOARY_APPLE = registerBlockNoItem("hanging_hoary_apple", () -> new HangingHoaryAppleBlock(BFProperties.blockCopy(BFBlocks.HANGING_APPLE.get())));
    public static final Supplier<Block> HOARY_LEAVES = registerBlock("hoary_leaves", () -> new FruitLeavesBlock(HANGING_HOARY_APPLE.get().defaultBlockState(), BFProperties.block().mapColor(MapColor.PLANT).strength(0.2f).randomTicks().sound(SoundType.GRASS).noOcclusion().ignitedByLava().pushReaction(PushReaction.POPPED)));

    public static final Supplier<Block> WALNUT_SAPLING = registerBlock("walnut_sapling", () -> new PublicSaplingBlock(BFSaplingGenerators.WALNUT_SAPLING_GENERATOR, BFProperties.block().mapColor(MapColor.PLANT).randomTicks().noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> POTTED_WALNUT_SAPLING = registerBlockNoItem("potted_walnut_sapling", () -> new FlowerPotBlock(BFBlocks.WALNUT_SAPLING.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> WALNUT_LOG = registerBlock("walnut_log", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.OAK_LOG).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_WOOD = registerBlock("walnut_wood", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> STRIPPED_WALNUT_LOG = registerBlock("stripped_walnut_log", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> STRIPPED_WALNUT_WOOD = registerBlock("stripped_walnut_wood", () -> new RotatedPillarBlock(BFProperties.blockCopy(Blocks.STRIPPED_OAK_WOOD).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_PLANKS = registerBlock("walnut_planks", () -> new Block(BFProperties.blockCopy(Blocks.OAK_PLANKS).strength(2.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_STAIRS = registerBlock("walnut_stairs", () -> new ModStairsBlock(BFBlocks.WALNUT_PLANKS.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.WALNUT_PLANKS.get())));
    public static final Supplier<Block> WALNUT_SLAB = registerBlock("walnut_slab", () -> new SlabBlock(BFProperties.blockCopy(BFBlocks.WALNUT_PLANKS.get())));
    public static final Supplier<Block> WALNUT_FENCE = registerBlock("walnut_fence", () -> new FenceBlock(BFProperties.blockCopy(Blocks.OAK_FENCE).strength(2.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_FENCE_GATE = registerBlock("walnut_fence_gate", () -> new ModFenceGateBlock(BFProperties.blockCopy(Blocks.OAK_FENCE_GATE).strength(2.0f, 5.0f).mapColor(MapColor.COLOR_BROWN), BFWoodTypes.WALNUT));
    public static final Supplier<Block> WALNUT_DOOR = registerBlock("walnut_door", () -> new PublicDoorBlock(BFBlockSetTypes.WALNUT, BFProperties.blockCopy(Blocks.OAK_DOOR).strength(2.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_TRAPDOOR = registerBlock("walnut_trapdoor", () -> new ModTrapdoorBlock(BFProperties.blockCopy(Blocks.OAK_TRAPDOOR).strength(2.0f, 5.0f).mapColor(MapColor.COLOR_BROWN), BFBlockSetTypes.WALNUT));
    public static final Supplier<Block> WALNUT_PRESSURE_PLATE = registerBlock("walnut_pressure_plate", () -> new PublicPressurePlateBlock(BFBlockSetTypes.WALNUT, BFProperties.blockCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_BROWN).strength(0.5f, 5.0f)));
    public static final Supplier<Block> WALNUT_BUTTON = registerBlock("walnut_button", () -> new PublicButtonBlock(BFBlockSetTypes.WALNUT, 30, BFProperties.blockCopy(BFBlocks.WALNUT_PLANKS.get()).mapColor(MapColor.COLOR_BROWN).noCollision().strength(0.5f, 5f)));
    public static final Supplier<Block> WALNUT_SIGN = registerBlockNoItem("walnut_sign", () -> new StandingSignBlock(BFWoodTypes.WALNUT, BFProperties.blockCopy(Blocks.OAK_SIGN).strength(1.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_WALL_SIGN = registerBlockNoItem("walnut_wall_sign", () -> new WallSignBlock(BFWoodTypes.WALNUT, BFProperties.blockCopy(Blocks.OAK_WALL_SIGN).overrideLootTable(WALNUT_SIGN.get().getLootTable()).strength(1.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_HANGING_SIGN = registerBlockNoItem("walnut_hanging_sign", () -> new CeilingHangingSignBlock(BFWoodTypes.WALNUT, BFProperties.blockCopy(Blocks.OAK_HANGING_SIGN).strength(1.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> WALNUT_WALL_HANGING_SIGN = registerBlockNoItem("walnut_wall_hanging_sign", () -> new WallHangingSignBlock(BFWoodTypes.WALNUT, BFProperties.blockCopy(Blocks.OAK_WALL_HANGING_SIGN).overrideLootTable(WALNUT_HANGING_SIGN.get().getLootTable()).strength(1.0f, 5.0f).mapColor(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> HANGING_WALNUTS = registerBlockNoItem("hanging_walnuts", () -> new HangingWalnutsBlock(BFProperties.blockCopy(BFBlocks.HANGING_APPLE.get())));
    public static final Supplier<Block> FALLEN_WALNUTS = registerBlockNoItem("fallen_walnuts", () -> new FallenWalnutsBlock(BFProperties.block().mapColor(MapColor.COLOR_BROWN).noCollision().sound(SoundType.AZALEA).pushReaction(PushReaction.POPPED).instabreak().noTerrainParticles()));
    public static final Supplier<Block> WALNUT_LEAVES = registerBlock("walnut_leaves", () -> new FruitLeavesBlock(HANGING_WALNUTS.get().defaultBlockState(), BFProperties.block().mapColor(MapColor.PLANT).strength(0.2f).randomTicks().sound(SoundType.GRASS).noOcclusion().ignitedByLava().pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WALNUT_MULCH = registerBlock("walnut_mulch", () -> new MulchBlock(BFProperties.block().forceSolidOff().mapColor(MapColor.COLOR_BROWN).forceSolidOff().strength(0.4f).sound(SoundType.ROOTED_DIRT).ignitedByLava()));
    public static final Supplier<Block> WALNUT_MULCH_BLOCK = registerBlock("walnut_mulch_block", () -> new MulchBlockBlock(BFProperties.block().mapColor(MapColor.COLOR_BROWN).strength(0.4f).sound(SoundType.ROOTED_DIRT).ignitedByLava()));

    public static final Supplier<Block> WALNUT_CANDLE = registerBlock("walnut_candle", () -> new WalnutCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));

    public static final Supplier<Block> PALM_SAPLING = registerBlockNoItem("palm_sapling", () -> new PalmSaplingBlock(BFSaplingGenerators.PALM_SAPLING_GENERATOR, BFProperties.blockCopy(Blocks.OAK_SAPLING).sound(SoundType.CROP)));
    public static final Supplier<Block> PALM_LOG = registerBlock("palm_log", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_LOG.get())));
    public static final Supplier<Block> PALM_WOOD = registerBlock("palm_wood", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.APPLE_WOOD.get())));
    public static final Supplier<Block> STRIPPED_PALM_LOG = registerBlock("stripped_palm_log", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_LOG.get())));
    public static final Supplier<Block> STRIPPED_PALM_WOOD = registerBlock("stripped_palm_wood", () -> new StrippedFruitLogBlock(BFProperties.blockCopy(BFBlocks.STRIPPED_APPLE_WOOD.get())));
    public static final Supplier<Block> PALM_CROWN = registerBlock("palm_crown", () -> new PalmCrownBlock(BFProperties.blockCopy(Blocks.SPRUCE_PLANKS)));
    public static final Supplier<Block> PALM_FROND = registerBlockNoItem("palm_frond", () -> new PalmFrondBlock(BFProperties.block().noCollision().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.2F).sound(SoundType.AZALEA_LEAVES).noOcclusion().ignitedByLava().pushReaction(PushReaction.POPPED).forceSolidOff()));
    public static final Supplier<Block> POTTED_PALM_FROND = registerBlockNoItem("potted_palm_frond", () -> new FlowerPotBlock(PALM_FROND.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> WALL_PALM_FROND = registerBlockNoItem("wall_palm_frond", () -> new WallPalmFrondBlock(BFProperties.block().noCollision().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.2F).sound(SoundType.AZALEA_LEAVES).noOcclusion().ignitedByLava().pushReaction(PushReaction.POPPED).forceSolidOff()));
    public static final Supplier<Block> COCONUT = registerBlockNoItem("coconut", () -> new CoconutBlock(BFProperties.block().strength(0.2f).mapColor(MapColor.COLOR_BROWN).dynamicShape().sound(SoundType.AZALEA).pushReaction(PushReaction.POPPED).randomTicks().noOcclusion()));
    public static final Supplier<Block> PALM_MULCH = registerBlock("palm_mulch", () -> new MulchBlock(BFProperties.block().forceSolidOff().mapColor(MapColor.COLOR_BROWN).forceSolidOff().strength(0.4f).sound(SoundType.ROOTED_DIRT).ignitedByLava()));
    public static final Supplier<Block> PALM_MULCH_BLOCK = registerBlock("palm_mulch_block", () -> new MulchBlockBlock(BFProperties.block().mapColor(MapColor.COLOR_BROWN).strength(0.4f).sound(SoundType.ROOTED_DIRT).ignitedByLava()));
    public static final Supplier<Block> COCONUT_CAKE = registerBlock("coconut_cake", () -> new NoCandleCakeBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> PACKED_COCONUT_COIR = registerBlock("packed_coconut_coir", () -> new Block(BFProperties.block().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 1.0F).sound(BFSoundTypes.COIR)));
    // 26.3 consolidated the per-dyecolor carpet/bed/wool/etc. block families into a single
    // Blocks.CARPET / Blocks.BED ColorCollection<Block>, indexed with .pick(DyeColor) - the
    // old flat Blocks.WHITE_CARPET / Blocks.BROWN_BED constants no longer exist (confirmed via
    // javap on Blocks).
    public static final Supplier<Block> COIR_CARPET = registerBlock("coir_carpet", () -> new CarpetBlock(BFProperties.blockCopy(Blocks.CARPET.pick(net.minecraft.world.item.DyeColor.WHITE)).mapColor(MapColor.WOOD).sound(BFSoundTypes.COIR)));
    public static final Supplier<Block> COIR_BRICKS = registerBlock("coir_bricks", () -> new Block(BFProperties.block().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.5F, 1.0F).sound(SoundType.PACKED_MUD)));
    public static final Supplier<Block> COIR_BRICK_SLAB = registerBlock("coir_brick_slab", () -> new SlabBlock(BFProperties.block().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.5F, 1.0F).sound(SoundType.PACKED_MUD)));
    public static final Supplier<Block> COIR_BRICK_STAIRS = registerBlock("coir_brick_stairs", () -> new ModStairsBlock(COIR_BRICKS.get().defaultBlockState(), BFProperties.block().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.5F, 1.0F).sound(SoundType.PACKED_MUD)));
    public static final Supplier<Block> COIR_BRICK_WALL = registerBlock("coir_brick_wall", () -> new WallBlock(BFProperties.block().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.5F, 1.0F).sound(SoundType.PACKED_MUD)));
    public static final Supplier<Block> COIR_BED = registerBlock("coir_bed", () -> new CoirBedBlock(BFProperties.blockCopy(Blocks.BED.pick(net.minecraft.world.item.DyeColor.BROWN))), new Item.Properties().stacksTo(1));
    public static final Supplier<Block> COCONUT_CANDLE = registerBlock("coconut_candle", () -> new CoconutCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));

    public static final Supplier<Block> WILD_WHEAT = registerBlock("wild_wheat", () -> new WildCropBlock(BFProperties.block().mapColor(MapColor.WOOD).replaceable().noCollision().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_CARROTS = registerBlock("wild_carrots", () -> new WildCropBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_POTATOES = registerBlock("wild_potatoes", () -> new WildCropBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_BEETROOTS = registerBlock("wild_beetroots", () -> new WildCropBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_LEEKS = registerBlock("wild_leeks", () -> new WildCropBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_MAIZE = registerBlock("wild_maize", () -> new WildMaizeBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).offsetType(BlockBehaviour.OffsetType.XYZ).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_PASSION_FRUIT_VINE = registerBlock("wild_passion_fruit_vine", () -> new WildVineCropBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> WILD_ELDERBERRY_VINE = registerBlock("wild_elderberry_vine", () -> new WildVineCropBlock(BFProperties.block().mapColor(MapColor.COLOR_GREEN).replaceable().noCollision().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> FELDSPAR_BLOCK = registerBlock("feldspar_block", () -> new Block(BFProperties.blockCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f).sound(SoundType.TUFF)));
    public static final Supplier<Block> CUT_FELDSPAR_BLOCK = registerBlock("cut_feldspar_block", () -> new Block(BFProperties.blockCopy(BFBlocks.FELDSPAR_BLOCK.get())));
    public static final Supplier<Block> FELDSPAR_BRICKS = registerBlock("feldspar_bricks", () -> new Block(BFProperties.blockCopy(BFBlocks.FELDSPAR_BLOCK.get())));
    public static final Supplier<Block> FELDSPAR_BRICK_STAIRS = registerBlock("feldspar_brick_stairs", () -> new ModStairsBlock(BFBlocks.FELDSPAR_BRICKS.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.FELDSPAR_BLOCK.get())));
    public static final Supplier<Block> FELDSPAR_BRICK_SLAB = registerBlock("feldspar_brick_slab", () -> new SlabBlock(BFProperties.blockCopy(BFBlocks.FELDSPAR_BLOCK.get())));
    public static final Supplier<Block> FELDSPAR_BRICK_WALL = registerBlock("feldspar_brick_wall", () -> new WallBlock(BFProperties.blockCopy(BFBlocks.FELDSPAR_BLOCK.get())));
    public static final Supplier<Block> FELDSPAR_LANTERN = registerBlock("feldspar_lantern", () -> new FeldsparLanternBlock(BFProperties.block().mapColor(MapColor.METAL).forceSolidOn().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 8).noOcclusion().pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> TINGED_GLASS = registerBlock("tinged_glass", () -> new TingedGlassBlock(BFProperties.block().instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS).noOcclusion()));

    public static final Supplier<Block> CERAMIC_CLAY_BLOCK = registerBlock("ceramic_clay_block", () -> new Block(BFProperties.blockCopy(Blocks.CLAY).instrument(NoteBlockInstrument.FLUTE).mapColor(MapColor.SNOW)));
    public static final Supplier<Block> CERAMIC_TILES = registerDyeableCeramicBlock("ceramic_tiles", () -> new CeramicTilesBlock(BFProperties.block().requiresCorrectToolForDrops().strength(2f, 16f).sound(BFSoundTypes.CERAMIC_TILES).instrument(BFNoteBlockInstruments.OCARINA).mapColor(MapColor.QUARTZ)));
    public static final Supplier<Block> CERAMIC_TILE_STAIRS = registerDyeableCeramicBlock("ceramic_tile_stairs", () -> new CeramicTileStairsBlock(BFBlocks.CERAMIC_TILES.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.CERAMIC_TILES.get())));
    public static final Supplier<Block> CERAMIC_TILE_SLAB = registerDyeableCeramicBlock("ceramic_tile_slab", () -> new CeramicTileSlabBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_TILES.get())));
    //public static final Supplier<Block> CERAMIC_TILE_WALL = registerDyeableCeramicBlock("ceramic_tile_wall", () -> new CeramicTileWallBlock(AbstractBlock.Settings.copy(BFBlocks.CERAMIC_TILES)));
    public static final Supplier<Block> CRACKED_CERAMIC_TILES = registerDyeableCeramicBlock("cracked_ceramic_tiles", () -> new CeramicTilesBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_TILES.get())));
    public static final Supplier<Block> CHECKERED_CERAMIC_TILES = registerDyeableCeramicBlock("checkered_ceramic_tiles", () -> new CeramicTilesBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_TILES.get())));
    public static final Supplier<Block> CHECKERED_CERAMIC_TILE_STAIRS = registerDyeableCeramicBlock("checkered_ceramic_tile_stairs", () -> new CeramicTileStairsBlock(BFBlocks.CHECKERED_CERAMIC_TILES.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.CHECKERED_CERAMIC_TILES.get())));
    public static final Supplier<Block> CHECKERED_CERAMIC_TILE_SLAB = registerDyeableCeramicBlock("checkered_ceramic_tile_slab", () -> new CeramicTileSlabBlock(BFProperties.blockCopy(BFBlocks.CHECKERED_CERAMIC_TILES.get())));
    //public static final Supplier<Block> CHECKERED_CERAMIC_TILE_WALL = registerDyeableCeramicBlock("checkered_ceramic_tile_wall", () -> new CeramicTileWallBlock(AbstractBlock.Settings.copy(BFBlocks.CHECKERED_CERAMIC_TILES)));
    public static final Supplier<Block> CRACKED_CHECKERED_CERAMIC_TILES = registerDyeableCeramicBlock("cracked_checkered_ceramic_tiles", () -> new CeramicTilesBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_TILES.get())));
    public static final Supplier<Block> CERAMIC_TILE_PILLAR = registerDyeableCeramicBlock("ceramic_tile_pillar", () -> new CeramicTilePillarBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_TILES.get())));
    public static final Supplier<Block> CERAMIC_MOSAIC = registerDyeableCeramicBlock("ceramic_mosaic", () -> new CeramicTilesBlock(BFProperties.block().requiresCorrectToolForDrops().strength(2f, 16f).sound(BFSoundTypes.CERAMIC_TILES).instrument(BFNoteBlockInstruments.OCARINA).mapColor(MapColor.QUARTZ)));
    public static final Supplier<Block> CERAMIC_MOSAIC_STAIRS = registerDyeableCeramicBlock("ceramic_mosaic_stairs", () -> new CeramicTileStairsBlock(BFBlocks.CERAMIC_MOSAIC.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.CERAMIC_MOSAIC.get())));
    public static final Supplier<Block> CERAMIC_MOSAIC_SLAB = registerDyeableCeramicBlock("ceramic_mosaic_slab", () -> new CeramicTileSlabBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_MOSAIC.get())));
    //public static final Supplier<Block> CERAMIC_MOSAIC_WALL = registerDyeableCeramicBlock("ceramic_mosaic_wall", () -> new CeramicTileWallBlock(AbstractBlock.Settings.copy(BFBlocks.CERAMIC_MOSAIC)));
    public static final Supplier<Block> CHECKERED_CERAMIC_MOSAIC = registerDyeableCeramicBlock("checkered_ceramic_mosaic", () -> new CeramicTilesBlock(BFProperties.blockCopy(BFBlocks.CERAMIC_MOSAIC.get())));
    public static final Supplier<Block> CHECKERED_CERAMIC_MOSAIC_STAIRS = registerDyeableCeramicBlock("checkered_ceramic_mosaic_stairs", () -> new CeramicTileStairsBlock(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get().defaultBlockState(), BFProperties.blockCopy(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get())));
    public static final Supplier<Block> CHECKERED_CERAMIC_MOSAIC_SLAB = registerDyeableCeramicBlock("checkered_ceramic_mosaic_slab", () -> new CeramicTileSlabBlock(BFProperties.blockCopy(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get())));
    //public static final Supplier<Block> CHECKERED_CERAMIC_MOSAIC_WALL = registerDyeableCeramicBlock("checkered_ceramic_mosaic_wall", () -> new CeramicTileWallBlock(AbstractBlock.Settings.copy(BFBlocks.CHECKERED_CERAMIC_MOSAIC)));

    public static final Supplier<Block> CERAMIC_PRESSURE_PLATE = registerDyeableCeramicBlock("ceramic_pressure_plate", () -> new CeramicPressurePlateBlock(BFProperties.block().mapColor(MapColor.NONE).forceSolidOn().sound(BFSoundTypes.CERAMIC_DECORATION).instrument(BFNoteBlockInstruments.OCARINA).noCollision().strength(0.5F).pushReaction(PushReaction.POPPED), BFBlockSetTypes.CERAMIC));
    public static final Supplier<Block> CERAMIC_BUTTON = registerDyeableCeramicBlock("ceramic_button", () -> new CeramicButtonBlock(BFProperties.block().mapColor(MapColor.NONE).forceSolidOn().sound(BFSoundTypes.CERAMIC_DECORATION).instrument(BFNoteBlockInstruments.OCARINA).noCollision().strength(0.5F).pushReaction(PushReaction.POPPED), BFBlockSetTypes.CERAMIC, 10, true));
    public static final Supplier<Block> CERAMIC_LEVER = registerDyeableCeramicBlock("ceramic_lever", () -> new CeramicLeverBlock(BFProperties.block().mapColor(MapColor.NONE).forceSolidOn().sound(BFSoundTypes.CERAMIC_DECORATION).instrument(BFNoteBlockInstruments.OCARINA).noCollision().strength(0.5F).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> CERAMIC_DISH = registerCeramicDishBlock("ceramic_dish", () -> new CeramicDishBlock(BFProperties.block().mapColor(MapColor.NONE).sound(BFSoundTypes.CERAMIC_DECORATION).strength(0.2F).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> CERAMIC_DOOR = registerDyeableCeramicBlock("ceramic_door", () -> new CeramicDoorBlock(BFProperties.block().requiresCorrectToolForDrops().strength(2f, 16f).sound(BFSoundTypes.CERAMIC_DECORATION).instrument(BFNoteBlockInstruments.OCARINA).mapColor(MapColor.QUARTZ), BFBlockSetTypes.CERAMIC));
    public static final Supplier<Block> CERAMIC_TRAPDOOR = registerDyeableCeramicBlock("ceramic_trapdoor", () -> new CeramicTrapdoorBlock(BFProperties.block().requiresCorrectToolForDrops().strength(2f, 16f).sound(BFSoundTypes.CERAMIC_DECORATION).instrument(BFNoteBlockInstruments.OCARINA).mapColor(MapColor.QUARTZ), BFBlockSetTypes.CERAMIC));
    public static final Supplier<Block> CERAMIC_CHEST = registerDyeableCeramicBlock("ceramic_chest", () -> new CeramicChestBlock(BFProperties.block().requiresCorrectToolForDrops().strength(2f, 16f).sound(BFSoundTypes.CERAMIC_TILES).instrument(BFNoteBlockInstruments.OCARINA).mapColor(MapColor.QUARTZ)));

    public static final Supplier<Block> SOLID_CERAMIC = registerDyeableCeramicBlock("solid_ceramic", () -> new SolidCeramicBlock(BFProperties.blockCopy(CERAMIC_TILES.get()).destroyTime(100f).sound(BFSoundTypes.CERAMIC_DECORATION).lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)), props -> props.rarity(Rarity.EPIC));

    public static final Supplier<Block> FERMENTATION_VESSEL = registerBlock("fermentation_vessel", () -> new FermentationVesselBlock(BFProperties.block().mapColor(MapColor.QUARTZ).strength(2, 5).instrument(BFNoteBlockInstruments.OCARINA).requiresCorrectToolForDrops().noOcclusion().sound(BFSoundTypes.CERAMIC_DECORATION)));
    public static final Supplier<Block> APPLE_BLOCK = registerBlock("apple_block", () -> new AppleBlock(BFProperties.block().mapColor(MapColor.COLOR_RED).strength(1f).instrument(NoteBlockInstrument.DIDGERIDOO).sound(SoundType.WOOD).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> GOLDEN_APPLE_BLOCK = registerBlock("golden_apple_block", () -> new GoldenAppleBlock(BFProperties.block().mapColor(MapColor.COLOR_YELLOW).strength(1f).instrument(NoteBlockInstrument.DIDGERIDOO).sound(SoundType.METAL).pushReaction(PushReaction.POPPED)), new Item.Properties().rarity(Rarity.RARE));
    public static final Supplier<Block> ORANGE_BLOCK = registerBlock("orange_block", () -> new OrangeBlock(BFProperties.block().mapColor(MapColor.COLOR_ORANGE).strength(0.5f).instrument(NoteBlockInstrument.DIDGERIDOO).sound(SoundType.WOOD)));
    public static final Supplier<Block> LEMON_BLOCK = registerBlock("lemon_block", () -> new LemonBlock(BFProperties.block().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).instrument(NoteBlockInstrument.DIDGERIDOO).sound(SoundType.WOOD)));
    public static final Supplier<Block> PLUM_BLOCK = registerBlock("plum_block", () -> new PlumBlock(BFProperties.block().mapColor(MapColor.WARPED_HYPHAE).strength(0.5f).instrument(NoteBlockInstrument.DIDGERIDOO).sound(SoundType.WOOD)));
    public static final Supplier<Block> HOARY_APPLE_BLOCK = registerBlock("hoary_apple_block", () -> new HoaryAppleBlock(BFProperties.block().mapColor(MapColor.TERRACOTTA_GRAY).strength(0.5f).instrument(BFNoteBlockInstruments.OLD_PIANO).sound(SoundType.WOOD)));
    public static final Supplier<Block> LEEKS = registerBlockNoItem("leeks", () -> new LeekCropBlock(BFProperties.block().mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> MAIZE_CROP = registerBlockNoItem("maize_crop", () -> new MaizeCropBlock(BFProperties.block().mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> SPONGEKIN_SPROUT = registerBlock("spongekin_sprout", () -> new SpongekinSproutBlock(BFProperties.block().mapColor(MapColor.WATER).noCollision().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> SPONGEKIN_STEM = registerBlockNoItem("spongekin_stem", () -> new SpongekinStemBlock(BFProperties.block().mapColor(MapColor.WATER).noCollision().randomTicks().instabreak().sound(SoundType.WET_GRASS).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> SPONGEKIN = registerBlock("spongekin", () -> new SpongekinBlock(BFProperties.block().mapColor(MapColor.WARPED_WART_BLOCK).instrument(BFNoteBlockInstruments.STEEL_DRUM).strength(1.0f).sound(BFSoundTypes.SPONGEKIN).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> PRISMARINE_BLOSSOM = registerBlock("prismarine_blossom", () -> new PrismarineBlossomBlock(BFProperties.block().mapColor(MapColor.COLOR_CYAN).randomTicks().strength(0.4f).noOcclusion().noCollision().sound(SoundType.CALCITE).lightLevel(state -> state.getValue(BlockStateProperties.WATERLOGGED) ? 12 : 0).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> SCORCHKIN_STEM = registerBlockNoItem("scorchkin_stem", () -> new ScorchkinStemBlock(BFProperties.block().mapColor(MapColor.NONE).noCollision().randomTicks().instabreak().sound(SoundType.NETHER_WART).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> SCORCHKIN = registerBlock("scorchkin", () -> new Block(BFProperties.block().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.DIDGERIDOO).strength(1.0f).sound(SoundType.WOOD).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> TEA_SHRUB = registerBlockNoItem("tea_shrub", () -> new TeaShrubBlock(BFProperties.block().noOcclusion().strength(0.5f).randomTicks().noCollision().mapColor(MapColor.COLOR_GREEN).sound(SoundType.AZALEA).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> CHAMOMILE_FLOWERS = registerBlock("chamomile_flowers", () -> new ChamomileFlowersBlock(BFProperties.block().mapColor(MapColor.QUARTZ).noCollision().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> HONEYSUCKLE = registerBlock("honeysuckle", () -> new TeaFlowerBlock(MobEffects.REGENERATION, 5, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> POTTED_HONEYSUCKLE = registerBlockNoItem("potted_honeysuckle", () -> new FlowerPotBlock(BFBlocks.HONEYSUCKLE.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));
    public static final Supplier<Block> VIOLET_BELLFLOWER = registerBlock("violet_bellflower", () -> new TeaFlowerBlock(MobEffects.INVISIBILITY, 5, BFProperties.blockCopy(Blocks.POPPY)));
    public static final Supplier<Block> POTTED_VIOLET_BELLFLOWER = registerBlockNoItem("potted_violet_bellflower", () -> new FlowerPotBlock(BFBlocks.VIOLET_BELLFLOWER.get(), BFProperties.blockCopy(Blocks.POTTED_POPPY)));

    public static final Supplier<Block> GRISTMILL = registerBlock("gristmill", () -> new GristmillBlock(BFProperties.block().destroyTime(2.5f).instrument(NoteBlockInstrument.DIDGERIDOO).mapColor(MapColor.WOOD).sound(SoundType.WOOD)));
    public static final Supplier<Block> GREEN_TEA_CANDLE = registerBlock("green_tea_candle", () -> new GreenTeaCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> BLACK_TEA_CANDLE = registerBlock("black_tea_candle", () -> new BlackTeaCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> CHAMOMILE_CANDLE = registerBlock("chamomile_candle", () -> new ChamomileCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> HONEYSUCKLE_CANDLE = registerBlock("honeysuckle_candle", () -> new HoneysuckleCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> BELLFLOWER_CANDLE = registerBlock("bellflower_candle", () -> new BellflowerCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> TORCHFLOWER_CANDLE = registerBlock("torchflower_candle", () -> new TorchflowerCandleBlock(BFProperties.block().noOcclusion().strength(0.1f).sound(SoundType.CANDLE).lightLevel(createLightLevelFromLitBlockState(12)).pushReaction(PushReaction.POPPED)));

    public static final Supplier<Block> PASSION_FRUIT_TART = registerBlock("passion_fruit_tart", () -> new TartBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> ELDERBERRY_TART = registerBlock("elderberry_tart", () -> new TartBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> GLOW_BERRY_TART = registerBlock("glow_berry_tart", () -> new TartBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> LAPISBERRY_TART = registerBlock("lapisberry_tart", () -> new TartBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> SWEET_BERRY_TART = registerBlock("sweet_berry_tart", () -> new TartBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> APPLE_PIE = registerBlock("apple_pie", () -> new PieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> ORANGE_PIE = registerBlock("orange_pie", () -> new PieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> LEMON_PIE = registerBlock("lemon_pie", () -> new PieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> PLUM_PIE = registerBlock("plum_pie", () -> new PieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> HOARY_PIE = registerBlock("hoary_pie", () -> new PieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> PUMPKIN_PIE = registerBlockNoItem("pumpkin_pie", () -> new PumpkinPieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)));
    public static final Supplier<Block> MELON_PIE = registerBlock("melon_pie", () -> new PieBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> COCOA_CAKE = registerBlock("cocoa_cake", () -> new CakeWithToppingBlock(() -> Items.SWEET_BERRIES, BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> SPONGE_CAKE = registerBlock("sponge_cake", () -> new SpongeCakeBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED).lightLevel((state) -> state.getValue(SpongeCakeBlock.PICKLED) && state.getValue(BlockStateProperties.BITES) == 0 ? 5 : 0)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> ARTISAN_BREAD = registerBlock("artisan_bread", () -> new ArtisanBreadBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)), new Item.Properties().stacksTo(16));
    public static final Supplier<Block> ARTISAN_COOKIE = registerBlockNoItem("artisan_cookies", () -> new ArtisanCookiesBlock(BFProperties.block().noOcclusion().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.POPPED)));

//    public static final Supplier<Block> OAK_PICKETS = registerBlock("oak_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> SPRUCE_PICKETS = registerBlock("spruce_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> BIRCH_PICKETS = registerBlock("birch_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> JUNGLE_PICKETS = registerBlock("jungle_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> ACACIA_PICKETS = registerBlock("acacia_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> DARK_OAK_PICKETS = registerBlock("dark_oak_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> MANGROVE_PICKETS = registerBlock("mangrove_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> CHERRY_PICKETS = registerBlock("cherry_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> BAMBOO_PICKETS = registerBlock("bamboo_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> WALNUT_PICKETS = registerBlock("walnut_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> HOARY_PICKETS = registerBlock("hoary_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> CRIMSON_PICKETS = registerBlock("crimson_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
//    public static final Supplier<Block> WARPED_PICKETS = registerBlock("warped_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()));
    public static final Supplier<Block> IRON_RAILING = registerBlock("iron_railing", () -> new PicketsBlock(BFProperties.block().mapColor(MapColor.NONE).strength(1.0F, 2.0f).sound(SoundType.METAL).forceSolidOff().noOcclusion()));

    public static final Supplier<Block> GRASSY_DIRT = registerBlock("grassy_dirt", () -> new GrassyDirtBlock(BFProperties.blockCopy(Blocks.DIRT).randomTicks()));
    public static final Supplier<Block> FLOUR_BLOCK = registerBlock("flour_block", () -> new FlourBlock(BFProperties.blockCopy(Blocks.SAND).sound(SoundType.POWDER_SNOW).mapColor(MapColor.TERRACOTTA_WHITE)));


    public static final Supplier<Block> GOLDEN_APPLE_LOG = registerBlock("golden_apple_log", () -> new FruitLogBlock(BFProperties.blockCopy(Blocks.OAK_LOG).noOcclusion().forceSolidOff()), new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Supplier<Block> GOLDEN_APPLE_WOOD = registerBlock("golden_apple_wood", () -> new FruitLogBlock(BFProperties.blockCopy(Blocks.OAK_WOOD).noOcclusion().forceSolidOff()), new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Supplier<Block> GOLDEN_APPLE_LEAVES = registerBlock("golden_apple_leaves", () -> new GoldenAppleLeavesBlock(BFProperties.blockCopy(Blocks.OAK_LEAVES).mapColor(MapColor.GOLD)), new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Supplier<Block> FLOWERING_GOLDEN_APPLE_LEAVES = registerBlock("flowering_golden_apple_leaves", () -> new GoldenAppleLeavesBlock(BFProperties.blockCopy(Blocks.OAK_LEAVES).mapColor(MapColor.GOLD)), new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Supplier<Block> GOLDEN_APPLE_SAPLING = registerBlock("golden_apple_sapling", () -> new GoldenAppleSaplingBlock(BFSaplingGenerators.GOLDEN_APPLE_SAPLING_GENERATOR, BFProperties.blockCopy(Blocks.OAK_SAPLING).randomTicks().lightLevel(state -> 7)), new Item.Properties().rarity(Rarity.EPIC));
    public static final Supplier<Block> POTTED_GOLDEN_APPLE_SAPLING = registerBlockNoItem("potted_golden_apple_sapling", () -> new PottedGoldenAppleSaplingBlock(BFBlocks.GOLDEN_APPLE_SAPLING.get(), BFProperties.blockCopy(Blocks.POTTED_OAK_SAPLING).randomTicks().lightLevel(state -> 7)));
    public static final Supplier<Block> HANGING_GOLDEN_APPLE = registerBlockNoItem("hanging_golden_apple", () -> new HangingGoldenAppleBlock(BFProperties.block().strength(0.5f).mapColor(MapColor.GOLD).noOcclusion().dynamicShape().sound(SoundType.AZALEA).pushReaction(PushReaction.IMMOVEABLE).randomTicks().offsetType(BlockBehaviour.OffsetType.XZ).lightLevel((state) -> 7)));
    public static final Supplier<Block> HANGING_WITHERED_GOLDEN_APPLE = registerBlockNoItem("hanging_withered_golden_apple", () -> new HangingWitheredGoldenAppleBlock(BFProperties.block().mapColor(MapColor.TERRACOTTA_BLACK).noOcclusion().dynamicShape().sound(SoundType.AZALEA).pushReaction(PushReaction.POPPED).offsetType(BlockBehaviour.OffsetType.XZ)));

    private static void registerWoodBlocks() {
        for (String wood : BountifulFaresUtil.WOOD_TYPES) {
            if (wood != "oak") {
                TRELLISES.put(wood, registerBlockNoItem(wood + "_trellis", () -> new TrellisBlock(BFProperties.block().noOcclusion().strength(1.0f).sound(BFSoundTypes.LIGHT_WOOD).mapColor(MapColor.NONE).instrument(NoteBlockInstrument.BASS).randomTicks().noOcclusion())));
            } else {
                TRELLISES.put(wood, registerBlockNoItem("trellis", () -> new TrellisBlock(BFProperties.block().noOcclusion().strength(1.0f).sound(BFSoundTypes.LIGHT_WOOD).mapColor(MapColor.NONE).instrument(NoteBlockInstrument.BASS).randomTicks().noOcclusion())));
            }

            PICKETS.put(wood, registerBlock(wood + "_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion())));
        }
    }

    private static void registerDyeBlocks() {
        for (DyeColor color : Arrays.stream(DyeColor.values()).limit(16).toList()) {
            if (color == DyeColor.BROWN) {
                JACK_O_STRAWS.put(color, registerBlock(color.getName() + "_jack_o_straw", () ->
                        new BrownJackOStrawBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion().pushReaction(PushReaction.POPPED))
                ));
            } else {
                JACK_O_STRAWS.put(color, registerBlock(color.getName() + "_jack_o_straw", () ->
                        new JackOStrawBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).lightLevel(createLightLevelFromLitBlockState(12)).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion().pushReaction(PushReaction.POPPED))
                ));
            }
        }
    }

    public static ToIntFunction<BlockState> createLightLevelFromLitBlockState(int litLevel) {
        return state -> state.getValue(BlockStateProperties.LIT) ? litLevel : 0;
    }

    public static Supplier<Block> registerBlock(String name, Supplier<Block> block) {
        return BFRegistryHelper.registerBlock(BountifulFares.MOD_ID, name, block);
    }

    public static Supplier<Block> registerBlock(String name, Supplier<Block> block, Item.Properties properties) {
        return BFRegistryHelper.registerBlock(BountifulFares.MOD_ID, name, block, properties);
    }

    private static Supplier<Block> registerDyeableCeramicBlock(String name, Supplier<Block> block) {
        Supplier<Block> block1 = registerBlockNoItem(name, block);
        BFRegistryHelper.registerItem(BountifulFares.MOD_ID, name, () -> new DyeableCeramicBlockItem(block1.get(), BFProperties.blockItem(block1.get())));
        return block1;
    }

    // Item properties are customized through an operator applied inside the item registration
    // scope, since 26.3 item properties must carry their id at construction (see BFProperties).
    private static Supplier<Block> registerDyeableCeramicBlock(String name, Supplier<Block> block, java.util.function.UnaryOperator<Item.Properties> itemSettings) {
        Supplier<Block> block1 = registerBlockNoItem(name, block);
        BFRegistryHelper.registerItem(BountifulFares.MOD_ID, name, () -> new DyeableCeramicBlockItem(block1.get(), itemSettings.apply(BFProperties.blockItem(block1.get()))));
        return block1;
    }

    private static Supplier<Block> registerBlockNoItem(String name, Supplier<Block> block) {
        return BFRegistryHelper.registerBlockNoItem(BountifulFares.MOD_ID, name, block);
    }

    private static Supplier<Block> registerCeramicDishBlock(String name, Supplier<Block> block) {
        Supplier<Block> block1 = registerBlockNoItem(name, block);
        BFRegistryHelper.registerItem(BountifulFares.MOD_ID, name, () -> new CeramicDishBlockItem(block1.get(), BFProperties.blockItem(block1.get())));
        return block1;
    }

    public static void registerBlocks() {
        registerWoodBlocks();
        registerDyeBlocks();
    }
}
