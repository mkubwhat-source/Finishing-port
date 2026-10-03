package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.client.render.HHRenderUtil;
import alabaster.hearthandharvest.common.block.entity.TroughBlockEntity;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Trough contents: the water surface, or the feed scattered in a pile (26.3 render-state renderer). */
public class TroughRenderer implements BlockEntityRenderer<TroughBlockEntity, TroughRenderer.State> {

    private static final float INNER_MIN = 2f / 16f;
    private static final float INNER_MAX = 14f / 16f;
    private static final float SCATTER_MIN = 7f / 16f;
    private static final float SCATTER_MAX = 9f / 16f;
    private static final float SCATTER_SIZE = SCATTER_MAX - SCATTER_MIN;

    private static final float FLOOR_Y = 3f / 16f + 0.002f;
    private static final float FLUID_MIN_Y = 3f / 16f + 0.002f;
    private static final float FLUID_MAX_Y = 7.4f / 16f;
    private static final float MAX_PILE_HEIGHT = 6f / 16f;

    public static class State extends BlockEntityRenderState {
        @Nullable Fluid fluid;
        float fill;
        int color;
        int itemCount;
        long seed;
        final ItemStackRenderState item = new ItemStackRenderState();
    }

    private final ItemModelResolver itemModelResolver;

    public TroughRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TroughBlockEntity be, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
        FluidStack fluid = be.getFluidTank().getFluid();
        ItemStack stack = be.getItemHandler().getStackInSlot(0);
        state.fluid = null;
        state.itemCount = 0;
        state.seed = be.getBlockPos().asLong();
        if (!fluid.isEmpty()) {
            state.fluid = fluid.getFluid();
            state.fill = (float) be.getFluidTank().getFluidAmount() / be.getFluidTank().getCapacity();
            int color = HHRenderUtil.fluidColor(state.fluid, be.getLevel(), be.getBlockPos());
            state.color = (color & 0xFFFFFF) | 0xBF000000;
        } else if (!stack.isEmpty()) {
            state.itemCount = stack.getCount();
            itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.GROUND, be.getLevel(), null, (int) state.seed);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.fluid != null) {
            if (state.fill <= 0f) return;
            float surfaceY = FLUID_MIN_Y + state.fill * (FLUID_MAX_Y - FLUID_MIN_Y);
            HHRenderUtil.submitFluidSurface(poseStack, collector, state.fluid, state.color, INNER_MIN, INNER_MIN, INNER_MAX, INNER_MAX, surfaceY, state.lightCoords);
            return;
        }
        float fillFrac = Math.min(state.itemCount / (float) TroughBlockEntity.ITEM_SLOT_LIMIT, 1f);
        for (int i = 0; i < state.itemCount; i++) {
            float[] pos = itemPosition(state.seed, i);
            poseStack.pushPose();
            poseStack.translate(SCATTER_MIN + pos[0] * SCATTER_SIZE, FLOOR_Y + pos[3] * fillFrac * MAX_PILE_HEIGHT, SCATTER_MIN + pos[1] * SCATTER_SIZE);
            poseStack.rotate(Axis.YP.rotationDegrees(pos[2] * 360f));
            poseStack.rotate(Axis.XP.rotationDegrees(-90f));
            state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
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
        r = r * 0x6c62272e07bb0142L + 0x62b821756295c58dL;
        float py = ((r >>> 33) & 0xFFFFL) / 65535f;
        return new float[]{ px, pz, rot, py };
    }

}
