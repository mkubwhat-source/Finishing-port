package net.hecco.bountifulfares.definition.data.grass_seeds;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.item.custom.GrassSeedsItem;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Same SimpleJsonResourceReloadListener<T> Codec-based redesign as TrellisPlantResourceLoader.
public class GrassSeedsInteractionResourceLoader extends SimpleJsonResourceReloadListener<GrassSeedsInteractionDefinition> {

    private final Map<Identifier, GrassSeedsInteractionDefinition> definitions = new HashMap<>();
    public GrassSeedsInteractionResourceLoader() {
        super(GrassSeedsInteractionDefinition.CODEC, FileToIdConverter.json("bountifulfares/grass_seeds_interaction"));
    }

    @Override
    protected void apply(Map<Identifier, GrassSeedsInteractionDefinition> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        definitions.clear();
        for (var entry : map.entrySet()) {
            Identifier id = entry.getKey();
            GrassSeedsInteractionDefinition def = entry.getValue();
            if (def.soil() == null) {
                BountifulFares.LOGGER.error("Failed to load grass seeds interaction '{}': Soil was not found", id);
            } else if (def.result() == null) {
                BountifulFares.LOGGER.error("Failed to load grass seeds interaction '{}': Result was not found", id);
            } else {
                definitions.put(id, def);
            }
        }
        for (GrassSeedsInteractionDefinition definition : definitions.values().stream().toList()) {
            GrassSeedsItem.INTERACTIONS.add(Triple.of(definition.soil(), definition.result(), definition.onTop()));
        }
    }

    public Collection<GrassSeedsInteractionDefinition> getAllDefinitions() {
        return this.definitions.values();
    }
}
