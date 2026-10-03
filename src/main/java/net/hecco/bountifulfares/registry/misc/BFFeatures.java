package net.hecco.bountifulfares.registry.misc;

import com.mojang.serialization.MapCodec;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.world.wild_vine_feature.WildVineFeature;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.function.Supplier;

// In 26.3 `BuiltInRegistries.FEATURE` (a registry of Feature "type" singletons, generic over their
// config) is gone. Feature no longer has a generic config type - it's a self-contained record - so
// the static registry of feature *types* is now `Registries.FEATURE_TYPE`, a registry of the
// feature's MapCodec itself (mirroring how e.g. BlockEntityType's own codec-registry works
// elsewhere in 26.3). The dynamic per-instance registry (what used to be Registries.CONFIGURED_FEATURE)
// is the separate `Registries.FEATURE` registry, populated via datapack/datagen, not here.
public class BFFeatures {
    public static final Supplier<MapCodec<WildVineFeature>> WILD_VINE_FEATURE = register("wild_vine", () -> WildVineFeature.CODEC);

    private static <T extends MapCodec<? extends Feature>> Supplier<T> register(String name, Supplier<T> codec) {
        return BFRegistryHelper.register(BountifulFares.MOD_ID, name,
                                            (Registry<T>) BuiltInRegistries.FEATURE_TYPE, codec);
    }

    public static void register() {
    }
}
