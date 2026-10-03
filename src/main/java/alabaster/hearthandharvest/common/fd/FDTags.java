package alabaster.hearthandharvest.common.fd;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * The Farmer's Delight tags used by the ported FD blocks/items (FarmersDelightRefabricated 26.3
 * ModTags / CommonTags / FDRefabricatedTags), now under {@code hearthandharvest:} (or {@code c:}
 * where FD used the common namespace).
 */
public final class FDTags {
    private FDTags() {
    }

    public static final class Blocks {
        public static final TagKey<Block> HEAT_SOURCES = hh("heat_sources");
        public static final TagKey<Block> HEAT_CONDUCTORS = hh("heat_conductors");
        /** Heat sources that also render a tray under a cooking pot. Included in HEAT_SOURCES. */
        public static final TagKey<Block> TRAY_HEAT_SOURCES = hh("tray_heat_sources");
        public static final TagKey<Block> COMPOST_ACTIVATORS = hh("compost_activators");
        public static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = hh("unaffected_by_rich_soil");
        public static final TagKey<Block> PLANTED_FROM_BELOW = hh("planted_from_below");
        public static final TagKey<Block> GROWS_WILD_CROPS = hh("grows_wild_crops");
        public static final TagKey<Block> MINEABLE_WITH_KNIFE = c("mineable/knife");
        public static final TagKey<Block> KNIFE_INSTANTLY_MINES = hh("knife_instantly_mines");
        public static final TagKey<Block> CABINETS = hh("cabinets");
        public static final TagKey<Block> WOODEN_CABINETS = hh("cabinets/wooden");
        public static final TagKey<Block> DROPS_CAKE_SLICE = hh("drops_cake_slice");

        private static TagKey<Block> hh(String path) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, path));
        }

        private static TagKey<Block> c(String path) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", path));
        }
    }

    public static final class Items {
        /** Every knife/cleaver (Flavored's knife and HH's cleavers are in it). Used by the cutting board. */
        public static final TagKey<Item> TOOLS_KNIFE = c("tools/knife");
        public static final TagKey<Item> KNIVES = TOOLS_KNIFE;
        public static final TagKey<Item> STRAW_HARVESTERS = hh("straw_harvesters");
        public static final TagKey<Item> SERVING_CONTAINERS = hh("serving_containers");
        public static final TagKey<Item> FLAT_ON_CUTTING_BOARD = hh("flat_on_cutting_board");
        public static final TagKey<Item> CABINETS = hh("cabinets");
        public static final TagKey<Item> WOODEN_CABINETS = hh("cabinets/wooden");
        public static final TagKey<Item> FLINT_TOOL_MATERIALS = hh("flint_tool_materials");

        private static TagKey<Item> hh(String path) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, path));
        }

        private static TagKey<Item> c(String path) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
        }
    }

    public static final class MobEffects {
        public static final TagKey<MobEffect> MILK_BOTTLE_IGNORED = hh("ignored/milk_bottle");
        public static final TagKey<MobEffect> HOT_COCOA_IGNORED = hh("ignored/hot_cocoa");

        private static TagKey<MobEffect> hh(String path) {
            return TagKey.create(Registries.MOB_EFFECT, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, path));
        }
    }
}
