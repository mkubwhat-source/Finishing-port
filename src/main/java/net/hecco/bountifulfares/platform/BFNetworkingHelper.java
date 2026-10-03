package net.hecco.bountifulfares.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/**
 * Fabric-only networking helper, ported in-tree from NexusLib's
 * {@code NLServices.NETWORK}/{@code FabricNetworkingHelper} as part of removing the
 * NexusLib dependency during the 26.3 Fabric port. This mod doesn't use NexusLib's cape
 * packets, so {@code sendSelectedCape} wasn't ported.
 */
public class BFNetworkingHelper {
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, BlockPos pos, CustomPacketPayload payload) {
        for (ServerPlayer targeter : level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), false)) {
            sendToPlayer(targeter, payload);
        }
    }

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
