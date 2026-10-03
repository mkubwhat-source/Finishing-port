package com.sidden.flavored.client.compat.emi;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.recipe.BakingRecipe;
import com.sidden.flavored.recipe.FermentingRecipe;
import com.sidden.flavored.recipe.MixingRecipe;
import com.sidden.flavored.registry.FlavoredBlocks;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiReloadLog;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * EMI support for Flavored's keg, mixing bowl and oven (new in this port; 1.21.1 Flavored only
 * shipped a JEI plugin). Layouts match the JEI categories and reuse their background textures.
 */
@EmiEntrypoint
public class FlavoredEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory FERMENTING = new EmiRecipeCategory(id("fermenting"),
            EmiStack.of(FlavoredBlocks.KEG.get()), EmiStack.of(FlavoredBlocks.KEG.get()), EmiRecipeSorting.compareOutputThenInput());
    public static final EmiRecipeCategory MIXING = new EmiRecipeCategory(id("mixing"),
            EmiStack.of(FlavoredBlocks.MIXING_BOWL.get()), EmiStack.of(FlavoredBlocks.MIXING_BOWL.get()), EmiRecipeSorting.compareOutputThenInput());
    public static final EmiRecipeCategory BAKING = new EmiRecipeCategory(id("baking"),
            EmiStack.of(FlavoredBlocks.OVEN.get()), EmiStack.of(FlavoredBlocks.OVEN.get()), EmiRecipeSorting.compareOutputThenInput());

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Flavored.MOD_ID, path);
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FERMENTING);
        registry.addCategory(MIXING);
        registry.addCategory(BAKING);
        registry.addWorkstation(FERMENTING, EmiStack.of(FlavoredBlocks.KEG.get()));
        registry.addWorkstation(MIXING, EmiStack.of(FlavoredBlocks.MIXING_BOWL.get()));
        registry.addWorkstation(BAKING, EmiStack.of(FlavoredBlocks.OVEN.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.FUEL, EmiStack.of(FlavoredBlocks.OVEN.get()));

        for (FermentingRecipe r : getRecipes(registry, FlavoredRecipeTypes.KEG_TYPE.get())) {
            addSafe(registry, () -> new EmiKegRecipe(r), r);
        }
        for (MixingRecipe r : getRecipes(registry, FlavoredRecipeTypes.MIXING_BOWL_TYPE.get())) {
            addSafe(registry, () -> new EmiMixingRecipe(r), r);
        }
        for (BakingRecipe r : getRecipes(registry, FlavoredRecipeTypes.OVEN_TYPE.get())) {
            addSafe(registry, () -> new EmiBakingRecipe(r), r);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> List<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        Collection<RecipeHolder<?>> recipes = registry.getRecipes();
        if (recipes == null) return List.of();
        return recipes.stream().map(RecipeHolder::value).filter(r -> r.getType() == type).map(r -> (T) r).toList();
    }

    private static void addSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Recipe<?> recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing flavored recipe " + EmiPort.getId(recipe));
        }
    }
}
