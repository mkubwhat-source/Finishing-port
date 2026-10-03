package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.client.render.HHExtraModels;
import alabaster.hearthandharvest.common.block.JarBlock;
import alabaster.hearthandharvest.common.block.entity.JarBlockEntity;
import alabaster.hearthandharvest.common.item.JarBlockItem;
import alabaster.hearthandharvest.common.item.VintageHelper;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.item.Item;
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

/** The (up to four) jars on a jar block, each drawn as its display block plus any vintage overlay. */
public class JarRenderer implements BlockEntityRenderer<JarBlockEntity, JarRenderer.State> {

    private static final float[][] SLOT_OFFSETS = {
            { -4/16f, 0f, -4/16f },
            {  4/16f, 0f, -4/16f },
            { -4/16f, 0f,  4/16f },
            {  4/16f, 0f,  4/16f },
    };

    public record Entry(int slot, BlockStateModel model, @Nullable BlockStateModel vintage) {}

    public static class State extends BlockEntityRenderState {
        final List<Entry> entries = new ArrayList<>();
    }

    public JarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(JarBlockEntity be, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPosition, breakProgress);
        state.entries.clear();
        for (int i = 0; i < 4; i++) {
            if (!be.getBlockState().getValue(JarBlock.SLOTS[i])) continue;
            Item item = be.getSlot(i);
            if (!(item instanceof JarBlockItem jarItem)) continue;
            Identifier overlay = VintageHelper.overlayModel(be.getSlotStack(i));
            state.entries.add(new Entry(i, HHRenderUtil.blockModel(jarItem.getDisplayBlock().defaultBlockState()),
                    overlay != null ? HHExtraModels.get(overlay) : null));
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (Entry entry : state.entries) {
            float[] offset = SLOT_OFFSETS[entry.slot()];
            poseStack.pushPose();
            poseStack.translate(offset[0], offset[1], offset[2]);
            HHRenderUtil.submitModel(poseStack, collector, entry.model(), state.lightCoords, false);
            HHRenderUtil.submitModel(poseStack, collector, entry.vintage(), state.lightCoords, false);
            poseStack.popPose();
        }
    }
}
