package net.hecco.bountifulfares.registry.misc;

import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.grower.TreeGrower;

// TreeGrower's constructor changed shape entirely in 26.3, following the ConfiguredFeature merge:
// it used to take a chance float plus a handful of Optional<ResourceKey<ConfiguredFeature<?,?>>>
// slots (mangrove/mega/normal/etc). Now that ConfiguredFeature is gone, it just takes weighted
// lists of ResourceKey<Feature> for the normal/mega/flowering variants plus a "shortest tree"
// fallback key (see TreeGrower's real ctor, confirmed via javap against the 26.3 jar).
public class BFSaplingGenerators {
    public static final TreeGrower APPLE_SAPLING_GENERATOR = new TreeGrower("apple",
            WeightedList.of(BFConfiguredFeatures.APPLE_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.APPLE_KEY);
    public static final TreeGrower ORANGE_SAPLING_GENERATOR = new TreeGrower("orange",
            WeightedList.of(BFConfiguredFeatures.ORANGE_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.ORANGE_KEY);
    public static final TreeGrower LEMON_SAPLING_GENERATOR = new TreeGrower("lemon",
            WeightedList.of(BFConfiguredFeatures.LEMON_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.LEMON_KEY);
    public static final TreeGrower PLUM_SAPLING_GENERATOR = new TreeGrower("plum",
            WeightedList.of(BFConfiguredFeatures.PLUM_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.PLUM_KEY);
    public static final TreeGrower PALM_SAPLING_GENERATOR = new TreeGrower("palm",
            WeightedList.of(BFConfiguredFeatures.PALM_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.PALM_KEY);
    public static final TreeGrower GOLDEN_APPLE_SAPLING_GENERATOR = new TreeGrower("golden_apple",
            WeightedList.of(BFConfiguredFeatures.GOLDEN_APPLE_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.GOLDEN_APPLE_KEY);
    public static final TreeGrower HOARY_SAPLING_GENERATOR = new TreeGrower("hoary",
            WeightedList.of(BFConfiguredFeatures.HOARY_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.HOARY_KEY);
    public static final TreeGrower WALNUT_SAPLING_GENERATOR = new TreeGrower("walnut",
            WeightedList.of(BFConfiguredFeatures.WALNUT_KEY),
            WeightedList.of(),
            WeightedList.of(),
            BFConfiguredFeatures.WALNUT_KEY);

}
