package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.Map;

/**
 * Registry aliases, so ids from older Hearth and Harvest worlds keep loading.
 * <ul>
 *   <li>HH's own 1.21.1 renames (elote, cheddar cheese, wine rack).</li>
 *   <li>The bundle merges (user decisions): HH items that now are another bundled mod's item.</li>
 * </ul>
 * The rotten tomato crate was dropped (its Farmer's Delight rotten tomatoes aren't in the bundle)
 * and has no alias.
 */
public final class HHRegistryAliases {
    private static final Map<String, String> RENAMES = Map.of(
            "elote", "street_corn",
            "cheddar_cheese_wheel", "cheese_wheel",
            "unripe_cheddar_cheese_wheel", "unripe_cheese_wheel",
            "cheddar_cheese_slice", "cheese_slice"
    );

    /** HH item id -> the bundled item that replaced it. */
    private static final Map<String, String> MERGED_ITEMS = Map.ofEntries(
            Map.entry("mead", "bountifulfares:mead_bottle"),
            Map.entry("sweet_berry_wine", "flavored:sweet_berry_wine"),
            Map.entry("glow_berry_wine", "flavored:glow_berry_wine"),
            Map.entry("sweet_berry_juice", "flavored:sweet_berry_juice"),
            Map.entry("glow_berry_juice", "flavored:glow_berry_juice"),
            Map.entry("pizza", "flavored:pizza"),
            Map.entry("pizza_slice", "flavored:pizza_slice"),
            Map.entry("pickled_beetroots", "bountifulfares:pickled_beetroot"),
            Map.entry("flour", "bountifulfares:flour"),
            Map.entry("butter", "flavored:butter"),
            Map.entry("batter", "flavored:batter"),
            Map.entry("chocolate_bar", "flavored:chocolate"),
            Map.entry("flour_bag", "bountifulfares:flour_block"),
            Map.entry("trellis", "bountifulfares:trellis"),
            Map.entry("bamboo_trellis", "bountifulfares:bamboo_trellis"),
            Map.entry("stripped_bamboo_trellis", "bountifulfares:bamboo_trellis")
    );

    /** HH block id -> the bundled block that replaced it (placed blocks in old worlds). */
    private static final Map<String, String> MERGED_BLOCKS = Map.of(
            "pizza", "flavored:pizza",
            "flour_bag", "bountifulfares:flour_block",
            "trellis", "bountifulfares:trellis",
            "grape_trellis", "bountifulfares:trellis"
    );

    private HHRegistryAliases() {
    }

    public static void init() {
        FabricRegistry items = (FabricRegistry) BuiltInRegistries.ITEM;
        FabricRegistry blocks = (FabricRegistry) BuiltInRegistries.BLOCK;
        RENAMES.forEach((from, to) -> {
            items.addAlias(hh(from), hh(to));
            blocks.addAlias(hh(from), hh(to));
        });
        MERGED_ITEMS.forEach((from, to) -> items.addAlias(hh(from), Identifier.parse(to)));
        MERGED_BLOCKS.forEach((from, to) -> blocks.addAlias(hh(from), Identifier.parse(to)));
        ((FabricRegistry) BuiltInRegistries.BLOCK_ENTITY_TYPE).addAlias(hh("wine_rack"), hh("bottle_rack"));
    }

    private static Identifier hh(String path) {
        return Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, path);
    }
}
