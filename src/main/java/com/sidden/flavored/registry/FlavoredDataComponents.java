package com.sidden.flavored.registry;

import com.mojang.serialization.Codec;
import com.sidden.flavored.Flavored;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public final class FlavoredDataComponents {
    /** Spiciness level (1-3) added to a food by crafting it with dried peppers. */
    public static final Supplier<DataComponentType<Integer>> SPICINESS = BFRegistryHelper.registerComponentType(Flavored.MOD_ID, "spiciness",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static void init() {
    }

    private FlavoredDataComponents() {
    }
}
