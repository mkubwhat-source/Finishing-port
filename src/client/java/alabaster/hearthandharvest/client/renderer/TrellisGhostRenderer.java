package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.client.render.HHRenderUtil;
import alabaster.hearthandharvest.common.item.TrellisBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Translucent preview of the trellis piece the held trellis would place (config
 * "trellisPlacementPreview"). 1.21.1 drew it in NeoForge's RenderHighlightEvent.Block through a 40%
 * alpha buffer wrapper; 26.3 splits frames into extraction and submission, so the placement is
 * simulated when the block outline is extracted and the model is submitted as translucent geometry.
 */
public final class TrellisGhostRenderer {
    private static final int GHOST_COLOR = 0x66FFFFFF; // 40% alpha, as 1.21.1
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Direction[] FACES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null};

    private static @Nullable BlockPos ghostPos;
    private static @Nullable BlockState ghostState;

    private TrellisGhostRenderer() {}

    public static void register() {
        LevelRenderEvents.AFTER_BLOCK_OUTLINE_EXTRACTION.register(TrellisGhostRenderer::extract);
        LevelRenderEvents.COLLECT_SUBMITS.register(TrellisGhostRenderer::submit);
    }

    private static void extract(LevelExtractionContext context, HitResult hitResult) {
        ghostPos = null;
        ghostState = null;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !(hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof TrellisBlockItem trellisItem)) return;
        if (!Config.TRELLIS_PLACEMENT_PREVIEW.get()) return;

        UseOnContext ctx = new UseOnContext(context.level(), player, InteractionHand.MAIN_HAND, held, hit);
        Optional<TrellisBlockItem.PlacementResult> result = trellisItem.simulatePlacement(ctx);
        if (result.isEmpty() || result.get().state().isAir()) return;
        ghostPos = result.get().pos();
        ghostState = result.get().state();
    }

    private static void submit(LevelRenderContext context) {
        BlockPos pos = ghostPos;
        BlockState state = ghostState;
        if (pos == null || state == null) return;
        Vec3 cam = context.levelState().cameraRenderState.pos;

        List<BlockStateModelPart> parts = new ArrayList<>();
        RANDOM.setSeed(42L);
        HHRenderUtil.blockModel(state).collectParts(RANDOM, parts);
        if (parts.isEmpty()) return;

        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
        QuadInstance instance = new QuadInstance();
        instance.setColor(GHOST_COLOR);
        instance.setLightCoords(0xF000F0);
        instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
        context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, consumer) -> {
            for (BlockStateModelPart part : parts) {
                for (Direction face : FACES) {
                    for (BakedQuad quad : part.getQuads(face)) {
                        consumer.putBakedQuad(pose, quad, instance);
                    }
                }
            }
        });
        poseStack.popPose();
    }
}
