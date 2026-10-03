package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

public class HHModParticleTypes {
    // 1.21.1: sap and flies were "always show" (overrideLimiter=true), feather was not.
    public static final Supplier<SimpleParticleType> DRIPPING_SAP = register("dripping_sap", true);
    public static final Supplier<SimpleParticleType> FLIES = register("flies", true);
    public static final Supplier<SimpleParticleType> FEATHER = register("feather", false);
    // Farmer's Delight (cooking pot steam, rich soil star, ...)
    public static final Supplier<SimpleParticleType> STAR = register("star", true);
    public static final Supplier<SimpleParticleType> STEAM = register("steam", true);
    public static final Supplier<SimpleParticleType> SPARKLE = register("sparkle", true);

    private static Supplier<SimpleParticleType> register(String name, boolean alwaysShow) {
        return BFRegistryHelper.registerTyped(HearthAndHarvest.MODID, name, BuiltInRegistries.PARTICLE_TYPE, () -> FabricParticleTypes.simple(alwaysShow));
    }

    public static void init() {
    }
}
