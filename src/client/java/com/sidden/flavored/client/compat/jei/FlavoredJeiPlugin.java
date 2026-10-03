package com.sidden.flavored.client.compat.jei;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.client.screen.KegScreen;
import com.sidden.flavored.client.screen.MixingBowlScreen;
import com.sidden.flavored.client.screen.OvenScreen;
import com.sidden.flavored.recipe.BakingRecipe;
import com.sidden.flavored.recipe.FermentingRecipe;
import com.sidden.flavored.recipe.MixingRecipe;
import com.sidden.flavored.registry.FlavoredBlocks;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.hecco.bountifulfares.registry.client.BFClientRecipes;
import net.minecraft.resources.Identifier;

/**
 * Flavored's JEI plugin (26.3 / JEI 31 port of 1.21.1's JEIFlavoredPlugin): the keg, mixing
 * bowl and oven categories. Recipes come from Fabric's client recipe sync (see BFClientRecipes;
 * 1.21.1 read the client RecipeManager, which 26.3 no longer has).
 */
@JeiPlugin
public class FlavoredJeiPlugin implements IModPlugin {
    public static final IRecipeType<FermentingRecipe> FERMENTING = IRecipeType.create(Flavored.MOD_ID, "fermenting", FermentingRecipe.class);
    public static final IRecipeType<MixingRecipe> MIXING = IRecipeType.create(Flavored.MOD_ID, "mixing", MixingRecipe.class);
    public static final IRecipeType<BakingRecipe> BAKING = IRecipeType.create(Flavored.MOD_ID, "baking", BakingRecipe.class);

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new FermentingCategory(gui), new MixingCategory(gui), new BakingCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FERMENTING, BFClientRecipes.getAllOfType(FlavoredRecipeTypes.KEG_TYPE.get()));
        registration.addRecipes(MIXING, BFClientRecipes.getAllOfType(FlavoredRecipeTypes.MIXING_BOWL_TYPE.get()));
        registration.addRecipes(BAKING, BFClientRecipes.getAllOfType(FlavoredRecipeTypes.OVEN_TYPE.get()));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(KegScreen.class, 87, 33, 22, 20, FERMENTING);
        registration.addRecipeClickArea(MixingBowlScreen.class, 100, 25, 22, 20, MIXING);
        registration.addRecipeClickArea(OvenScreen.class, 90, 16, 22, 20, BAKING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(FERMENTING, FlavoredBlocks.KEG.get());
        registration.addCraftingStation(MIXING, FlavoredBlocks.MIXING_BOWL.get());
        // the oven burns furnace fuel, as 1.21.1 showed with JEI's fueling category
        registration.addCraftingStation(BAKING, FlavoredBlocks.OVEN.get());
        registration.addCraftingStation(RecipeTypes.SMELTING_FUEL, FlavoredBlocks.OVEN.get());
    }
}
