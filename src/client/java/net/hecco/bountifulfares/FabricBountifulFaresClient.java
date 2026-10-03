package net.hecco.bountifulfares;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockTintsFactory;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.hecco.bountifulfares.definition.block.entity.renderer.*;
import net.hecco.bountifulfares.definition.block.entity.renderer.model.TrellisBlockEntityModel;
import net.hecco.bountifulfares.definition.particle.FermentedBubbleParticle;
import net.hecco.bountifulfares.definition.particle.FlourCloudParticle;
import net.hecco.bountifulfares.definition.particle.GoldenPetalParticle;
import net.hecco.bountifulfares.definition.particle.PrismarineBlossomParticle;
import net.hecco.bountifulfares.definition.screen.GristmillScreen;
import net.hecco.bountifulfares.registry.BFClientMessages;
import net.hecco.bountifulfares.registry.client.BFClientRecipes;
import net.hecco.bountifulfares.registry.content.BFBlockEntities;
import net.hecco.bountifulfares.registry.content.BFEntities;
import net.hecco.bountifulfares.registry.content.BFMenus;
import net.hecco.bountifulfares.registry.content.BFParticles;
import net.hecco.bountifulfares.registry.misc.BFModelLayers;
import net.hecco.bountifulfares.registry.util.BFTooltipEvents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Pair;

public class FabricBountifulFaresClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BFClientMessages.registerS2CPackets();
        BFClientRecipes.register();
        ItemTooltipCallback.EVENT.register(BFTooltipEvents::addTooltipsToVanillaItemsFabric);
        BountifulFaresClient.onInitializeClient();
        ParticleProviderRegistry.getInstance().register(BFParticles.PRISMARINE_BLOSSOM.get(), PrismarineBlossomParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(BFParticles.FERMENTED_BUBBLE.get(), FermentedBubbleParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(BFParticles.FLOUR_CLOUD.get(), FlourCloudParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(BFParticles.GOLDEN_PETAL.get(), GoldenPetalParticle.Factory::new);
        // 26.3 block-entity-renderer "render state" redesign (project checkpoint doc part B):
        // beds have no dedicated block-entity renderer in 26.3 at all (confirmed by extracting
        // vanilla's own red_bed.json/template_bed_head.json assets - beds render entirely via a
        // multipart static block model), so CoirBedRenderer was deleted rather than migrated; the
        // remaining three renderers below were migrated to the new
        // createRenderState()/extractRenderState()/submit(SubmitNodeCollector) shape.
        BlockEntityRenderers.register(BFBlockEntities.TRELLIS_BLOCK_ENTITY.get(), TrellisRenderer::new);
        BlockEntityRenderers.register(BFBlockEntities.CERAMIC_DISH_BLOCK_ENTITY.get(), CeramicDishRenderer::new);
        BlockEntityRenderers.register(BFBlockEntities.CERAMIC_CHEST_BLOCK_ENTITY.get(), CeramicChestRenderer::new);
        ModelLayerRegistry.registerModelLayer(BFModelLayers.TRELLIS_DEFAULT, TrellisBlockEntityModel::createDefaultLayer);
        ModelLayerRegistry.registerModelLayer(BFModelLayers.TRELLIS_INVERTED, TrellisBlockEntityModel::createInvertedLayer);
        ModelLayerRegistry.registerModelLayer(BFModelLayers.CERAMIC_CHEST, CeramicChestRenderer::createSingleBodyLayer);
        ModelLayerRegistry.registerModelLayer(BFModelLayers.CERAMIC_DOUBLE_CHEST_LEFT, CeramicChestRenderer::createDoubleBodyLeftLayer);
        ModelLayerRegistry.registerModelLayer(BFModelLayers.CERAMIC_DOUBLE_CHEST_RIGHT, CeramicChestRenderer::createDoubleBodyRightLayer);

        for (Pair<BlockTintsFactory, Block> pair : BountifulFaresClient.blockColors) {
            BlockColorRegistry.register(pair.getA(), pair.getB());
        }

        EntityRendererRegistry.register(BFEntities.THROWN_FLOUR_PROJECTILE.get(), ThrownItemRenderer::new);
        MenuScreens.register(BFMenus.GRISTMILL_SCREEN_HANDLER.get(), GristmillScreen::new);
    }
}
