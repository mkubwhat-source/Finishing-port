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

// Same SimpleJsonResourceReloadListener<T> Codec-based redesign as TrellisPlantResourceLoader.
public class TrellisCropResourceLoader extends SimpleJsonResourceReloadListener<TrellisCropDefinition> {

    private final Map<Identifier, TrellisCropDefinition> registeredPlants = new HashMap<>();
    public TrellisCropResourceLoader() {
        super(TrellisCropDefinition.CODEC, FileToIdConverter.json("bountifulfares/trellis_crop"));
    }

    @Override
    protected void apply(Map<Identifier, TrellisCropDefinition> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        registeredPlants.clear();
        for (var entry : map.entrySet()) {
            Identifier id = entry.getKey();
            TrellisCropDefinition def = entry.getValue();
            if (def.seeds() == Items.AIR) {
                BountifulFares.LOGGER.error("Failed to load trellis crop '{}': Seeds item was not found", id);
            } else if (def.produce() == Items.AIR) {
                BountifulFares.LOGGER.error("Failed to load trellis crop '{}': Produce item was not found", id);
            } else {
                registeredPlants.put(id, def);
            }
        }
        for (TrellisCropDefinition cropDefinition : registeredPlants.values().stream().toList()) {
            TrellisBlock.CROPS.put(cropDefinition.seeds(), cropDefinition);
        }
    }

    public Collection<TrellisCropDefinition> getAllTrellisCrops() {
        return this.registeredPlants.values();
    }
}
