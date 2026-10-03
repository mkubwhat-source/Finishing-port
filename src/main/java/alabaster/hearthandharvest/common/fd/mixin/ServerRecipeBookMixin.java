package alabaster.hearthandharvest.common.fd.mixin;

import alabaster.hearthandharvest.common.fd.network.RecipeBookValuesPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.ServerRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla's settings packet only carries its own book types, so HH's are sent alongside (from FarmersDelightRefabricated). */
@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin extends RecipeBook {
    @Inject(method = "sendInitialRecipeBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 1))
    private void hearthandharvest$sendRecipeBookValues(ServerPlayer player, CallbackInfo ci) {
        if (ServerPlayNetworking.canSend(player, RecipeBookValuesPayload.TYPE)) {
            ServerPlayNetworking.send(player, RecipeBookValuesPayload.of(getBookSettings()));
        }
    }
}
