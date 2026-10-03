package net.hecco.bountifulfares.definition.data.trellis;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.TrellisBlock;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// SimpleJsonResourceReloadListener is now generic over the *decoded* type and takes a
// Codec<T> + FileToIdConverter in its constructor instead of a Gson + bare path prefix
// (confirmed via javap): prepare() already runs the codec over the raw JSON for us, so apply()
// receives Map<Identifier, TrellisPlantDefinition> directly rather than Map<Identifier,
// JsonElement> - the per-entry try/catch JsonOps.parse() this used to do by hand is gone, since
// the base class now does that decoding (and logs failures) itself.
public class TrellisPlantResourceLoader extends SimpleJsonResourceReloadListener<TrellisPlantDefinition> {

    private final Map<Identifier, TrellisPlantDefinition> registeredPlants = new HashMap<>();
    public TrellisPlantResourceLoader() {
        super(TrellisPlantDefinition.CODEC, FileToIdConverter.json("bountifulfares/trellis_plant"));
    }

    @Override
    protected void apply(Map<Identifier, TrellisPlantDefinition> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        registeredPlants.clear();
        for (var entry : map.entrySet()) {
            Identifier id = entry.getKey();
            TrellisPlantDefinition def = entry.getValue();
            if (def.plant() == Items.AIR) {
                BountifulFares.LOGGER.error("Failed to load trellis plant '{}': Plant item was not found", id);
            } else {
                registeredPlants.put(id, def);
            }
        }
        for (TrellisPlantDefinition plantDefinition : registeredPlants.values().stream().toList()) {
            TrellisBlock.PLANTS.put(plantDefinition.plant(), plantDefinition);
        }
    }

    public Collection<TrellisPlantDefinition> getAllTrellisPlants() {
        return this.registeredPlants.values();
    }
}
