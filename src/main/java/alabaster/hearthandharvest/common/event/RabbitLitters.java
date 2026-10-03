package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.Animal;

import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import alabaster.hearthandharvest.Config;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.level.Level;

public class RabbitLitters {

    public static void register() {
        HHEvents.BABY_SPAWN.register((parentA, parentB, child, cause) -> onBreed(parentA, parentB, cause));
    }

    private static void onBreed(Animal parentA, Animal parentB, ServerPlayer cause) {
        if (!(parentA instanceof Rabbit) || !(parentB instanceof Rabbit)) return;
        if (Config.DISABLE_RABBIT_LITTERS.get()) return;
        Level world = parentA.level();
        RandomSource random = parentA.getRandom();
        int extraCount = 1 + random.nextInt(3);
        if (extraCount == 3) HHSimpleTrigger.trigger(HHModTriggers.BIG_RABBIT_LITTER, cause);

        for (int i = 0; i < extraCount; i++) {
            Rabbit babyRabbit = net.minecraft.world.entity.EntityTypes.RABBIT.create(world, EntitySpawnReason.BREEDING);
            if (babyRabbit != null) {
                babyRabbit.setPos(parentA.getX(), parentA.getY(), parentA.getZ());
                babyRabbit.setBaby(true);
                world.addFreshEntity(babyRabbit);
            }
        }
    }
}
