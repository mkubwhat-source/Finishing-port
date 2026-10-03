package net.hecco.bountifulfares.lib.compat;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.platform.BFPlatformCompat;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Ported in-tree from NexusLib's {@code CompatManager} as part of removing the NexusLib
 * dependency during the 26.3 Fabric port. Fabric-only, so unlike the original there's no
 * loader-abstraction split - this talks straight to {@link BFRegistryHelper}/
 * {@link BFPlatformCompat}.
 */
public class CompatManager {

    public final String modId;
    protected final List<ModIntegration> INTEGRATIONS = new ArrayList<>();
    public final Map<Identifier, ModIntegration> CONTENT_ID_TO_INTEGRATION = new HashMap<>();
    public final Map<Supplier<?>, ModIntegration> CONTENT_TO_INTEGRATION = new HashMap<>();

    public CompatManager(String modId) {
        this.modId = modId;
    }

    public void addIntegration(ModIntegration module) {
        INTEGRATIONS.add(module);
    }

    public List<ModIntegration> getIntegrations() {
        return this.INTEGRATIONS;
    }

    public void registerCompatContent() {
        for (ModIntegration integration : INTEGRATIONS) {
            integration.registerContent();
            if (integration.shouldCreateDatapack() && integration.modIds().stream().allMatch(BFPlatformCompat::isModLoaded)) {
                if (integration.modIds().isEmpty()) {
                    BountifulFares.LOGGER.error("Cannot create datapack with no mod ids for integration " + integration);
                    continue;
                }
                StringBuilder id = new StringBuilder();
                for (String modId : integration.modIds()) {
                    id.append(modId).append("_");
                }
                id.append("dat");
                BFRegistryHelper.registerBuiltInDatapack(modId, id.toString(), integration.getDatapackName() != null ? integration.getDatapackName() : id.toString(), true, true);
            }
        }
        for (Map.Entry<Identifier, ModIntegration> entries : CONTENT_ID_TO_INTEGRATION.entrySet()) {
            if (!(entries.getValue().modIds().stream().allMatch(BFPlatformCompat::isModLoaded) || BFPlatformCompat.isDatagen())) {
                CompatAPI.DISABLED_CACHE.add(entries.getKey());
            }
        }
    }
}
