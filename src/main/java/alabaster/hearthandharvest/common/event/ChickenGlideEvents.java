package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.common.registry.HHModAttachments;
import alabaster.hearthandharvest.platform.event.HHEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.entity.Entity;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

public class ChickenGlideEvents {
    private static final double GLIDE_FALL_SPEED = 0.12D;
    private static final double GLIDE_FORWARD_BOOST = 0.012D;
    private static final double GLIDE_MAX_HORIZONTAL = 0.36D;
    private static final double ADVANCEMENT_DESCENT = 10.0D;
    private static final int FEATHER_INTERVAL = 4;
    private static final int CLUCK_MIN_INTERVAL = 40;
    private static final int CLUCK_CHANCE = 60;
    private static final String GLIDE_START_TAG = "hearthandharvest:glide_start_y";

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, target, hit) -> hit == null ? onInteractChicken(player, hand, target) : InteractionResult.PASS);
        HHEvents.PLAYER_TICK_POST.register(ChickenGlideEvents::onPlayerTick);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> !(entity instanceof Chicken chicken
                && chicken.getVehicle() instanceof Player
                && source.is(DamageTypes.IN_WALL)));
    }

    private static InteractionResult onInteractChicken(Player player, InteractionHand hand, Entity target) {
        if (!Config.CHICKEN_GLIDING.get()) return InteractionResult.PASS;
        if (!(target instanceof Chicken chicken)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player.isShiftKeyDown() || !player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
        if (chicken.isBaby() || chicken.isPassenger() || chicken.isVehicle() || chicken.isLeashed()) return InteractionResult.PASS;
        if (!player.getPassengers().isEmpty() || player.isPassenger() || player.isSpectator()) return InteractionResult.PASS;
        if (!player.level().isClientSide() && chicken.startRiding(player, true, true)) {
            syncPassengersToSelf(player);
            chicken.getNavigation().stop();
            chicken.playAmbientSound();
        }
        return InteractionResult.SUCCESS;
    }

    private static void onPlayerTick(Player player) {
        Chicken chicken = getHeldChicken(player);
        if (chicken == null) {
            clearGlideStart(player);
            return;
        }

        faceWithPlayer(chicken, player);

        if (!player.level().isClientSide()) {
            if (!Config.CHICKEN_GLIDING.get() || player.isShiftKeyDown() || player.isPassenger() || player.isSpectator()) {
                putDown(chicken, player);
                return;
            }
        }

        if (!canGlide(player)) {
            clearGlideStart(player);
            return;
        }
        player.resetFallDistance();

        if (player instanceof ServerPlayer serverPlayer) {
            trackDescent(serverPlayer);
            glideEffects(serverPlayer, chicken);
        }

        if (player.level().isClientSide() && player.isLocalPlayer()) {
            applyGlide(player);
        }
    }

    

    private static void glideEffects(ServerPlayer player, Chicken chicken) {
        if (player.tickCount % FEATHER_INTERVAL == 0) {
            FeatherParticles.trail(chicken, 1);
        }
        if (player.tickCount % CLUCK_MIN_INTERVAL == 0 && player.getRandom().nextInt(CLUCK_CHANCE) < CLUCK_MIN_INTERVAL) {
            chicken.playAmbientSound();
        }
    }

    private static void trackDescent(ServerPlayer player) {
        Double start = player.getAttached(HHModAttachments.GLIDE_START_Y);
        if (start == null) {
            player.setAttached(HHModAttachments.GLIDE_START_Y, player.getY());
            return;
        }
        if (start - player.getY() >= ADVANCEMENT_DESCENT) {
            HHModTriggers.CHICKEN_GLIDE.trigger(player);
        }
    }

    private static void clearGlideStart(Player player) {
        if (!player.level().isClientSide() && player.hasAttached(HHModAttachments.GLIDE_START_Y)) {
            player.removeAttached(HHModAttachments.GLIDE_START_Y);
        }
    }

    public static boolean isHoldingChicken(Player player) {
        return getHeldChicken(player) != null;
    }

    @Nullable
    private static Chicken getHeldChicken(Player player) {
        for (var passenger : player.getPassengers()) {
            if (passenger instanceof Chicken chicken) return chicken;
        }
        return null;
    }

    private static boolean canGlide(Player player) {
        return !player.onGround()
                && !player.isInWater()
                && !player.isInLava()
                && !player.onClimbable()
                && !player.isFallFlying()
                && !player.getAbilities().flying;
    }

    private static void applyGlide(Player player) {
        Vec3 motion = player.getDeltaMovement();
        double y = motion.y < -GLIDE_FALL_SPEED ? -GLIDE_FALL_SPEED : motion.y;
        double x = motion.x;
        double z = motion.z;

        if (player.zza > 0.0F) {
            float yawRad = player.getYRot() * Mth.DEG_TO_RAD;
            x += -Mth.sin(yawRad) * GLIDE_FORWARD_BOOST;
            z += Mth.cos(yawRad) * GLIDE_FORWARD_BOOST;
        }

        double horizontal = Math.sqrt(x * x + z * z);
        if (horizontal > GLIDE_MAX_HORIZONTAL) {
            double scale = GLIDE_MAX_HORIZONTAL / horizontal;
            x *= scale;
            z *= scale;
        }

        player.setDeltaMovement(x, y, z);
    }

    private static void faceWithPlayer(Chicken chicken, Player player) {
        chicken.setYRot(player.getYRot());
        chicken.yRotO = player.yRotO;
        chicken.yBodyRot = player.yBodyRot;
        chicken.yBodyRotO = player.yBodyRotO;
        chicken.setYHeadRot(player.getYHeadRot());
    }

    private static void syncPassengersToSelf(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(serverPlayer));
        }
    }

    private static void putDown(Chicken chicken, Player player) {
        chicken.stopRiding();
        syncPassengersToSelf(player);
        Vec3 look = Vec3.directionFromRotation(0.0F, player.getYRot());
        chicken.snapTo(player.getX() + look.x * 0.8D, player.getY() + 0.5D, player.getZ() + look.z * 0.8D, player.getYRot(), 0.0F);
        chicken.setDeltaMovement(player.getDeltaMovement());
    }
}