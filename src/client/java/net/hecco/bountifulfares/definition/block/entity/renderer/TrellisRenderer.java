package net.hecco.bountifulfares.definition.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.TrellisBlock;
import net.hecco.bountifulfares.definition.block.entity.TrellisBlockEntity;
import net.hecco.bountifulfares.definition.block.entity.renderer.state.TrellisRenderState;
import net.hecco.bountifulfares.registry.misc.BFModelLayers;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

/**
 * 26.3 "render state" redesign fix - see the project checkpoint doc, part B.
 * <p>
 * Wraps each of the two vine {@link ModelPart}s ("vines" child of the default/inverted layers)
 * in a trivial {@link Model} (constructor confirmed via javap to be a thin, already-concrete
 * wrapper - {@code Model(ModelPart, Function<Identifier, RenderType>)} - with no abstract
 * methods to implement) so that {@code SubmitNodeCollector.submitModel(Model, S, PoseStack,
 * Identifier, int, int, int)} can be used directly - the simplest confirmed draw-call shape for
 * a single ModelPart against a plain (non-atlas) texture, sidestepping the separate,
 * unconfirmed {@code submitModelPart(..., UvMapping)} overload entirely. The old
 * {@code Material(TextureAtlas.LOCATION_BLOCKS, texture).buffer(...)} trick (which sampled the
 * shared BLOCK atlas for a texture that's really just a standalone PNG) is replaced by binding
 * that same PNG directly as its own entity-style texture via
 * {@code RenderTypes.entityCutout(Identifier, boolean)} - pixel-identical since it's the same
 * source image either way, and avoids needing sprite-atlas lookup machinery for what is, in
 * substance, an ordinary fixed texture.
 */
public class TrellisRenderer implements BlockEntityRenderer<TrellisBlockEntity, TrellisRenderState> {

    private static final java.util.function.Function<Identifier, RenderType> VINE_RENDER_TYPE = id -> RenderTypes.entityCutout(id, false);

    private final Model<Void> defaultModel;
    private final Model<Void> invertedModel;

    public TrellisRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart defaultLayer = context.bakeLayer(BFModelLayers.TRELLIS_DEFAULT);
        ModelPart invertedLayer = context.bakeLayer(BFModelLayers.TRELLIS_INVERTED);
        this.defaultModel = new Model<Void>(defaultLayer.getChild("vines"), VINE_RENDER_TYPE) {};
        this.invertedModel = new Model<Void>(invertedLayer.getChild("vines"), VINE_RENDER_TYPE) {};
    }

    @Override
    public TrellisRenderState createRenderState() {
        return new TrellisRenderState();
    }

    @Override
    public void extractRenderState(TrellisBlockEntity entity, TrellisRenderState state, float partialTick,
                                    Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, breakProgress);

        BlockState blockState = entity.getBlockState();
        Direction direction = blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;

        state.hasPlant = entity.getPlant() != null
                && (TrellisBlock.PLANTS.containsKey(entity.getPlant()) || TrellisBlock.CROPS.containsKey(entity.getPlant()));
        if (state.hasPlant) {
            state.inverted = false;
            state.texture = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/trellis");
            if (TrellisBlock.PLANTS.containsKey(entity.getPlant())) {
                if (TrellisBlock.PLANTS.get(entity.getPlant()).model().equalsIgnoreCase("inverted")) {
                    state.inverted = true;
                }
                state.texture = TrellisBlock.PLANTS.get(entity.getPlant()).texture();
            }
            if (TrellisBlock.CROPS.containsKey(entity.getPlant())) {
                if (TrellisBlock.CROPS.get(entity.getPlant()).model().equalsIgnoreCase("inverted")) {
                    state.inverted = true;
                }
                state.texture = TrellisBlock.CROPS.get(entity.getPlant()).texture().withSuffix("_" + entity.getStage());
            }
            // Datapack trellis definitions name block-atlas sprite ids (e.g.
            // "bountifulfares:block/vine_trellis"); binding the PNG directly as an entity texture
            // needs the full resource path ("textures/block/vine_trellis.png") - the convention
            // every vanilla entity renderer's texture constant uses (confirmed from the jar).
            state.texture = state.texture.withPath(path -> "textures/" + path + ".png");
            state.yRot = switch (direction) {
                case SOUTH -> 0;
                case NORTH -> (float) Math.PI;
                case EAST -> (float) (Math.PI * 0.5);
                case WEST -> (float) (Math.PI * 1.5);
                default -> 0;
            };
        }
    }

    @Override
    public void submit(TrellisRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (!state.hasPlant) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.scale(-1, -1, -1);

        Model<Void> model = state.inverted ? this.invertedModel : this.defaultModel;
        model.root().yRot = state.yRot;
        // The Identifier overload's last int is the *outline* color (-1 would request a white
        // outline); use the full overload with color -1 (untinted) and outline 0 (none).
        RenderType renderType = model.renderType(state.texture);
        collector.submitModel(model, null, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, 0);
        if (state.breakProgress != null) {
            collector.order(1).submitCrumblingOverlay(model, null, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, state.breakProgress);
        }

        poseStack.popPose();
    }
}
