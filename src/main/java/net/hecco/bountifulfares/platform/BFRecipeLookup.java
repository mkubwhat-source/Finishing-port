package net.hecco.bountifulfares.platform;

import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Recipe lookup usable from code that runs on both logical sides.
 * <p>
 * 1.21.1's {@code Level.getRecipeManager()} worked on the client too. In 26.3 only
 * {@code ServerLevel.recipeAccess()} can look recipes up; casting a {@code ClientLevel} to
 * {@code ServerLevel} (as earlier port code did, e.g. in {@code FermentationVesselBlock#useItemOn},
 * which the client runs for prediction) crashes the game. On the client this falls back to the
 * recipes Fabric synchronizes to it (this mod's serializers are opted in, see
 * {@code FabricBountifulFares}); {@code BFClientRecipes} hands them over here.
 */
public final class BFRecipeLookup {
    private static volatile SynchronizedRecipes clientRecipes;

    public static void setClientRecipes(SynchronizedRecipes recipes) {
        clientRecipes = recipes;
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getFirstMatch(Level level, RecipeType<T> type, I input) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().getRecipeFor(type, input, level);
        }
        SynchronizedRecipes recipes = clientRecipes;
        return recipes == null ? Optional.empty() : recipes.getFirstMatch(type, input, level);
    }

    /** Every recipe of a type (server: all loaded recipes; client: the synchronized ones). */
    @SuppressWarnings("unchecked")
    public static <I extends RecipeInput, T extends Recipe<I>> java.util.Collection<RecipeHolder<T>> getAllOfType(Level level, RecipeType<T> type) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.recipeAccess().getRecipes().stream()
                    .filter(holder -> holder.value().getType() == type)
                    .map(holder -> (RecipeHolder<T>) holder)
                    .toList();
        }
        SynchronizedRecipes recipes = clientRecipes;
        return recipes == null ? java.util.List.of() : recipes.getAllOfType(type);
    }

    private BFRecipeLookup() {
    }
}
