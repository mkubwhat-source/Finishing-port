package alabaster.hearthandharvest.platform.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Replacement for 1.21.1's {@code RecipeManager#getAllRecipesFor}: 26.3 only exposes the full recipe
 * list server-side, so results are grouped by type once per loaded recipe manager (a reload creates a
 * new manager, which invalidates the cache).
 */
public final class RecipeLookup {
    private static final Map<RecipeManager, Map<RecipeType<?>, List<RecipeHolder<?>>>> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private RecipeLookup() {}

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> List<RecipeHolder<T>> allOfType(ServerLevel level, RecipeType<T> type) {
        RecipeManager manager = level.recipeAccess();
        Map<RecipeType<?>, List<RecipeHolder<?>>> byType = CACHE.computeIfAbsent(manager, m -> {
            Map<RecipeType<?>, List<RecipeHolder<?>>> map = new HashMap<>();
            for (RecipeHolder<?> holder : m.getRecipes()) {
                map.computeIfAbsent(holder.value().getType(), t -> new ArrayList<>()).add(holder);
            }
            return map;
        });
        return (List<RecipeHolder<T>>) (List<?>) byType.getOrDefault(type, List.of());
    }
}
