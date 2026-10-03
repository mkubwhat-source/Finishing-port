package com.sidden.flavored.recipe.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FermentingRecipeInput(ItemStack mainInput, ItemStack fermentingInput) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        if (i == 0) return mainInput;
        if (i == 1) return fermentingInput;
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 2;
    }
}
