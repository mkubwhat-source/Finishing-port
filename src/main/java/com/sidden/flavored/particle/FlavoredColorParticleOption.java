package com.sidden.flavored.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sidden.flavored.registry.FlavoredParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Particle options carrying an RGB color (the keg's fermentation bubbles are tinted per recipe). */
public record FlavoredColorParticleOption(int color) implements ParticleOptions {
    public static final MapCodec<FlavoredColorParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("color").forGetter(FlavoredColorParticleOption::color))
                    .apply(instance, FlavoredColorParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlavoredColorParticleOption> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FlavoredColorParticleOption::color,
            FlavoredColorParticleOption::new);

    @Override
    public ParticleType<FlavoredColorParticleOption> getType() {
        return FlavoredParticles.FERMENTATION_BUBBLES.get();
    }
}
