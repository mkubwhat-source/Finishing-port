package net.hecco.bountifulfares.definition.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

// NOTE (26.3 particle redesign): TextureSheetParticle is gone entirely - SingleQuadParticle (its
// old superclass) is now the concrete base. BFParticleRenderTypes.PARTICLE_SHEET_CLOUD (a custom
// anonymous ParticleRenderType subclass with its own begin()/blend setup) is no longer possible at
// all: ParticleRenderType is now a final record with just 4 fixed static instances and no
// overridable rendering hooks - see BFParticleRenderTypes.java, which is kept only as a historical
// note now that nothing references it. Layer.TRANSLUCENT is the direct functional replacement
// (same default alpha blend / no depth-mask-write behavior the old cloud type set up by hand).
public class FlourCloudParticle extends SingleQuadParticle {
    public FlourCloudParticle(ClientLevel world, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd, int angle) {
        // pickSprite(SpriteSet) is gone along with TextureSheetParticle - SpriteSet.get(RandomSource)
        // reproduces the same "pick one random static frame" behavior, done up front since the
        // sprite must now be passed into super(...) itself.
        super(world, xCoord, yCoord, zCoord, xd, yd, zd, spriteSet.get(world.getRandom()));
        this.friction = 0.95f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 0.5f + world.getRandom().nextFloat();
        this.lifetime = 200 + world.getRandom().nextIntBetweenInclusive(0, 10);
        this.hasPhysics = true;
        this.oRoll = angle;
    }

    @Override
    public void tick() {
        this.roll = oRoll;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.age >= lifetime) {
            this.remove();
            return;
        }
        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
        if (this.age != this.lifetime) {
            this.quadSize *= 1.001f;
        }
        if (this.age >= this.lifetime - 100 && this.alpha > 0f) {
            this.alpha -= 0.01f;
        }
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Factory(SpriteSet spriteProvider) {
            this.sprites = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel clientWorld, double x, double y, double z, double xd, double yd, double zd, RandomSource randomSource) {
            return new FlourCloudParticle(clientWorld, x, y, z, this.sprites, xd, yd, zd, randomSource.nextIntBetweenInclusive(0, 180));
        }
    }
}
