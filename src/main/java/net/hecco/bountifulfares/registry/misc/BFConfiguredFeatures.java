package net.hecco.bountifulfares.registry.misc;

import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

// `ConfiguredFeature` no longer exists in 26.3 - a Feature carries its own config directly now, so
// what used to be the dynamic "configured feature" registry is just `Registries.FEATURE` (a
// registry of actual Feature instances, keyed the same way ConfiguredFeature entries used to be;
// see the class javadoc on BFFeatures for the corresponding static feature-*type* registry).
public class BFConfiguredFeatures {
    public static final ResourceKey<Feature> APPLE_KEY = registerKey("apple");
    public static final ResourceKey<Feature> ORANGE_KEY = registerKey("orange");
    public static final ResourceKey<Feature> LEMON_KEY = registerKey("lemon");
    public static final ResourceKey<Feature> PLUM_KEY = registerKey("plum");
    public static final ResourceKey<Feature> HOARY_KEY = registerKey("hoary");
    public static final ResourceKey<Feature> WALNUT_KEY = registerKey("walnut");
    public static final ResourceKey<Feature> PALM_KEY = registerKey("palm");
    public static final ResourceKey<Feature> GOLDEN_APPLE_KEY = registerKey("golden_apple");

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, name));
    }
}
