package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.client.render.HHRenderUtil;
import alabaster.hearthandharvest.common.block.entity.SprinklerBlockEntity;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Draws the fluid surface inside the block (26.3 render-state renderer). */
public class SprinklerRenderer implements BlockEntityRenderer<SprinklerBlockEntity, SprinklerRenderer.State> {
    private static final float MIN_X = 3f / 16f;
    private static final float MAX_X = 13f / 16f;
    private static final float MIN_Z = 3f / 16f;
    private static final float MAX_Z = 13f / 16f;
    private static final float FLUID_BOTTOM = 1f / 16f;
    private static final float FLUID_TOP = 7f / 16f;

    public static class State extends BlockEntityRenderState {
        @Nullable Fluid fluid;
        float fill;
        int color;
        Direction facing = Direction.SOUTH;
    }

    public SprinklerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SprinklerBlockEntity be, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
        FluidStack fluid = be.tank.getFluid();
        state.fluid = fluid.isEmpty() ? null : fluid.getFluid();
        state.fill = fluid.isEmpty() ? 0 : Math.min(1.0F, (float) fluid.getAmount() / (SprinklerBlockEntity.CAPACITY));
        if (state.fluid != null) {
            int color = HHRenderUtil.fluidColor(state.fluid, be.getLevel(), be.getBlockPos());
            state.color = (color & 0xFFFFFF) | 0xCC000000;
        }
        state.facing = be.getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.SOUTH;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.fluid == null || state.fill <= 0) return;
        float surfaceY = FLUID_BOTTOM + state.fill * (FLUID_TOP - FLUID_BOTTOM);
        poseStack.pushPose();
        HHRenderUtil.submitFluidSurface(poseStack, collector, state.fluid, state.color, MIN_X, MIN_Z, MAX_X, MAX_Z, surfaceY, state.lightCoords);
        poseStack.popPose();
    }
}
