package alabaster.hearthandharvest.client.fd.gui;

import alabaster.hearthandharvest.common.registry.HHModItems;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jspecify.annotations.NonNull;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHRecipeBookCategories;
import com.sidden.flavored.registry.FlavoredItems;
import alabaster.hearthandharvest.common.fd.crafting.display.CookingPotRecipeDisplay;
import alabaster.hearthandharvest.common.fd.block.entity.container.CookingPotMenu;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;

import java.util.List;

public class CookingPotRecipeBookComponent extends RecipeBookComponent<CookingPotMenu>
{
	private static final SearchRecipeBookCategory COOKING_SEARCH_CATEGORY = SearchRecipeBookCategory.HEARTHANDHARVEST_COOKING;

	protected static final WidgetSprites RECIPE_BOOK_BUTTONS = new WidgetSprites(
			Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "recipe_book/cooking_pot_enabled"),
			Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "recipe_book/cooking_pot_disabled"),
			Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "recipe_book/cooking_pot_enabled_highlighted"),
			Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "recipe_book/cooking_pot_disabled_highlighted"));
	private static final List<TabInfo> TABS = List.of(
			new TabInfo(COOKING_SEARCH_CATEGORY),
			// FD's tab icons were vegetable noodles / apple cider / dumplings + tomato sauce; HH has no noodles or
			// dumplings and FD apple cider is Flavored apple juice in this bundle.
			new TabInfo(HHModItems.CORN_STEW.get(), HHRecipeBookCategories.COOKING_MEALS.get()),
			new TabInfo(FlavoredItems.APPLE_JUICE.get(), HHRecipeBookCategories.COOKING_DRINKS.get()),
			new TabInfo(HHModItems.TOMATO_SAUCE.get(), HHRecipeBookCategories.COOKING_MISC.get())
	);

	public CookingPotRecipeBookComponent(CookingPotMenu menu) {
		super(menu, TABS);
	}

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return RECIPE_BOOK_BUTTONS;
    }

    @Override
	protected boolean isCraftingSlot(Slot slot) {
		return switch (slot.index) {
			case 0, 1, 2, 3, 4, 5 -> true;
			default -> false;
		};
	}

	@Override
	protected void selectMatchingRecipes(RecipeCollection possibleRecipes, StackedItemContents stackedItemContents) {
		possibleRecipes.selectRecipes(stackedItemContents, recipeDisplay -> recipeDisplay instanceof CookingPotRecipeDisplay);
	}

	@Override
	@NonNull
	protected Component getRecipeFilterName() {
		return TextUtils.container("recipe_book.cookable");
	}

	@Override
	protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
		ghostSlots.setResult(this.menu.getSlot(6), contextMap, recipeDisplay.result());
		if (recipeDisplay instanceof CookingPotRecipeDisplay cookingPotRecipeDisplay) {
			for (int i = 0; i < cookingPotRecipeDisplay.ingredients().size(); ++i) {
				SlotDisplay display = cookingPotRecipeDisplay.ingredients().get(i);
				ghostSlots.setInput(menu.getSlot(i), contextMap, display);
			}

			if (menu.getSlot(7).getItem().isEmpty() && cookingPotRecipeDisplay.container().isPresent()) {
				ghostSlots.setInput(menu.getSlot(7), contextMap, cookingPotRecipeDisplay.container().get());
			}
		}
	}
}
