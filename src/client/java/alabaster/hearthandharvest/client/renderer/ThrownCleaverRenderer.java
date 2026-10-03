package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.common.entity.cleaver.ThrownCleaver;
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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

/** A thrown cleaver: spins end over end in flight, sticks blade-first (26.3 render-state renderer). */
public class ThrownCleaverRenderer extends EntityRenderer<ThrownCleaver, ThrownCleaverRenderer.State> {

    private static final float SPIN_DEG_PER_TICK = 54f;

    public static class State extends EntityRenderState {
        final ItemStackRenderState item = new ItemStackRenderState();
        float yRot;
        float spin;
        boolean stuck;
    }

    private final ItemModelResolver itemModelResolver;

    public ThrownCleaverRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemModelResolver = ctx.getItemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ThrownCleaver entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        state.stuck = entity.isStuck();
        state.spin = (entity.tickCount + partialTicks) * SPIN_DEG_PER_TICK;
        itemModelResolver.updateForNonLiving(state.item, entity.getCleaverStack(), ItemDisplayContext.NONE, entity);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float yrRad = state.yRot * Mth.DEG_TO_RAD;
        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        if (state.stuck) {
            poseStack.rotate(Axis.YP.rotationDegrees(state.yRot + 90f));
            poseStack.rotate(Axis.ZP.rotationDegrees(180f));
        } else {
            float fdx = Mth.sin(yrRad);
            float fdz = Mth.cos(yrRad);
            poseStack.rotate(new Quaternionf().rotateAxis(state.spin * Mth.DEG_TO_RAD, fdz, 0f, -fdx));
            poseStack.rotate(Axis.YP.rotationDegrees(state.yRot - 90f));
        }
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
