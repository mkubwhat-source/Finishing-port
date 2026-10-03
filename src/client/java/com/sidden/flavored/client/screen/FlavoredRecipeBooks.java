package com.sidden.flavored.client.screen;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.menu.KegMenu;
import com.sidden.flavored.menu.MixingBowlMenu;
import com.sidden.flavored.menu.OvenMenu;
import com.sidden.flavored.recipe.FlavoredRecipeDisplays;
import com.sidden.flavored.registry.FlavoredItems;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

/**
 * The keg, mixing bowl and oven recipe books.
 * <p>
 * 26.3 rebuilt the recipe book: recipes send "displays" to the client, grouped by registered
 * {@code RecipeBookCategory}s, and a book component renders one tab per category. 1.21.1's
 * NeoForge-extended enums (recipe book types/categories with a compass "search" tab) cannot be
 * extended in 26.3 (the search tabs are a fixed vanilla enum), so each book has its category
 * tabs only; the search box still works within a tab. Tab icons and filter sprites/names are
 * 1.21.1's.
 */
public final class FlavoredRecipeBooks {
    private static WidgetSprites filterSprites(String name) {
        return new WidgetSprites(
                Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "recipe_book/" + name + "_filter_enabled"),
                Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "recipe_book/" + name + "_filter_disabled"),
                Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "recipe_book/" + name + "_filter_enabled_highlighted"),
                Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "recipe_book/" + name + "_filter_disabled_highlighted"));
    }

    public static class Keg extends RecipeBookComponent<KegMenu> {
        private static final WidgetSprites FILTER_SPRITES = filterSprites("keg");
        private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.fermentable");

        public Keg(KegMenu menu) {
            super(menu, List.of(
                    new TabInfo(FlavoredItems.SWEET_BERRY_WINE.get(), FlavoredRecipeTypes.KEG_FOOD_CATEGORY.get()),
                    new TabInfo(Items.LAVA_BUCKET, Items.FERMENTED_SPIDER_EYE, FlavoredRecipeTypes.KEG_MISC_CATEGORY.get())));
        }

        @Override
        protected WidgetSprites getFilterButtonTextures() {
            return FILTER_SPRITES;
        }

        @Override
        protected boolean isCraftingSlot(Slot slot) {
            return slot.index <= 2;
        }

        @Override
        protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
            ghostSlots.setResult(this.menu.getResultSlot(), context, display.result());
            if (display instanceof FurnaceRecipeDisplay keg) {
                ghostSlots.setInput(this.menu.getIngredientSlot(), context, keg.ingredient());
                if (this.menu.getFermenterSlot().getItem().isEmpty()) {
                    ghostSlots.setInput(this.menu.getFermenterSlot(), context, keg.fuel());
                }
            }
        }

        @Override
        protected Component getRecipeFilterName() {
            return FILTER_NAME;
        }

        @Override
        protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents contents) {
            collection.selectRecipes(contents, display -> display instanceof FurnaceRecipeDisplay);
        }
    }

    public static class MixingBowl extends RecipeBookComponent<MixingBowlMenu> {
        private static final WidgetSprites FILTER_SPRITES = filterSprites("mixing_bowl");
        private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.mixable");

        public MixingBowl(MixingBowlMenu menu) {
            super(menu, List.of(new TabInfo(FlavoredItems.PORRIDGE.get(), FlavoredRecipeTypes.MIXING_BOWL_CATEGORY.get())));
        }

        @Override
        protected WidgetSprites getFilterButtonTextures() {
            return FILTER_SPRITES;
        }

        @Override
        protected boolean isCraftingSlot(Slot slot) {
            return slot.index < MixingBowlMenu.SLOT_COUNT;
        }

        @Override
        protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
            if (display instanceof FlavoredRecipeDisplays.MixingRecipeDisplay mixing) {
                List<Slot> grid = this.menu.getIngredientSlots();
                for (int i = 0; i < Math.min(grid.size(), mixing.ingredients().size()); i++) {
                    ghostSlots.setInput(grid.get(i), context, mixing.ingredients().get(i));
                }
                if (!(mixing.vessel() instanceof SlotDisplay.Empty)) {
                    ghostSlots.setInput(this.menu.getVesselSlot(), context, mixing.vessel());
                }
                if (!(mixing.liquid() instanceof SlotDisplay.Empty)) {
                    ghostSlots.setInput(this.menu.getLiquidSlot(), context, mixing.liquid());
                }
            }
        }

        @Override
        protected Component getRecipeFilterName() {
            return FILTER_NAME;
        }

        @Override
        protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents contents) {
            collection.selectRecipes(contents, display -> display instanceof FlavoredRecipeDisplays.MixingRecipeDisplay);
        }
    }

    public static class Oven extends RecipeBookComponent<OvenMenu> {
        private static final WidgetSprites FILTER_SPRITES = filterSprites("oven");
        private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.bakable");

        public Oven(OvenMenu menu) {
            super(menu, List.of(new TabInfo(FlavoredItems.PIZZA_SLICE.get(), FlavoredRecipeTypes.OVEN_CATEGORY.get())));
        }

        @Override
        protected WidgetSprites getFilterButtonTextures() {
            return FILTER_SPRITES;
        }

        @Override
        protected boolean isCraftingSlot(Slot slot) {
            return slot.index < OvenMenu.INPUT_SLOT_END || slot.index == OvenMenu.RESULT_SLOT;
        }

        @Override
        protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, ContextMap context) {
            ghostSlots.setResult(this.menu.getResultSlot(), context, display.result());
            if (display instanceof ShapedCraftingRecipeDisplay shaped) {
                List<Slot> grid = this.menu.getInputGridSlots();
                PlaceRecipeHelper.placeRecipe(3, 3, shaped.width(), shaped.height(), shaped.ingredients(),
                        (ingredient, slotIndex, x, y) -> ghostSlots.setInput(grid.get(slotIndex), context, ingredient));
            }
        }

        @Override
        protected Component getRecipeFilterName() {
            return FILTER_NAME;
        }

        @Override
        protected void selectMatchingRecipes(RecipeCollection collection, StackedItemContents contents) {
            collection.selectRecipes(contents, display -> display instanceof ShapedCraftingRecipeDisplay);
        }
    }

    private FlavoredRecipeBooks() {
    }
}
