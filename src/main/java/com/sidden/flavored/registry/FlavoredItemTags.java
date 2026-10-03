package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class FlavoredItemTags {
    public static final TagKey<Item> CHOCOLATY = create("chocolaty");
    public static final TagKey<Item> TOMATOES = create("tomatoes");
    public static final TagKey<Item> BOWLS = create("bowls");
    public static final TagKey<Item> BOTTLES = create("bottles");
    public static final TagKey<Item> FUNGI = create("fungi");
    public static final TagKey<Item> SALAD_FINISHINGS = create("salad_finishings");
    public static final TagKey<Item> WINES = create("wines");
    public static final TagKey<Item> DRINKS = create("drinks");
    public static final TagKey<Item> BEVERAGES = create("beverages");
    public static final TagKey<Item> NOT_SPICEABLE = create("not_spiceable");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Flavored.MOD_ID, name));
    }

    private FlavoredItemTags() {
    }
}
