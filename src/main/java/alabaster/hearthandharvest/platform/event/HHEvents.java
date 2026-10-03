package alabaster.hearthandharvest.platform.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Fabric events for the NeoForge events Hearth and Harvest used that Fabric API has no equivalent
 * for. Each is fired from a mixin in {@code alabaster.hearthandharvest.common.mixin} at the same
 * point NeoForge fired its event.
 */
public final class HHEvents {
    private HHEvents() {
    }

    /** NeoForge {@code PlayerTickEvent.Pre/Post} (both sides): start/end of {@code Player.tick}. */
    public interface PlayerTick {
        void onTick(Player player);
    }

    public static final Event<PlayerTick> PLAYER_TICK_PRE = EventFactory.createArrayBacked(PlayerTick.class, listeners -> player -> {
        for (PlayerTick l : listeners) l.onTick(player);
    });
    public static final Event<PlayerTick> PLAYER_TICK_POST = EventFactory.createArrayBacked(PlayerTick.class, listeners -> player -> {
        for (PlayerTick l : listeners) l.onTick(player);
    });

    /** NeoForge {@code EntityTickEvent.Post} for living entities (both sides): end of {@code LivingEntity.tick}. */
    public interface LivingTick {
        void onTick(LivingEntity entity);
    }

    public static final Event<LivingTick> LIVING_TICK_POST = EventFactory.createArrayBacked(LivingTick.class, listeners -> entity -> {
        for (LivingTick l : listeners) l.onTick(entity);
    });

    /**
     * NeoForge {@code ItemEntityPickupEvent.Post} (server): a player picked up (part of) an item
     * entity. {@code original} is the stack before pickup, {@code current} what is left on the ground.
     */
    public interface ItemPickup {
        void onPickup(Player player, ItemEntity itemEntity, ItemStack original, ItemStack current);
    }

    public static final Event<ItemPickup> ITEM_PICKUP = EventFactory.createArrayBacked(ItemPickup.class, listeners -> (player, item, original, current) -> {
        for (ItemPickup l : listeners) l.onPickup(player, item, original, current);
    });

    /**
     * NeoForge {@code BabyEntitySpawnEvent} (server): two animals bred, the baby is about to be added.
     * {@code cause} is the player who fed either parent, if any.
     */
    public interface BabySpawn {
        void onBabySpawn(Animal parentA, Animal parentB, AgeableMob child, @Nullable ServerPlayer cause);
    }

    public static final Event<BabySpawn> BABY_SPAWN = EventFactory.createArrayBacked(BabySpawn.class, listeners -> (a, b, child, cause) -> {
        for (BabySpawn l : listeners) l.onBabySpawn(a, b, child, cause);
    });

    /** NeoForge {@code LivingEntityUseItemEvent.Finish} (both sides): an item finished being used (eaten, drunk...). */
    public interface UseItemFinish {
        void onFinish(LivingEntity entity, ItemStack used);
    }

    public static final Event<UseItemFinish> USE_ITEM_FINISH = EventFactory.createArrayBacked(UseItemFinish.class, listeners -> (entity, used) -> {
        for (UseItemFinish l : listeners) l.onFinish(entity, used);
    });

    /**
     * NeoForge {@code AlterGroundEvent}: a tree decorator (podzol from big spruce trees, etc.) is
     * about to replace ground. Listeners may return a different state to place.
     */
    public interface AlterGround {
        BlockState modify(WorldGenLevel level, RandomSource random, BlockPos pos, BlockState replacement);
    }

    public static final Event<AlterGround> ALTER_GROUND = EventFactory.createArrayBacked(AlterGround.class, listeners -> (level, random, pos, state) -> {
        for (AlterGround l : listeners) state = l.modify(level, random, pos, state);
        return state;
    });
}
