package net.hecco.bountifulfares.definition.recipe;

import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class FermentingRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final int particleColor;
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private final FermentationRecipe.RecipeFactory<?> recipeFactory;

    public FermentingRecipeBuilder(Ingredient ingredient, ItemLike output, int count, int particleColor, FermentationRecipe.RecipeFactory<?> recipeFactory) {
        this.ingredient = ingredient;
        this.result = output.asItem();
        this.count = count;
        this.particleColor = particleColor;
        this.recipeFactory = recipeFactory;
    }

    public static <T extends FermentationRecipe> FermentingRecipeBuilder create(Ingredient input, ItemLike output, int count, int particleColor) {
        return new FermentingRecipeBuilder(input, output, count, particleColor, FermentationRecipe::new);
    }

    @Override
    public FermentingRecipeBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
        this.advancementBuilder.unlockedBy(string, advancementCriterion);
        return this;
    }

    @Override
    public FermentingRecipeBuilder group(String group) {
        return this;
    }

    public Item getResult() {
        return result;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(getResult());
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(resultId.getNamespace(), resultId.getPath() + "_from_fermenting"));
    }

    @Override
    public void save(RecipeOutput exporter, ResourceKey<Recipe<?>> recipeId) {
        FermentationRecipe fermentationRecipe = this.recipeFactory.create(this.ingredient, new ItemStackTemplate(this.result), this.count, this.particleColor);
        exporter.accept(recipeId, fermentationRecipe, this.advancementBuilder.build(exporter, recipeId, RecipeCategory.MISC));
    }
}
