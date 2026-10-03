package alabaster.hearthandharvest.common.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

import alabaster.hearthandharvest.common.entity.goal.StayNearSaltGoal;
import net.minecraft.world.entity.animal.Animal;

public class SaltBlockEvents {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Animal animal) {
                animal.goalSelector.addGoal(4, new StayNearSaltGoal(animal));
            }
        });
    }
}