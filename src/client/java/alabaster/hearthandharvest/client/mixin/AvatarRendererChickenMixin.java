package alabaster.hearthandharvest.client.mixin;

import alabaster.hearthandharvest.client.render.HHRenderStateKeys;
import alabaster.hearthandharvest.common.event.ChickenGlideEvents;
import alabaster.hearthandharvest.common.registry.HHModAttachments;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Copies HH player state into the avatar render state: holding a chicken ({@link PlayerModelChickenArmsMixin}) and shoulder crows (CrowOnShoulderLayer). */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererChickenMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void hearthandharvest$extractHoldingChicken(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        state.setData(HHRenderStateKeys.HOLDING_CHICKEN, entity instanceof Player player && ChickenGlideEvents.isHoldingChicken(player));
        state.setData(HHRenderStateKeys.SHOULDER_CROWS, entity.getAttachedOrElse(HHModAttachments.SHOULDER_CROWS, 0));
    }
}
