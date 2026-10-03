package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.worldgen.feature.TippedPillarFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class FlavoredFeatures {
    public static final ResourceKey<PlacedFeature> CINNAMON_PILLAR = ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "cinnamon_pillar"));

    public static void init() {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "tipped_pillar"), TippedPillarFeature.CODEC);
        // 1.21.1: NeoForge biome modifier data/flavored/neoforge/biome_modifier/add_cinamon_pillar.json
        BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, CINNAMON_PILLAR);
    }

    private FlavoredFeatures() {
    }
}
