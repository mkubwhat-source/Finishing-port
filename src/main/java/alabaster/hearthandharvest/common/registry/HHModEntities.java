package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.entity.cleaver.ThrownCleaver;
import alabaster.hearthandharvest.common.entity.crow.CrowEntity;
import alabaster.hearthandharvest.common.entity.horseshoe.ThrownHorseshoe;
import alabaster.hearthandharvest.common.entity.manure.ManureProjectile;
import alabaster.hearthandharvest.common.entity.pitchfork.ThrownPitchfork;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class HHModEntities {
    // 1.21.1 put crows in their own NeoForge-extended MobCategory ("crow": cap 15, friendly,
    // persistent). Fabric can't extend that enum; crows are CREATURE here and HH's own crop/nest
    // spawning rules (CrowSpawnRules) still decide where they appear.
    public static final Supplier<EntityType<CrowEntity>> CROW = register("crow",
            EntityType.Builder.of(CrowEntity::new, MobCategory.CREATURE).sized(0.4f, 0.5f));
    public static final Supplier<EntityType<ManureProjectile>> MANURE_PROJECTILE = register("manure_projectile",
            EntityType.Builder.<ManureProjectile>of(ManureProjectile::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final Supplier<EntityType<ThrownPitchfork>> THROWN_PITCHFORK = register("thrown_pitchfork",
            EntityType.Builder.<ThrownPitchfork>of(ThrownPitchfork::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(1));
    public static final Supplier<EntityType<ThrownCleaver>> THROWN_CLEAVER = register("thrown_cleaver",
            EntityType.Builder.<ThrownCleaver>of(ThrownCleaver::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(1));
    public static final Supplier<EntityType<ThrownHorseshoe>> THROWN_HORSESHOE = register("thrown_horseshoe",
            EntityType.Builder.<ThrownHorseshoe>of(ThrownHorseshoe::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(1));

    private static <T extends Entity> Supplier<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, name));
        return BFRegistryHelper.registerEntityType(HearthAndHarvest.MODID, name, () -> builder.build(key));
    }

    public static void init() {
    }
}
