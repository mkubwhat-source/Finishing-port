package net.hecco.bountifulfares.definition.compat.jei;

import net.hecco.bountifulfares.definition.recipe.FermentationRecipe;
import net.hecco.bountifulfares.definition.recipe.MillingRecipe;
import net.hecco.bountifulfares.registry.client.BFClientRecipes;
import net.hecco.bountifulfares.registry.misc.BFRecipes;

import java.util.List;

/**
 * 26.3: recipes come from Fabric API's server-to-client recipe sync (see {@link BFClientRecipes})
 * instead of the removed client-side {@code RecipeManager}.
 */
public class BFJEIRecipes {
    public List<MillingRecipe> getMillingRecipes() {
        return BFClientRecipes.getAllOfType(BFRecipes.MILLING.get());
    }

    public List<FermentationRecipe> getFermentationRecipes() {
        return BFClientRecipes.getAllOfType(BFRecipes.FERMENTING.get());
    }
}
