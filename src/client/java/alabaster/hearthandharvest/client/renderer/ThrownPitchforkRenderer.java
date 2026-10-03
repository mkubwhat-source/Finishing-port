package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.client.model.ThrownPitchforkModel;
import alabaster.hearthandharvest.common.entity.pitchfork.ThrownPitchfork;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Unit;

public class ThrownPitchforkRenderer extends EntityRenderer<ThrownPitchfork, ThrownPitchforkRenderer.State> {

    public static class State extends EntityRenderState {
        float xRot;
        float yRot;
    }

    private final ThrownPitchforkModel model;

    public ThrownPitchforkRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new ThrownPitchforkModel(ctx.bakeLayer(ThrownPitchforkModel.LAYER_LOCATION));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ThrownPitchfork entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        state.xRot = entity.getXRot(partialTicks);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotate(Axis.YP.rotationDegrees(state.yRot - 90.0f));
        poseStack.rotate(Axis.ZP.rotationDegrees(state.xRot + 90.0f));
        collector.submitModel(model, Unit.INSTANCE, poseStack, ThrownPitchforkModel.TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
