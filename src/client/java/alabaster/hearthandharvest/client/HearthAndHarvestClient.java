package alabaster.hearthandharvest.client;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.client.entity.crow.CrowModel;
import alabaster.hearthandharvest.client.entity.crow.CrowOnShoulderLayer;
import alabaster.hearthandharvest.client.entity.crow.CrowRenderer;
import alabaster.hearthandharvest.client.fd.gui.CookingPotScreen;
import alabaster.hearthandharvest.client.fd.gui.CookingPotTooltip;
import alabaster.hearthandharvest.client.fd.particle.SparkleParticle;
import alabaster.hearthandharvest.client.fd.particle.StarParticle;
import alabaster.hearthandharvest.client.fd.particle.SteamParticle;
import alabaster.hearthandharvest.client.fd.renderer.CuttingBoardRenderer;
import alabaster.hearthandharvest.client.gui.CaskGUI;
import alabaster.hearthandharvest.client.gui.KegGUI;
import alabaster.hearthandharvest.client.model.ThrownPitchforkModel;
import alabaster.hearthandharvest.client.particle.DrippingSapParticle;
import alabaster.hearthandharvest.client.particle.FeatherParticle;
import alabaster.hearthandharvest.client.particle.FliesParticle;
import alabaster.hearthandharvest.client.render.HHExtraModels;
import alabaster.hearthandharvest.client.renderer.*;
import alabaster.hearthandharvest.common.fd.item.CookingPotTooltipComponent;
import alabaster.hearthandharvest.common.fd.network.RecipeBookValuesPayload;
import alabaster.hearthandharvest.common.fd.network.RichSoilBoostParticlesPayload;
import alabaster.hearthandharvest.common.item.component.SeedPouchContents;
import alabaster.hearthandharvest.common.network.PlayerPoopCooldownPacket;
import alabaster.hearthandharvest.common.network.PlayerPoopPacket;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.platform.util.BlockEntityItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ExtractItemDecorationsCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;

/**
 * Hearth and Harvest client setup (1.21.1: NeoForge ClientEventHandler, ChickenGlideClientEvents,
 * SaltedFoodTooltipHandler and FarmersDelight's ClientSetupEvents).
 * <ul>
 *   <li>The trellis block colour handler is gone with HH's trellises (grapes use BF's trellis).</li>
 *   <li>Crate and pitchfork item renderers (BEWLR) are gone: the crate item is a plain item model and
 *       the pitchfork uses its plain item model (no trident-style rendering, per the merge plan).</li>
 *   <li>The "vintage" item property is a {@code minecraft:select} on the {@code hearthandharvest:vintage}
 *       component in the item definitions, so it needs no code.</li>
 *   <li>Recipe book categories are registered server-side (HHRecipeBookCategories) and shown by the
 *       recipe book components.</li>
 *   <li>Chicken gliding and shoulder crows are render-state mixins (client mixins json).</li>
 * </ul>
 */
public class HearthAndHarvestClient implements ClientModInitializer {
    private static final int POOP_COOLDOWN_TICKS = 300;
    private static int poopCooldownTicks = 0;

    @Override
    public void onInitializeClient() {
        registerRenderers();
        registerParticles();
        registerFluids();
        registerScreens();
        registerNetworking();
        registerTooltips();
        registerKeys();
        HHExtraModels.register();

        ExtractItemDecorationsCallback.EVENT.register((gui, font, stack, x, y) -> {
            if (!stack.is(HHModItems.SEED_POUCH.get())) return;
            SeedPouchContents contents = stack.get(HHModDataComponents.SEED_POUCH_CONTENTS.get());
            if (contents == null || contents.count() == 0) return;
            gui.pose().pushMatrix();
            gui.pose().translate(x + 8, y + 8);
            gui.pose().scale(0.5F, 0.5F);
            gui.fakeItem(new ItemStack(contents.seedType()), 0, 0);
            gui.pose().popMatrix();
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> BlockEntityItems.setCurrentRegistries(handler.registryAccess()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BlockEntityItems.setCurrentRegistries(null));

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return HearthAndHarvest.id("display_models");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                DisplayModels.clearCache();
            }
        });
    }

    private static void registerRenderers() {
        BlockEntityRenderers.register(HHModBlockEntities.NEST.get(), NestRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.BOTTLE_RACK.get(), BottleRackRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.CRATE.get(), CrateRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.STOMPING_BASIN.get(), StompingBasinRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.JAR.get(), JarRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.TROUGH.get(), TroughRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.TREE_TAPPER.get(), TreeTapperRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.BASIN.get(), BasinRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.SPRINKLER.get(), SprinklerRenderer::new);
        BlockEntityRenderers.register(HHModBlockEntities.CUTTING_BOARD.get(), CuttingBoardRenderer::new);

        EntityRenderers.register(HHModEntities.CROW.get(), CrowRenderer::new);
        EntityRenderers.register(HHModEntities.MANURE_PROJECTILE.get(), ThrownItemRenderer::new);
        EntityRenderers.register(HHModEntities.THROWN_PITCHFORK.get(), ThrownPitchforkRenderer::new);
        EntityRenderers.register(HHModEntities.THROWN_CLEAVER.get(), ThrownCleaverRenderer::new);
        EntityRenderers.register(HHModEntities.THROWN_HORSESHOE.get(), ThrownHorseshoeRenderer::new);

        ModelLayerRegistry.registerModelLayer(CrowModel.LAYER_LOCATION, CrowModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ThrownPitchforkModel.LAYER_LOCATION, ThrownPitchforkModel::createBodyLayer);

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, renderer, helper, context) -> {
            if (renderer instanceof AvatarRenderer<?> avatarRenderer) {
                helper.register(new CrowOnShoulderLayer(avatarRenderer, context.getModelSet()));
            }
        });
    }

    private static void registerParticles() {
        ParticleProviderRegistry particles = ParticleProviderRegistry.getInstance();
        particles.register(HHModParticleTypes.DRIPPING_SAP.get(), DrippingSapParticle.Provider::new);
        particles.register(HHModParticleTypes.FLIES.get(), FliesParticle.Provider::new);
        particles.register(HHModParticleTypes.FEATHER.get(), FeatherParticle.Provider::new);
        particles.register(HHModParticleTypes.STAR.get(), StarParticle.Factory::new);
        particles.register(HHModParticleTypes.STEAM.get(), SteamParticle.Factory::new);
        particles.register(HHModParticleTypes.SPARKLE.get(), SparkleParticle.Factory::new);
    }

    /**
     * Fluid textures (1.21.1: IClientFluidTypeExtensions still/flowing textures). The fluid model is
     * what HH's block entity renderers and screens read the still sprite from. HH's "milk" fluid had
     * no texture of its own (NeoForge supplied one), so it uses goat milk's.
     */
    private static void registerFluids() {
        for (HHModFluids.FluidEntry entry : HHModFluids.ALL) {
            String texture = entry.name().equals("milk") ? "goat_milk" : entry.name();
            FluidRenderingRegistry.register(entry.source().get(), entry.flowing().get(), new FluidModel.Unbaked(
                    new Material(HearthAndHarvest.id("block/fluid/" + texture + "_still")),
                    new Material(HearthAndHarvest.id("block/fluid/" + texture + "_flow")),
                    null, null));
        }
    }

    private static void registerScreens() {
        MenuScreens.register(HHModMenuTypes.CASK_MENU.get(), CaskGUI::new);
        MenuScreens.register(HHModMenuTypes.KEG_MENU.get(), KegGUI::new);
        MenuScreens.register(HHModMenuTypes.COOKING_POT.get(), CookingPotScreen::new);
    }

    private static void registerNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerPoopCooldownPacket.TYPE, (payload, context) -> poopCooldownTicks = POOP_COOLDOWN_TICKS);
        ClientPlayNetworking.registerGlobalReceiver(HHDataMaps.SyncPayload.TYPE, (payload, context) -> HHDataMaps.applySync(payload));
        ClientPlayNetworking.registerGlobalReceiver(RecipeBookValuesPayload.TYPE, (payload, context) -> payload.applyTo(context.player().getRecipeBook()));
        ClientPlayNetworking.registerGlobalReceiver(RichSoilBoostParticlesPayload.TYPE, (payload, context) ->
                BoneMealItem.addGrowthParticles(context.player().level(), payload.pos(), 15));
    }

    private static void registerTooltips() {
        ClientTooltipComponentCallback.EVENT.register(data -> data instanceof CookingPotTooltipComponent component ? new CookingPotTooltip(component) : null);
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (stack.has(HHModDataComponents.SALTED.get())) {
                lines.add(Math.min(1, lines.size()), Component.translatable("tooltip.hearthandharvest.salted").withStyle(ChatFormatting.GRAY));
            }
        });
    }

    private static void registerKeys() {
        KeyMappingHelper.registerKeyMapping(HHKeyBindings.POOP);
        ClientTickEvents.END_CLIENT_TICK.register(HearthAndHarvestClient::tickPoop);
    }

    private static void tickPoop(Minecraft mc) {
        boolean pressed = false;
        while (HHKeyBindings.POOP.consumeClick()) {
            pressed = true;
        }
        if (pressed && mc.hasShiftDown() && poopCooldownTicks <= 0 && mc.player != null) {
            ClientPlayNetworking.send(new PlayerPoopPacket());
            poopCooldownTicks = POOP_COOLDOWN_TICKS; // optimistic; the server's cooldown packet reaffirms it
        }
        if (poopCooldownTicks <= 0) return;
        poopCooldownTicks--;
        if (poopCooldownTicks % 4 != 0) return;
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;
        for (int i = 0; i < 2; i++) {
            double ox = (level.getRandom().nextDouble() - 0.5) * 1.5;
            double oy = level.getRandom().nextDouble() * player.getBbHeight();
            double oz = (level.getRandom().nextDouble() - 0.5) * 1.5;
            level.addParticle(HHModParticleTypes.FLIES.get(), player.getX() + ox, player.getY() + oy, player.getZ() + oz, 0, 0, 0);
        }
    }
}
