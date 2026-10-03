package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeBookCategory;

import java.util.function.Supplier;

/**
 * 26.3 recipe book categories (a registry since 1.21.2). Farmer's Delight's cooking pot tabs and
 * cutting board category, and the meals/drinks/misc tabs of HH's keg (fermenting) and cask (aging)
 * books (1.21.1: NeoForge-extended RecipeBookCategories enum entries).
 */
public class HHRecipeBookCategories {
    public static final Supplier<RecipeBookCategory> COOKING_MEALS = register("cooking_meals");
    public static final Supplier<RecipeBookCategory> COOKING_DRINKS = register("cooking_drinks");
    public static final Supplier<RecipeBookCategory> COOKING_MISC = register("cooking_misc");
    public static final Supplier<RecipeBookCategory> CUTTING = register("cutting");
    public static final Supplier<RecipeBookCategory> FERMENTING_MEALS = register("fermenting_meals");
    public static final Supplier<RecipeBookCategory> FERMENTING_DRINKS = register("fermenting_drinks");
    public static final Supplier<RecipeBookCategory> FERMENTING_MISC = register("fermenting_misc");
    public static final Supplier<RecipeBookCategory> AGING_MEALS = register("aging_meals");
    public static final Supplier<RecipeBookCategory> AGING_DRINKS = register("aging_drinks");
    public static final Supplier<RecipeBookCategory> AGING_MISC = register("aging_misc");

    private static Supplier<RecipeBookCategory> register(String name) {
        return BFRegistryHelper.register(HearthAndHarvest.MODID, name, BuiltInRegistries.RECIPE_BOOK_CATEGORY, RecipeBookCategory::new);
    }

    public static void init() {
    }
}
