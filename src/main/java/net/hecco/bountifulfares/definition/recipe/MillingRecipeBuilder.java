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

public class MillingRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private final MillingRecipe.RecipeFactory<?> recipeFactory;

    public MillingRecipeBuilder(ItemLike ingredient, ItemLike output, int count, MillingRecipe.RecipeFactory<?> recipeFactory) {
        this.ingredient = Ingredient.of(ingredient);
        this.result = output.asItem();
        this.count = count;
        this.recipeFactory = recipeFactory;
    }

    public static <T extends MillingRecipe> MillingRecipeBuilder create(Item input, ItemLike output, int count) {
        return new MillingRecipeBuilder(input, output, count, MillingRecipe::new);
    }

    @Override
    public MillingRecipeBuilder unlockedBy(String string, Criterion<?> advancementCriterion) {
        this.advancementBuilder.unlockedBy(string, advancementCriterion);
        return this;
    }

    @Override
    public MillingRecipeBuilder group(String group) {
        return this;
    }

    public Item getResult() {
        return result;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(getResult());
        // Ingredient.items() returns Stream<Holder<Item>> in 26.3, not Stream<ItemStack> - .value()
        // unwraps the Holder to the Item itself (confirmed via the "cannot find symbol getItem()
        // location: interface Holder<Item>" compiler error).
        Identifier ingredientId = BuiltInRegistries.ITEM.getKey(this.ingredient.items().findFirst().orElseThrow().value());
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(resultId.getNamespace(), resultId.getPath() + "_from_" + ingredientId.getPath() + "_milling"));
    }

    @Override
    public void save(RecipeOutput exporter, ResourceKey<Recipe<?>> recipeId) {
        MillingRecipe millingRecipe = this.recipeFactory.create(this.ingredient, new ItemStackTemplate(this.result), this.count);
        exporter.accept(recipeId, millingRecipe, this.advancementBuilder.build(exporter, recipeId, RecipeCategory.MISC));
    }
}
