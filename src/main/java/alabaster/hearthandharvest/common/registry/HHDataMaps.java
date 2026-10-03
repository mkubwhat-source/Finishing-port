package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.data.VintageStyle;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hearth and Harvest's two NeoForge data maps, re-implemented for Fabric with the same files and
 * format: {@code data/<namespace>/data_maps/fluid/fluid_bottle.json} (fluid -> its bottled item) and
 * {@code data/<namespace>/data_maps/item/vintage_style.json} (how an ageable item shows its vintage).
 * Like NeoForge, every pack's file is merged in pack order, a {@code "replace": true} file drops what
 * came before, and {@code "remove": [...]} drops entries. Entries naming unknown ids are skipped (the
 * NeoForge loader skipped unloaded optional entries the same way).
 * <p>
 * Both maps are needed on the client too (keg/cask screens, recipe viewers, vintage tooltips), so
 * they are sent to players on join and after /reload ({@link SyncPayload}).
 */
public final class HHDataMaps {
    public static final int BOTTLE_VOLUME = 250;
    private static final Identifier FLUID_BOTTLE_ID = Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "fluid_bottle");
    private static final Identifier VINTAGE_STYLE_ID = Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "vintage_style");

    private static volatile Map<Fluid, Item> FLUID_TO_BOTTLE = Map.of();
    private static volatile Map<Item, Fluid> BOTTLE_TO_FLUID = Map.of();
    private static volatile Map<Item, VintageStyle> VINTAGE_STYLES = Map.of();

    private HHDataMaps() {
    }

    @Nullable
    public static VintageStyle getVintageStyle(Item item) {
        if (item == null || item == Items.AIR) return null;
        return VINTAGE_STYLES.get(item);
    }

    @Nullable
    public static Item getBottleForFluid(Fluid fluid) {
        if (fluid == null) return null;
        Fluid source = fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
        Item bottle = FLUID_TO_BOTTLE.get(source);
        return bottle == Items.AIR ? null : bottle;
    }

    @Nullable
    public static Fluid getFluidForBottle(Item item) {
        if (item == null || item == Items.AIR) return null;
        return BOTTLE_TO_FLUID.get(item);
    }

    public static Map<Fluid, Item> fluidBottles() {
        return FLUID_TO_BOTTLE;
    }

    private static void set(Map<Fluid, Item> bottles, Map<Item, VintageStyle> styles) {
        Map<Fluid, Item> forward = new IdentityHashMap<>(bottles);
        Map<Item, Fluid> reverse = new IdentityHashMap<>();
        // 1.21.1 iterated the fluid registry in order and kept the first fluid per bottle.
        for (Fluid fluid : BuiltInRegistries.FLUID) {
            Item bottle = forward.get(fluid);
            if (bottle != null && bottle != Items.AIR) reverse.putIfAbsent(bottle, fluid);
        }
        FLUID_TO_BOTTLE = forward;
        BOTTLE_TO_FLUID = reverse;
        VINTAGE_STYLES = new IdentityHashMap<>(styles);
    }

    public static SyncPayload syncPayload() {
        return new SyncPayload(new LinkedHashMap<>(FLUID_TO_BOTTLE), new LinkedHashMap<>(VINTAGE_STYLES));
    }

    /** Client side: replaces the maps with the server's. */
    public static void applySync(SyncPayload payload) {
        set(payload.bottles(), payload.styles());
    }

    // ------------------------------------------------------------------ loading

    private record Loaded(Map<Fluid, Item> bottles, Map<Item, VintageStyle> styles) {
    }

    public static final class Loader extends SimplePreparableReloadListener<Loaded> implements IdentifiableResourceReloadListener {
        @Override
        public Identifier getFabricId() {
            return Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "data_maps");
        }

        @Override
        protected Loaded prepare(ResourceManager manager, ProfilerFiller profiler) {
            Map<Identifier, JsonElement> bottlesRaw = merge(manager, "data_maps/fluid", FLUID_BOTTLE_ID);
            Map<Identifier, JsonElement> stylesRaw = merge(manager, "data_maps/item", VINTAGE_STYLE_ID);
            Map<Fluid, Item> bottles = new HashMap<>();
            bottlesRaw.forEach((fluidId, value) -> {
                var fluid = BuiltInRegistries.FLUID.getOptional(fluidId);
                Identifier itemId = Identifier.tryParse(value.getAsString());
                var item = itemId == null ? java.util.Optional.<Item>empty() : BuiltInRegistries.ITEM.getOptional(itemId);
                if (fluid.isPresent() && item.isPresent()) {
                    bottles.put(fluid.get(), item.get());
                } else {
                    HearthAndHarvest.LOGGER.debug("Skipping fluid_bottle entry {} -> {} (not registered)", fluidId, value);
                }
            });
            Map<Item, VintageStyle> styles = new HashMap<>();
            stylesRaw.forEach((itemId, value) -> {
                var item = BuiltInRegistries.ITEM.getOptional(itemId);
                if (item.isEmpty()) return;
                VintageStyle.CODEC.parse(JsonOps.INSTANCE, value)
                        .resultOrPartial(error -> HearthAndHarvest.LOGGER.error("Bad vintage_style entry {}: {}", itemId, error))
                        .ifPresent(style -> styles.put(item.get(), style));
            });
            return new Loaded(bottles, styles);
        }

        @Override
        protected void apply(Loaded loaded, ResourceManager manager, ProfilerFiller profiler) {
            set(loaded.bottles(), loaded.styles());
        }

        /** NeoForge data-map merge: all namespaces' files with this name, in pack order. */
        private static Map<Identifier, JsonElement> merge(ResourceManager manager, String directory, Identifier mapId) {
            Map<Identifier, JsonElement> values = new LinkedHashMap<>();
            String path = directory + "/" + mapId.getPath() + ".json";
            for (Map.Entry<Identifier, List<Resource>> entry : manager.listResourceStacks(directory,
                    id -> id.getNamespace().equals(mapId.getNamespace()) && id.getPath().equals(path)).entrySet()) {
                for (Resource resource : entry.getValue()) {
                    try (Reader reader = resource.openAsReader()) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        if (json.has("replace") && json.get("replace").getAsBoolean()) values.clear();
                        if (json.has("values")) {
                            for (Map.Entry<String, JsonElement> value : json.getAsJsonObject("values").entrySet()) {
                                String key = value.getKey();
                                if (key.startsWith("#")) continue; // tag keys: HH ships none
                                JsonElement element = value.getValue();
                                // NeoForge's object form {"value": ..., "replace": ...}
                                if (element.isJsonObject() && element.getAsJsonObject().has("value") && mapId.equals(FLUID_BOTTLE_ID)) {
                                    element = element.getAsJsonObject().get("value");
                                }
                                Identifier id = Identifier.tryParse(key);
                                if (id != null) values.put(id, element);
                            }
                        }
                        if (json.has("remove")) {
                            json.getAsJsonArray("remove").forEach(e -> {
                                Identifier id = Identifier.tryParse(e.getAsString());
                                if (id != null) values.remove(id);
                            });
                        }
                    } catch (Exception e) {
                        HearthAndHarvest.LOGGER.error("Couldn't read data map {} from {}", entry.getKey(), resource.sourcePackId(), e);
                    }
                }
            }
            return values;
        }
    }

    // ------------------------------------------------------------------ sync

    public record SyncPayload(Map<Fluid, Item> bottles, Map<Item, VintageStyle> styles) implements CustomPacketPayload {
        public static final Type<SyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "data_maps"));
        private static final Codec<VintageStyle> STYLE = VintageStyle.CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(net.minecraft.core.registries.Registries.FLUID), ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM)), SyncPayload::bottles,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM), ByteBufCodecs.fromCodecWithRegistries(STYLE)), SyncPayload::styles,
                SyncPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
