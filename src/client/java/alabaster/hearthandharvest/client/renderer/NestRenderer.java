package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.common.block.entity.NestBlockEntity;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
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

/** Eggs (display model) or other items lying in a nest (26.3 render-state renderer). */
public class NestRenderer implements BlockEntityRenderer<NestBlockEntity, NestRenderer.State> {
    private static final Identifier EGG_MODEL = Identifier.withDefaultNamespace("display/egg");

    private static final float CORNER = 0.12F;
    private static final float[][] CORNERS = {
            {CORNER, CORNER},
            {CORNER, -CORNER},
            {-CORNER, -CORNER},
            {-CORNER, CORNER}
    };

    private static final float EGG_SCALE = 0.7F;
    private static final float EGG_HEIGHT = 1.0F / 16.0F;
    private static final float ITEM_SCALE = 0.5F;
    private static final float ITEM_HEIGHT = 1.0F / 16.0F;

    public record Entry(int slot, boolean egg, ItemStackRenderState model) {}

    public static class State extends BlockEntityRenderState {
        final List<Entry> entries = new ArrayList<>();
    }

    private final ItemModelResolver itemModelResolver;

    public NestRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(NestBlockEntity nest, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(nest, state, partialTick, cameraPosition, breakProgress);
        state.entries.clear();
        for (int slot = 0; slot < NestBlockEntity.SLOTS; slot++) {
            ItemStack stack = nest.getInventory().getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Identifier egg = stack.is(ConventionalItemTags.EGGS) ? DisplayModels.get(EGG_MODEL) : null;
            ItemStackRenderState model = new ItemStackRenderState();
            if (egg != null) {
                DisplayModels.resolve(itemModelResolver, model, stack, egg, ItemDisplayContext.FIXED, nest.getLevel(), slot * 1013);
            } else {
                itemModelResolver.updateForTopItem(model, stack, ItemDisplayContext.FIXED, nest.getLevel(), null, slot * 1013);
            }
            state.entries.add(new Entry(slot, egg != null, model));
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (Entry entry : state.entries) {
            float[] corner = CORNERS[entry.slot()];
            poseStack.pushPose();
            poseStack.translate(0.5F + corner[0], entry.egg() ? EGG_HEIGHT : ITEM_HEIGHT, 0.5F + corner[1]);
            poseStack.rotate(Axis.YP.rotationDegrees(15.0F + (90.0F * entry.slot())));
            if (entry.egg()) {
                poseStack.scale(EGG_SCALE, EGG_SCALE, EGG_SCALE);
                poseStack.translate(0.0F, 0.5F, 0.0F);
            } else {
                poseStack.rotate(Axis.XP.rotationDegrees(75.0F));
                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
                poseStack.translate(-0.2F, 0.15F, -0.2F);
            }
            entry.model().submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
