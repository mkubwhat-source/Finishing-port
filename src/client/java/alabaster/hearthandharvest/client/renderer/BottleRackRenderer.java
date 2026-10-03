package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.common.block.entity.BottleRackBlockEntity;
import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import alabaster.hearthandharvest.client.render.HHRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Bottles lying in a bottle rack, drawn with their display models (26.3 render-state renderer). */
public class BottleRackRenderer implements BlockEntityRenderer<BottleRackBlockEntity, BottleRackRenderer.State> {

    public record Bottle(int slot, boolean shortBottle, ItemStackRenderState model, @Nullable ItemStackRenderState vintage) {}

    public static class State extends BlockEntityRenderState {
        Direction facing = Direction.NORTH;
        final List<Bottle> bottles = new ArrayList<>();
    }

    private final ItemModelResolver itemModelResolver;

    public BottleRackRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BottleRackBlockEntity rack, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(rack, state, partialTick, cameraPosition, breakProgress);
        state.facing = rack.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        state.bottles.clear();
        int seed = (int) rack.getBlockPos().asLong();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = rack.getItem(slot);
            Identifier model = DisplayModels.get(stack);
            if (model == null) continue;
            ItemStackRenderState modelState = new ItemStackRenderState();
            DisplayModels.resolve(itemModelResolver, modelState, stack, model, ItemDisplayContext.FIXED, rack.getLevel(), seed + slot);
            ItemStackRenderState vintageState = null;
            Identifier vintage = DisplayModels.vintageOverlay(stack);
            if (vintage != null) {
                vintageState = new ItemStackRenderState();
                DisplayModels.resolve(itemModelResolver, vintageState, stack, vintage, ItemDisplayContext.FIXED, rack.getLevel(), seed + slot);
            }
            state.bottles.add(new Bottle(slot, stack.is(HHModTags.SHORT_BOTTLES), modelState, vintageState));
        }
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.rotate(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        pose.translate(-0.5F, -0.5F, -0.5F);

        for (Bottle bottle : state.bottles) {
            pose.pushPose();
            int row = bottle.slot() / 3, col = bottle.slot() % 3;
            double px = 1. / 16, spacing = px, slotSize = 4 * px;

            double baseX = spacing + col * (slotSize + spacing);
            double baseY = 1.0 - (spacing + slotSize + row * (slotSize + spacing));
            double baseZ = px;

            pose.translate(baseX + 2 * px, baseY + 2 * px, baseZ + 9 * px + (bottle.shortBottle() ? 3 * px : 0));
            pose.rotate(Axis.XP.rotationDegrees(90));
            bottle.model().submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            if (bottle.vintage() != null) bottle.vintage().submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            pose.popPose();
        }
        pose.popPose();
    }
}
