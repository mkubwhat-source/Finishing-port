package alabaster.hearthandharvest.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Hearth and Harvest client rendering helpers for 26.3's submit-based block entity renderers:
 * fluid surfaces inside blocks (1.21.1 used NeoForge's IClientFluidTypeExtensions + a buffer) and
 * whole block models (1.21.1 used BlockRenderDispatcher#renderSingleBlock / standalone models).
 */
public final class HHRenderUtil {
    private static final RandomSource RANDOM = RandomSource.create();

    private HHRenderUtil() {}

    /** The fluid's still texture, from the fluid model registered for it (vanilla or FluidRenderingRegistry). */
    public static Material.Baked stillMaterial(Fluid fluid) {
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
        return model.stillMaterial();
    }

    /** ARGB colour for a fluid at a position: biome water colour for water, the fluid's render colour otherwise. */
    public static int fluidColor(Fluid fluid, @Nullable Level level, @Nullable BlockPos pos) {
        BlockAndTintGetter tint = level instanceof BlockAndTintGetter t && pos != null ? t : null;
        if (fluid.is(FluidTags.WATER)) {
            return 0xFF000000 | (tint != null ? BiomeColors.getAverageWaterColor(tint, pos) : 0x3F76E4);
        }
        int color = tint != null
                ? FluidVariantRendering.getColor(FluidVariant.of(fluid), tint, pos)
                : FluidVariantRendering.getColor(FluidVariant.of(fluid));
        return (color >>> 24) == 0 ? 0xFF000000 | color : color;
    }

    /** A horizontal fluid surface at height {@code y}, spanning [x0,x1] x [z0,z1] in block units (0..1), facing up. */
    public static void submitFluidSurface(PoseStack poseStack, SubmitNodeCollector collector, Fluid fluid, int color,
                                          float x0, float z0, float x1, float z1, float y, int light) {
        Material.Baked material = stillMaterial(fluid);
        // The texture starts at the quad's corner and spans its size (at most one block).
        float u0 = material.sprite().getU(0), u1 = material.sprite().getU(Math.min(1.0F, x1 - x0));
        float v0 = material.sprite().getV(0), v1 = material.sprite().getV(Math.min(1.0F, z1 - z0));
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, consumer) -> {
            vertex(consumer, pose, x0, y, z0, color, u0, v0, light);
            vertex(consumer, pose, x0, y, z1, color, u0, v1, light);
            vertex(consumer, pose, x1, y, z1, color, u1, v1, light);
            vertex(consumer, pose, x1, y, z0, color, u1, v0, light);
        });
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int color, float u, float v, int light) {
        consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }

    /** Submits a baked block-state model (cutout, or translucent when asked) at the current pose. */
    public static void submitModel(PoseStack poseStack, SubmitNodeCollector collector, @Nullable BlockStateModel model, int light, boolean translucent) {
        if (model == null) return;
        List<BlockStateModelPart> parts = new ArrayList<>();
        RANDOM.setSeed(42L);
        model.collectParts(RANDOM, parts);
        if (parts.isEmpty()) return;
        collector.submitBlockModel(poseStack, translucent ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet(),
                parts, BlockModelRenderState.EMPTY_TINTS, light, OverlayTexture.NO_OVERLAY, 0);
    }

    /** The model of a block state. */
    public static BlockStateModel blockModel(net.minecraft.world.level.block.state.BlockState state) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
    }
}
