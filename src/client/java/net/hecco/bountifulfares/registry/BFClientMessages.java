package net.hecco.bountifulfares.registry;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hecco.bountifulfares.definition.networking.BFC2SPackets;
import net.hecco.bountifulfares.definition.networking.BFS2CPackets;
import net.hecco.bountifulfares.definition.networking.payload.*;

// Split out of registry.BFMessages (src/main) because these receiver registrations reference
// client-only classes (BFS2CPackets, and ClientPlayNetworking itself needs Minecraft on the
// classpath) that aren't visible to main's compileJava. Only ever called from the client
// entrypoint (FabricBountifulFaresClient), same as before the split.
public class BFClientMessages {
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(CeramicDishEmptyPayload.ID, (payload, context) ->
                context.client().execute(() -> BFS2CPackets.ceramicDishEmpty(payload)));

        ClientPlayNetworking.registerGlobalReceiver(CeramicDishItemPayload.ID, (payload, context) ->
                context.client().execute(() -> BFS2CPackets.ceramicDishItem(payload)));

        ClientPlayNetworking.registerGlobalReceiver(CeramicBlockColorPayload.ID, (payload, context) ->
                context.client().execute(() -> BFS2CPackets.ceramicBlockColor(payload)));

        ClientPlayNetworking.registerGlobalReceiver(TrellisPlantPayload.ID, (payload, context) ->
                context.client().execute(() -> BFS2CPackets.trellisPlant(payload)));

        ClientPlayNetworking.registerGlobalReceiver(TrellisEmptyPayload.ID, (payload, context) ->
                context.client().execute(() -> BFS2CPackets.trellisEmpty(payload)));

        ClientPlayNetworking.registerGlobalReceiver(TrellisSyncPayload.ID, (payload, context) ->
                context.client().execute(() -> BFS2CPackets.trellisSync(payload)));

        ServerPlayNetworking.registerGlobalReceiver(TiffinFillPayload.ID, (payload, context) ->
                context.server().execute(() -> BFC2SPackets.tiffinFill(payload, context.player())));

        ServerPlayNetworking.registerGlobalReceiver(UseArtisanBrushPayload.ID, (payload, context) ->
                context.server().execute(() -> BFC2SPackets.useArtisanBrushInInventory(payload, context.player())));
    }
}
