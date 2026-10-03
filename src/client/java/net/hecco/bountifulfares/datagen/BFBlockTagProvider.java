package net.hecco.bountifulfares.datagen;

import com.sidden.flavored.registry.FlavoredBlocks;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.integration.AppledogIntegration;
import net.hecco.bountifulfares.registry.integration.DungeonsDelightIntegration;
import net.hecco.bountifulfares.registry.integration.FarmersDelightIntegration;
import net.hecco.bountifulfares.registry.tags.BFBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BFBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {

    public BFBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // Hearth and Harvest entries of the tags this provider writes (see HHTagAdditions).
        alabaster.hearthandharvest.datagen.HHTagAdditions.addBlockTags(this::builder);

        builder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BFBlocks.FELDSPAR_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.CUT_FELDSPAR_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICKS.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_LANTERN.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_TILE_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_TILE_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CHECKERED_CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_PILLAR.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_MOSAIC_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_PRESSURE_PLATE.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TRAPDOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_BUTTON.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DISH.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_CHEST.get().builtInRegistryHolder().key())
                .add(BFBlocks.FERMENTATION_VESSEL.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.PACKED_COCONUT_COIR.get().builtInRegistryHolder().key())
                .add(BFBlocks.COIR_BRICKS.get().builtInRegistryHolder().key())
                .add(BFBlocks.COIR_BRICK_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.COIR_BRICK_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.COIR_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(BFBlocks.IRON_RAILING.get().builtInRegistryHolder().key())
                .add(DungeonsDelightIntegration.STAINED_SCRAP_RAILING.get().builtInRegistryHolder().key())
        ;

        builder(BlockTags.MINEABLE_WITH_AXE)
                .add(BFBlocks.APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_APPLE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_ORANGE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_ORANGE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_LEMON_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_LEMON_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PLUM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PLUM_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_HOARY_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_HOARY_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_PLANKS.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_FENCE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_FENCE_GATE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_DOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_TRAPDOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_PRESSURE_PLATE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_BUTTON.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_WALL_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_HANGING_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_WALL_HANGING_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_WALNUT_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_WALNUT_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_PLANKS.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_FENCE.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_FENCE_GATE.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_DOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_TRAPDOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_PRESSURE_PLATE.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_BUTTON.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_WALL_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_HANGING_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_WALL_HANGING_SIGN.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PALM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PALM_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_CROWN.get().builtInRegistryHolder().key())
                .add(BFBlocks.GRISTMILL.get().builtInRegistryHolder().key())
                .add(BFBlocks.SPONGEKIN.get().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_APPLE_BLOCK.get().builtInRegistryHolder().key())
                .add(FarmersDelightIntegration.WALNUT_CABINET.get().builtInRegistryHolder().key())
                .add(FarmersDelightIntegration.HOARY_CABINET.get().builtInRegistryHolder().key())
                .add(AppledogIntegration.APPLEDOG_BLOCK.get().builtInRegistryHolder().key())

                .addTag(BFBlockTags.JACK_O_STRAWS)
        ;

        for (Supplier<Block> block : BFBlocks.PICKETS.values()) {
            builder(BlockTags.MINEABLE_WITH_AXE).add(block.get().builtInRegistryHolder().key());
        }

        for (Supplier<Block> block : BFBlocks.TRELLISES.values()) {
            builder(BlockTags.MINEABLE_WITH_AXE).add(block.get().builtInRegistryHolder().key());
        }


        builder(BlockTags.MINEABLE_WITH_HOE)
                .add(BFBlocks.APPLE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_APPLE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_ORANGE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_LEMON_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_PLUM_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.TEA_SHRUB.get().builtInRegistryHolder().key())
        ;
        builder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BFBlocks.WALNUT_MULCH.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_MULCH_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_MULCH.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_MULCH_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_CLAY_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.GRASSY_DIRT.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOUR_BLOCK.get().builtInRegistryHolder().key())
        ;

        builder(BlockTags.FLOWERS)
                .add(BFBlocks.FLOWERING_APPLE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_ORANGE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_LEMON_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_PLUM_LEAVES.get().builtInRegistryHolder().key())
        ;
        builder(BlockTags.BEACON_BASE_BLOCKS).add(BFBlocks.GOLDEN_APPLE_BLOCK.get().builtInRegistryHolder().key());
        builder(BlockTags.CROPS).add(BFBlocks.HOARY_APPLE_SAPLING_CROP.get().builtInRegistryHolder().key(), BFBlocks.MAIZE_CROP.get().builtInRegistryHolder().key(), BFBlocks.LEEKS.get().builtInRegistryHolder().key());

        builder(BlockTags.FLOWER_POTS)
                .add(BFBlocks.POTTED_HOARY_APPLE_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_APPLE_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_GOLDEN_APPLE_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_ORANGE_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_LEMON_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_PLUM_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_GOLDEN_APPLE_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_WALNUT_SAPLING.get().builtInRegistryHolder().key(), BFBlocks.POTTED_PALM_FROND.get().builtInRegistryHolder().key(), BFBlocks.POTTED_HONEYSUCKLE.get().builtInRegistryHolder().key(), BFBlocks.POTTED_VIOLET_BELLFLOWER.get().builtInRegistryHolder().key());

        builder(BlockTags.SMALL_FLOWERS).add(BFBlocks.HONEYSUCKLE.get().builtInRegistryHolder().key(), BFBlocks.VIOLET_BELLFLOWER.get().builtInRegistryHolder().key());

        builder(BlockTags.LEAVES)
                .addTag(BFBlockTags.APPLE_LEAVES)
                .add(BFBlocks.GOLDEN_APPLE_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().builtInRegistryHolder().key())
                .addTag(BFBlockTags.ORANGE_LEAVES)
                .addTag(BFBlockTags.LEMON_LEAVES)
                .addTag(BFBlockTags.PLUM_LEAVES)
                .add(BFBlocks.HOARY_LEAVES.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_LEAVES.get().builtInRegistryHolder().key());
        builder(net.minecraft.tags.BlockItemTags.LOGS_THAT_BURN.block())
                .addTag(BFBlockTags.APPLE_LOGS)
                .addTag(BFBlockTags.GOLDEN_APPLE_LOGS)
                .addTag(BFBlockTags.ORANGE_LOGS)
                .addTag(BFBlockTags.LEMON_LOGS)
                .addTag(BFBlockTags.PLUM_LOGS)
                .addTag(BFBlockTags.PALM_LOGS)
                .addTag(BFBlockTags.WALNUT_LOGS)
                .addTag(BFBlockTags.HOARY_LOGS);
        builder(BlockTags.OVERWORLD_NATURAL_LOGS)
                .add(BFBlocks.WALNUT_LOG.get().builtInRegistryHolder().key());
        builder(BlockTags.PLANKS).add(BFBlocks.WALNUT_PLANKS.get().builtInRegistryHolder().key(), BFBlocks.HOARY_PLANKS.get().builtInRegistryHolder().key());
        builder(BlockTags.STANDING_SIGNS).add(BFBlocks.WALNUT_SIGN.get().builtInRegistryHolder().key(), BFBlocks.HOARY_SIGN.get().builtInRegistryHolder().key());
        builder(BlockTags.CEILING_HANGING_SIGNS).add(BFBlocks.WALNUT_HANGING_SIGN.get().builtInRegistryHolder().key(), BFBlocks.HOARY_HANGING_SIGN.get().builtInRegistryHolder().key());
        builder(BlockTags.WALL_HANGING_SIGNS).add(BFBlocks.WALNUT_WALL_HANGING_SIGN.get().builtInRegistryHolder().key(), BFBlocks.HOARY_WALL_HANGING_SIGN.get().builtInRegistryHolder().key());
        builder(BlockTags.WALL_SIGNS).add(BFBlocks.WALNUT_WALL_SIGN.get().builtInRegistryHolder().key(), BFBlocks.HOARY_WALL_SIGN.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_BUTTONS).add(BFBlocks.WALNUT_BUTTON.get().builtInRegistryHolder().key(), BFBlocks.HOARY_BUTTON.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_DOORS).add(BFBlocks.WALNUT_DOOR.get().builtInRegistryHolder().key(), BFBlocks.HOARY_DOOR.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_FENCES).add(BFBlocks.WALNUT_FENCE.get().builtInRegistryHolder().key(), BFBlocks.HOARY_FENCE.get().builtInRegistryHolder().key());
        builder(BlockTags.FENCE_GATES).add(BFBlocks.WALNUT_FENCE_GATE.get().builtInRegistryHolder().key(), BFBlocks.HOARY_FENCE_GATE.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_PRESSURE_PLATES).add(BFBlocks.WALNUT_PRESSURE_PLATE.get().builtInRegistryHolder().key(), BFBlocks.HOARY_PRESSURE_PLATE.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_SLABS).add(BFBlocks.WALNUT_SLAB.get().builtInRegistryHolder().key(), BFBlocks.HOARY_SLAB.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_STAIRS).add(BFBlocks.WALNUT_STAIRS.get().builtInRegistryHolder().key(), BFBlocks.HOARY_STAIRS.get().builtInRegistryHolder().key());
        builder(BlockTags.WOODEN_TRAPDOORS).add(BFBlocks.WALNUT_TRAPDOOR.get().builtInRegistryHolder().key(), BFBlocks.HOARY_TRAPDOOR.get().builtInRegistryHolder().key());
        builder(BlockTags.DOORS).add(BFBlocks.CERAMIC_DOOR.get().builtInRegistryHolder().key());
        builder(BlockTags.TRAPDOORS).add(BFBlocks.CERAMIC_TRAPDOOR.get().builtInRegistryHolder().key());
        builder(BlockTags.PRESSURE_PLATES).add(BFBlocks.CERAMIC_PRESSURE_PLATE.get().builtInRegistryHolder().key());
        builder(BlockTags.BUTTONS).add(BFBlocks.CERAMIC_BUTTON.get().builtInRegistryHolder().key());

        builder(BlockTags.STAIRS)
                .add(BFBlocks.COIR_BRICK_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get().builtInRegistryHolder().key())
        ;
        builder(BlockTags.SLABS)
                .add(BFBlocks.COIR_BRICK_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get().builtInRegistryHolder().key())
        ;
        builder(BlockTags.WALLS)
                .add(BFBlocks.COIR_BRICK_WALL.get().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_WALL.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_TILE_WALL.builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_MOSAIC_WALL.builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_TILE_WALL.builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL.builtInRegistryHolder().key())
        ;

        builder(BFBlockTags.INFUSED_CANDLES)
                .add(BFBlocks.GREEN_TEA_CANDLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.BLACK_TEA_CANDLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHAMOMILE_CANDLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HONEYSUCKLE_CANDLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.BELLFLOWER_CANDLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.TORCHFLOWER_CANDLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_CANDLE.get().builtInRegistryHolder().key());

        builder(BFBlockTags.APPLE_LEAVES).add(BFBlocks.APPLE_LEAVES.get().builtInRegistryHolder().key(), BFBlocks.FLOWERING_APPLE_LEAVES.get().builtInRegistryHolder().key());
        builder(BFBlockTags.ORANGE_LEAVES).add(BFBlocks.ORANGE_LEAVES.get().builtInRegistryHolder().key(), BFBlocks.FLOWERING_ORANGE_LEAVES.get().builtInRegistryHolder().key());
        builder(BFBlockTags.LEMON_LEAVES).add(BFBlocks.LEMON_LEAVES.get().builtInRegistryHolder().key(), BFBlocks.FLOWERING_LEMON_LEAVES.get().builtInRegistryHolder().key());
        builder(BFBlockTags.PLUM_LEAVES).add(BFBlocks.PLUM_LEAVES.get().builtInRegistryHolder().key(), BFBlocks.FLOWERING_PLUM_LEAVES.get().builtInRegistryHolder().key());
        builder(BFBlockTags.GOLDEN_APPLE_LEAVES).add(BFBlocks.GOLDEN_APPLE_LEAVES.get().builtInRegistryHolder().key(), BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().builtInRegistryHolder().key());
        builder(BFBlockTags.APPLE_LOGS)
                .add(BFBlocks.APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_APPLE_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.GOLDEN_APPLE_LOGS)
                .add(BFBlocks.GOLDEN_APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.ORANGE_LOGS)
                .add(BFBlocks.ORANGE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_ORANGE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_ORANGE_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.LEMON_LOGS)
                .add(BFBlocks.LEMON_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_LEMON_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_LEMON_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.PLUM_LOGS)
                .add(BFBlocks.PLUM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PLUM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PLUM_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.PALM_LOGS)
                .add(BFBlocks.PALM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PALM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PALM_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.WALNUT_LOGS)
                .add(BFBlocks.WALNUT_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_WALNUT_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_WALNUT_WOOD.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.HOARY_LOGS)
                .add(BFBlocks.HOARY_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_HOARY_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_HOARY_WOOD.get().builtInRegistryHolder().key())
        ;

        for (Supplier<Block> block : BFBlocks.JACK_O_STRAWS.values()) {
            builder(BFBlockTags.JACK_O_STRAWS).add(block.get().builtInRegistryHolder().key());
        }
        builder(BFBlockTags.JACK_O_STRAWS)
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("coral_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("umber_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("canary_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("wasabi_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("sacramento_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("sky_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("blurple_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("lavender_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("sangria_jack_o_straw")))
                .addOptional(ResourceKey.create(Registries.BLOCK, BountifulFares.id("rose_jack_o_straw")))
        ;


        builder(BFBlockTags.HANGING_FRUIT)
                .add(BFBlocks.HANGING_APPLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_ORANGE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_LEMON.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_PLUM.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_HOARY_APPLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_GOLDEN_APPLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_WITHERED_GOLDEN_APPLE.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_WALNUTS.get().builtInRegistryHolder().key())
        ;

        builder(BlockTags.BEE_GROWABLES)
                .addTag(BFBlockTags.HANGING_FRUIT)
        ;

        builder(BFBlockTags.CERAMIC_TILES)
                .add(BFBlocks.CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_TILE_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_TILE_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CHECKERED_CERAMIC_TILES.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_PILLAR.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_MOSAIC_WALL.builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL.builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.DYEABLE_CERAMIC_BLOCKS)
                .addTag(BFBlockTags.CERAMIC_TILES)
                .add(BFBlocks.CERAMIC_DOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TRAPDOOR.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_PRESSURE_PLATE.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_BUTTON.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_LEVER.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DISH.get().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_CHEST.get().builtInRegistryHolder().key())
                .add(BFBlocks.SOLID_CERAMIC.get().builtInRegistryHolder().key())
        ;

        builder(BFBlockTags.FELSIC_STONES)
                .add(Blocks.ANDESITE.builtInRegistryHolder().key())
                .add(Blocks.GRANITE.builtInRegistryHolder().key())
                .add(Blocks.DIORITE.builtInRegistryHolder().key())
                .add(Blocks.TUFF.builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.PICKETS)
                .add(BFBlocks.IRON_RAILING.get().builtInRegistryHolder().key())
                .add(DungeonsDelightIntegration.STAINED_SCRAP_RAILING.get().builtInRegistryHolder().key())
        ;
        for (Supplier<Block> block : BFBlocks.PICKETS.values()) {
            builder(BFBlockTags.PICKETS).add(block.get().builtInRegistryHolder().key());
        }
        builder(BlockTags.SAPLINGS)
                .add(BFBlocks.APPLE_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_APPLE_SAPLING.get().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_SAPLING.get().builtInRegistryHolder().key())
        ;
        builder(BFBlockTags.GRASS_SEEDS_PLANTABLE_ON)
                .add(Blocks.DIRT.builtInRegistryHolder().key())
                .add(Blocks.COARSE_DIRT.builtInRegistryHolder().key())
                .add(Blocks.ROOTED_DIRT.builtInRegistryHolder().key())
                .add(Blocks.PODZOL.builtInRegistryHolder().key())
                .add(Blocks.MYCELIUM.builtInRegistryHolder().key())
        ;
        builder(BlockTags.DIRT)
                .add(BFBlocks.GRASSY_DIRT.get().builtInRegistryHolder().key())
        ;

        builder(BFBlockTags.WILD_ELDERBERRY_PLACEABLE_ON)
                .addTag(BlockTags.OVERWORLD_NATURAL_LOGS)
                .add(Blocks.MANGROVE_ROOTS.builtInRegistryHolder().key())
                ;

        builder(BFBlockTags.PALM_SAPLINGS_PLANTABLE_ON)
                .addTag(BlockTags.DIRT)
                .add(Blocks.SAND.builtInRegistryHolder().key())
                .add(Blocks.RED_SAND.builtInRegistryHolder().key())
                .add(Blocks.GRAVEL.builtInRegistryHolder().key())
        ;

        builder(BFBlockTags.SPLITS_COCONUTS)
                .add(Blocks.POINTED_DRIPSTONE.builtInRegistryHolder().key());


        builder(BFBlockTags.IGNORE_PARTICLE_TINT)
                .add(BFBlocks.GRASSY_DIRT.get().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_WOOD.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_LOG.get().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_WOOD.get().builtInRegistryHolder().key())
        ;

        builder(BlockTags.DIRT)
                .add(BFBlocks.WALNUT_MULCH_BLOCK.get().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_MULCH_BLOCK.get().builtInRegistryHolder().key());

        builder(BlockTags.OCCLUDES_VIBRATION_SIGNALS).add(BFBlocks.PACKED_COCONUT_COIR.get().builtInRegistryHolder().key()).add(BFBlocks.COIR_CARPET.get().builtInRegistryHolder().key());

        builder(BFBlockTags.PRISMARINE_PROPAGATION_SUBSTRATE)
                .add(Blocks.SEA_LANTERN.builtInRegistryHolder().key());

        builder(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("insanelib", "blacklisted_better_falling_blocks")))
                .add(BFBlocks.COCONUT.get().builtInRegistryHolder().key())
                .add(BFBlocks.HANGING_WALNUTS.get().builtInRegistryHolder().key());
            // Bundled Flavored: its vanilla-tag entries (1.21.1: hand-written data/minecraft/tags files,
        // which would now collide with these generated ones). Maize is already in #crops above.
        builder(BlockTags.CROPS).add(key(FlavoredBlocks.TOMATO_BUSH), key(FlavoredBlocks.PEPPER_BUSH), key(FlavoredBlocks.GARLICS), key(FlavoredBlocks.SPINACH_BUSH));
        builder(BlockTags.MINEABLE_WITH_AXE).add(key(FlavoredBlocks.CINNAMON_STALK), key(FlavoredBlocks.STRIPPED_CINNAMON_STALK),
                key(FlavoredBlocks.WAXED_STRIPPED_CINNAMON_STALK), key(FlavoredBlocks.KEG));
        builder(BlockTags.MINEABLE_WITH_PICKAXE).add(key(FlavoredBlocks.CHOCOLATE_BLOCK), key(FlavoredBlocks.CHOCOLATE_TILES),
                key(FlavoredBlocks.CHOCOLATE_TILE_STAIRS), key(FlavoredBlocks.CHOCOLATE_TILE_SLAB), key(FlavoredBlocks.KEG),
                key(FlavoredBlocks.MIXING_BOWL), key(FlavoredBlocks.OVEN));
    }

    private static ResourceKey<Block> key(java.util.function.Supplier<Block> block) {
        return block.get().builtInRegistryHolder().key();
    }
}
