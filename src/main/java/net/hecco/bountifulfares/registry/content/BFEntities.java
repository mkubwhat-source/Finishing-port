package net.hecco.bountifulfares.registry.content;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.entity.flour.FlourProjectileEntity;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class BFEntities {
    // EntityType.Builder.build(String) no longer accepts a bare String in 26.3 - needs a
    // ResourceKey<EntityType<?>> instead (same fix already applied in BFBoats.java).
    public static final Supplier<EntityType<FlourProjectileEntity>> THROWN_FLOUR_PROJECTILE = register("flour", () ->
        EntityType.Builder.<FlourProjectileEntity>of(FlourProjectileEntity::new, MobCategory.MISC)
                .clientTrackingRange(8).updateInterval(10).sized(0.25F, 0.25F)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("bountifulfares", "flour"))));

    private static <E extends Entity> Supplier<EntityType<E>> register(String id, Supplier<EntityType<E>> registry) {
        return BFRegistryHelper.registerEntityType(BountifulFares.MOD_ID, id, registry);
    }

    public static void registerEntities() {
    }
}
