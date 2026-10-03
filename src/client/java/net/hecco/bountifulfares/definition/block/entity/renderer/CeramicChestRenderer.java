package net.hecco.bountifulfares.definition.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.CeramicChestBlock;
import net.hecco.bountifulfares.definition.block.entity.CeramicChestBlockEntity;
import net.hecco.bountifulfares.definition.block.entity.renderer.state.CeramicChestRenderState;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.misc.BFModelLayers;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;

/**
 * 26.3 "render state" redesign fix - see the project checkpoint doc, part B. The last and most
 * complex renderer of the migration.
 * <p>
 * Follows the same {@code Model<Void>} + standalone-{@code Identifier} approach as
 * {@code TrellisRenderer}: each of the mod's 9 pre-existing {@link ModelPart} fields (lid/bottom/
 * lock, x3 for single/double-left/double-right) is wrapped in its own trivial {@code Model<Void>}
 * sharing one {@code RenderTypes.entityCutout(Identifier, boolean)}-backed render-type function -
 * a separate {@code Model} per part (rather than one combined-root model) is required because the
 * old code (confirmed by re-reading it) rendered {@code lock} WITHOUT the ceramic dye tint while
 * {@code lid}/{@code bottom} DO get tinted, a distinction one {@code submitModel} call can't
 * express for multiple parts at once. The old chest-atlas {@code Material(TextureAtlas...)}
 * sprite-sheet trick is replaced by binding the mod's existing standalone
 * {@code bountifulfares:entity/chest/ceramic[.png/_left.png/_right.png]} textures directly (all
 * three confirmed present on disk) - pixel-identical since the underlying images are the same
 * files either way.
 * <p>
 * The openness/brightness-combining logic (previously in {@code render(...)}) now lives in
 * {@link #extractRenderState}, matching vanilla's own {@code ChestRenderer.extractRenderState(...)}
 * (confirmed via {@code javap -c}): {@code state.open} comes from
 * {@code neighborCombineResult.apply(CeramicChestBlock.opennessCombiner(entity)).get(partialTick)}
 * (then eased exactly as the old code did), and, only when the chest is part of a double chest
 * ({@code state.type != ChestType.SINGLE}), {@code state.lightCoords} is overwritten with the
 * neighbor-combined brightness via {@code ((Int2IntFunction) neighborCombineResult.apply(new
 * BrightnessCombiner<>())).applyAsInt(state.lightCoords)}.
 */
public class CeramicChestRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T, CeramicChestRenderState> {

    private static final java.util.function.Function<Identifier, RenderType> CHEST_RENDER_TYPE = id -> RenderTypes.entityCutout(id, false);

    private static final Identifier CHEST_TEXTURE = BountifulFares.id("textures/entity/chest/ceramic.png");
    private static final Identifier CHEST_TEXTURE_LEFT = BountifulFares.id("textures/entity/chest/ceramic_left.png");
    private static final Identifier CHEST_TEXTURE_RIGHT = BountifulFares.id("textures/entity/chest/ceramic_right.png");

    private final Model<Void> lid;
    private final Model<Void> bottom;
    private final Model<Void> lock;
    private final Model<Void> doubleLeftLid;
    private final Model<Void> doubleLeftBottom;
    private final Model<Void> doubleLeftLock;
    private final Model<Void> doubleRightLid;
    private final Model<Void> doubleRightBottom;
    private final Model<Void> doubleRightLock;

    public CeramicChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(BFModelLayers.CERAMIC_CHEST);
        this.bottom = new Model<Void>(modelpart.getChild("bottom"), CHEST_RENDER_TYPE) {};
        this.lid = new Model<Void>(modelpart.getChild("lid"), CHEST_RENDER_TYPE) {};
        this.lock = new Model<Void>(modelpart.getChild("lock"), CHEST_RENDER_TYPE) {};
        ModelPart modelpart1 = context.bakeLayer(BFModelLayers.CERAMIC_DOUBLE_CHEST_LEFT);
        this.doubleLeftBottom = new Model<Void>(modelpart1.getChild("bottom"), CHEST_RENDER_TYPE) {};
        this.doubleLeftLid = new Model<Void>(modelpart1.getChild("lid"), CHEST_RENDER_TYPE) {};
        this.doubleLeftLock = new Model<Void>(modelpart1.getChild("lock"), CHEST_RENDER_TYPE) {};
        ModelPart modelpart2 = context.bakeLayer(BFModelLayers.CERAMIC_DOUBLE_CHEST_RIGHT);
        this.doubleRightBottom = new Model<Void>(modelpart2.getChild("bottom"), CHEST_RENDER_TYPE) {};
        this.doubleRightLid = new Model<Void>(modelpart2.getChild("lid"), CHEST_RENDER_TYPE) {};
        this.doubleRightLock = new Model<Void>(modelpart2.getChild("lock"), CHEST_RENDER_TYPE) {};
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createDoubleBodyRightLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createDoubleBodyLeftLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public CeramicChestRenderState createRenderState() {
        return new CeramicChestRenderState();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void extractRenderState(T blockEntity, CeramicChestRenderState state, float partialTick,
                                    Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, breakProgress);

        Level level = blockEntity.getLevel();
        boolean hasLevel = level != null;
        BlockState blockstate = hasLevel ? blockEntity.getBlockState()
                : BFBlocks.CERAMIC_CHEST.get().defaultBlockState().setValue(CeramicChestBlock.FACING, Direction.SOUTH);

        state.type = blockstate.hasProperty(CeramicChestBlock.TYPE) ? blockstate.getValue(CeramicChestBlock.TYPE) : ChestType.SINGLE;
        state.facing = blockstate.getValue(CeramicChestBlock.FACING);

        Block block = blockstate.getBlock();
        if (block instanceof AbstractChestBlock<?> abstractChestBlock) {
            DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> neighborCombineResult;
            if (hasLevel) {
                neighborCombineResult = abstractChestBlock.combine(blockstate, level, blockEntity.getBlockPos(), true);
            } else {
                neighborCombineResult = DoubleBlockCombiner.Combiner::acceptNone;
            }

            float f = neighborCombineResult.apply(CeramicChestBlock.opennessCombiner(blockEntity)).get(partialTick);
            f = 1.0F - f;
            f = 1.0F - f * f * f;
            state.open = f;

            if (state.type != ChestType.SINGLE) {
                state.lightCoords = ((Int2IntFunction) neighborCombineResult.apply(new BrightnessCombiner<>())).applyAsInt(state.lightCoords);
            }
        }

        int color = CeramicChestBlockEntity.DEFAULT_COLOR;
        if (blockEntity instanceof CeramicChestBlockEntity entity) {
            color = entity.color;
        }
        state.color = ARGB.opaque(color);
    }

    @Override
    public void submit(CeramicChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        Identifier texture;
        Model<Void> lidModel;
        Model<Void> lockModel;
        Model<Void> bottomModel;
        switch (state.type) {
            case LEFT -> {
                texture = CHEST_TEXTURE_LEFT;
                lidModel = this.doubleLeftLid;
                lockModel = this.doubleLeftLock;
                bottomModel = this.doubleLeftBottom;
            }
            case RIGHT -> {
                texture = CHEST_TEXTURE_RIGHT;
                lidModel = this.doubleRightLid;
                lockModel = this.doubleRightLock;
                bottomModel = this.doubleRightBottom;
            }
            default -> {
                texture = CHEST_TEXTURE;
                lidModel = this.lid;
                lockModel = this.lock;
                bottomModel = this.bottom;
            }
        }

        float lidXRot = -(state.open * ((float) Math.PI / 2F));
        lidModel.root().xRot = lidXRot;
        lockModel.root().xRot = lidXRot;

        // NB: submitModel(Model, S, PoseStack, Identifier, light, overlay, int) takes an *outline*
        // color as its last int and always renders with color -1 (javap: it forwards
        // light, overlay, -1, null, arg7). Tinting needs the full overload: (..., RenderType, light,
        // overlay, color, UvMapping, outlineColor), outline 0 = none, as vanilla's ChestRenderer.
        submitPart(collector, lidModel, poseStack, texture, state, state.color);
        submitPart(collector, lockModel, poseStack, texture, state, -1);
        submitPart(collector, bottomModel, poseStack, texture, state, state.color);

        poseStack.popPose();
    }

    private static void submitPart(SubmitNodeCollector collector, Model<Void> model, PoseStack poseStack, Identifier texture, CeramicChestRenderState state, int color) {
        RenderType renderType = model.renderType(texture);
        collector.submitModel(model, null, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, color, null, 0);
        // Block-breaking cracks on the block-entity geometry (vanilla ChestRenderer does the same;
        // in 1.21.1 this came for free from the crumbling buffer the BER was drawn into).
        if (state.breakProgress != null) {
            collector.order(1).submitCrumblingOverlay(model, null, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, state.breakProgress);
        }
    }
}
