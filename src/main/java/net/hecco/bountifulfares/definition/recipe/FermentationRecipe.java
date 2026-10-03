package net.hecco.bountifulfares.definition.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.misc.BFRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class FermentationRecipe implements Recipe<RecipeInput> {

    // 26.3: recipes keep their result as an ItemStackTemplate (like vanilla's own recipes) - a
    // live ItemStack needs its item's data components bound, which isn't the case during datagen
    // (and ItemStacks can't be built during mod init at all: "Components not bound yet"). Stacks
    // are created from the template on demand. JSON shape ("result" = {id, count, components},
    // plus "result_count") is unchanged - ItemStackTemplate's codec uses the same field names.
    private final Identifier id;
    private final ItemStackTemplate output;
    private final Ingredient ingredient;
    private final int particleColor;

    public FermentationRecipe(Identifier id, ItemStackTemplate output, int outputCount, Ingredient input, int particleColor) {
        this.id = id;
        this.output = new ItemStackTemplate(output.item().value(), outputCount);
        this.ingredient = input;
        this.particleColor = particleColor;
    }

    public FermentationRecipe(Ingredient ingredient, ItemStackTemplate itemStack, int outputCount, int particleColor) {
        this.id = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "fermenting");
        // As before, only the result item is kept (components dropped) with the given count.
        this.output = new ItemStackTemplate(itemStack.item().value(), outputCount);
        this.ingredient = ingredient;
        this.particleColor = particleColor;
    }

    @Override
    public boolean matches(RecipeInput input, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
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
        return PlacementInfo.create(this.ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return output.create();
    }

    public ItemStack getOutput() {
        return output.create();
    }

    public ItemStackTemplate getResultTemplate() {
        return output;
    }

    public int getParticleColor() {
        return particleColor;
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return BFRecipes.FERMENTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return BFRecipes.FERMENTING.get();
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(BFBlocks.FERMENTATION_VESSEL.get());
    }

    public interface RecipeFactory<T extends FermentationRecipe> {
        T create(Ingredient ingredient, ItemStackTemplate result, int resultCount, int ParticleColor);
    }

    // RecipeSerializer<T> is a final Record in 26.3, not an interface - build one directly rather
    // than implementing it (see the identical note on MillingRecipe.Serializer).
    public static class Serializer {
        private final FermentationRecipe.RecipeFactory<FermentationRecipe> recipeFactory;

        public Serializer(FermentationRecipe.RecipeFactory<FermentationRecipe> recipeFactory) {
            this.recipeFactory = recipeFactory;
        }

        public FermentationRecipe read(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStackTemplate itemStack = ItemStackTemplate.STREAM_CODEC.decode(buf);
            int count = ByteBufCodecs.INT.decode(buf);
            int particleColor = ByteBufCodecs.INT.decode(buf);
            return this.recipeFactory.create(ingredient, itemStack, count, particleColor);
        }

        public void write(RegistryFriendlyByteBuf buf, FermentationRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
            ByteBufCodecs.INT.encode(buf, recipe.output.count());
            ByteBufCodecs.INT.encode(buf, recipe.particleColor);
        }

        public RecipeSerializer<FermentationRecipe> build() {
            MapCodec<FermentationRecipe> codec = RecordCodecBuilder.mapCodec((instance) ->
                    instance.group(
                            Ingredient.CODEC.fieldOf("ingredient")
                                .forGetter((recipe) -> recipe.ingredient),
                            ItemStackTemplate.CODEC.fieldOf("result")
                                    .forGetter((recipe) -> recipe.output),
                            ExtraCodecs.intRange(1, 99).fieldOf("result_count")
                                    .forGetter((recipe) -> recipe.output.count()),
                            Codec.INT.fieldOf("particle_color").forGetter(
                                    (recipe) -> recipe.particleColor)
                            )
                            .apply(instance, recipeFactory::create));
            StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> streamCodec = StreamCodec.of(this::write, this::read);
            return new RecipeSerializer<>(codec, streamCodec);
        }
    }
}
