package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.common.entity.horseshoe.ThrownHorseshoe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

/** A thrown horseshoe: flat, spinning in flight, at its landing angle once stuck (26.3 render-state renderer). */
public class ThrownHorseshoeRenderer extends EntityRenderer<ThrownHorseshoe, ThrownHorseshoeRenderer.State> {

    public static class State extends EntityRenderState {
        final ItemStackRenderState item = new ItemStackRenderState();
        float yRot;
        float spin;
    }

    private final ItemModelResolver itemModelResolver;

    public ThrownHorseshoeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ThrownHorseshoe entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        float stuckAngle = entity.getStuckAngle();
        state.spin = stuckAngle >= 0 ? stuckAngle : (entity.tickCount + partialTicks) * 36.0f;
        itemModelResolver.updateForNonLiving(state.item, entity.getHorseshoeStack(), ItemDisplayContext.FIXED, entity);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotate(Axis.YP.rotationDegrees(-state.yRot));
        poseStack.rotate(Axis.XP.rotationDegrees(90.0f));
        poseStack.rotate(Axis.ZP.rotationDegrees(state.spin));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
