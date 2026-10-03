package com.sidden.flavored.registry;

import com.mojang.serialization.MapCodec;
import com.sidden.flavored.Flavored;
import com.sidden.flavored.recipe.BakingRecipe;
import com.sidden.flavored.recipe.FermentingRecipe;
import com.sidden.flavored.recipe.FlavoredRecipeDisplays;
import com.sidden.flavored.recipe.MixingRecipe;
import com.sidden.flavored.recipe.SpicyRecipe;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.function.Supplier;

/**
 * Recipe types, serializers, recipe book categories and the mixing display type.
 * <p>
 * 26.3: recipe book categories are a registry ({@code RECIPE_BOOK_CATEGORY}) instead of
 * NeoForge's enum extension ({@code EnumParameters}/{@code RegisterRecipeBookCategoriesEvent}).
 */
public final class FlavoredRecipeTypes {
    public static final Supplier<RecipeType<FermentingRecipe>> KEG_TYPE = BFRegistryHelper.registerRecipeType(Flavored.MOD_ID, "fermenting");
    public static final Supplier<RecipeType<MixingRecipe>> MIXING_BOWL_TYPE = BFRegistryHelper.registerRecipeType(Flavored.MOD_ID, "mixing");
    public static final Supplier<RecipeType<BakingRecipe>> OVEN_TYPE = BFRegistryHelper.registerRecipeType(Flavored.MOD_ID, "baking");

    public static final Supplier<RecipeSerializer<FermentingRecipe>> KEG_SERIALIZER = BFRegistryHelper.registerRecipeSerializer(Flavored.MOD_ID, "fermenting", FermentingRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<MixingRecipe>> MIXING_BOWL_SERIALIZER = BFRegistryHelper.registerRecipeSerializer(Flavored.MOD_ID, "mixing", MixingRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<BakingRecipe>> OVEN_SERIALIZER = BFRegistryHelper.registerRecipeSerializer(Flavored.MOD_ID, "baking", BakingRecipe.SERIALIZER);

    private static final SpicyRecipe SPICY_INSTANCE = new SpicyRecipe();
    // One shared instance for both codecs (StreamCodec.unit only encodes that exact instance).
    public static final Supplier<RecipeSerializer<SpicyRecipe>> SPICING = BFRegistryHelper.registerRecipeSerializer(Flavored.MOD_ID, "spicing",
            new RecipeSerializer<>(MapCodec.unit(SPICY_INSTANCE), StreamCodec.unit(SPICY_INSTANCE)));

    public static final Supplier<RecipeBookCategory> KEG_FOOD_CATEGORY = category("keg_food");
    public static final Supplier<RecipeBookCategory> KEG_MISC_CATEGORY = category("keg_misc");
    public static final Supplier<RecipeBookCategory> MIXING_BOWL_CATEGORY = category("mixing_bowl");
    public static final Supplier<RecipeBookCategory> OVEN_CATEGORY = category("oven");

    public static final Supplier<RecipeDisplay.Type<FlavoredRecipeDisplays.MixingRecipeDisplay>> MIXING_DISPLAY = register(
            BuiltInRegistries.RECIPE_DISPLAY, "mixing", FlavoredRecipeDisplays.MixingRecipeDisplay.TYPE);

    private static Supplier<RecipeBookCategory> category(String name) {
        return register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, name, new RecipeBookCategory());
    }

    private static <T, V extends T> Supplier<V> register(Registry<T> registry, String name, V value) {
        Registry.register(registry, Identifier.fromNamespaceAndPath(Flavored.MOD_ID, name), value);
        return () -> value;
    }

    public static void init() {
    }

    private FlavoredRecipeTypes() {
    }
}
