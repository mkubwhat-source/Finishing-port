package com.sidden.flavored.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * An oven recipe: a shaped 3x3 pattern (same JSON shape as a crafting-table shaped recipe) plus a
 * baking time and experience, baked with fuel like a furnace.
 */
public class BakingRecipe implements Recipe<CraftingInput> {
    private final String group;
    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate result;
    private final float experience;
    private final int bakingTime;
    private PlacementInfo placementInfo;

    public BakingRecipe(String group, ShapedRecipePattern pattern, ItemStackTemplate result, float experience, int bakingTime) {
        this.group = group;
        this.pattern = pattern;
        this.result = result;
        this.experience = experience;
        this.bakingTime = bakingTime;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.result.create();
    }

    public ShapedRecipePattern pattern() {
        return pattern;
    }

    public ItemStackTemplate resultTemplate() {
        return result;
    }

    public ItemStack getResultItem() {
        return result.create();
    }

    public float getExperience() {
        return this.experience;
    }

    public int getBakingTime() {
        return this.bakingTime;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
        }
        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return FlavoredRecipeDisplays.baking(this);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return FlavoredRecipeTypes.OVEN_CATEGORY.get();
    }

    @Override
    public RecipeSerializer<BakingRecipe> getSerializer() {
        return FlavoredRecipeTypes.OVEN_SERIALIZER.get();
    }

    @Override
    public RecipeType<BakingRecipe> getType() {
        return FlavoredRecipeTypes.OVEN_TYPE.get();
    }

    public static final MapCodec<BakingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
            ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
            Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(recipe -> recipe.experience),
            Codec.INT.fieldOf("bakingtime").orElse(200).forGetter(recipe -> recipe.bakingTime)
    ).apply(instance, BakingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BakingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, recipe -> recipe.group,
            ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
            ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
            ByteBufCodecs.FLOAT, recipe -> recipe.experience,
            ByteBufCodecs.VAR_INT, recipe -> recipe.bakingTime,
            BakingRecipe::new);

    public static final RecipeSerializer<BakingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
