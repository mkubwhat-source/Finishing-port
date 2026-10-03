package com.sidden.flavored.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sidden.flavored.entity.ThrownHotSauce;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * The thrown hot sauce bottle: the item (ground transform) tumbling end over end, instead of the
 * camera-facing sprite of a normal thrown item. Hidden for its first two ticks near the camera,
 * like vanilla thrown items were in 1.21.1.
 */
public class ThrownHotSauceRenderer extends EntityRenderer<ThrownHotSauce, ThrownHotSauceRenderer.State> {
    private final ItemModelResolver itemModelResolver;

    public static class State extends ThrownItemRenderState {
        public boolean hidden;
    }

    public ThrownHotSauceRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ThrownHotSauce entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        this.itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
        state.hidden = entity.tickCount < 2 && state.distanceToCameraSq < 12.25;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.hidden) return;
        poseStack.pushPose();
        poseStack.rotateAround(Axis.XP.rotation(state.ageInTicks / 2), 0.0F, 0.0F, 0.0F);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
