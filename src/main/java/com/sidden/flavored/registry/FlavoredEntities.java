package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.entity.Chocken;
import com.sidden.flavored.entity.ThrownChocolateEgg;
import com.sidden.flavored.entity.ThrownHotSauce;
import com.sidden.flavored.entity.ThrownTomato;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public final class FlavoredEntities {
    public static final Supplier<EntityType<Chocken>> CHOCKEN = BFRegistryHelper.registerEntityType(Flavored.MOD_ID, "chocken",
            () -> EntityType.Builder.of(Chocken::new, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).build(key("chocken")));
    public static final Supplier<EntityType<ThrownTomato>> TOMATO = BFRegistryHelper.registerEntityType(Flavored.MOD_ID, "tomato",
            () -> EntityType.Builder.<ThrownTomato>of(ThrownTomato::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key("tomato")));
    public static final Supplier<EntityType<ThrownChocolateEgg>> CHOCOLATE_EGG = BFRegistryHelper.registerEntityType(Flavored.MOD_ID, "chocolate_egg",
            () -> EntityType.Builder.<ThrownChocolateEgg>of(ThrownChocolateEgg::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key("chocolate_egg")));
    public static final Supplier<EntityType<ThrownHotSauce>> HOT_SAUCE = BFRegistryHelper.registerEntityType(Flavored.MOD_ID, "hot_sauce",
            () -> EntityType.Builder.<ThrownHotSauce>of(ThrownHotSauce::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key("hot_sauce")));

    private static ResourceKey<EntityType<?>> key(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Flavored.MOD_ID, name));
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(CHOCKEN.get(), Chocken.createAttributes());
    }

    private FlavoredEntities() {
    }
}
