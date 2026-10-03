package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.component.BlockTransformer;

/**
 * Glue between Flavored and Bountiful Fares, plus the block interactions that were NeoForge hooks.
 * <ul>
 * <li>Merged content: Flavored's flour and corn are Bountiful Fares' flour and maize in this
 * bundle. Registry aliases keep old worlds working - saved {@code flavored:flour},
 * {@code flavored:corn}, {@code flavored:corn_seeds} (and the unused {@code flavored:corn_bush}
 * item) stacks load as the Bountiful Fares items, and planted {@code flavored:corn_bush} blocks as
 * maize crops.</li>
 * <li>Stripping a cinnamon stalk with an axe drops 2-4 cinnamon (1.21.1:
 * {@code CinnamonStalkBlock.getToolModifiedState}, a NeoForge hook). 26.3 axe behavior is the
 * data-driven {@code BlockTransformer}; this registers an axe transform with a loot table
 * ({@code flavored:gameplay/strip_cinnamon_stalk}), exactly how vanilla drops hanging roots when
 * tilling rooted dirt.</li>
 * <li>Honeycomb waxes a stripped cinnamon stalk and an axe scrapes the wax off (1.21.1: NeoForge
 * {@code waxables} data map).</li>
 * </ul>
 */
public final class FlavoredMerges {
    public static final ResourceKey<LootTable> STRIP_CINNAMON_STALK = ResourceKey.create(Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "gameplay/strip_cinnamon_stalk"));

    public static void init() {
        FabricRegistry items = (FabricRegistry) BuiltInRegistries.ITEM;
        items.addAlias(flavored("flour"), bf("flour"));
        items.addAlias(flavored("corn"), bf("maize"));
        items.addAlias(flavored("corn_seeds"), bf("maize_seeds"));
        items.addAlias(flavored("corn_bush"), bf("maize_seeds"));
        ((FabricRegistry) BuiltInRegistries.BLOCK).addAlias(flavored("corn_bush"), bf("maize_crop"));

        BlockTransformerHelper.registerAxe(BlockTransformer.BlockTransformData.builder(
                        RuleBasedStateProvider.builder()
                                .ifTrueThenProvide(BlockPredicate.matchesBlocks(FlavoredBlocks.CINNAMON_STALK.get()),
                                        new CopyPropertiesProvider(FlavoredBlocks.STRIPPED_CINNAMON_STALK.get()))
                                .build())
                .sound(SoundEvents.AXE_STRIP)
                .loot(STRIP_CINNAMON_STALK)
                .dropStrategy(BlockTransformer.DropStrategy.FROM_MIDDLE)
                .build());

        OxidizableBlocksRegistry.registerWaxable(FlavoredBlocks.STRIPPED_CINNAMON_STALK.get(), FlavoredBlocks.WAXED_STRIPPED_CINNAMON_STALK.get());
    }

    private static Identifier flavored(String path) {
        return Identifier.fromNamespaceAndPath(Flavored.MOD_ID, path);
    }

    private static Identifier bf(String path) {
        return Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, path);
    }

    private FlavoredMerges() {
    }
}
