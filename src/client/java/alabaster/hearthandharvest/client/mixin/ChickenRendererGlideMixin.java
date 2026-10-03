package alabaster.hearthandharvest.client.mixin;

import alabaster.hearthandharvest.client.render.HHRenderStateKeys;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A chicken held overhead (chicken gliding) faces where the player's body faces, looks straight
 * ahead and is drawn without legs, a little lower (1.21.1: ChickenGlideClientEvents on RenderLivingEvent).
 */
@Mixin(ChickenRenderer.class)
public abstract class ChickenRendererGlideMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/chicken/Chicken;Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;F)V", at = @At("TAIL"))
    private void hearthandharvest$extractHeldChicken(Chicken chicken, ChickenRenderState state, float partialTick, CallbackInfo ci) {
        boolean held = chicken.getVehicle() instanceof Player;
        state.setData(HHRenderStateKeys.HELD_CHICKEN, held);
        if (held && chicken.getVehicle() instanceof Player player) {
            state.bodyRot = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
            state.yRot = 0.0F;
            state.xRot = 0.0F;
        }
    }
}
