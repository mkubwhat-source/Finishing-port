package net.hecco.bountifulfares.definition.recipe;

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

public class MillingRecipe implements Recipe<RecipeInput> {

    // 26.3: recipes keep their result as an ItemStackTemplate (like vanilla's own recipes) - a
    // live ItemStack needs its item's data components bound, which isn't the case during datagen
    // (and ItemStacks can't be built during mod init at all: "Components not bound yet"). Stacks
    // are created from the template on demand. JSON shape ("result" = {id, count, components},
    // plus "result_count") is unchanged - ItemStackTemplate's codec uses the same field names.
    private final Identifier id;
    private final ItemStackTemplate output;
    private final Ingredient ingredient;

    public MillingRecipe(Identifier id, ItemStackTemplate output, Ingredient input) {
        this.id = id;
        this.output = output;
        this.ingredient = input;
    }

    public MillingRecipe(Ingredient ingredient, ItemStackTemplate itemStack, int count) {
        this.id = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "milling");
        this.output = itemStack.withCount(count);
        this.ingredient = ingredient;
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

    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return BFRecipes.MILLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return BFRecipes.MILLING.get();
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(BFBlocks.GRISTMILL.get());
    }

    public interface RecipeFactory<T extends MillingRecipe> {
        T create(Ingredient ingredient, ItemStackTemplate result, int count);
    }

    public static class Type<T extends MillingRecipe> implements RecipeType<T> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "milling";
    }

    // `RecipeSerializer<T>` is a final Record in 26.3, not an interface anymore - a serializer is
    // now just a (MapCodec, StreamCodec) pair. This factory builds that record directly instead of
    // implementing it; BFRecipes registers the RecipeSerializer<MillingRecipe> that build() returns.
    public static class Serializer {
        private final MillingRecipe.RecipeFactory<MillingRecipe> recipeFactory;

        public Serializer(MillingRecipe.RecipeFactory<MillingRecipe> recipeFactory) {
            this.recipeFactory = recipeFactory;
        }

        public MillingRecipe read(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStackTemplate itemStack = ItemStackTemplate.STREAM_CODEC.decode(buf);
            int count = ByteBufCodecs.INT.decode(buf);
            return this.recipeFactory.create(ingredient, itemStack, count);
        }

        public void write(RegistryFriendlyByteBuf buf, MillingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.ingredient);
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
            ByteBufCodecs.INT.encode(buf, recipe.output.count());
        }

        public RecipeSerializer<MillingRecipe> build() {
            MapCodec<MillingRecipe> codec = RecordCodecBuilder.mapCodec((instance) ->
                    instance.group(
                            Ingredient.CODEC.fieldOf("ingredient")
                                    .forGetter((recipe) -> recipe.ingredient),
                            ItemStackTemplate.CODEC.fieldOf("result")
                                    .forGetter((recipe) -> recipe.output),
                            ExtraCodecs.intRange(1, 99).fieldOf("result_count")
                                    .forGetter((recipe) -> recipe.output.count())
                            )
                            .apply(instance, recipeFactory::create));
            StreamCodec<RegistryFriendlyByteBuf, MillingRecipe> streamCodec = StreamCodec.of(this::write, this::read);
            return new RecipeSerializer<>(codec, streamCodec);
        }
    }
}
