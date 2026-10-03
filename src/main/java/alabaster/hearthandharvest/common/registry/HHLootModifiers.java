package alabaster.hearthandharvest.common.registry;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

/**
 * Hearth and Harvest's global loot modifier {@code cherries_from_cherry_leaves} (1.21.1: a Farmer's
 * Delight {@code add_item} modifier): cherry leaves broken without shears drop a cherry 2% of the time.
 * Fabric has no global loot modifiers, so the cherry is an extra pool of the cherry leaves table.
 */
public final class HHLootModifiers {
    private static final TagKey<Item> SHEARS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/shear"));

    private HHLootModifiers() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, table, source, registries) -> {
            if (!source.isBuiltin() || !Blocks.CHERRY_LEAVES.getLootTable().map(key::equals).orElse(false)) return;
            table.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(HHModItems.CHERRY.get()))
                    .when(InvertedLootItemCondition.invert(MatchTool.toolMatches(
                            ItemPredicate.Builder.item().of(registries.lookupOrThrow(Registries.ITEM), SHEARS))))
                    .when(LootItemRandomChanceCondition.randomChance(0.02F)));
        });
    }
}
