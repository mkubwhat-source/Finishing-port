package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.common.registry.HHModAttachments;
import alabaster.hearthandharvest.common.registry.HHModEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keeps {@link HHModAttachments#SHOULDER_CROWS} in step with the player's shoulder entities (synced to clients for rendering). */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerShoulderMixin {
    @Shadow public abstract CompoundTag getShoulderEntityLeft();
    @Shadow public abstract CompoundTag getShoulderEntityRight();

    @Inject(method = {"setShoulderEntityLeft", "setShoulderEntityRight"}, at = @At("TAIL"))
    private void hearthandharvest$syncShoulderCrows(CompoundTag tag, CallbackInfo ci) {
        int mask = (hearthandharvest$isCrow(getShoulderEntityLeft()) ? 1 : 0) | (hearthandharvest$isCrow(getShoulderEntityRight()) ? 2 : 0);
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (self.getAttachedOrElse(HHModAttachments.SHOULDER_CROWS, 0) != mask) {
            self.setAttached(HHModAttachments.SHOULDER_CROWS, mask);
        }
    }

    @Unique
    private static boolean hearthandharvest$isCrow(CompoundTag tag) {
        return !tag.isEmpty() && tag.getStringOr("id", "").equals(BuiltInRegistries.ENTITY_TYPE.getKey(HHModEntities.CROW.get()).toString());
    }
}
