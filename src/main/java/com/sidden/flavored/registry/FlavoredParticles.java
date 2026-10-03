package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.particle.FlavoredColorParticleOption;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public final class FlavoredParticles {
    public static final Supplier<SimpleParticleType> CHEESE_AGING = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "cheese_aging");
    public static final Supplier<SimpleParticleType> DRIPPING_CHOCOLATE = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "dripping_chocolate");
    public static final Supplier<SimpleParticleType> FALLING_CHOCOLATE = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "falling_chocolate");
    public static final Supplier<SimpleParticleType> LANDING_CHOCOLATE = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "landing_chocolate");
    public static final Supplier<SimpleParticleType> POPCORN_POPS = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "popcorn_pops");
    public static final Supplier<SimpleParticleType> FLAME_BUNCH = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "flame_bunch");
    public static final Supplier<ParticleType<FlavoredColorParticleOption>> FERMENTATION_BUBBLES = BFRegistryHelper.registerParticleType(Flavored.MOD_ID, "fermentation_bubbles",
            type -> FlavoredColorParticleOption.CODEC, type -> FlavoredColorParticleOption.STREAM_CODEC);

    public static void init() {
    }

    private FlavoredParticles() {
    }
}
