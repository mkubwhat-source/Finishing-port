package com.sidden.flavored.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sidden.flavored.recipe.input.MixingRecipeInput;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * A mixing bowl recipe: up to six shapeless ingredients, an optional vessel (consumed, e.g. a
 * bowl) and an optional liquid (not consumed, e.g. a water or milk bucket), mixed with a whisk.
 * <p>
 * Port notes (26.3): ingredients can no longer be empty ({@code Ingredient.EMPTY} is gone), so the
 * optional vessel/liquid are {@code Optional<Ingredient>}; the result is an ItemStackTemplate.
 */
public class MixingRecipe implements Recipe<MixingRecipeInput> {
    private final List<Ingredient> ingredientsInput;
    private final Optional<Ingredient> vesselInput;
    private final Optional<Ingredient> liquidInput;
    private final ItemStackTemplate output;
    private PlacementInfo placementInfo;

    public MixingRecipe(List<Ingredient> ingredientsInput, Optional<Ingredient> vesselInput, Optional<Ingredient> liquidInput, ItemStackTemplate output) {
        this.ingredientsInput = List.copyOf(ingredientsInput);
        this.vesselInput = vesselInput;
        this.liquidInput = liquidInput;
        this.output = output;
    }

    public List<Ingredient> ingredientsInput() {
        return ingredientsInput;
    }

    public Optional<Ingredient> vesselInput() {
        return vesselInput;
    }

    public Optional<Ingredient> liquidInput() {
        return liquidInput;
    }

    public ItemStackTemplate outputTemplate() {
        return output;
    }

    public ItemStack output() {
        return output.create();
    }

    @Override
    public boolean matches(MixingRecipeInput input, Level level) {
        List<ItemStack> remaining = new ArrayList<>(input.ingredientInputs());

        for (Ingredient ingredient : ingredientsInput) {
            boolean matched = false;
            Iterator<ItemStack> it = remaining.iterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    it.remove();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        for (ItemStack stack : remaining) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        if (vesselInput.isPresent() && !vesselInput.get().test(input.getVessel())) {
            return false;
        }
        return liquidInput.isEmpty() || liquidInput.get().test(input.liquid());
    }

    @Override
    public ItemStack assemble(MixingRecipeInput input) {
        return output.create();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            // Only the grid ingredients: the recipe book places into the 3x2 grid (the vessel and
            // liquid are shown as ghost items, like a furnace's fuel).
            this.placementInfo = PlacementInfo.create(ingredientsInput);
        }
        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return FlavoredRecipeDisplays.mixing(this);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return FlavoredRecipeTypes.MIXING_BOWL_CATEGORY.get();
    }

    @Override
    public RecipeSerializer<MixingRecipe> getSerializer() {
        return FlavoredRecipeTypes.MIXING_BOWL_SERIALIZER.get();
    }

    @Override
    public RecipeType<MixingRecipe> getType() {
        return FlavoredRecipeTypes.MIXING_BOWL_TYPE.get();
    }

    public static final MapCodec<MixingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.listOf(1, 6).fieldOf("ingredients").forGetter(MixingRecipe::ingredientsInput),
            Ingredient.CODEC.optionalFieldOf("vessel").forGetter(MixingRecipe::vesselInput),
            Ingredient.CODEC.optionalFieldOf("liquid").forGetter(MixingRecipe::liquidInput),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(MixingRecipe::outputTemplate)
    ).apply(inst, MixingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MixingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), MixingRecipe::ingredientsInput,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), MixingRecipe::vesselInput,
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), MixingRecipe::liquidInput,
            ItemStackTemplate.STREAM_CODEC, MixingRecipe::outputTemplate,
            MixingRecipe::new);

    public static final RecipeSerializer<MixingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
