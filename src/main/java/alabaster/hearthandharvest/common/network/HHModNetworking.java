package alabaster.hearthandharvest.common.network;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.fd.network.RichSoilBoostParticlesPayload;
import alabaster.hearthandharvest.common.registry.HHDataMaps;
import alabaster.hearthandharvest.common.registry.HHModAttachments;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.food.FoodData;

/**
 * Hearth and Harvest's packets: the poop keybind (client -> server) and its cooldown (server ->
 * client), plus the data map sync and Farmer's Delight's rich soil particles. Client receivers are
 * registered in the client entrypoint.
 */
public class HHModNetworking {
    private static final int POOP_COOLDOWN_TICKS = 300; // 15 seconds

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(PlayerPoopPacket.TYPE, PlayerPoopPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayerPoopCooldownPacket.TYPE, PlayerPoopCooldownPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HHDataMaps.SyncPayload.TYPE, HHDataMaps.SyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(RichSoilBoostParticlesPayload.TYPE, RichSoilBoostParticlesPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(alabaster.hearthandharvest.common.fd.network.RecipeBookValuesPayload.TYPE, alabaster.hearthandharvest.common.fd.network.RecipeBookValuesPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PlayerPoopPacket.TYPE, (packet, context) -> {
            if (!Config.PLAYER_POOP_ENABLED.get()) return;
            ServerPlayer player = context.player();
            long now = player.level().getGameTime();
            if (now - player.getAttachedOrGet(HHModAttachments.PLAYER_LAST_POOP_TIME, HHModAttachments.PLAYER_LAST_POOP_TIME.initializer()) < POOP_COOLDOWN_TICKS) return;
            FoodData food = player.getFoodData();
            if (food.getFoodLevel() < 1) return;
            food.setFoodLevel(food.getFoodLevel() - 1);
            player.setAttached(HHModAttachments.PLAYER_LAST_POOP_TIME, now);
            player.spawnAtLocation(player.level(), HHModItems.MANURE.get());
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    HHModSounds.FART.get(), SoundSource.PLAYERS,
                    0.7f, 0.8f + player.getRandom().nextFloat() * 0.4f);
            ServerPlayNetworking.send(player, new PlayerPoopCooldownPacket());
            HHSimpleTrigger.trigger(HHModTriggers.PLAYER_POOPED, player);
        });
    }
}
