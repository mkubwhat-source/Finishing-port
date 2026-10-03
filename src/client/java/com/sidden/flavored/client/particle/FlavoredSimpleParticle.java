package com.sidden.flavored.client.particle;

import com.sidden.flavored.particle.FlavoredColorParticleOption;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Flavored's cheese aging, popcorn pops, flame bunch and fermentation bubble particles.
 * <p>
 * 26.3: {@code TextureSheetParticle} is gone; {@code SingleQuadParticle} takes the initial sprite in
 * its constructor and render types are layers (opaque/translucent). Four 1.21.1 classes that
 * differed only in lifetime, layer, color and whether the sprite animates with age are one class.
 */
public class FlavoredSimpleParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final boolean animated;
    private final Layer layer;

    protected FlavoredSimpleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
                                     SpriteSet sprites, int minLife, int maxLife, boolean animated, Layer layer) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites.first());
        this.sprites = sprites;
        this.animated = animated;
        this.layer = layer;
        this.friction = 0.8f;
        this.lifetime = level.getRandom().nextInt(minLife, maxLife);
        this.setSpriteFromAge(sprites);
        this.scale(1.5f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.animated) {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    protected Layer getLayer() {
        return this.layer;
    }

    public static ParticleProvider<SimpleParticleType> cheeseAging(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) -> new FlavoredSimpleParticle(level, x, y, z, dx, dy, dz, sprites, 25, 35, false, Layer.TRANSLUCENT);
    }

    public static ParticleProvider<SimpleParticleType> popcornPops(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) -> new FlavoredSimpleParticle(level, x, y, z, dx, dy, dz, sprites, 25, 35, false, Layer.TRANSLUCENT);
    }

    public static ParticleProvider<SimpleParticleType> flameBunch(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) -> new FlavoredSimpleParticle(level, x, y, z, dx, dy, dz, sprites, 15, 25, true, Layer.TRANSLUCENT);
    }

    /** Keg bubbles, tinted with the fermenting recipe's color. */
    public static ParticleProvider<FlavoredColorParticleOption> fermentationBubbles(SpriteSet sprites) {
        return (options, level, x, y, z, dx, dy, dz, random) -> {
            FlavoredSimpleParticle particle = new FlavoredSimpleParticle(level, x, y, z, dx, dy, dz, sprites, 25, 35, false, Layer.OPAQUE);
            int color = options.color();
            particle.setColor(((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f);
            particle.setSprite(sprites.get(random));
            return particle;
        };
    }
}
