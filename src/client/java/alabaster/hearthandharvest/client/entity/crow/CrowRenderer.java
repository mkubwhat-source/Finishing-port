package alabaster.hearthandharvest.client.entity.crow;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.entity.crow.CrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.resources.Identifier;

public class CrowRenderer extends MobRenderer<CrowEntity, CrowRenderState, CrowModel> {
    public static final Identifier TEXTURE = HearthAndHarvest.id("textures/entity/crow.png");

    public CrowRenderer(EntityRendererProvider.Context context) {
        super(context, new CrowModel(context.bakeLayer(CrowModel.LAYER_LOCATION)), 0.15f);
        this.addLayer(new CrowHeldItemLayer(this));
    }

    @Override
    public Identifier getTextureLocation(CrowRenderState state) {
        return TEXTURE;
    }

    @Override
    public CrowRenderState createRenderState() {
        return new CrowRenderState();
    }

    @Override
    public void extractRenderState(CrowEntity entity, CrowRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, this.itemModelResolver);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.flyingAnimationState.copyFrom(entity.flyingAnimationState);
        state.glidingAnimationState.copyFrom(entity.glidingAnimationState);
        state.sittingAnimationState.copyFrom(entity.sittingAnimationState);
        state.visuallyFlying = entity.isVisuallyFlying();
        state.flapAnimationSpeed = entity.getFlapAnimationSpeed();
        state.flightPitch = entity.getFlightPose().pitch(partialTick);
        state.flightRoll = entity.getFlightPose().roll(partialTick);
        state.bbHeight = entity.getBbHeight();
        state.onShoulder = false;
    }

    @Override
    protected void setupRotations(CrowRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);
        float pitch = state.flightPitch;
        float roll = state.flightRoll;
        if (Math.abs(pitch) < 0.01F && Math.abs(roll) < 0.01F) return;

        float pivot = state.bbHeight * 0.5F;
        poseStack.translate(0.0F, pivot, 0.0F);
        poseStack.rotate(Axis.XP.rotationDegrees(pitch));
        poseStack.rotate(Axis.ZP.rotationDegrees(-roll));
        poseStack.translate(0.0F, -pivot, 0.0F);
    }
}
