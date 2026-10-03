package net.hecco.bountifulfares.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric-only platform-environment checks, ported in-tree from NexusLib's
 * {@code NLServices.PLATFORM}/{@code FabricPlatformHelper} as part of removing the
 * NexusLib dependency during the 26.3 Fabric port. Named "Compat" (rather than colliding
 * with the mod's own pre-existing {@link FabricPlatformHelper}, which backs an unrelated
 * config abstraction) since almost every call site is mod-compat-related
 * (isModLoaded/isDatagen checks feeding {@link net.hecco.bountifulfares.lib.compat.CompatManager}).
 */
public class BFPlatformCompat {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isDatagen() {
        try {
            Class.forName("net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint");
            return System.getProperty("fabric-api.datagen") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
