package net.hecco.bountifulfares.registry;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;

public class BFFabricLootTableModifiers {

    private static final ResourceKey<LootTable> SHORT_GRASS_ID = Blocks.SHORT_GRASS.getLootTable().orElseThrow();
    private static final ResourceKey<LootTable> TALL_GRASS_ID = Blocks.TALL_GRASS.getLootTable().orElseThrow();
    private static final ResourceKey<LootTable> FERN_ID = Blocks.FERN.getLootTable().orElseThrow();
    private static final ResourceKey<LootTable> LARGE_FERN_ID = Blocks.LARGE_FERN.getLootTable().orElseThrow();

    private static final ResourceKey<LootTable> GUARDIAN_ID = ResourceKey.create(
            Registries.LOOT_TABLE, Identifier.withDefaultNamespace("entities/guardian"));

    private static final ResourceKey<LootTable> SNIFFER_DIGGING_ID = BuiltInLootTables.SNIFFER_DIGGING;

    public static void modifyLootTables() { //TODO: create loot table override to just completely remove apples from leaves loot tables
        // Prefetch all config settings for easier read
        boolean do_lapisberries = Services.PLATFORM.get().getBoolConfigValue("enableLapisberrySeeds");
        boolean do_hoaryseeds = Services.PLATFORM.get().getBoolConfigValue("enableHoarySeeds");
        boolean do_spongekinseed_guardian = Services.PLATFORM.get().getBoolConfigValue("enableGuardianSpongekinSeeds");
        boolean do_grass_override = Services.PLATFORM.get().getBoolConfigValue("grassLootTableOverride");

        // Short Grass
        LootTableEvents.REPLACE.register((key, original, source, wrapperLookup) -> {
            if (SHORT_GRASS_ID.equals(key) && do_grass_override) {
                LootTable.Builder builder = newGrassDropsShort(Blocks.SHORT_GRASS, BFItems.GRASS_SEEDS.get(), wrapperLookup);
                if (BFPlatformCompat.isModLoaded(BountifulFares.FARMERS_DELIGHT_MOD_ID))
                {
                    builder = addFDStraw(builder, wrapperLookup);
                }
                return builder.build();
            }
            return null;
        });
        // Tall Grass
        LootTableEvents.REPLACE.register((key, original, source, wrapperLookup) -> {
            if (TALL_GRASS_ID.equals(key) && do_grass_override) {
                LootTable.Builder builder = newGrassDropsTall(Blocks.TALL_GRASS, Blocks.SHORT_GRASS, BFItems.GRASS_SEEDS.get(), wrapperLookup);
                if (BFPlatformCompat.isModLoaded(BountifulFares.FARMERS_DELIGHT_MOD_ID))
                {
                    builder = addFDStraw(builder, wrapperLookup);
                }
                return builder.build();
            }
            return null;
        });
        // Short Fern
        LootTableEvents.REPLACE.register((key, original, source, wrapperLookup) -> {
            if (FERN_ID.equals(key) && do_grass_override) {
                LootTable.Builder builder = newGrassDropsShort(Blocks.FERN, BFItems.GRASS_SEEDS.get(), wrapperLookup);
                if (BFPlatformCompat.isModLoaded(BountifulFares.FARMERS_DELIGHT_MOD_ID))
                {
                    builder = addFDStraw(builder, wrapperLookup);
                }
                return builder.build();
            }
            return null;
        });
        // Large Fern
        LootTableEvents.REPLACE.register((key, original, source, wrapperLookup) -> {
            if (LARGE_FERN_ID.equals(key) && do_grass_override) {
                LootTable.Builder builder = newGrassDropsTall(Blocks.LARGE_FERN, Blocks.FERN, BFItems.GRASS_SEEDS.get(), wrapperLookup);
                if (BFPlatformCompat.isModLoaded(BountifulFares.FARMERS_DELIGHT_MOD_ID))
                {
                    builder = addFDStraw(builder, wrapperLookup);
                }
                return builder.build();
            }
            return null;
        });
        // Sniffer Digging table (registers only what is set in Config)
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (SNIFFER_DIGGING_ID.equals(key)) {

                // Lapisberries
                if (do_lapisberries) tableBuilder.modifyPools(itemEntry -> {
                    itemEntry.add((LootItem.lootTableItem(BFItems.LAPISBERRY_SEEDS.get())).build());
                });

                // Hoary Seeds
                if (do_hoaryseeds) tableBuilder.modifyPools(itemEntry -> {
                    itemEntry.add((LootItem.lootTableItem(BFItems.HOARY_SEEDS.get())).build());
                });
            }
        });
//        // Elder Guardian
//        LootTableEvents.MODIFY.register((key, original, source, wrapperLookup) -> {
//            if (ELDER_GUARDIAN_ID.equals(key) && do_spongekinseed_elderguardian) {
//                original.withPool(LootPool.lootPool()
//                        .setRolls(Holder.direct(new ConstantValue(1)))
//                        .add(LootItem.lootTableItem(BFItems.SPONGEKIN_SEEDS.get()))
//                );
//            }
//        });
        // Guardian
        LootTableEvents.MODIFY.register((key, original, source, wrapperLookup) -> {
            if (GUARDIAN_ID.equals(key) && do_spongekinseed_guardian) {
                original.withPool(LootPool.lootPool()
                        .setRolls(Holder.direct(new ConstantValue(1)))
                        .add(LootItem.lootTableItem(BFItems.SPONGEKIN_SEEDS.get()).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(5))
                );
            }
        });
    }

//    @Deprecated
//    private static LootTable mergePools(LootTable lootTable, LootPool lootPool) {
//        lootPool = LootPool.lootPool().with(lootTable.pools.getFirst().entries).with(lootPool.entries).build();
//        return LootTable.lootTable().pools(List.of(lootPool)).build();
//    }

    /** Returns the Holder for the vanilla "has shears" loot condition, equivalent to the old
     * BlockLootSubProvider.HAS_SHEARS static field (now an instance method backed by a registry lookup). */
    private static Holder<LootItemCondition> hasShears(HolderLookup.Provider wrapper) {
        HolderGetter<LootItemCondition> predicates = wrapper.lookupOrThrow(Registries.PREDICATE);
        return predicates.getOrThrow(LootPredicates.TOOL_CAN_SHEAR);
    }

    /** A hacky, yet functional method of rebuilding short grass drops. Contains an input for the seed to drop as well. */
    public static LootTable.Builder newGrassDropsShort(Block grass, Item seed, HolderLookup.Provider wrapper) {

        HolderLookup.RegistryLookup<Enchantment> impl = wrapper.lookupOrThrow(Registries.ENCHANTMENT);

        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(Holder.direct(new ConstantValue(1)))
                .add(LootItem.lootTableItem(grass)
                                .when(hasShears(wrapper))
                                .otherwise(LootItem.lootTableItem(seed)
                                        .when(LootItemRandomChanceCondition.randomChance(0.125F))
                                        .apply(ApplyExplosionDecay.explosionDecay())
                                        .apply(ApplyBonusCount.addUniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 2)))
                )
        );
    }

    /** A method of rebuilding tall grass drops. Contains an input for the seed to drop as well. */
    public static LootTable.Builder newGrassDropsTall(Block tallPlant, Block shortPlant, Item seed, HolderLookup.Provider wrapper) {
        HolderGetter<Block> blocks = wrapper.lookupOrThrow(Registries.BLOCK);
        LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(shortPlant)
                .apply(SetItemCountFunction.setCount(Holder.direct(new ConstantValue(2))))
                .when(hasShears(wrapper))
                .otherwise(
                        (LootItem.lootTableItem(seed)
                                .when(ExplosionCondition.survivesExplosion())
                                .when(LootItemRandomChanceCondition.randomChance(0.125F)))
                );
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(builder)
                                .when(
                                        MatchBlock.blockMatches(blocks, tallPlant, StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                )
                                .when(
                                        LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location()
                                                        .setBlock(BlockPredicate.Builder.block().of(blocks, tallPlant).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))),
                                                new BlockPos(0, 1, 0)
                                        )
                                )
                )
                .withPool(
                        LootPool.lootPool()
                                .add(builder)
                                .when(
                                        MatchBlock.blockMatches(blocks, tallPlant, StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                                )
                                .when(
                                        LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location()
                                                        .setBlock(BlockPredicate.Builder.block().of(blocks, tallPlant).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))),
                                                new BlockPos(0, -1, 0)
                                        )
                                )
                );
    }

    /** Reimplements straw grabbing in FD. Lazy workaround, but the disabling of the feature entirely still works. */
    public static LootTable.Builder addFDStraw(LootTable.Builder builder, HolderLookup.Provider wrapper)
    {
        HolderGetter<Item> items = wrapper.lookupOrThrow(Registries.ITEM);
        return builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(BountifulFares.FARMERS_DELIGHT_MOD_ID, "straw")))
                        .when(LootItemRandomChanceCondition.randomChance(0.2F))
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(items,
                                TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BountifulFares.FARMERS_DELIGHT_MOD_ID, "straw_harvesters")))
                                )
                        )
                )
        );
    }
}
