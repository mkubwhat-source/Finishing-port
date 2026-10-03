package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.entity.crow.CrowEntity;
import alabaster.hearthandharvest.common.registry.HHModTriggers;

public class CrowStashEvents {

    public static void register() {
        HHEvents.ITEM_PICKUP.register((player, itemEntity, original, current) -> {
            if (player.level().isClientSide()) return;
            if (original.isEmpty()) return;
            if (itemEntity.getOwner() instanceof CrowEntity) {
                HHSimpleTrigger.trigger(HHModTriggers.CROW_STASH_FOUND, player);
            }
        });
    }
}