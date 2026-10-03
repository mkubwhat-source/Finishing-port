package alabaster.hearthandharvest.common.fd.refabricated;

import net.minecraft.world.inventory.RecipeBookType;

import java.util.List;

/**
 * Recipe book types added to the vanilla enum (class tweaker {@code extend-enum} + {@code RecipeBookTypeMixin}),
 * following FarmersDelightRefabricated. Their open/filtering settings are saved and synced by
 * {@code RecipeBookSettingsMixin} / {@code ServerRecipeBookMixin}.
 */
public final class HHRecipeBookTypes {
    public static final RecipeBookType COOKING = RecipeBookType.HEARTHANDHARVEST_COOKING;
    public static final RecipeBookType FERMENTING = RecipeBookType.HEARTHANDHARVEST_FERMENTING;
    public static final RecipeBookType AGING = RecipeBookType.HEARTHANDHARVEST_AGING;

    public static final List<RecipeBookType> ALL = List.of(COOKING, FERMENTING, AGING);

    private HHRecipeBookTypes() {}
}
