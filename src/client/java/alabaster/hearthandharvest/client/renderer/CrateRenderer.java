package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.common.block.CrateBlock;
import alabaster.hearthandharvest.common.block.entity.CrateBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Items stood upright in a crate, drawn with their display models (26.3 render-state renderer). */
public class CrateRenderer implements BlockEntityRenderer<CrateBlockEntity, CrateRenderer.State> {

    private static final double PX = 1.0 / 16.0;
    private static final double SPACING = PX;
    private static final double SLOT_SIZE = 4 * PX;

    public record Entry(int slot, double surfaceY, ItemStackRenderState model, @Nullable ItemStackRenderState vintage) {}

    public static class State extends BlockEntityRenderState {
        float yRot;
        final List<Entry> entries = new ArrayList<>();
    }

    private final ItemModelResolver itemModelResolver;

    public CrateRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CrateBlockEntity crate, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(crate, state, partialTick, cameraPosition, breakProgress);
        BlockState blockState = crate.getBlockState();
        state.yRot = switch (blockState.getValue(CrateBlock.FACING)) {
            case EAST -> -90f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            default -> 0f;
        };
        state.entries.clear();
        SlabType type = blockState.getValue(CrateBlock.TYPE);
        if (type == SlabType.BOTTOM || type == SlabType.DOUBLE) addHalf(crate, state, 0, 0.5);
        if (type == SlabType.DOUBLE) addHalf(crate, state, CrateBlockEntity.SLOTS_PER_HALF, 1.0);
        if (type == SlabType.TOP) addHalf(crate, state, 0, 1.0);
    }

    private void addHalf(CrateBlockEntity crate, State state, int slotOffset, double surfaceY) {
        int seed = (int) crate.getBlockPos().asLong();
        for (int i = 0; i < CrateBlockEntity.SLOTS_PER_HALF; i++) {
            Entry entry = entry(itemModelResolver, crate.getItem(slotOffset + i), i, surfaceY, crate.getLevel(), seed + slotOffset + i);
            if (entry != null) state.entries.add(entry);
        }
    }

    /** An item stood in crate slot {@code slot} (0..8 of a half), or null when it has no display model. */
    public static @Nullable Entry entry(ItemModelResolver resolver, ItemStack stack, int slot, double surfaceY, @Nullable Level level, int seed) {
        Identifier model = DisplayModels.get(stack);
        if (model == null) return null;
        ItemStackRenderState modelState = new ItemStackRenderState();
        DisplayModels.resolve(resolver, modelState, stack, model, ItemDisplayContext.FIXED, level, seed);
        ItemStackRenderState vintageState = null;
        Identifier vintage = DisplayModels.vintageOverlay(stack);
        if (vintage != null) {
            vintageState = new ItemStackRenderState();
            DisplayModels.resolve(resolver, vintageState, stack, vintage, ItemDisplayContext.FIXED, level, seed);
        }
        return new Entry(slot, surfaceY, modelState, vintageState);
    }

    /** Submits an entry in crate (block) space, facing north. */
    public static void submitEntry(Entry entry, PoseStack pose, SubmitNodeCollector collector, int light) {
        int col = entry.slot() % 3;
        int row = entry.slot() / 3;
        pose.pushPose();
        pose.translate(SPACING + col * (SLOT_SIZE + SPACING) + SLOT_SIZE / 2.0, entry.surfaceY() + PX, SPACING + row * (SLOT_SIZE + SPACING) + SLOT_SIZE / 2.0);
        entry.model().submit(pose, collector, light, OverlayTexture.NO_OVERLAY, 0);
        if (entry.vintage() != null) entry.vintage().submit(pose, collector, light, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.translate(0.5, 0.0, 0.5);
        pose.rotate(Axis.YP.rotationDegrees(state.yRot));
        pose.translate(-0.5, 0.0, -0.5);
        for (Entry entry : state.entries) {
            submitEntry(entry, pose, collector, state.lightCoords);
        }
        pose.popPose();
    }
}
