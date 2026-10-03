package net.hecco.bountifulfares.lib.compat;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Ported in-tree from NexusLib's {@code FabricNLCompatAPI} as part of removing the NexusLib
 * dependency during the 26.3 Fabric port. Datagen-only: generates a builtin resource pack of
 * compat recipes per integration that asked for one.
 */
public class FabricCompatAPI {
    public static void generateCompatDatapacks(FabricDataGenerator dataGenerator, CompatManager manager) {
        for (ModIntegration integration : manager.getIntegrations()) {
            if (integration.shouldCreateDatapack()) {
                if (integration.modIds().isEmpty()) {
                    BountifulFares.LOGGER.error("Cannot create datapack with no mod ids for integration " + integration);
                    continue;
                }
                StringBuilder id = new StringBuilder();
                for (String modId : integration.modIds()) {
                    id.append(modId).append("_");
                }
                id.append("dat");
                var pack = dataGenerator.createBuiltinResourcePack(Identifier.fromNamespaceAndPath(manager.modId, id.toString()));
                pack.addProvider((a, b) -> new FabricRecipeProvider(a, b) {
                    @Override
                    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
                        return new RecipeProvider(recipeOutput, advancementOutput) {
                            @Override
                            public void buildRecipes() {
                                integration.recipeGeneration(this.output, registries.lookupOrThrow(Registries.ITEM));
                            }
                        };
                    }
                });
            }
        }
    }
}
