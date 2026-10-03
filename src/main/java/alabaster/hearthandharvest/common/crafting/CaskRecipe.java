package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModRecipeSerializers;
import alabaster.hearthandharvest.common.registry.HHModRecipeTypes;
import alabaster.hearthandharvest.common.registry.HHRecipeBookCategories;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import alabaster.hearthandharvest.platform.inventory.RecipeWrapper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A cask (aging) recipe: up to four ingredients, shapeless, age into the result over
 * {@code cookingtime} ticks. Vintages are cask recipes whose ingredient is a
 * {@code hearthandharvest:vintage} custom ingredient. JSON format unchanged from 1.21.1; 26.3 recipe
 * API (template result, never-empty ingredients, record serializer).
 */
public class CaskRecipe implements Recipe<RecipeWrapper> {
    public static final int INPUT_SLOTS = 4;

    @Nullable
    private final CaskRecipeBookTab tab;
    private final List<Ingredient> inputItems;
    private final ItemStackTemplate output;
    private final float experience;
    private final int cookTime;
    private PlacementInfo placementInfo;

    public CaskRecipe(@Nullable CaskRecipeBookTab tab, List<Ingredient> inputItems, ItemStackTemplate output, float experience, int cookTime) {
        this.tab = tab;
        this.inputItems = List.copyOf(inputItems);
        this.output = output;
        this.experience = experience;
        this.cookTime = cookTime;
    }

    @Nullable
    public CaskRecipeBookTab getRecipeBookTab() {
        return tab;
    }

    public List<Ingredient> getIngredients() {
        return inputItems;
    }

    public ItemStackTemplate getOutputTemplate() {
        return output;
    }

    /** A new stack of the result. */
    public ItemStack getOutput() {
        return output.create();
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return output.create();
    }

    public float getExperience() {
        return experience;
    }

    public int getCookTime() {
        return cookTime;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int j = 0; j < INPUT_SLOTS; ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) inputs.add(itemstack);
        }
        return IngredientMatcher.matchesExactly(inputs, inputItems);
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
        if (placementInfo == null) placementInfo = PlacementInfo.create(inputItems);
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new HHRecipeDisplays.FluidMachineDisplay(
                inputItems.stream().map(Ingredient::display).toList(), FluidStack.EMPTY, FluidStack.EMPTY,
                new SlotDisplay.ItemStackSlotDisplay(output),
                new SlotDisplay.ItemSlotDisplay(HHModBlocks.CASK.get().asItem()), cookTime));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        if (tab == CaskRecipeBookTab.DRINKS) return HHRecipeBookCategories.AGING_DRINKS.get();
        if (tab == CaskRecipeBookTab.MEALS) return HHRecipeBookCategories.AGING_MEALS.get();
        return HHRecipeBookCategories.AGING_MISC.get();
    }

    @Override
    public RecipeSerializer<CaskRecipe> getSerializer() {
        return HHModRecipeSerializers.AGING.get();
    }

    @Override
    public RecipeType<CaskRecipe> getType() {
        return HHModRecipeTypes.AGING.get();
    }

    public static final MapCodec<CaskRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            CaskRecipeBookTab.CODEC.optionalFieldOf("recipe_book_tab").forGetter(r -> Optional.ofNullable(r.tab)),
            Ingredient.CODEC.listOf(1, INPUT_SLOTS).fieldOf("ingredients").forGetter(CaskRecipe::getIngredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(CaskRecipe::getOutputTemplate),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(CaskRecipe::getExperience),
            Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(CaskRecipe::getCookTime)
    ).apply(inst, (tab, inputs, result, xp, time) -> new CaskRecipe(tab.orElse(null), inputs, result, xp, time)));

    public static final StreamCodec<RegistryFriendlyByteBuf, CaskRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {
                buffer.writeUtf(recipe.tab != null ? recipe.tab.toString() : "");
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.inputItems);
                ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
                buffer.writeFloat(recipe.experience);
                buffer.writeVarInt(recipe.cookTime);
            },
            buffer -> new CaskRecipe(
                    CaskRecipeBookTab.findByName(buffer.readUtf()),
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                    ItemStackTemplate.STREAM_CODEC.decode(buffer),
                    buffer.readFloat(),
                    buffer.readVarInt()));

    public static final RecipeSerializer<CaskRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
