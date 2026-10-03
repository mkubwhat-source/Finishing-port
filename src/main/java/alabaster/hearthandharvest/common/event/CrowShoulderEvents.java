package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.entity.crow.CrowEntity;
import alabaster.hearthandharvest.common.entity.crow.goals.CrowRetrieveItemsGoal;
import alabaster.hearthandharvest.common.registry.HHModEntities;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.function.Consumer;

public class CrowShoulderEvents {
    private static final int CHECK_INTERVAL = 20;
    private static final double SEARCH_RADIUS = 8.0D;
    private static final double SEARCH_HEIGHT = 4.0D;

    public static void register() {
        HHEvents.PLAYER_TICK_POST.register(CrowShoulderEvents::onPlayerTick);
    }

    private static void onPlayerTick(net.minecraft.world.entity.player.Player entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (player.tickCount % CHECK_INTERVAL != 0) return;
        if (!player.isAlive() || player.isSpectator() || player.isSleeping()) return;

        boolean crowOnLeft = isCrow(player.getShoulderEntityLeft());
        boolean crowOnRight = isCrow(player.getShoulderEntityRight());
        if (!crowOnLeft && !crowOnRight) return;

        if (crowOnLeft && crowOnRight) {
            HHSimpleTrigger.trigger(HHModTriggers.CROW_PAIR, player);
        }

        if (!Config.CROW_FETCH_ITEMS.get() || !Config.CROW_LEAVE_SHOULDER_TO_FETCH.get()) return;
        if (!hasItemToFetch(player)) return;

        if (crowOnLeft) {
            release(player, player.getShoulderEntityLeft(), player::setShoulderEntityLeft, 0.4D);
        } else {
            release(player, player.getShoulderEntityRight(), player::setShoulderEntityRight, -0.4D);
        }
    }

    private static boolean isCrow(CompoundTag tag) {
        if (tag.isEmpty()) return false;
        return tag.getStringOr("id", "").equals(BuiltInRegistries.ENTITY_TYPE.getKey(HHModEntities.CROW.get()).toString());
    }

    private static boolean hasItemToFetch(ServerPlayer player) {
        return !player.level().getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(SEARCH_RADIUS, SEARCH_HEIGHT, SEARCH_RADIUS),
                item -> CrowRetrieveItemsGoal.isWorthFetching(item, player)).isEmpty();
    }

    private static void release(ServerPlayer player, CompoundTag tag, Consumer<CompoundTag> clearShoulder, double sideOffset) {
        ServerLevel level = player.level();
        EntityType.create(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag), level,
                new EntitySpawnRequest(EntitySpawnReason.LOAD, false)).ifPresent(entity -> {
            if (!(entity instanceof CrowEntity crow)) return;

            double yawRad = Math.toRadians(player.getYRot());
            double x = player.getX() + Math.cos(yawRad) * sideOffset;
            double z = player.getZ() + Math.sin(yawRad) * sideOffset;
            crow.setOwner(player);
            crow.snapTo(x, player.getY() + 1.3D, z, player.getYRot(), 0.0F);
            if (level.addWithUUID(crow)) {
                clearShoulder.accept(new CompoundTag());
            }
        });
    }
}