package com.sidden.flavored.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sidden.flavored.recipe.input.FermentingRecipeInput;
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

import java.util.List;

/**
 * A keg recipe: the ingredient ferments into the result; the fermenter (e.g. sugar, fungi) is
 * consumed alongside it and makes fermentation four times faster, but is optional.
 * <p>
 * Port notes (26.3): the result is an {@link ItemStackTemplate} (see BF's MillingRecipe), 26.3
 * ingredients are never empty, and {@code RecipeSerializer} is a (MapCodec, StreamCodec) record.
 * <p>
 * Added for the Bountiful Fares bundle: {@code fermenter_required} (default false). The keg
 * versions of Bountiful Fares' fermentation-vessel drinks need their bottle/jar in the fermenter
 * slot, just as the vessel needs one to collect them, so those recipes only match with it present.
 */
public class FermentingRecipe implements Recipe<FermentingRecipeInput> {
    public static final String DEFAULT_PARTICLE_COLOR = "349ad6";

    private final String group;
    private final FermentingBookCategory category;
    private final Ingredient ingredient;
    private final Ingredient fermenter;
    private final boolean fermenterRequired;
    private final String particleColor;
    private final ItemStackTemplate result;
    private PlacementInfo placementInfo;

    public FermentingRecipe(String group, FermentingBookCategory category, Ingredient ingredient, Ingredient fermenter, boolean fermenterRequired, String particleColor, ItemStackTemplate result) {
        this.group = group;
        this.category = category;
        this.ingredient = ingredient;
        this.fermenter = fermenter;
        this.fermenterRequired = fermenterRequired;
        this.particleColor = particleColor;
        this.result = result;
    }

    public String getGroup() {
        return group;
    }

    public FermentingBookCategory category() {
        return category;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public Ingredient fermenter() {
        return fermenter;
    }

    public boolean fermenterRequired() {
        return fermenterRequired;
    }

    public String particleColor() {
        return particleColor;
    }

    public ItemStackTemplate resultTemplate() {
        return result;
    }

    public ItemStack result() {
        return result.create();
    }

    @Override
    public boolean matches(FermentingRecipeInput input, Level level) {
        if (fermenterRequired && !this.fermenter.test(input.fermentingInput())) {
            return false;
        }
        return this.ingredient.test(input.mainInput());
    }

    @Override
    public ItemStack assemble(FermentingRecipeInput input) {
        return this.result.create();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            // like a furnace: only the ingredient is placed, the fermenter is the "fuel"
            this.placementInfo = PlacementInfo.create(this.ingredient);
        }
        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return FlavoredRecipeDisplays.fermenting(this);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return this.category == FermentingBookCategory.FOOD ? FlavoredRecipeTypes.KEG_FOOD_CATEGORY.get() : FlavoredRecipeTypes.KEG_MISC_CATEGORY.get();
    }

    @Override
    public RecipeSerializer<FermentingRecipe> getSerializer() {
        return FlavoredRecipeTypes.KEG_SERIALIZER.get();
    }

    @Override
    public RecipeType<FermentingRecipe> getType() {
        return FlavoredRecipeTypes.KEG_TYPE.get();
    }

    public static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(FermentingRecipe::getGroup),
            FermentingBookCategory.CODEC.fieldOf("category").forGetter(FermentingRecipe::category),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(FermentingRecipe::ingredient),
            Ingredient.CODEC.fieldOf("fermenter").forGetter(FermentingRecipe::fermenter),
            Codec.BOOL.optionalFieldOf("fermenter_required", false).forGetter(FermentingRecipe::fermenterRequired),
            Codec.STRING.optionalFieldOf("particle_color", DEFAULT_PARTICLE_COLOR).forGetter(FermentingRecipe::particleColor),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(FermentingRecipe::resultTemplate)
    ).apply(inst, FermentingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {
                buffer.writeUtf(recipe.group);
                buffer.writeEnum(recipe.category);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.fermenter);
                ByteBufCodecs.BOOL.encode(buffer, recipe.fermenterRequired);
                buffer.writeUtf(recipe.particleColor);
                ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.result);
            },
            buffer -> new FermentingRecipe(
                    buffer.readUtf(),
                    buffer.readEnum(FermentingBookCategory.class),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                    ByteBufCodecs.BOOL.decode(buffer),
                    buffer.readUtf(),
                    ItemStackTemplate.STREAM_CODEC.decode(buffer)));

    public static final RecipeSerializer<FermentingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
