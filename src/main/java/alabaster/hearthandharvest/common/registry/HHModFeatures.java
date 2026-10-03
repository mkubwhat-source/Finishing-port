package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.world.WildCropFeature;
import alabaster.hearthandharvest.common.worldgen.NestFeature;
import alabaster.hearthandharvest.common.worldgen.SaltCaveFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * 26.3: {@code Feature} is an interface implemented by codec-registered records (configuration and
 * feature are one object), and the static registry holds their codecs ({@code FEATURE_TYPE}).
 */
public class HHModFeatures {
    public static void init() {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "salt_cave"), SaltCaveFeature.CODEC);
        Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "nest"), NestFeature.CODEC);
        // Farmer's Delight's 1.21.1 wild crop patch feature (HH's wild grapes/peanuts/cotton and
        // berry bush patches used it).
        Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "wild_crop"), WildCropFeature.CODEC);
    }
}
