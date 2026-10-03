package alabaster.hearthandharvest.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.animal.chicken.Chicken;

public class FeatherParticles {
    private static final int HURT_COUNT = 10;

    public static void register() {
        // NeoForge LivingDamageEvent.Post fired for damage that was actually applied.
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (!blocked && entity instanceof Chicken chicken) {
                burst(chicken, HURT_COUNT);
            }
        });
    }

    public static void burst(Chicken chicken, int count) {
        burst(chicken.level(), chicken.getX(), chicken.getY() + chicken.getBbHeight() * 0.6D, chicken.getZ(), count, 0.2D);
    }

    public static void burst(Level level, double x, double y, double z, int count, double spread) {
        if (!(level instanceof ServerLevel server)) return;
        server.sendParticles(HHModParticleTypes.FEATHER.get(), x, y, z, count, spread, spread * 0.6D, spread, 0.1D);
    }

    public static void trail(Chicken chicken, int count) {
        if (!(chicken.level() instanceof ServerLevel server)) return;
        server.sendParticles(HHModParticleTypes.FEATHER.get(),
                chicken.getX(),
                chicken.getY() + chicken.getBbHeight() * 0.4D,
                chicken.getZ(),
                count, 0.15D, 0.1D, 0.15D, 0.02D);
    }
}