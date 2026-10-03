package com.sidden.flavored.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sidden.flavored.block.entity.KegBlockEntity;
import com.sidden.flavored.registry.FlavoredBlocks;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

/**
 * Recipe displays (26.3's client-facing recipe descriptions, used by the recipe book and recipe
 * viewers) for Flavored's three workstations.
 * <ul>
 * <li>Oven: a vanilla shaped-crafting display (3x3 grid) with the oven as station.</li>
 * <li>Keg: a vanilla furnace display - ingredient + "fuel" (the fermenter) - exactly how 1.21.1's
 * keg recipe book rendered it (Flavored's OverlayRecipeComponentMixin forced the furnace style).</li>
 * <li>Mixing bowl: its own display type (ingredients + vessel + liquid), registered as
 * {@code flavored:mixing}.</li>
 * </ul>
 */
public final class FlavoredRecipeDisplays {
    private FlavoredRecipeDisplays() {
    }

    public static List<RecipeDisplay> baking(BakingRecipe recipe) {
        return List.of(new ShapedCraftingRecipeDisplay(
                recipe.pattern().width(), recipe.pattern().height(),
                recipe.pattern().ingredients().stream().map(Ingredient::optionalIngredientToDisplay).toList(),
                new SlotDisplay.ItemStackSlotDisplay(recipe.resultTemplate()),
                new SlotDisplay.ItemSlotDisplay(FlavoredBlocks.OVEN.get().asItem())));
    }

    public static List<RecipeDisplay> fermenting(FermentingRecipe recipe) {
        return List.of(new FurnaceRecipeDisplay(
                recipe.ingredient().display(),
                recipe.fermenter().display(),
                new SlotDisplay.ItemStackSlotDisplay(recipe.resultTemplate()),
                new SlotDisplay.ItemSlotDisplay(FlavoredBlocks.KEG.get().asItem()),
                KegBlockEntity.MAX_PROGRESS,
                0.0F));
    }

    public static List<RecipeDisplay> mixing(MixingRecipe recipe) {
        return List.of(new MixingRecipeDisplay(
                recipe.ingredientsInput().stream().map(Ingredient::display).toList(),
                Ingredient.optionalIngredientToDisplay(recipe.vesselInput()),
                Ingredient.optionalIngredientToDisplay(recipe.liquidInput()),
                new SlotDisplay.ItemStackSlotDisplay(recipe.outputTemplate()),
                new SlotDisplay.ItemSlotDisplay(FlavoredBlocks.MIXING_BOWL.get().asItem())));
    }

    public record MixingRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay vessel, SlotDisplay liquid,
                                      SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
        public static final MapCodec<MixingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(MixingRecipeDisplay::ingredients),
                SlotDisplay.CODEC.fieldOf("vessel").forGetter(MixingRecipeDisplay::vessel),
                SlotDisplay.CODEC.fieldOf("liquid").forGetter(MixingRecipeDisplay::liquid),
                SlotDisplay.CODEC.fieldOf("result").forGetter(MixingRecipeDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(MixingRecipeDisplay::craftingStation)
        ).apply(i, MixingRecipeDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, MixingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), MixingRecipeDisplay::ingredients,
                SlotDisplay.STREAM_CODEC, MixingRecipeDisplay::vessel,
                SlotDisplay.STREAM_CODEC, MixingRecipeDisplay::liquid,
                SlotDisplay.STREAM_CODEC, MixingRecipeDisplay::result,
                SlotDisplay.STREAM_CODEC, MixingRecipeDisplay::craftingStation,
                MixingRecipeDisplay::new);
        public static final RecipeDisplay.Type<MixingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public RecipeDisplay.Type<MixingRecipeDisplay> type() {
            return FlavoredRecipeTypes.MIXING_DISPLAY.get();
        }

        @Override
        public boolean isEnabled(FeatureFlagSet flags) {
            return this.ingredients.stream().allMatch(d -> d.isEnabled(flags)) && this.vessel.isEnabled(flags)
                    && this.liquid.isEnabled(flags) && RecipeDisplay.super.isEnabled(flags);
        }
    }
}
