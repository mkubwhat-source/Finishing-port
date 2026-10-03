package net.hecco.bountifulfares.mixin.gameplay;

import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sleeping in a coir bed never sets the player's spawn point (and clears an existing one, as
 * before). 26.3 folded the four separate respawnPosition/respawnDimension/respawnAngle/
 * respawnForced fields into a single nullable {@code ServerPlayer.RespawnConfig respawnConfig}
 * (a record of {@code LevelData.RespawnData(GlobalPos, yaw, pitch)} + {@code forced}), and
 * {@code setRespawnPosition(ResourceKey, BlockPos, float, boolean, boolean)} became
 * {@code setRespawnPosition(RespawnConfig, boolean sendMessage)} - confirmed via javap. Clearing
 * the old four fields to null/overworld/0/false is exactly {@code respawnConfig = null}.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow
    private ServerPlayer.RespawnConfig respawnConfig;

    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void bountifulfares$preventCustomBedSpawnPoint(@Nullable ServerPlayer.RespawnConfig config, boolean sendMessage, CallbackInfo ci) {
        if (config != null) {
            if (((ServerPlayer) (Object) this).level().getBlockState(config.respawnData().pos()).is(BFBlocks.COIR_BED.get())) {
                this.respawnConfig = null;
                ci.cancel();
            }
        }
    }
}
