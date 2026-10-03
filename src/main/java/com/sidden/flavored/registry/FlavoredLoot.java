package com.sidden.flavored.registry;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.UniformGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Flavored's chest loot. 1.21.1 Flavored replaced six vanilla chest tables with copies that had
 * two extra entries (weight 10) in their first pool; the entries are now added to the live
 * vanilla tables' first pool instead, so the rest of the vanilla (and other mods') loot is kept.
 * The savanna house corn / corn seeds are Bountiful Fares' maize / maize seeds in this bundle.
 * (The knife-butchering animal tables are data: data/minecraft/loot_table/entities.)
 */
public final class FlavoredLoot {
    private record Extra(Supplier<? extends ItemLike> item, int max) {
    }

    private static final Map<ResourceKey<LootTable>, List<Extra>> CHEST_EXTRAS = Map.of(
            BuiltInLootTables.JUNGLE_TEMPLE, List.of(new Extra(FlavoredItems.CHOCOLATE_EGG, 2)),
            BuiltInLootTables.VILLAGE_TAIGA_HOUSE, List.of(new Extra(FlavoredItems.GARLIC, 7)),
            BuiltInLootTables.VILLAGE_DESERT_HOUSE, List.of(new Extra(FlavoredItems.PEPPER_SEEDS, 7), new Extra(FlavoredItems.DRIED_PEPPER, 5)),
            BuiltInLootTables.VILLAGE_SAVANNA_HOUSE, List.of(new Extra(BFItems.MAIZE_SEEDS, 7), new Extra(BFItems.MAIZE, 5)),
            BuiltInLootTables.VILLAGE_PLAINS_HOUSE, List.of(new Extra(FlavoredItems.TOMATO_SEEDS, 7), new Extra(FlavoredItems.RED_TOMATO, 4)),
            BuiltInLootTables.VILLAGE_SNOWY_HOUSE, List.of(new Extra(FlavoredItems.SPINACH_SEEDS, 7), new Extra(FlavoredItems.SPINACH, 5)));

    public static void init() {
        LootTableEvents.MODIFY.register((key, table, source, registries) -> {
            List<Extra> extras = CHEST_EXTRAS.get(key);
            if (extras == null || !source.isBuiltin()) return;
            List<LootPoolEntryContainer> entries = new ArrayList<>();
            for (Extra extra : extras) {
                entries.add(LootItem.lootTableItem(extra.item().get()).setWeight(10)
                        .apply(SetItemCountFunction.setCount(Holder.direct(new UniformGenerator(
                                Holder.direct(new ConstantValue(1)), Holder.direct(new ConstantValue(extra.max())))))).build());
            }
            boolean[] first = {true};
            table.modifyPools(pool -> {
                if (first[0]) {
                    pool.add(entries);
                    first[0] = false;
                }
            });
        });
    }

    private FlavoredLoot() {
    }
}
