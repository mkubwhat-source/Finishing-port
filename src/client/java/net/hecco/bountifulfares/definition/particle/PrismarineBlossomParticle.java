package net.hecco.bountifulfares.definition.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

// NOTE (26.3 particle redesign): TextureSheetParticle is gone entirely - SingleQuadParticle (its
// old superclass) is now the concrete base, and its constructor requires the initial
// TextureAtlasSprite up front (setSpriteFromAge(...) below still exists unchanged and re-picks the
// animated frame immediately afterwards, same as before).
public class PrismarineBlossomParticle extends SingleQuadParticle {
    public PrismarineBlossomParticle(ClientLevel world, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(world, xCoord, yCoord, zCoord, xd, yd, zd, spriteSet.get(world.getRandom()));
        this.friction = 0.95f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize = 0.05f + world.getRandom().nextFloat()/20;
        this.lifetime = 20 + world.getRandom().nextInt(10);
        this.hasPhysics = false;

        this.setSpriteFromAge(spriteSet);
    }

    // NOTE (26.3 particle redesign): ParticleRenderType.PARTICLE_SHEET_LIT is gone (the type no
    // longer has fullbright-vs-lit render buckets at all - just OPAQUE/TRANSLUCENT via Layer, plus
    // whatever getLightCoords(float) reports). Layer.TRANSLUCENT + a fixed fullbright light value
    // approximates the old "always lit" look; this is a visual-parity approximation, not confirmed
    // byte-for-byte against a vanilla always-lit particle, and can be revisited later.
    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return 0xF000F0;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Factory(SpriteSet spriteProvider) {
            this.sprites = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel clientWorld, double x, double y, double z, double xd, double yd, double zd, RandomSource randomSource) {
            return new PrismarineBlossomParticle(clientWorld, x, y, z, this.sprites, xd, yd, zd);
        }
    }
}
