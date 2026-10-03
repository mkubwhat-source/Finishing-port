package alabaster.hearthandharvest.client.gui;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.container.CaskMenu;
import alabaster.hearthandharvest.common.block.entity.container.KegMenu;
import alabaster.hearthandharvest.common.crafting.HHRecipeDisplays;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.registry.HHRecipeBookCategories;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
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

import java.util.List;

/**
 * The keg (fermenting) and cask (aging) recipe books.
 * <p>
 * 1.21.1 extended NeoForge's RecipeBookCategories enum (search/meals/drinks/misc tabs, icons from
 * EnumParameters). 26.3 recipes carry a {@code RecipeBookCategory} (HHRecipeBookCategories) and the
 * search tab is a {@link SearchRecipeBookCategory} constant added by the class tweaker +
 * SearchRecipeBookCategoryMixin, as FarmersDelightRefabricated does for the cooking pot.
 * Tab icons are 1.21.1's.
 */
public final class HHRecipeBooks {
    private HHRecipeBooks() {}

    private static WidgetSprites filterSprites(String name) {
        return new WidgetSprites(
                HearthAndHarvest.id("recipe_book/" + name + "_enabled"),
                HearthAndHarvest.id("recipe_book/" + name + "_disabled"),
                HearthAndHarvest.id("recipe_book/" + name + "_enabled_highlighted"),
                HearthAndHarvest.id("recipe_book/" + name + "_disabled_highlighted"));
    }

    private static void fillResult(GhostSlots ghostSlots, Slot resultSlot, ContextMap context, SlotDisplay result) {
        if (resultSlot.getItem().isEmpty() && !(result instanceof SlotDisplay.Empty)) {
            ghostSlots.setResult(resultSlot, context, result);
        }
    }

    public static class Keg extends RecipeBookComponent<KegMenu> {
        private static final WidgetSprites FILTER_SPRITES = filterSprites("keg");
        /** Item input slots 0-1 (KegBlockEntity.INPUT_SLOT_ONE/TWO); 1.21.1 ghosted the result in slot 4. */
        private static final int RESULT_SLOT = 4;

        public Keg(KegMenu menu) {
            super(menu, List.of(
                    new TabInfo(SearchRecipeBookCategory.HEARTHANDHARVEST_FERMENTING),
                    new TabInfo(HHModItems.PICKLED_CARROTS.get(), HHRecipeBookCategories.FERMENTING_MEALS.get()),
                    new TabInfo(HHModItems.RED_GRAPE_WINE.get(), HHRecipeBookCategories.FERMENTING_DRINKS.get()),
                    new TabInfo(HHModItems.CHEESE_WHEEL.get(), HHRecipeBookCategories.FERMENTING_MISC.get())));
        }

        @Override
        protected WidgetSprites getFilterButtonTextures() {
            return FILTER_SPRITES;
        }

        @Override
        protected boolean isCraftingSlot(Slot slot) {
            return slot.index <= 1;
        }

        @Override
        protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents contents) {
            collection.selectRecipes(contents, display -> display instanceof HHRecipeDisplays.FluidMachineDisplay);
        }

        @Override
        protected Component getRecipeFilterName() {
            return HHTextUtils.getTranslation("container.recipe_book.fermentable");
        }

        @Override
        protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
            fillResult(ghostSlots, this.menu.getSlot(RESULT_SLOT), context, display.result());
            if (display instanceof HHRecipeDisplays.FluidMachineDisplay keg) {
                for (int i = 0; i < Math.min(2, keg.ingredients().size()); i++) {
                    ghostSlots.setInput(this.menu.getSlot(i), context, keg.ingredients().get(i));
                }
            }
        }
    }

    public static class Cask extends RecipeBookComponent<CaskMenu> {
        private static final WidgetSprites FILTER_SPRITES = filterSprites("cask");
        /** 2x2 input grid in slots 0-3, result in slot 4. */
        private static final int RESULT_SLOT = 4;

        public Cask(CaskMenu menu) {
            super(menu, List.of(
                    new TabInfo(SearchRecipeBookCategory.HEARTHANDHARVEST_AGING),
                    new TabInfo(HHModItems.CHEESE_WHEEL.get(), HHRecipeBookCategories.AGING_MEALS.get()),
                    new TabInfo(HHModItems.RED_GRAPE_WINE.get(), HHRecipeBookCategories.AGING_DRINKS.get()),
                    new TabInfo(HHModItems.JERKY.get(), HHModItems.PICKLED_CARROTS.get(), HHRecipeBookCategories.AGING_MISC.get())));
        }

        @Override
        protected WidgetSprites getFilterButtonTextures() {
            return FILTER_SPRITES;
        }

        @Override
        protected boolean isCraftingSlot(Slot slot) {
            return slot.index < RESULT_SLOT;
        }

        @Override
        protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents contents) {
            collection.selectRecipes(contents, display -> display instanceof HHRecipeDisplays.FluidMachineDisplay);
        }

        @Override
        protected Component getRecipeFilterName() {
            return HHTextUtils.getTranslation("container.recipe_book.ageable");
        }

        @Override
        protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
            fillResult(ghostSlots, this.menu.getSlot(RESULT_SLOT), context, display.result());
            if (display instanceof HHRecipeDisplays.FluidMachineDisplay cask) {
                for (int i = 0; i < Math.min(RESULT_SLOT, cask.ingredients().size()); i++) {
                    ghostSlots.setInput(this.menu.getSlot(i), context, cask.ingredients().get(i));
                }
            }
        }
    }
}
