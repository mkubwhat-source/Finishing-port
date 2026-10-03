package net.hecco.bountifulfares.registry.misc;

import com.mojang.serialization.MapCodec;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.recipe.*;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;

import java.util.function.Supplier;

public class BFRecipes {
    public static final Supplier<RecipeType<MillingRecipe>> MILLING = register("milling_recipe");
    public static final Supplier<RecipeType<FermentationRecipe>> FERMENTING = register("fermenting_recipe");

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
        return BFRegistryHelper.registerRecipeType(BountifulFares.MOD_ID, id);
    }

    public static final Supplier<RecipeSerializer<MillingRecipe>> MILLING_SERIALIZER = registerSerializer("milling",
            new MillingRecipe.Serializer(MillingRecipe::new).build());

    public static final Supplier<RecipeSerializer<FermentationRecipe>> FERMENTING_SERIALIZER = registerSerializer("fermenting",
            new FermentationRecipe.Serializer(FermentationRecipe::new).build());

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerSerializer(String id, RecipeSerializer<?> serializer) {
        return BFRegistryHelper.registerRecipeSerializer(BountifulFares.MOD_ID, id, (RecipeSerializer<T>) serializer);
    }

    public static final Supplier<RecipeSerializer<CeramicMassDyeingRecipe>> CERAMIC_MASS_DYEING = registerSpecialRecipe("ceramic_mass_dyeing", CeramicMassDyeingRecipe::new);
    public static final Supplier<RecipeSerializer<TiffinColoringRecipe>> TIFFIN_COLORING = registerSpecialRecipe("tiffin_coloring", TiffinColoringRecipe::new);
    public static final Supplier<RecipeSerializer<TiffinFoodCraftingRecipe>> TIFFIN_FOOD_CRAFTING = registerSpecialRecipe("tiffin_food_crafting", TiffinFoodCraftingRecipe::new);

    // `CustomRecipe` subclasses take no constructor args in 26.3 (no more `CraftingBookCategory`),
    // and `RecipeSerializer<T>` is a plain (MapCodec, StreamCodec) record rather than an interface
    // (see the identical note on MillingRecipe.Serializer), so a "special" recipe with no data of
    // its own is just a serializer built from `MapCodec.unit`/`StreamCodec.unit` over a single
    // shared instance - this mirrors how vanilla builds e.g. RepairItemRecipe.SERIALIZER.
    // Returning Supplier<RecipeSerializer<T>> (bounded to the concrete recipe type) rather than
    // the erased Supplier<RecipeSerializer<?>> keeps CustomRecipe.getSerializer()'s callers
    // (which must return RecipeSerializer<? extends CustomRecipe>) happy without a capture-
    // conversion mismatch - a bare wildcard capture isn't provably a CustomRecipe subtype to javac
    // even though it always is one here.
    // BFRegistryHelper.register<T>'s T is pinned to RecipeSerializer<?> by BuiltInRegistries.
    // RECIPE_SERIALIZER's own type, which no longer unifies with this method's own <T extends
    // CustomRecipe> against the lambda's RecipeSerializer<T> return - an explicit
    // RecipeSerializer<?> type witness (matching the registry) plus the same unchecked-cast
    // pattern already used by registerSerializer above resolves the inference conflict.
    @SuppressWarnings("unchecked")
    private static <T extends CustomRecipe> Supplier<RecipeSerializer<T>> registerSpecialRecipe(String name, java.util.function.Supplier<T> toRecipe){
        // One shared instance for both codecs, as vanilla does: StreamCodec.unit only encodes the
        // exact instance it was given, so a MapCodec that created a fresh instance per recipe file
        // made network sync of these recipes fail ("Can't encode ..., expected ...").
        Supplier<RecipeSerializer<?>> registered = BFRegistryHelper.<RecipeSerializer<?>>register(BountifulFares.MOD_ID, name, BuiltInRegistries.RECIPE_SERIALIZER, () -> {
            T instance = toRecipe.get();
            return new RecipeSerializer<>(MapCodec.unit(instance), StreamCodec.unit(instance));
        });
        return (Supplier<RecipeSerializer<T>>) (Supplier<?>) registered;
    }

    public static void registerRecipes() {
    }
}
