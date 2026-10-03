package alabaster.hearthandharvest.common.worldgen;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Hearth and Harvest's worldgen additions. 1.21.1 shipped them as NeoForge biome modifiers
 * ({@code data/hearthandharvest/neoforge/biome_modifier}, some of type
 * {@code farmersdelight:add_features_by_filter}); Fabric applies them with BiomeModifications using
 * the same biomes, temperature ranges, features and spawn weights. Farmer's Delight's wild
 * cabbages and onions are generated too, since HH needs those crops and FD is no longer required
 * (FD 1.21.1's rules: beaches; 0.4-0.9 temperature in the onion whitelist).
 */
public final class HHBiomeModifiers {
    private static final List<ResourceKey<Biome>> MUM_BIOMES = List.of(Biomes.PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.MEADOW);
    private static final List<String> MUM_PAIRS = List.of(
            "yellow_orange", "yellow_red", "yellow_blue", "yellow_light_blue", "yellow_purple", "yellow_pink", "yellow_white",
            "orange_red", "orange_blue", "orange_light_blue", "orange_purple", "orange_pink", "orange_white",
            "red_blue", "red_light_blue", "red_purple", "red_pink", "red_white",
            "blue_light_blue", "blue_purple", "blue_pink", "blue_white",
            "light_blue_purple", "light_blue_pink", "light_blue_white",
            "purple_pink", "purple_white", "pink_white");
    private static final List<ResourceKey<Biome>> WILD_CROP_DENIED = List.of(Biomes.LUSH_CAVES, Biomes.MUSHROOM_FIELDS);

    private HHBiomeModifiers() {
    }

    public static void register() {
        // add_mums
        for (String pair : MUM_PAIRS) {
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(MUM_BIOMES), GenerationStep.Decoration.VEGETAL_DECORATION,
                    placed(pair + "_mums_placed"));
        }
        // add_salt_caves
        BiomeModifications.addFeature(BiomeSelectors.tag(biomeTag(HearthAndHarvest.MODID, "has_salt_caves")),
                GenerationStep.Decoration.UNDERGROUND_DECORATION, placed("salt_cave"));
        // nest + crow spawns
        BiomeModifications.addFeature(BiomeSelectors.tag(biomeTag(HearthAndHarvest.MODID, "has_crows")),
                GenerationStep.Decoration.VEGETAL_DECORATION, placed("nest"));
        BiomeModifications.addSpawn(BiomeSelectors.tag(biomeTag(HearthAndHarvest.MODID, "has_crows")),
                MobCategory.CREATURE, HHModEntities.CROW.get(), 60, 3, 5);
        // wild crops (farmersdelight:add_features_by_filter in 1.21.1)
        wildCrop("patch_wild_cotton", biomeTag("c", "is_dry/overworld"), -4f, 4f);
        wildCrop("patch_raspberry_bush", biomeTag("minecraft", "is_overworld"), 0.4f, 0.9f);
        wildCrop("patch_blueberry_bush", biomeTag("minecraft", "is_overworld"), 0.4f, 0.9f);
        wildCrop("patch_wild_green_grapes", biomeTag("c", "is_hot/overworld"), -4f, 4f);
        wildCrop("patch_wild_peanuts", biomeTag("c", "is_temperate/overworld"), -4f, 4f);
        wildCrop("patch_wild_red_grapes", biomeTag("c", "is_cold/overworld"), -4f, 4f);

        // Farmer's Delight wild cabbages / onions (now hearthandharvest:)
        BiomeModifications.addFeature(new FilterSelector(-4f, 4f, biomeTag(HearthAndHarvest.MODID, "has_wild_cabbage"), null, List.of()),
                GenerationStep.Decoration.VEGETAL_DECORATION, placed("patch_wild_cabbages"));
        BiomeModifications.addFeature(new FilterSelector(0.4f, 0.9f, biomeTag(HearthAndHarvest.MODID, "wild_onions_whitelist"),
                        biomeTag(HearthAndHarvest.MODID, "wild_onions_blacklist"), List.of()),
                GenerationStep.Decoration.VEGETAL_DECORATION, placed("patch_wild_onions"));
    }

    private static void wildCrop(String feature, TagKey<Biome> allowed, float minTemperature, float maxTemperature) {
        BiomeModifications.addFeature(new FilterSelector(minTemperature, maxTemperature, allowed, null, WILD_CROP_DENIED),
                GenerationStep.Decoration.VEGETAL_DECORATION, placed(feature));
    }

    /** FD's add_features_by_filter: allowed tag, denied tag/biomes, base temperature range (inclusive). */
    private record FilterSelector(float minTemperature, float maxTemperature, TagKey<Biome> allowed,
                                  @Nullable TagKey<Biome> deniedTag, List<ResourceKey<Biome>> denied) implements Predicate<BiomeSelectionContext> {
        @Override
        public boolean test(BiomeSelectionContext context) {
            Holder<Biome> biome = context.getBiomeHolder();
            if (deniedTag != null && biome.is(deniedTag)) return false;
            if (denied.contains(context.getBiomeKey())) return false;
            float temperature = biome.value().getBaseTemperature();
            return biome.is(allowed) && temperature >= minTemperature && temperature <= maxTemperature;
        }
    }

    private static ResourceKey<PlacedFeature> placed(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, path));
    }

    private static TagKey<Biome> biomeTag(String namespace, String path) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(namespace, path));
    }
}
