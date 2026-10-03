package com.sidden.flavored.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/** The mixing bowl's six ingredient slots plus its vessel (slot 6) and liquid (slot 7) slots. */
public record MixingRecipeInput(List<ItemStack> ingredientInputs, ItemStack vesselInput, ItemStack liquid) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        if (i < ingredientInputs().size()) {
            return ingredientInputs().get(i);
        } else if (i == 6) {
            return vesselInput();
        } else if (i >= 7) {
            return liquid();
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getVessel() {
        return vesselInput();
    }

    public ItemStack getLiquid() {
        return liquid();
    }

    @Override
    public int size() {
        return 8;
    }
}
