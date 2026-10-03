package com.sidden.flavored;

import com.mojang.logging.LogUtils;
import com.sidden.flavored.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import org.slf4j.Logger;

/**
 * Flavored (by CodenamedSuper), bundled into the Bountiful Fares 26.3 Fabric jar.
 * <p>
 * 1.21.1 Flavored was a standalone NeoForge mod; here it keeps its own {@code flavored} namespace
 * (item/block ids, assets, data, creative tab) and is initialized by its own Fabric entrypoint,
 * run after Bountiful Fares' (fabric.mod.json lists it second), since the bundle reuses Bountiful
 * Fares' flour and maize. The jar also {@code provides} the {@code flavored} mod id.
 * <p>
 * Removed with the port: NeoForge's example {@code Config} (only template values nobody read) and
 * the config screen that exposed it.
 */
public class Flavored implements ModInitializer {
    public static final String MOD_ID = "flavored";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        FlavoredSoundEvents.init();
        FlavoredEffects.init();
        FlavoredDataComponents.init();
        FlavoredParticles.init();
        FlavoredBlocks.init();
        FlavoredEntities.init();
        FlavoredItems.init();
        FlavoredBlockEntities.init();
        FlavoredRecipeTypes.init();
        FlavoredMenus.init();
        FlavoredFeatures.init();
        FlavoredCreativeTabs.init();
        FlavoredDataAttachments.init();
        FlavoredStats.init();
        FlavoredMerges.init();
        FlavoredLoot.init();
        FlavoredEvents.register();

        // 26.3 only syncs recipes to clients per opted-in serializer; recipe viewers (JEI/EMI) and
        // the mixing bowl's client-side slot routing need these.
        RecipeSynchronization.synchronizeRecipeSerializer(FlavoredRecipeTypes.KEG_SERIALIZER.get());
        RecipeSynchronization.synchronizeRecipeSerializer(FlavoredRecipeTypes.MIXING_BOWL_SERIALIZER.get());
        RecipeSynchronization.synchronizeRecipeSerializer(FlavoredRecipeTypes.OVEN_SERIALIZER.get());
        RecipeSynchronization.synchronizeRecipeSerializer(FlavoredRecipeTypes.SPICING.get());
    }
}
