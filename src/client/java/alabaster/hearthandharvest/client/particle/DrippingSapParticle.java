package alabaster.hearthandharvest.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class DrippingSapParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    public DrippingSapParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites.first());
        this.sprites = sprites;
        this.setSize(0.1F, 0.1F);
        this.gravity = 0.008F;
        this.lifetime = 16 + world.getRandom().nextInt(5);
        this.xd = 0.0;
        this.yd = -0.02D;
        this.zd = 0.0;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();

        // Static delay before falling
        if (this.age < 5) {
            this.yd = 0.0D;
        } else {
            this.yd -= 0.005D;
            if (this.yd < -0.08D) this.yd = -0.08D;
        }

        // Early fade-out before death
        if (this.age > this.lifetime - 6) {
            this.alpha = (this.lifetime - this.age) / 6.0F;
        }

        if (this.onGround || this.age >= this.lifetime) {
            this.remove();
        }

        this.setSpriteFromAge(sprites);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
            return new DrippingSapParticle(world, x, y, z, sprites);
        }
    }
}