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
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.level.Level;

public class PigLitters {

    public static void register() {
        HHEvents.BABY_SPAWN.register((parentA, parentB, child, cause) -> onBreed(parentA, parentB, cause));
    }

    private static void onBreed(Animal parentA, Animal parentB, ServerPlayer cause) {
        if (!(parentA instanceof Pig) || !(parentB instanceof Pig)) return;
        if (Config.DISABLE_PIG_LITTERS.get()) return;
        Level world = parentA.level();
        RandomSource random = parentA.getRandom();
        int extraCount = 1 + random.nextInt(3);
        if (extraCount == 3) HHSimpleTrigger.trigger(HHModTriggers.BIG_PIG_LITTER, cause);

        for (int i = 0; i < extraCount; i++) {
            Pig babyPig = net.minecraft.world.entity.EntityTypes.PIG.create(world, EntitySpawnReason.BREEDING);
            if (babyPig != null) {
                babyPig.setPos(parentA.getX(), parentA.getY(), parentA.getZ());
                babyPig.setBaby(true);
                world.addFreshEntity(babyPig);
            }
        }
    }
}
