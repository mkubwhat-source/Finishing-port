package alabaster.hearthandharvest.client.entity.crow;

import alabaster.hearthandharvest.client.render.HHRenderStateKeys;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Crows sitting on a player's shoulders. 26.3's AvatarRenderState only carries parrot variants, so
 * the server syncs a left/right mask (HHModAttachments.SHOULDER_CROWS) that AvatarRendererChickenMixin
 * copies into the render state.
 */
public class CrowOnShoulderLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final CrowModel crowModel;

    public CrowOnShoulderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.crowModel = new CrowModel(modelSet.bakeLayer(CrowModel.LAYER_LOCATION));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        int mask = state.getDataOrDefault(HHRenderStateKeys.SHOULDER_CROWS, 0);
        if ((mask & 1) != 0) submitCrow(poseStack, collector, lightCoords, state, true, yRot, xRot);
        if ((mask & 2) != 0) submitCrow(poseStack, collector, lightCoords, state, false, yRot, xRot);
    }

    private void submitCrow(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, AvatarRenderState playerState, boolean left, float yRot, float xRot) {
        poseStack.pushPose();
        poseStack.translate(left ? 0.4F : -0.35F, playerState.isCrouching ? -1.3F : -1.5F, 0.0F);
        CrowRenderState crowState = new CrowRenderState();
        crowState.onShoulder = true;
        crowState.ageInTicks = playerState.ageInTicks;
        crowState.yRot = yRot;
        crowState.xRot = xRot;
        collector.submitModel(this.crowModel, crowState, poseStack, CrowRenderer.TEXTURE, lightCoords, OverlayTexture.NO_OVERLAY, playerState.outlineColor);
        poseStack.popPose();
    }
}
