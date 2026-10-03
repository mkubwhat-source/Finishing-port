package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.item.TrellisBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.api.distmarker.Dist;

import java.util.Optional;

@EventBusSubscriber(modid = HearthAndHarvest.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TrellisGhostRenderer {

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !(player.level() instanceof ClientLevel clientLevel)) return;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof TrellisBlockItem trellisItem)) return;
        if (!Config.TRELLIS_PLACEMENT_PREVIEW.get()) return;

        BlockHitResult hit = event.getTarget();
        UseOnContext ctx = new UseOnContext(clientLevel, player, InteractionHand.MAIN_HAND, held, hit);

        Optional<TrellisBlockItem.PlacementResult> result = trellisItem.simulatePlacement(ctx);
        if (result.isEmpty()) return;

        BlockPos ghostPos = result.get().pos();
        BlockState ghostState = result.get().state();
        if (ghostState.isAir()) return;

        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();

        poseStack.pushPose();
        poseStack.translate(
                ghostPos.getX() - camPos.x,
                ghostPos.getY() - camPos.y,
                ghostPos.getZ() - camPos.z
        );

        GhostMultiBufferSource ghostBuffers = new GhostMultiBufferSource(bufferSource, 0.4f);
        mc.getBlockRenderer().renderSingleBlock(
                ghostState, poseStack, ghostBuffers,
                0xF000F0, OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    private static class GhostMultiBufferSource implements MultiBufferSource {
        private final MultiBufferSource delegate;
        private final float alpha;

        GhostMultiBufferSource(MultiBufferSource delegate, float alpha) {
            this.delegate = delegate;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            return new GhostVertexConsumer(delegate.getBuffer(RenderType.translucent()), alpha);
        }
    }

    private static class GhostVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final int ghostAlpha;

        GhostVertexConsumer(VertexConsumer delegate, float alpha) {
            this.delegate = delegate;
            this.ghostAlpha = (int) (alpha * 255);
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            delegate.setColor(r, g, b, ghostAlpha);
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            delegate.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            delegate.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float nx, float ny, float nz) {
            delegate.setNormal(nx, ny, nz);
            return this;
        }
    }
}