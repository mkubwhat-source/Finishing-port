package net.hecco.bountifulfares.data;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.data.trellis.TrellisPlantResourceLoader;
import net.minecraft.resources.Identifier;

public class FabricTrellisPlantResourceLoader extends TrellisPlantResourceLoader implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "bountifulfares/trellis_plant");
    }
}
