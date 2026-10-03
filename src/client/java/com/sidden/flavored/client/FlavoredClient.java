package com.sidden.flavored.client;

import com.sidden.flavored.client.entity.ChockenModel;
import com.sidden.flavored.client.entity.ChockenRenderer;
import com.sidden.flavored.client.particle.ChocolateDripParticle;
import com.sidden.flavored.client.particle.FlavoredSimpleParticle;
import com.sidden.flavored.client.renderer.MixingBowlRenderer;
import com.sidden.flavored.client.renderer.ThrownHotSauceRenderer;
import com.sidden.flavored.client.screen.KegScreen;
import com.sidden.flavored.client.screen.MixingBowlScreen;
import com.sidden.flavored.client.screen.OvenScreen;
import com.sidden.flavored.registry.FlavoredBlockEntities;
import com.sidden.flavored.registry.FlavoredDataComponents;
import com.sidden.flavored.registry.FlavoredEntities;
import com.sidden.flavored.registry.FlavoredMenus;
import com.sidden.flavored.registry.FlavoredParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

/**
 * Flavored's client entrypoint (1.21.1: NeoForge client mod-bus events in Flavored and
 * FlavoredModBusEvents, the HUD layer and the tooltip game-bus event).
 */
public class FlavoredClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(FlavoredMenus.KEG.get(), KegScreen::new);
        MenuScreens.register(FlavoredMenus.MIXING_BOWL.get(), MixingBowlScreen::new);
        MenuScreens.register(FlavoredMenus.OVEN.get(), OvenScreen::new);

        ModelLayerRegistry.registerModelLayer(ChockenModel.LAYER_LOCATION, ChockenModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ChockenModel.BABY_LAYER_LOCATION, () -> ChockenModel.createBodyLayer().apply(ChockenModel.BABY_TRANSFORM));
        ModelLayerRegistry.registerModelLayer(MixingBowlRenderer.LAYER_LOCATION, MixingBowlRenderer::createBodyLayer);

        EntityRendererRegistry.register(FlavoredEntities.CHOCKEN.get(), ChockenRenderer::new);
        EntityRendererRegistry.register(FlavoredEntities.CHOCOLATE_EGG.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(FlavoredEntities.TOMATO.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(FlavoredEntities.HOT_SAUCE.get(), ThrownHotSauceRenderer::new);
        BlockEntityRenderers.register(FlavoredBlockEntities.MIXING_BOWL.get(), MixingBowlRenderer::new);

        ParticleProviderRegistry particles = ParticleProviderRegistry.getInstance();
        particles.register(FlavoredParticles.CHEESE_AGING.get(), FlavoredSimpleParticle::cheeseAging);
        particles.register(FlavoredParticles.POPCORN_POPS.get(), FlavoredSimpleParticle::popcornPops);
        particles.register(FlavoredParticles.FLAME_BUNCH.get(), FlavoredSimpleParticle::flameBunch);
        particles.register(FlavoredParticles.FERMENTATION_BUBBLES.get(), FlavoredSimpleParticle::fermentationBubbles);
        particles.register(FlavoredParticles.DRIPPING_CHOCOLATE.get(), ChocolateDripParticle::hang);
        particles.register(FlavoredParticles.FALLING_CHOCOLATE.get(), ChocolateDripParticle::fall);
        particles.register(FlavoredParticles.LANDING_CHOCOLATE.get(), ChocolateDripParticle::land);

        SugarCraveHud.register();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new MixingBowlLiquids());

        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            Integer spiciness = stack.get(FlavoredDataComponents.SPICINESS.get());
            if (spiciness != null && spiciness >= 1 && spiciness <= 3) {
                lines.add(Component.translatable("spiciness.flavored.level" + spiciness).withStyle(ChatFormatting.DARK_RED));
            }
        });
    }
}
