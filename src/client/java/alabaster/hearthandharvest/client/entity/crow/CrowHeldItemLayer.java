package alabaster.hearthandharvest.client.entity.crow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** The item a crow carries in its beak (1.21.1 rendered it with ItemInHandRenderer in GROUND context). */
public class CrowHeldItemLayer extends RenderLayer<CrowRenderState, CrowModel> {
    private static final float BEAK_Y = 0.25F / 16.0F;
    private static final float BEAK_Z = -3.25F / 16.0F;
    private static final float ITEM_SCALE = 0.5F;

    public CrowHeldItemLayer(RenderLayerParent<CrowRenderState, CrowModel> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, CrowRenderState state, float yRot, float xRot) {
        if (state.heldItem.isEmpty()) return;

        poseStack.pushPose();
        this.getParentModel().translateToBeak(poseStack);
        poseStack.translate(0.0F, BEAK_Y, BEAK_Z);
        poseStack.rotate(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        state.heldItem.submit(poseStack, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
