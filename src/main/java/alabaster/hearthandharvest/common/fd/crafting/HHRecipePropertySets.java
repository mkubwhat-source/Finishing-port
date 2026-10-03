package alabaster.hearthandharvest.common.fd.crafting;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipePropertySet;

/** FarmersDelightRefabricated's recipe property sets (which items fit the cooking pot / cutting board), filled by {@code RecipeManagerMixin}. */
public final class HHRecipePropertySets {
    public static final ResourceKey<RecipePropertySet> COOKING_POT_INPUT_ONE = key("cooking_pot_input_one");
    public static final ResourceKey<RecipePropertySet> COOKING_POT_INPUT_TWO = key("cooking_pot_input_two");
    public static final ResourceKey<RecipePropertySet> COOKING_POT_INPUT_THREE = key("cooking_pot_input_three");
    public static final ResourceKey<RecipePropertySet> COOKING_POT_INPUT_FOUR = key("cooking_pot_input_four");
    public static final ResourceKey<RecipePropertySet> COOKING_POT_INPUT_FIVE = key("cooking_pot_input_five");
    public static final ResourceKey<RecipePropertySet> COOKING_POT_INPUT_SIX = key("cooking_pot_input_six");
    public static final ResourceKey<RecipePropertySet> COOKING_POT_CONTAINER = key("cooking_pot_container");
    public static final ResourceKey<RecipePropertySet> CUTTING_BOARD_INPUT = key("cutting_board_input");
    public static final ResourceKey<RecipePropertySet> CUTTING_BOARD_TOOL = key("cutting_board_tool");

    private HHRecipePropertySets() {}

    private static ResourceKey<RecipePropertySet> key(String path) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, HearthAndHarvest.id(path));
    }
}
