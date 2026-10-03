package net.hecco.bountifulfares.registry;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.hecco.bountifulfares.definition.networking.payload.*;

public class BFMessages {
    // Client-side packet-receiver registration (ClientPlayNetworking.registerGlobalReceiver
    // plus the ServerPlayNetworking receivers it was historically bundled with) lives in
    // src/client's BFClientMessages.registerS2CPackets() now: this class only ever ran on the
    // client entrypoint (see FabricBountifulFaresClient), but it referenced client-only classes
    // (BFS2CPackets, Minecraft) that main's compileJava can't see. registerPayloads() below is
    // loader/side-agnostic (just codec registration) so it stays here in main.
    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(CeramicDishItemPayload.ID, CeramicDishItemPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(CeramicBlockColorPayload.ID, CeramicBlockColorPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(CeramicDishEmptyPayload.ID, CeramicDishEmptyPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TrellisPlantPayload.ID, TrellisPlantPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TrellisEmptyPayload.ID, TrellisEmptyPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TrellisSyncPayload.ID, TrellisSyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TiffinFillPayload.ID, TiffinFillPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UseArtisanBrushPayload.ID, UseArtisanBrushPayload.CODEC);
    }
}
