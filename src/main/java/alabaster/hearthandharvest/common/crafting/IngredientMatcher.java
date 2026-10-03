package alabaster.hearthandharvest.common.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * NeoForge's {@code RecipeMatcher.findMatches}: can each ingredient be matched to a different stack
 * (any order, one stack per ingredient)? Shapeless matching for HH's cask/stomping recipes.
 */
public final class IngredientMatcher {
    private IngredientMatcher() {
    }

    /** True when the stacks and ingredients pair up one-to-one. */
    public static boolean matchesExactly(List<ItemStack> stacks, List<Ingredient> ingredients) {
        if (stacks.size() != ingredients.size()) return false;
        return assign(0, stacks, ingredients, new boolean[stacks.size()]);
    }

    private static boolean assign(int index, List<ItemStack> stacks, List<Ingredient> ingredients, boolean[] used) {
        if (index == ingredients.size()) return true;
        Ingredient ingredient = ingredients.get(index);
        for (int i = 0; i < stacks.size(); i++) {
            if (used[i] || !ingredient.test(stacks.get(i))) continue;
            used[i] = true;
            if (assign(index + 1, stacks, ingredients, used)) return true;
            used[i] = false;
        }
        return false;
    }
}
