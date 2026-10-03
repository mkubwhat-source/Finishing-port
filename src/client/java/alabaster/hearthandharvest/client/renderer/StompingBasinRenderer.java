package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.client.render.HHExtraModels;
import alabaster.hearthandharvest.client.render.HHRenderUtil;
import alabaster.hearthandharvest.common.block.MultiblockPart;
import alabaster.hearthandharvest.common.block.entity.StompingBasinBlockEntity;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Stomping basin contents (items scattered on the floor, juice surface) and, for a 2x2 multiblock,
 * the big basin's quarter models (26.3 render-state renderer).
 */
public class StompingBasinRenderer implements BlockEntityRenderer<StompingBasinBlockEntity, StompingBasinRenderer.State> {

    private static final float INNER_MIN = 2f / 16f;
    private static final float INNER_MAX = 14f / 16f;
    private static final float SCATTER_MIN = 7f / 16f;
    private static final float SCATTER_MAX = 9f / 16f;
    private static final float SCATTER_SIZE = SCATTER_MAX - SCATTER_MIN;

    private static final float BIG_INNER_MIN = 2f / 16f;
    private static final float BIG_INNER_MAX = 30f / 16f;

    private static final float BIG_SCATTER_MIN = 5f / 16f;
    private static final float BIG_SCATTER_MAX = 27f / 16f;
    private static final float BIG_SCATTER_SIZE = BIG_SCATTER_MAX - BIG_SCATTER_MIN;

    private static final float FLOOR_Y = 1f / 16f + 0.002f;
    private static final float FLUID_MIN_Y = 1f / 16f + 0.01f;
    private static final float FLUID_MAX_Y = 11f / 16f;
    private static final float ITEM_Y_STEP = 0.001f;
    private static final int MAX_RENDERED_PER_SLOT = 64;

    private static final Identifier MODEL_NW = HearthAndHarvest.id("block/big_stomping_basin_nw");
    private static final Identifier MODEL_NE = HearthAndHarvest.id("block/big_stomping_basin_ne");
    private static final Identifier MODEL_SW = HearthAndHarvest.id("block/big_stomping_basin_sw");
    private static final Identifier MODEL_SE = HearthAndHarvest.id("block/big_stomping_basin_se");

    public record ItemEntry(ItemStackRenderState model, float x, float y, float z, float rotation, int light) {}

    public static class State extends BlockEntityRenderState {
        @Nullable Identifier quadrantModel;
        boolean combined;
        final List<ItemEntry> items = new ArrayList<>();
        @Nullable Fluid fluid;
        float fill;
        int color;
        final int[] quadrantLight = new int[4];
    }

    private final ItemModelResolver itemModelResolver;

    public StompingBasinRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        // A combined basin spans 2x2 blocks from its controller.
        return true;
    }

    @Override
    public void extractRenderState(StompingBasinBlockEntity be, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
        state.items.clear();
        state.fluid = null;
        state.quadrantModel = null;
        MultiblockPart role = be.getMultiblockRole();
        if (role == MultiblockPart.MEMBER) {
            state.quadrantModel = resolveQuadrantModel(be);
            return;
        }
        state.combined = role == MultiblockPart.CONTROLLER;
        if (state.combined) state.quadrantModel = MODEL_NW;

        Level level = be.getLevel();
        state.quadrantLight[0] = state.combined ? quadrantLight(be, 0, 0) : state.lightCoords;
        state.quadrantLight[1] = state.combined ? quadrantLight(be, 1, 0) : state.lightCoords;
        state.quadrantLight[2] = state.combined ? quadrantLight(be, 0, 1) : state.lightCoords;
        state.quadrantLight[3] = state.combined ? quadrantLight(be, 1, 1) : state.lightCoords;

        // Items scattered on the floor.
        float scatterMin = state.combined ? BIG_SCATTER_MIN : SCATTER_MIN;
        float scatterSize = state.combined ? BIG_SCATTER_SIZE : SCATTER_SIZE;
        long seed = be.getBlockPos().asLong();
        int renderIndex = 0;
        for (int slot = 0; slot < be.getItemHandler().getSlotCount(); slot++) {
            ItemStack stack = be.getItemHandler().getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            ItemStackRenderState model = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(model, stack, ItemDisplayContext.GROUND, level, null, (int) seed + slot);
            int rendered = Math.min(stack.getCount(), MAX_RENDERED_PER_SLOT);
            for (int i = 0; i < rendered; i++, renderIndex++) {
                float[] pos = itemPosition(seed, renderIndex);
                float x = scatterMin + pos[0] * scatterSize;
                float z = scatterMin + pos[1] * scatterSize;
                int light = z >= 1f ? (x >= 1f ? state.quadrantLight[3] : state.quadrantLight[2]) : (x >= 1f ? state.quadrantLight[1] : state.quadrantLight[0]);
                state.items.add(new ItemEntry(model, x, FLOOR_Y + renderIndex * ITEM_Y_STEP, z, pos[2] * 360f, light));
            }
        }

        // Juice surface.
        FluidStack fluid = be.getFluidTank().getFluid();
        if (!fluid.isEmpty()) {
            state.fluid = fluid.getFluid();
            state.fill = (float) be.getFluidTank().getFluidAmount() / be.getFluidTank().getCapacity();
            int color = HHRenderUtil.fluidColor(state.fluid, level, be.getBlockPos());
            state.color = (color & 0xFFFFFF) | 0xBF000000;
        }
    }

    private static Identifier resolveQuadrantModel(StompingBasinBlockEntity be) {
        BlockPos controllerPos = be.getControllerPos();
        if (controllerPos == null) return MODEL_NW;
        BlockPos pos = be.getBlockPos();
        int dx = pos.getX() - controllerPos.getX();
        int dz = pos.getZ() - controllerPos.getZ();
        if (dx == 1 && dz == 0) return MODEL_NE;
        if (dx == 0 && dz == 1) return MODEL_SW;
        if (dx == 1 && dz == 1) return MODEL_SE;
        return MODEL_NW;
    }

    private static int quadrantLight(StompingBasinBlockEntity controller, int dx, int dz) {
        Level level = controller.getLevel();
        if (level == null) return 0;
        return LightCoordsUtil.getLightCoords(level, controller.getBlockPos().offset(dx, 0, dz));
    }

    @Override
    public void submit(State state, PoseStack ps, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.quadrantModel != null) {
            HHRenderUtil.submitModel(ps, collector, HHExtraModels.get(state.quadrantModel), state.lightCoords, false);
        }
        for (ItemEntry item : state.items) {
            ps.pushPose();
            ps.translate(item.x(), item.y(), item.z());
            ps.rotate(Axis.YP.rotationDegrees(item.rotation()));
            ps.rotate(Axis.XP.rotationDegrees(-90f));
            item.model().submit(ps, collector, item.light(), OverlayTexture.NO_OVERLAY, 0);
            ps.popPose();
        }
        if (state.fluid != null && state.fill > 0f) {
            float surfaceY = FLUID_MIN_Y + state.fill * (FLUID_MAX_Y - FLUID_MIN_Y);
            if (state.combined) {
                float mid = 1f;
                HHRenderUtil.submitFluidSurface(ps, collector, state.fluid, state.color, BIG_INNER_MIN, BIG_INNER_MIN, mid, mid, surfaceY, state.quadrantLight[0]);
                HHRenderUtil.submitFluidSurface(ps, collector, state.fluid, state.color, mid, BIG_INNER_MIN, BIG_INNER_MAX, mid, surfaceY, state.quadrantLight[1]);
                HHRenderUtil.submitFluidSurface(ps, collector, state.fluid, state.color, BIG_INNER_MIN, mid, mid, BIG_INNER_MAX, surfaceY, state.quadrantLight[2]);
                HHRenderUtil.submitFluidSurface(ps, collector, state.fluid, state.color, mid, mid, BIG_INNER_MAX, BIG_INNER_MAX, surfaceY, state.quadrantLight[3]);
            } else {
                HHRenderUtil.submitFluidSurface(ps, collector, state.fluid, state.color, INNER_MIN, INNER_MIN, INNER_MAX, INNER_MAX, surfaceY, state.lightCoords);
            }
        }
    }

    private static float[] itemPosition(long worldSeed, int index) {
        long r = worldSeed ^ (index * 0x9e3779b97f4a7c15L);
        r = r * 0x6c62272e07bb0142L + 0x62b821756295c58dL;
        float px = ((r >>> 33) & 0xFFFFL) / 65535f;
        r = r * 0x6c62272e07bb0142L + 0x62b821756295c58dL;
        float pz = ((r >>> 33) & 0xFFFFL) / 65535f;
        r = r * 0x6c62272e07bb0142L + 0x62b821756295c58dL;
        float rot = ((r >>> 33) & 0xFFFFL) / 65535f;
        return new float[]{ px, pz, rot };
    }

}
