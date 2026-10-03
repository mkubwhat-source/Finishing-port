package net.hecco.bountifulfares.definition.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.hecco.bountifulfares.definition.block.custom.CeramicDishBlock;
import net.hecco.bountifulfares.definition.block.entity.CeramicDishBlockEntity;
import net.hecco.bountifulfares.definition.block.entity.renderer.state.CeramicDishRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 26.3 "render state" redesign fix - see the project checkpoint doc, part B. This renderer draws
 * a single held {@link net.minecraft.world.item.ItemStack} (the dish's contents), so it follows
 * vanilla's {@code ShelfRenderer} pattern (confirmed via {@code javap -c}) rather than the
 * heavier {@code ChestRenderer}/{@code Model<S>} pattern used by the mod's other renderers:
 * populate an {@link net.minecraft.client.renderer.item.ItemStackRenderState} once per frame via
 * {@link ItemModelResolver#updateForTopItem}, then draw it with its own
 * {@code ItemStackRenderState.submit(...)} - both the old {@code Minecraft.getInstance()
 * .getItemRenderer()}/{@code ItemRenderer.renderStatic(...)} entirely gone in 26.3.
 * <p>
 * {@code updateForTopItem}'s trailing {@code ItemOwner} parameter only needs a position/level/
 * visual-rotation source (confirmed via its interface shape) - rather than making
 * {@code CeramicDishBlockEntity} itself implement {@code ItemOwner} (an unrelated concern for a
 * plain container block entity), a small anonymous implementation is built here from the block
 * entity's own position/level, matching the level()/position()/getVisualRotationYInDegrees()
 * shape confirmed via javap against vanilla's {@code ShelfBlockEntity}.
 */
public class CeramicDishRenderer implements BlockEntityRenderer<CeramicDishBlockEntity, CeramicDishRenderState> {
    private final ItemModelResolver itemModelResolver;

    public CeramicDishRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CeramicDishRenderState createRenderState() {
        return new CeramicDishRenderState();
    }

    @Override
    public void extractRenderState(CeramicDishBlockEntity entity, CeramicDishRenderState state, float partialTick,
                                    Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, breakProgress);
        Level level = entity.getLevel();
        state.facing = entity.getBlockState().getValue(CeramicDishBlock.FACING);
        if (level != null) {
            ItemOwner owner = new ItemOwner() {
                @Override
                public Level level() {
                    return level;
                }

                @Override
                public Vec3 position() {
                    return Vec3.atCenterOf(entity.getBlockPos());
                }

                @Override
                public float getVisualRotationYInDegrees() {
                    return state.facing.toYRot();
                }
            };
            this.itemModelResolver.updateForTopItem(state.item, entity.getRenderStack(), ItemDisplayContext.FIXED,
                    level, owner, entity.getBlockPos().hashCode());
        }
    }

    @Override
    public void submit(CeramicDishRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.08f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.rotateDegrees(Axis.YN, state.facing.toYRot() + 180);
        poseStack.rotateDegrees(Axis.XP, 90);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
