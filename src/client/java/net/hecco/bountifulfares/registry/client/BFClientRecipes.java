package net.hecco.bountifulfares.registry.client;

import net.hecco.bountifulfares.platform.BFRecipeLookup;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.List;

/**
 * Client-side view of the server's recipes, for recipe viewers (JEI).
 * <p>
 * 26.3 no longer sends the full recipe set to clients - {@code ClientLevel.getRecipeManager()}
 * (what {@code BFJEIRecipes} used before) is gone, and the client only gets recipe-book displays.
 * Fabric API's replacement is opt-in recipe synchronization: the serializers are registered for
 * sync in {@code FabricBountifulFares.onInitialize} ({@code RecipeSynchronization
 * .synchronizeRecipeSerializer}), and the synced set arrives here through
 * {@link ClientRecipeSynchronizedEvent}. This is the same mechanism JEI itself uses (confirmed
 * via javap on {@code mezz.jei.fabric.JustEnoughItemsClient}).
 * <p>
 * JEI (re)starts its plugins from its own listener on this same event, so this listener is
 * registered in a phase ordered BEFORE the default phase - otherwise JEI could run
 * {@code registerRecipes} before the new recipe set is stored here.
 */
public final class BFClientRecipes {
    private static final Identifier EARLY_PHASE = BountifulFares.id("store_synced_recipes");
    private static volatile SynchronizedRecipes recipes;

    public static void register() {
        ClientRecipeSynchronizedEvent.EVENT.addPhaseOrdering(EARLY_PHASE, Event.DEFAULT_PHASE);
        ClientRecipeSynchronizedEvent.EVENT.register(EARLY_PHASE, (minecraft, synchronizedRecipes) -> {
            recipes = synchronizedRecipes;
            BFRecipeLookup.setClientRecipes(synchronizedRecipes);
        });
    }

    public static <T extends Recipe<?>> List<T> getAllOfType(RecipeType<T> type) {
        SynchronizedRecipes current = recipes;
        if (current == null) {
            return List.of();
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<RecipeHolder<T>> holders = (Collection) current.getAllOfType((RecipeType) type);
        return holders.stream().map(RecipeHolder::value).toList();
    }

    private BFClientRecipes() {
    }
}
