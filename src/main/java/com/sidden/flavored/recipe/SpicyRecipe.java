package com.sidden.flavored.recipe;

import com.sidden.flavored.registry.FlavoredDataComponents;
import com.sidden.flavored.registry.FlavoredItemTags;
import com.sidden.flavored.registry.FlavoredItems;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Crafting any single (spiceable) food with one to three dried peppers gives it a spiciness level
 * equal to the pepper count; eating spicy food gives the Heat effect for 30s per level.
 */
public class SpicyRecipe extends CustomRecipe {
    public SpicyRecipe() {
        super();
    }

    private static boolean isSpice(ItemStack stack) {
        return stack.is(FlavoredItems.DRIED_PEPPER.get());
    }

    private static boolean isSpiceableFood(ItemStack stack) {
        return !stack.is(FlavoredItemTags.NOT_SPICEABLE) && stack.has(DataComponents.FOOD);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int foodCount = 0;
        int spiceCount = 0;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack itemstack = input.getItem(i);
            if (!itemstack.isEmpty()) {
                if (isSpice(itemstack)) {
                    spiceCount++;
                } else if (isSpiceableFood(itemstack)) {
                    foodCount++;
                } else {
                    // 1.21.1 ignored unrelated items in the grid (and then consumed them); an
                    // unrelated item now simply means "not a spicing recipe".
                    return false;
                }
            }
        }
        return foodCount == 1 && spiceCount >= 1 && spiceCount < 4;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        int spiciness = 0;
        ItemStack result = ItemStack.EMPTY;
        for (int j = 0; j < input.size(); ++j) {
            ItemStack itemstack = input.getItem(j);
            if (isSpice(itemstack)) {
                spiciness++;
            } else if (!itemstack.isEmpty() && isSpiceableFood(itemstack)) {
                result = itemstack.copy();
            }
        }
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }
        result.set(FlavoredDataComponents.SPICINESS.get(), spiciness);
        result.setCount(1);
        return result;
    }

    @Override
    public RecipeSerializer<SpicyRecipe> getSerializer() {
        return FlavoredRecipeTypes.SPICING.get();
    }
}
