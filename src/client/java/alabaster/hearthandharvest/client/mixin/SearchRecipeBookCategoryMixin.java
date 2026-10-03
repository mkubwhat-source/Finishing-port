package alabaster.hearthandharvest.client.mixin;

import alabaster.hearthandharvest.common.registry.HHRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Search tabs for HH's recipe books (from FarmersDelightRefabricated's cooking pot search tab). */
@Mixin(SearchRecipeBookCategory.class)
public enum SearchRecipeBookCategoryMixin {
	HEARTHANDHARVEST_COOKING(HHRecipeBookCategories.COOKING_MEALS.get(), HHRecipeBookCategories.COOKING_DRINKS.get(), HHRecipeBookCategories.COOKING_MISC.get()),
	HEARTHANDHARVEST_FERMENTING(HHRecipeBookCategories.FERMENTING_MEALS.get(), HHRecipeBookCategories.FERMENTING_DRINKS.get(), HHRecipeBookCategories.FERMENTING_MISC.get()),
	HEARTHANDHARVEST_AGING(HHRecipeBookCategories.AGING_MEALS.get(), HHRecipeBookCategories.AGING_DRINKS.get(), HHRecipeBookCategories.AGING_MISC.get());

	@Shadow
	SearchRecipeBookCategoryMixin(RecipeBookCategory... includedCategories) {
	}
}
