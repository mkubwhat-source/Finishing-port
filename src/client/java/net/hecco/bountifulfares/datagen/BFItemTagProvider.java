package net.hecco.bountifulfares.datagen;

import com.sidden.flavored.registry.FlavoredItems;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BFItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public BFItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {

        builder(BFItemTags.C_FLOUR)
                .add(BFItems.FLOUR.get().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.C_MILKS)
                .add(Items.MILK_BUCKET.builtInRegistryHolder().key())
                .add(BFItems.COCONUT_MILK_BOTTLE.get().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.C_COCONUTS)
                .add(BFItems.COCONUT.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BountifulFares.NATURES_SPIRIT_MOD_ID, "coconut")))
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("wilderwild", "coconut")))
        ;

        builder(BFItemTags.C_COCONUT_HALVES)
                .add(BFItems.COCONUT_HALF.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BountifulFares.NATURES_SPIRIT_MOD_ID, "coconut_half")))
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("wilderwild", "split_coconut")))
        ;


        builder(BFItemTags.C_WALNUTS)
                .add(BFItems.WALNUT.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("nomansland", "walnuts")))
        ;

        builder(BFItemTags.C_ORANGES)
                .add(BFItems.ORANGE.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("atmospheric", "orange")))
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("atmospheric", "blood_orange")))
        ;

        builder(BFItemTags.C_LEMONS)
                .add(BFItems.LEMON.get().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.C_PLUMS)
                .add(BFItems.PLUM.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("environmental", "plum")))
        ;

        builder(BFItemTags.C_PASSION_FRUIT)
                .add(BFItems.PASSION_FRUIT.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("atmospheric", "passion_fruit")))
        ;

        builder(BFItemTags.C_ELDERBERRIES)
                .add(BFItems.ELDERBERRIES.get().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.C_CORN)
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("hauntedharvest", "corn")))
        ;

        builder(BFItemTags.C_FRUIT)
                .add(BFItems.ORANGE.get().builtInRegistryHolder().key())
                .add(BFItems.LEMON.get().builtInRegistryHolder().key())
                .add(BFItems.PLUM.get().builtInRegistryHolder().key())
                .add(BFItems.HOARY_APPLE.get().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.C_VEGETABLE)
                .add(BFItems.LEEK.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.C_BREAD)
                .add(BFItems.MAIZE_BREAD.get().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.C_CROPS)
                .add(BFItems.PASSION_FRUIT.get().builtInRegistryHolder().key())
                .add(BFItems.ELDERBERRIES.get().builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRIES.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
                .add(BFItems.SPONGEKIN_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.HOARY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.SWEET_BERRY_PIPS.get().builtInRegistryHolder().key())
                .add(BFItems.TEA_BERRIES.get().builtInRegistryHolder().key())
                .add(BFItems.TEA_LEAVES.get().builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFBlocks.SPONGEKIN.get().asItem().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.C_SEEDS)
                .add(BFItems.SPONGEKIN_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.HOARY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.SWEET_BERRY_PIPS.get().builtInRegistryHolder().key())
                .add(BFItems.TEA_BERRIES.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.CHICKEN_FOOD)
                .add(BFItems.GRASS_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.SWEET_BERRY_PIPS.get().builtInRegistryHolder().key())
                .add(BFItems.HOARY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.SPONGEKIN_SEEDS.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.PARROT_FOOD)
                .add(BFItems.GRASS_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.SWEET_BERRY_PIPS.get().builtInRegistryHolder().key())
                .add(BFItems.HOARY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.SPONGEKIN_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.WALNUT.get().builtInRegistryHolder().key())
                .add(BFItems.ARTISAN_COOKIE.get().builtInRegistryHolder().key())
                .add(BFItems.WALNUT_COOKIE.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.PIG_FOOD)
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.SNIFFER_FOOD)
                .add(Items.PITCHER_POD.builtInRegistryHolder().key())
                .add(BFItems.HOARY_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRY_SEEDS.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.FOX_FOOD)
                .add(BFItems.ELDERBERRIES.get().builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRIES.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.COW_FOOD)
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.SHEEP_FOOD)
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.GOAT_FOOD)
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.WOLF_FOOD)
                .addTag(BFItemTags.MULCH)
        ;

        builder(ItemTags.HORSE_FOOD)
                .add(BFItems.ORANGE.get().builtInRegistryHolder().key())
                .add(BFItems.LEMON.get().builtInRegistryHolder().key())
                .add(BFItems.PLUM.get().builtInRegistryHolder().key())
                .add(BFItems.HOARY_APPLE.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE.get().builtInRegistryHolder().key())
        ;

        builder(BlockItemTags.FLOWERS.item())
                .add(BFBlocks.FLOWERING_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_ORANGE_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_LEMON_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_PLUM_LEAVES.get().asItem().builtInRegistryHolder().key())
        ;
        builder(ItemTags.FENCE_GATES).add(BFBlocks.HOARY_FENCE_GATE.get().asItem().builtInRegistryHolder().key());
        builder(BlockItemTags.SMALL_FLOWERS.item()).add(BFBlocks.HONEYSUCKLE.get().asItem().builtInRegistryHolder().key(), BFBlocks.VIOLET_BELLFLOWER.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.LEAVES)
                .addTag(BFItemTags.APPLE_LEAVES)
                .add(BFBlocks.GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key())
                .addTag(BFItemTags.ORANGE_LEAVES)
                .addTag(BFItemTags.LEMON_LEAVES)
                .addTag(BFItemTags.PLUM_LEAVES)
                .addTag(BFItemTags.GOLDEN_APPLE_LEAVES)
                .add(BFBlocks.HOARY_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_LEAVES.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.LOGS_THAT_BURN)
                .addTag(BFItemTags.APPLE_LOGS)
                .addTag(BFItemTags.ORANGE_LOGS)
                .addTag(BFItemTags.LEMON_LOGS)
                .addTag(BFItemTags.PLUM_LOGS)
                .addTag(BFItemTags.HOARY_LOGS)
                .addTag(BFItemTags.WALNUT_LOGS)
                .addTag(BFItemTags.PALM_LOGS)
                .addTag(BFItemTags.GOLDEN_APPLE_LOGS);
        builder(ItemTags.PLANKS).add(BFBlocks.HOARY_PLANKS.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_PLANKS.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_BUTTONS).add(BFBlocks.HOARY_BUTTON.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_BUTTON.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_DOORS).add(BFBlocks.HOARY_DOOR.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_DOOR.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_FENCES).add(BFBlocks.HOARY_FENCE.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_FENCE.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_PRESSURE_PLATES).add(BFBlocks.HOARY_PRESSURE_PLATE.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_PRESSURE_PLATE.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_SLABS).add(BFBlocks.HOARY_SLAB.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_SLAB.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_STAIRS).add(BFBlocks.HOARY_STAIRS.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_STAIRS.get().asItem().builtInRegistryHolder().key());
        builder(ItemTags.WOODEN_TRAPDOORS).add(BFBlocks.HOARY_TRAPDOOR.get().asItem().builtInRegistryHolder().key(), BFBlocks.WALNUT_TRAPDOOR.get().asItem().builtInRegistryHolder().key());
        builder(BlockItemTags.BUTTONS.item()).add(BFBlocks.CERAMIC_BUTTON.get().asItem().builtInRegistryHolder().key());

        builder(BlockItemTags.STAIRS.item())
                .add(BFBlocks.COIR_BRICK_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BlockItemTags.SLABS.item())
                .add(BFBlocks.COIR_BRICK_SLAB.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_SLAB.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_SLAB.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_SLAB.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get().asItem().builtInRegistryHolder().key())
        ;
        builder(ItemTags.WALLS)
                .add(BFBlocks.COIR_BRICK_WALL.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FELDSPAR_BRICK_WALL.get().asItem().builtInRegistryHolder().key())
        //.add(BFBlocks.CERAMIC_TILE_WALL.asItem().builtInRegistryHolder().key())
        //.add(BFBlocks.CERAMIC_MOSAIC_WALL.asItem().builtInRegistryHolder().key())
        //.add(BFBlocks.CHECKERED_CERAMIC_TILE_WALL.asItem().builtInRegistryHolder().key())
        //.add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL.asItem().builtInRegistryHolder().key())
        ;

        builder(ItemTags.CANDLES)
                .add(BFBlocks.GREEN_TEA_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.BLACK_TEA_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHAMOMILE_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.HONEYSUCKLE_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.BELLFLOWER_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.TORCHFLOWER_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_CANDLE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.COCONUT_CANDLE.get().asItem().builtInRegistryHolder().key());

        builder(ItemTags.PIGLIN_LOVED)
                .add(BFBlocks.GOLDEN_APPLE_BLOCK.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key());

        builder(net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags.DYEABLE)
                .add(BFBlocks.CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_TILE_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_TILE_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CHECKERED_CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_PILLAR.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_MOSAIC_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DOOR.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TRAPDOOR.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_BUTTON.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_PRESSURE_PLATE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_LEVER.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DISH.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_CHEST.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.SOLID_CERAMIC.get().asItem().builtInRegistryHolder().key())
                .add(BFItems.ARTISAN_BRUSH.get().asItem().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.APPLE_LEAVES).add(BFBlocks.APPLE_LEAVES.get().asItem().builtInRegistryHolder().key(), BFBlocks.FLOWERING_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key());
        builder(BFItemTags.ORANGE_LEAVES).add(BFBlocks.ORANGE_LEAVES.get().asItem().builtInRegistryHolder().key(), BFBlocks.FLOWERING_ORANGE_LEAVES.get().asItem().builtInRegistryHolder().key());
        builder(BFItemTags.LEMON_LEAVES).add(BFBlocks.LEMON_LEAVES.get().asItem().builtInRegistryHolder().key(), BFBlocks.FLOWERING_LEMON_LEAVES.get().asItem().builtInRegistryHolder().key());
        builder(BFItemTags.PLUM_LEAVES).add(BFBlocks.PLUM_LEAVES.get().asItem().builtInRegistryHolder().key(), BFBlocks.FLOWERING_PLUM_LEAVES.get().asItem().builtInRegistryHolder().key());
        builder(BFItemTags.GOLDEN_APPLE_LEAVES).add(BFBlocks.GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key(), BFBlocks.FLOWERING_GOLDEN_APPLE_LEAVES.get().asItem().builtInRegistryHolder().key());
        builder(BFItemTags.APPLE_LOGS)
                .add(BFBlocks.APPLE_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_APPLE_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.APPLE_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_APPLE_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.ORANGE_LOGS)
                .add(BFBlocks.ORANGE_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_ORANGE_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_ORANGE_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.LEMON_LOGS)
                .add(BFBlocks.LEMON_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_LEMON_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_LEMON_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.PLUM_LOGS)
                .add(BFBlocks.PLUM_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PLUM_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PLUM_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.HOARY_LOGS)
                .add(BFBlocks.HOARY_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_HOARY_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_HOARY_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.PALM_LOGS)
                .add(BFBlocks.PALM_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PALM_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_PALM_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.GOLDEN_APPLE_LOGS)
                .add(BFBlocks.GOLDEN_APPLE_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.WALNUT_LOGS)
                .add(BFBlocks.WALNUT_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_WALNUT_LOG.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_WOOD.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.STRIPPED_WALNUT_WOOD.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.FRUIT_LOGS)
                .addTag(BFItemTags.APPLE_LOGS)
                .addTag(BFItemTags.GOLDEN_APPLE_LOGS)
                .addTag(BFItemTags.ORANGE_LOGS)
                .addTag(BFItemTags.LEMON_LOGS)
                .addTag(BFItemTags.PLUM_LOGS)
                .addTag(BFItemTags.PALM_LOGS)
        ;
        builder(BFItemTags.DYEABLE_CERAMIC_BLOCKS)
                .add(BFBlocks.CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_TILE_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_TILE_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CRACKED_CHECKERED_CERAMIC_TILES.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TILE_PILLAR.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_MOSAIC_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CERAMIC_MOSAIC_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get().asItem().builtInRegistryHolder().key())
                //.add(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL.asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DOOR.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_TRAPDOOR.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_PRESSURE_PLATE.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_BUTTON.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_LEVER.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_DISH.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.CERAMIC_CHEST.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.FELSIC_STONES)
                .add(Items.ANDESITE.builtInRegistryHolder().key())
                .add(Items.GRANITE.builtInRegistryHolder().key())
                .add(Items.DIORITE.builtInRegistryHolder().key())
                .add(Items.TUFF.builtInRegistryHolder().key())
        ;
        builder(BFItemTags.JACK_O_STRAW_LIGHTABLE)
                .addTag(ItemTags.CANDLES)
                .add(Items.TORCH.builtInRegistryHolder().key())
        ;

        builder(BFItemTags.VINE_CROP_SEEDS)
                .add(BFItems.PASSION_FRUIT.get().builtInRegistryHolder().key())
                .add(BFItems.ELDERBERRIES.get().builtInRegistryHolder().key())
                .add(Items.GLOW_BERRIES.builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRY_SEEDS.get().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.COOKED_FISHES)
                .add(Items.COOKED_COD.builtInRegistryHolder().key())
                .add(Items.COOKED_SALMON.builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BountifulFares.NO_MANS_LAND_MOD_ID, "cooked_billhook_bass")))
        ;
        builder(BFItemTags.MEALS)
                .add(BFItems.MUSHROOM_STUFFED_POTATO.get().builtInRegistryHolder().key())
                .add(BFItems.BERRY_STUFFED_POTATO.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE_STUFFED_POTATO.get().builtInRegistryHolder().key())
                .add(BFItems.STUFFED_HOARY_APPLE.get().builtInRegistryHolder().key())
                .add(BFItems.COCONUT_CRUSTED_COD.get().builtInRegistryHolder().key())
                .add(BFItems.PASSION_GLAZED_SALMON.get().builtInRegistryHolder().key())
                .add(BFItems.LEEK_STEW.get().builtInRegistryHolder().key())
                .add(BFItems.FISH_STEW.get().builtInRegistryHolder().key())
                .add(BFItems.APPLE_STEW.get().builtInRegistryHolder().key())
                .add(BFItems.COCONUT_STEW.get().builtInRegistryHolder().key())
                .add(BFItems.STONE_STEW.get().builtInRegistryHolder().key())
                .add(BFItems.BOUNTIFUL_STEW.get().builtInRegistryHolder().key())
                .add(BFItems.SEA_SALAD.get().builtInRegistryHolder().key())
                .add(BFItems.FOREST_MEDLEY.get().builtInRegistryHolder().key())
                .add(BFItems.ARID_MEDLEY.get().builtInRegistryHolder().key())
                .add(BFItems.MEADOW_MEDLEY.get().builtInRegistryHolder().key())
                .add(BFItems.MIRE_MEDLEY.get().builtInRegistryHolder().key())
                .add(BFItems.COASTAL_MEDLEY.get().builtInRegistryHolder().key())
                .add(BFItems.TROPICAL_MEDLEY.get().builtInRegistryHolder().key())
                .add(BFItems.CRUSTED_BEEF.get().builtInRegistryHolder().key())
                .add(BFItems.CRIMSON_CHOW.get().builtInRegistryHolder().key())
                .add(BFItems.WARPED_CHOW.get().builtInRegistryHolder().key())
//                .add(BFItems.CUSTARD.get().builtInRegistryHolder().key())
//                .add(BFItems.PIQUANT_CUSTARD.get().builtInRegistryHolder().key())
//                .add(BFItems.PASSION_CUSTARD.get().builtInRegistryHolder().key())
//                .add(BFItems.COCOA_CUSTARD.get().builtInRegistryHolder().key())
//                .add(BFItems.ANCIENT_CUSTARD.get().builtInRegistryHolder().key())
        ;
        builder(ItemTags.SAPLINGS)
                .add(BFBlocks.APPLE_SAPLING.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.GOLDEN_APPLE_SAPLING.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.ORANGE_SAPLING.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.LEMON_SAPLING.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.PLUM_SAPLING.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.HOARY_APPLE_SAPLING.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_SAPLING.get().asItem().builtInRegistryHolder().key())
        ;
        builder(ItemTags.DIRT)
                .add(BFBlocks.GRASSY_DIRT.get().asItem().builtInRegistryHolder().key())
        ;
        builder(BFItemTags.GRASS_SEEDS_PLANTABLE_ON)
                .add(Items.DIRT.builtInRegistryHolder().key())
                .add(Items.COARSE_DIRT.builtInRegistryHolder().key())
                .add(Items.ROOTED_DIRT.builtInRegistryHolder().key())
                .add(Items.PODZOL.builtInRegistryHolder().key())
                .add(Items.MYCELIUM.builtInRegistryHolder().key())
        ;

        builder(BFItemTags.VINE_CROPS)
                .add(BFItems.PASSION_FRUIT.get().builtInRegistryHolder().key())
                .add(BFItems.ELDERBERRIES.get().builtInRegistryHolder().key())
                .add(Items.GLOW_BERRIES.builtInRegistryHolder().key())
                .add(BFItems.LAPISBERRIES.get().builtInRegistryHolder().key())
        ;

        builder(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(BFItems.LEEK_SEEDS.get().builtInRegistryHolder().key())
                .add(BFItems.MAIZE_SEEDS.get().builtInRegistryHolder().key())
        ;

        for (Supplier<Block> block : BFBlocks.PICKETS.values()) {
            builder(BFItemTags.PICKETS).add(block.get().asItem().builtInRegistryHolder().key());
        }

        builder(BFItemTags.MULCH)
                .add(BFBlocks.WALNUT_MULCH.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.WALNUT_MULCH_BLOCK.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_MULCH.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_MULCH_BLOCK.get().asItem().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.SUGAR_INGREDIENTS)
                .add(Items.SUGAR.builtInRegistryHolder().key())
                .add(Items.HONEY_BOTTLE.builtInRegistryHolder().key())
        ;

        builder(ItemTags.DIRT)
                .add(BFBlocks.WALNUT_MULCH_BLOCK.get().asItem().builtInRegistryHolder().key())
                .add(BFBlocks.PALM_MULCH_BLOCK.get().asItem().builtInRegistryHolder().key());

        builder(BFItemTags.FOOD_CONTAINERS_TIFFINS_CAN_HOLD)
                .add(Items.BOWL.builtInRegistryHolder().key())
        ;

        builder(BFItemTags.FERMENTATION_WATER_SOURCES)
                .add(Items.POTION.builtInRegistryHolder().key())
                .add(Items.WATER_BUCKET.builtInRegistryHolder().key())
                .add(BFItems.WATER_CUP.get().builtInRegistryHolder().key())
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("miners_delight", "water_cup")))
        ;

        // Adds all tiffins automatically
        List<ResourceKey<Item>> tiffins = new ArrayList<>();
        for (Supplier<Item> item : BFItems.TIFFINS.values()) {
            Optional<ResourceKey<Item>> key = BuiltInRegistries.ITEM.getResourceKey(item.get());
            key.ifPresent(tiffins::add);
        }
        builder(BFItemTags.TIFFINS)
                .addAll(tiffins)
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("coral_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("umber_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("canary_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("wasabi_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("sacramento_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("sky_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("blurple_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("lavender_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("sangria_shulker_tiffin")))
                .addOptional(ResourceKey.create(Registries.ITEM, BountifulFares.id("rose_shulker_tiffin")))
        ;

        builder(BFItemTags.CERAMIC_DISH_BLACKLIST)
                .addOptional(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("supplementaries", "lunch_basket")))
        ;

        builder(BFItemTags.SPONGEKIN_INGREDIENTS)
                .add(BFItems.SPONGEKIN_SLICE.get().builtInRegistryHolder().key())
                .add(BFItems.PICKLED_SPONGEKIN.get().builtInRegistryHolder().key())
        ;

        builder(BFItemTags.BEETROOT_INGREDIENTS)
                .add(BFItems.PICKLED_BEETROOT.get().builtInRegistryHolder().key())
                .add(Items.BEETROOT.builtInRegistryHolder().key())
        ;

        builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("dungeonsdelight", "fleshes")))
                .add(BFItems.FOUL_FLESH.get().builtInRegistryHolder().key())
        ;

        builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("supplementaries", "lunch_basket_blacklist")))
                .addTag(BFItemTags.TIFFINS)
        ;
            // Bundled Flavored (1.21.1: data/minecraft/tags/item/pig_food.json); maize is added above.
        builder(ItemTags.PIG_FOOD)
                // the members of #flavored:tomatoes (a hand-written tag datagen cannot reference)
                .add(FlavoredItems.GREEN_TOMATO.get().builtInRegistryHolder().key())
                .add(FlavoredItems.YELLOW_TOMATO.get().builtInRegistryHolder().key())
                .add(FlavoredItems.RED_TOMATO.get().builtInRegistryHolder().key())
                .add(FlavoredItems.SPINACH.get().builtInRegistryHolder().key())
        ;
    }
}
