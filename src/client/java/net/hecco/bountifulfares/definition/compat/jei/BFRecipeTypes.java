package net.hecco.bountifulfares.definition.compat.jei;

import mezz.jei.api.recipe.types.IRecipeType;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.recipe.FermentationRecipe;
import net.hecco.bountifulfares.definition.recipe.MillingRecipe;

// JEI 31: mezz.jei.api.recipe.RecipeType is deprecated for removal; IRecipeType.create(...) is
// the replacement with the same (namespace, path, class) shape.
public class BFRecipeTypes {
    public static final IRecipeType<FermentationRecipe> FERMENTING = IRecipeType.create(BountifulFares.MOD_ID, "fermenting", FermentationRecipe.class);
    public static final IRecipeType<MillingRecipe> MILLING = IRecipeType.create(BountifulFares.MOD_ID, "milling", MillingRecipe.class);
    public static final IRecipeType<PropagationRecipe> PRISMARINE_PROPAGATION = IRecipeType.create(BountifulFares.MOD_ID, "prismarine_propagation", PropagationRecipe.class);

    public BFRecipeTypes() {
    }
}
