package com.sidden.flavored.client.particle;

import com.sidden.flavored.registry.FlavoredParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Chocolate drips (hang -> fall -> land) under sunlit chocolate blocks, tinted brown.
 * <p>
 * 26.3: TextureSheetParticle is gone; these are SingleQuadParticles.
 */
public class ChocolateDripParticle extends SingleQuadParticle {
    private static final float R = 0.416f, G = 0.239f, B = 0.169f;

    protected ChocolateDripParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.setColor(R, G, B);
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    /**
     * 1.21.1's drip classes defined vanilla-drip-style pre/post move hooks but never called them
     * (they extended TextureSheetParticle, whose tick does not), so the particles moved with the
     * plain particle physics (0.04 x gravity per tick, 0.98 friction). That motion is kept; the
     * hooks now run after it, so a falling drop splashes when it lands and a hanging drop falls.
     */
    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.postMoveUpdate();
        }
    }

    protected void postMoveUpdate() {
    }

    static class Hang extends ChocolateDripParticle {
        private final ParticleOptions fallingParticle;

        Hang(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, ParticleOptions fallingParticle) {
            super(level, x, y, z, sprite);
            this.fallingParticle = fallingParticle;
            this.gravity *= 0.02F;
            this.lifetime = 40;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.removed) {
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }
    }

    static class FallAndLand extends ChocolateDripParticle {
        private final ParticleOptions landParticle;

        FallAndLand(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, ParticleOptions landParticle) {
            super(level, x, y, z, sprite);
            this.landParticle = landParticle;
            this.lifetime = (int) (64.0 / (Math.random() * 0.8 + 0.2));
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    public static ParticleProvider<SimpleParticleType> hang(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) -> {
            Hang particle = new Hang(level, x, y, z, sprites.get(random), FlavoredParticles.FALLING_CHOCOLATE.get());
            particle.lifetime = 100;
            return particle;
        };
    }

    /** 1.21.1 used vanilla's honey landing particle for the landing splash. */
    public static ParticleProvider<SimpleParticleType> fall(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) -> {
            FallAndLand particle = new FallAndLand(level, x, y, z, sprites.get(random), ParticleTypes.LANDING_HONEY);
            particle.gravity = 1F;
            return particle;
        };
    }

    public static ParticleProvider<SimpleParticleType> land(SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz, random) -> {
            ChocolateDripParticle particle = new ChocolateDripParticle(level, x, y, z, sprites.get(random));
            particle.lifetime = (int) (128.0 / (Math.random() * 0.8 + 0.2));
            return particle;
        };
    }
}
