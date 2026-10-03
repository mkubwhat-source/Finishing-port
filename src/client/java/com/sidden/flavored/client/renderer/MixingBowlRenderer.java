package com.sidden.flavored.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sidden.flavored.Flavored;
import com.sidden.flavored.block.entity.MixingBowlBlockEntity;
import com.sidden.flavored.client.MixingBowlLiquids;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the mixing bowl's liquid surface and its ingredients stacked in the bowl, wiggling while
 * being whisked. 26.3 render-state form of 1.21.1's renderer (same geometry and transforms).
 */
public class MixingBowlRenderer implements BlockEntityRenderer<MixingBowlBlockEntity, MixingBowlRenderer.State> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "mixing_bowl_liquid"), "main");

    private final ItemModelResolver itemModelResolver;
    private final Model<Void> liquid;

    public static class State extends BlockEntityRenderState {
        @Nullable
        public Identifier liquidTexture;
        public final List<ItemStackRenderState> items = new ArrayList<>();
        public float wiggleTime;
    }

    public MixingBowlRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.liquid = new Model<>(context.bakeLayer(LAYER_LOCATION), id -> RenderTypes.entityCutout(id, false)) {
        };
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("bone", CubeListBuilder.create().texOffs(-10, 0)
                .addBox(-5, -19, 11, 10.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MixingBowlBlockEntity bowl, State state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(bowl, state, partialTick, cameraPos, breakProgress);
        state.liquidTexture = MixingBowlLiquids.textureFor(bowl.getItems().get(MixingBowlBlockEntity.LIQUID_SLOT).getItem());
        state.wiggleTime = bowl.wiggleTime > 0 ? bowl.wiggleTime - partialTick : 0;
        state.items.clear();
        int seed = (int) bowl.getBlockPos().asLong();
        for (int i = 0; i < MixingBowlBlockEntity.INGREDIENT_SLOTS.length; i++) {
            ItemStack stack = bowl.getItems().get(i);
            if (stack.isEmpty()) continue;
            ItemStackRenderState itemState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, bowl.getLevel(), null, seed + i);
            state.items.add(itemState);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.liquidTexture != null) {
            collector.submitModel(this.liquid, null, poseStack, RenderTypes.entityCutout(state.liquidTexture, false),
                    state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0);
        }

        int index = 0;
        for (ItemStackRenderState item : state.items) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.2 + (index * 0.08), 0.5);
            if (state.wiggleTime > 0) {
                float time = state.wiggleTime;
                float wiggleOffset = (float) Math.sin(time * 2.0f) * 0.05f;
                float wiggleRot = (float) Math.sin(time * 3.0f) * 10f;
                float bounce = (float) Math.sin(time * 4.0f) * 0.03f;
                poseStack.translate(wiggleOffset, bounce, -wiggleOffset);
                poseStack.rotateDegrees(Axis.YP, wiggleRot);
            }
            poseStack.rotateDegrees(Axis.XP, 90);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
            index++;
        }
    }
}
