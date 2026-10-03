package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public final class FlavoredStats {
    public static final Identifier INTERACT_WITH_OVEN = register("interact_with_oven");
    public static final Identifier INTERACT_WITH_KEG = register("interact_with_keg");
    public static final Identifier INTERACT_WITH_MIXING_BOWL = register("interact_with_mixing_bowl");
    public static final Identifier MIX_ITEM = register("mix_item");
    public static final Identifier EAT_PUDDING_SLICE = register("eat_pudding_slice");
    public static final Identifier TAKE_PIZZA_SLICE = register("take_pizza_slice");
    public static final Identifier CRAVE_CHOCOLATE = register("crave_chocolate");

    private static Identifier register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, name);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
        return id;
    }

    public static void init() {
    }

    private FlavoredStats() {
    }
}
