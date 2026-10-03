package alabaster.hearthandharvest.client.mixin;

import alabaster.hearthandharvest.client.render.HHRenderStateKeys;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Both arms raised while the player holds a chicken overhead (flag set by {@link AvatarRendererChickenMixin}). */
@Mixin(PlayerModel.class)
public abstract class PlayerModelChickenArmsMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    private void hearthandharvest$raiseArmsForChicken(AvatarRenderState state, CallbackInfo ci) {
        if (!state.getDataOrDefault(HHRenderStateKeys.HOLDING_CHICKEN, false)) return;

        PlayerModel model = (PlayerModel) (Object) this;
        model.rightArm.xRot = (float) -Math.PI;
        model.rightArm.yRot = 0.0F;
        model.rightArm.zRot = 0.0F;
        model.leftArm.xRot = (float) -Math.PI;
        model.leftArm.yRot = 0.0F;
        model.leftArm.zRot = 0.0F;
        // 26.3: the sleeves are children of the arms, so they follow without copying the pose.
    }
}
