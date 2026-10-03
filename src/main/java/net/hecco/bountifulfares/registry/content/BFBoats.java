package net.hecco.bountifulfares.registry.content;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;

import java.util.function.Supplier;

// `Boat.Type` is gone entirely in 26.3 - boats no longer distinguish their wood variant via an enum
// on a single shared entity type. Instead (matching how vanilla's own oak/birch/etc. boats work),
// each wood variant is its own registered EntityType, and the drop item is baked straight into the
// entity's constructor via a Supplier<Item> - see Boat's/ChestBoat's own constructors. This also
// means the old BoatTypeMixin (enum injection) and BoatMixin/ChestBoatMixin (getDropItem overrides)
// are no longer needed at all: AbstractBoat.getDropItem() is `final` now and just returns whatever
// Supplier<Item> the entity was built with.
public class BFBoats {
    public static final Supplier<EntityType<Boat>> WALNUT_BOAT = register("walnut_boat", () ->
            EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level, () -> BFItems.WALNUT_BOAT.get()), MobCategory.MISC)
                    .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("bountifulfares", "walnut_boat"))));
    public static final Supplier<EntityType<ChestBoat>> WALNUT_CHEST_BOAT = register("walnut_chest_boat", () ->
            EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level, () -> BFItems.WALNUT_CHEST_BOAT.get()), MobCategory.MISC)
                    .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("bountifulfares", "walnut_chest_boat"))));
    public static final Supplier<EntityType<Boat>> HOARY_BOAT = register("hoary_boat", () ->
            EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level, () -> BFItems.HOARY_BOAT.get()), MobCategory.MISC)
                    .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("bountifulfares", "hoary_boat"))));
    public static final Supplier<EntityType<ChestBoat>> HOARY_CHEST_BOAT = register("hoary_chest_boat", () ->
            EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level, () -> BFItems.HOARY_CHEST_BOAT.get()), MobCategory.MISC)
                    .noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("bountifulfares", "hoary_chest_boat"))));

    private static <T extends net.minecraft.world.entity.Entity> Supplier<EntityType<T>> register(String id, Supplier<EntityType<T>> supplier) {
        return BFRegistryHelper.registerEntityType(BountifulFares.MOD_ID, id, supplier);
    }

    public static void register() {
    }
}
