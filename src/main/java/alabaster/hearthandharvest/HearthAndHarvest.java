package alabaster.hearthandharvest;

import alabaster.hearthandharvest.common.block.SaltBlock;
import alabaster.hearthandharvest.common.block.StompingBasinBlock;
import alabaster.hearthandharvest.common.crafting.VintageIngredient;
import alabaster.hearthandharvest.common.entity.crow.CrowEntity;
import alabaster.hearthandharvest.common.entity.crow.CrowSpawnRules;
import alabaster.hearthandharvest.common.entity.goal.FoxEatBushBerriesGoal;
import alabaster.hearthandharvest.common.entity.goal.PungentEffectGoal;
import alabaster.hearthandharvest.common.entity.goal.SeekNestGoal;
import alabaster.hearthandharvest.common.entity.goal.TemptingEffectGoal;
import alabaster.hearthandharvest.common.event.*;
import alabaster.hearthandharvest.common.fd.block.CuttingBoardBlock;
import alabaster.hearthandharvest.common.fd.block.RichSoilBlock;
import alabaster.hearthandharvest.common.fd.block.entity.CabinetBlockEntity;
import alabaster.hearthandharvest.common.fd.block.entity.CookingPotBlockEntity;
import alabaster.hearthandharvest.common.fd.block.entity.CuttingBoardBlockEntity;
import alabaster.hearthandharvest.common.fd.crafting.ingredient.ItemAbilityIngredient;
import alabaster.hearthandharvest.common.fd.refabricated.CanItemPerformAbility;
import alabaster.hearthandharvest.common.item.CrateBlockItem;
import alabaster.hearthandharvest.common.item.SeedPouchItem;
import alabaster.hearthandharvest.common.network.HHModNetworking;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.worldgen.HHBiomeModifiers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hearth and Harvest (by AlabasterLeking), bundled into the Bountiful Fares 26.3 Fabric jar.
 * <p>
 * 1.21.1 was a NeoForge addon for Farmer's Delight; here it is standalone: the Farmer's Delight
 * pieces it used (cooking pot, cutting board, cabinets, straw, ...) are part of it
 * ({@code alabaster.hearthandharvest.common.fd}, from FarmersDelightRefabricated, MIT). It keeps its
 * {@code hearthandharvest} ids, assets and tabs, and initializes after Bountiful Fares and Flavored
 * (fabric.mod.json order) because some HH items are now those mods' items (see HHRegistryAliases).
 */
public class HearthAndHarvest implements ModInitializer {
    public static final String MODID = "hearthandharvest";
    public static final Logger LOGGER = LogManager.getLogger();

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Override
    public void onInitialize() {
        Config.COMMON_CONFIG.load("hearthandharvest-common.toml");

        // Registries (1.21.1 DeferredRegister order, with the ported FD entries alongside)
        HHModSounds.init();
        HHModEffects.init();
        HHModPotions.init();
        HHModDataComponents.init();
        HHModConsumeEffects.init();
        HHModParticleTypes.init();
        HHModBlocks.init();
        HHModItems.init();
        HHModFluids.init();
        HHModBlockEntities.init();
        HHModEntities.init();
        HHModMenuTypes.init();
        HHModRecipeTypes.init();
        HHModRecipeSerializers.init();
        HHRecipeBookCategories.init();
        VintageIngredient.init();
        alabaster.hearthandharvest.common.crafting.HHRecipeDisplays.init();
        ItemAbilityIngredient.touch();
        CanItemPerformAbility.init();
        HHModStructurePieces.init();
        HHModStructures.init();
        HHModFeatures.init();
        HHModCreativeTabs.init();
        HHModTriggers.init();
        HHModAttachments.init();
        HHRegistryAliases.init();
        HHModNetworking.register();

        FabricDefaultAttributeRegistry.register(HHModEntities.CROW.get(), CrowEntity.createAttributes());
        SpawnPlacements.register(HHModEntities.CROW.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrowSpawnRules::canSpawnCrow);
        HHBiomeModifiers.register();
        // Grapes grow on Bountiful Fares trellises (data/hearthandharvest/bountifulfares/trellis_crop); HH's
        // "grapes require farmland" option controls their soil requirement.
        net.hecco.bountifulfares.definition.block.custom.TrellisBlock.SOIL_REQUIRED = crop ->
                !net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(crop.seeds()).getNamespace().equals(MODID) || Config.GRAPE_REQUIRE_FARMLAND.get();

        // Block/item behaviour that 1.21.1 set up in common setup
        CrateBlockItem.registerDispenseBehavior(HHModItems.CRATE.get());
        SeedPouchItem.registerDispenseBehavior();
        SaltBlock.registerDispenseBehavior();
        CookingPotBlockEntity.init();
        alabaster.hearthandharvest.common.block.entity.CaskBlockEntity.init();
        alabaster.hearthandharvest.common.block.entity.KegBlockEntity.init();
        CuttingBoardBlockEntity.init();
        CabinetBlockEntity.init();
        CuttingBoardBlock.init();
        RichSoilBlock.init();
        HHCompostables.register();
        HHLootModifiers.register();

        // Events (1.21.1: NeoForge event bus subscribers)
        CapabilityRegistration.register();
        ChickenGlideEvents.register();
        ChickenPlucking.register();
        CleaverEvents.register();
        CowMilking.register();
        CrowShoulderEvents.register();
        CrowStashEvents.register();
        EffectEvents.register();
        FarmersHatEvents.register();
        FeatherParticles.register();
        FillSapCauldron.register();
        GoatMilking.register();
        HoeEnchantmentEvents.register();
        HorseshoeEventHandler.register();
        ManureEvents.register();
        MulchGroundEvents.register();
        PigLitters.register();
        PitchforkEventHandler.register();
        RabbitLitters.register();
        RandomArmorStands.register();
        SaltBlockEvents.register();
        SaltedEffectEvents.register();
        SeedPickupEvent.register();
        StructureDiscoveryEvents.register();

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Mob mob) {
                mob.goalSelector.addGoal(1, new PungentEffectGoal(mob, 1.0D, 1.5D, 8.0D));
                mob.goalSelector.addGoal(1, new TemptingEffectGoal(mob, 1.0D, 1.25D, 8.0D));
            }
            if (entity instanceof Fox fox) {
                fox.goalSelector.addGoal(10, new FoxEatBushBerriesGoal(fox, 1.2F, 12, 1));
            }
            if (entity instanceof Chicken chicken && Config.CHICKENS_SEEK_NESTS.get()) {
                chicken.goalSelector.addGoal(2, new SeekNestGoal(chicken, 1.0D));
            }
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> alabaster.hearthandharvest.platform.util.BlockEntityItems.setCurrentRegistries(server.registryAccess()));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> alabaster.hearthandharvest.platform.util.BlockEntityItems.setCurrentRegistries(null));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            LOGGER.info("Hearth and Harvest is starting");
            StompingBasinBlock.clearAirborneTracking();
        });

        // HH's two NeoForge data maps (fluid -> bottle, vintage styles), synced to clients.
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new HHDataMaps.Loader());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
                ServerPlayNetworking.send(player, HHDataMaps.syncPayload()));

        // 26.3 only syncs recipes to clients per opted-in serializer; HH's screens, recipe books and
        // recipe viewers need these on the client.
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.AGING.get());
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.FERMENTING.get());
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.STOMPING.get());
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.COOKING.get());
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.CUTTING.get());
        // HH's two non-special crafting serializers, so JEI's crafting category lists them (JEI itself
        // only syncs minecraft: serializers). Salting is a special recipe, hidden as in 1.21.1.
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.BOTTLE_CRATE.get());
        RecipeSynchronization.synchronizeRecipeSerializer(HHModRecipeSerializers.SHAPELESS_REMAINDER.get());
    }
}
