package alabaster.hearthandharvest.client.mixin;

import alabaster.hearthandharvest.client.render.HHRenderStateKeys;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.chicken.ChickenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Held chickens: legs hidden and the model lowered 0.2 blocks (see {@link ChickenRendererGlideMixin}). */
@Mixin(ChickenModel.class)
public abstract class ChickenModelGlideMixin extends EntityModel<ChickenRenderState> {
    /** 1.21.1 translated the pose stack down 0.2 blocks; model space is in pixels, y pointing down. */
    private static final float HEARTHANDHARVEST_DROP = 0.2F * 16.0F;

    @Shadow @Final private ModelPart rightLeg;
    @Shadow @Final private ModelPart leftLeg;

    protected ChickenModelGlideMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;)V", at = @At("TAIL"))
    private void hearthandharvest$heldChickenPose(ChickenRenderState state, CallbackInfo ci) {
        boolean held = state.getDataOrDefault(HHRenderStateKeys.HELD_CHICKEN, false);
        this.rightLeg.visible = !held;
        this.leftLeg.visible = !held;
        if (held) this.root.y += HEARTHANDHARVEST_DROP;
    }
}
