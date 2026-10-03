package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.common.registry.HHDataMaps;
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
import net.minecraft.world.item.Item;
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

import java.util.List;
import java.util.Optional;

/**
 * A keg (fermenting) recipe: up to four ingredients plus an optional input fluid become a result
 * fluid and/or a result item. Matching is done by the keg itself ({@link #matchesFluid} /
 * {@link #matchesItems}: any slot order, each ingredient one item), as in 1.21.1.
 * <p>
 * 26.3: the result item is an {@link ItemStackTemplate}, ingredients are never empty, and the
 * serializer is a (MapCodec, StreamCodec) record. JSON fields are unchanged.
 */
public class KegRecipe implements Recipe<RecipeWrapper> {
    public static final int DEFAULT_TIME = 600;

    @Nullable
    private final CaskRecipeBookTab tab;
    private final List<Ingredient> ingredients;
    private final FluidStack inputFluid;
    private final FluidStack resultFluid;
    private final Optional<ItemStackTemplate> resultItem;
    private final int fermentTime;
    private final float experience;
    private PlacementInfo placementInfo;

    public KegRecipe(@Nullable CaskRecipeBookTab tab, List<Ingredient> ingredients, FluidStack inputFluid, FluidStack resultFluid,
                     Optional<ItemStackTemplate> resultItem, int fermentTime, float experience) {
        this.tab = tab;
        this.ingredients = List.copyOf(ingredients);
        this.inputFluid = inputFluid;
        this.resultFluid = resultFluid;
        this.resultItem = resultItem;
        this.fermentTime = fermentTime;
        this.experience = experience;
    }

    @Nullable
    public CaskRecipeBookTab getRecipeBookTab() {
        return tab;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    public FluidStack getResultFluid() {
        return resultFluid;
    }

    public Optional<ItemStackTemplate> getResultTemplate() {
        return resultItem;
    }

    /** A new stack of the result item, or empty when the recipe only makes a fluid. */
    public ItemStack getResultItem() {
        return resultItem.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
    }

    public int getFermentTime() {
        return fermentTime;
    }

    public float getExperience() {
        return experience;
    }

    public boolean matchesFluid(FluidStack available) {
        if (inputFluid.isEmpty()) return true;
        return FluidStack.isSameFluidSameComponents(available, inputFluid) && available.getAmount() >= inputFluid.getAmount();
    }

    public boolean matchesItems(List<ItemStack> available) {
        if (ingredients.isEmpty()) return true;
        int[] remaining = new int[available.size()];
        for (int i = 0; i < available.size(); i++) {
            remaining[i] = available.get(i).getCount();
        }
        return assign(0, available, remaining);
    }

    private boolean assign(int index, List<ItemStack> available, int[] remaining) {
        if (index >= ingredients.size()) return true;
        Ingredient ingredient = ingredients.get(index);
        for (int slot = 0; slot < available.size(); slot++) {
            if (remaining[slot] <= 0 || !ingredient.test(available.get(slot))) continue;
            remaining[slot]--;
            if (assign(index + 1, available, remaining)) return true;
            remaining[slot]++;
        }
        return false;
    }

    /** The keg matches recipes itself (fluid + any-order items); vanilla lookups never match. */
    @Override
    public boolean matches(RecipeWrapper wrapper, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeWrapper wrapper) {
        return getResultItem();
    }

    /** The item shown for this recipe: the result item, else the bottle of the result fluid. */
    public ItemStack getDisplayResult() {
        if (resultItem.isPresent()) return resultItem.get().create();
        if (resultFluid.isEmpty()) return ItemStack.EMPTY;
        Item bottle = HHDataMaps.getBottleForFluid(resultFluid.getFluid());
        return bottle == null ? ItemStack.EMPTY : new ItemStack(bottle);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = ingredients.isEmpty() ? PlacementInfo.NOT_PLACEABLE : PlacementInfo.create(ingredients);
        }
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        ItemStack shown = getDisplayResult();
        SlotDisplay result = shown.isEmpty() ? SlotDisplay.Empty.INSTANCE : new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(shown));
        return List.of(new HHRecipeDisplays.FluidMachineDisplay(
                ingredients.stream().map(Ingredient::display).toList(), inputFluid, resultFluid, result,
                new SlotDisplay.ItemSlotDisplay(HHModBlocks.KEG.get().asItem()), fermentTime));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        if (tab == CaskRecipeBookTab.DRINKS) return HHRecipeBookCategories.FERMENTING_DRINKS.get();
        if (tab == CaskRecipeBookTab.MEALS) return HHRecipeBookCategories.FERMENTING_MEALS.get();
        return HHRecipeBookCategories.FERMENTING_MISC.get();
    }

    @Override
    public RecipeSerializer<KegRecipe> getSerializer() {
        return HHModRecipeSerializers.FERMENTING.get();
    }

    @Override
    public RecipeType<KegRecipe> getType() {
        return HHModRecipeTypes.FERMENTING.get();
    }

    public static final MapCodec<KegRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CaskRecipeBookTab.CODEC.optionalFieldOf("recipe_book_tab").forGetter(r -> Optional.ofNullable(r.tab)),
            Ingredient.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(KegRecipe::getIngredients),
            FluidStack.CODEC.optionalFieldOf("input_fluid", FluidStack.EMPTY).forGetter(KegRecipe::getInputFluid),
            FluidStack.CODEC.optionalFieldOf("result_fluid", FluidStack.EMPTY).forGetter(KegRecipe::getResultFluid),
            ItemStackTemplate.CODEC.optionalFieldOf("result_item").forGetter(KegRecipe::getResultTemplate),
            Codec.INT.optionalFieldOf("ferment_time", DEFAULT_TIME).forGetter(KegRecipe::getFermentTime),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(KegRecipe::getExperience)
    ).apply(instance, (tab, ingredients, inputFluid, resultFluid, resultItem, time, xp) ->
            new KegRecipe(tab.orElse(null), ingredients, inputFluid, resultFluid, resultItem, time, xp)));

    public static final StreamCodec<RegistryFriendlyByteBuf, KegRecipe> STREAM_CODEC = StreamCodec.of(
            (buf, recipe) -> {
                buf.writeUtf(recipe.tab != null ? recipe.tab.toString() : "");
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.ingredients);
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.inputFluid);
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.resultFluid);
                ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode(buf, recipe.resultItem);
                ByteBufCodecs.VAR_INT.encode(buf, recipe.fermentTime);
                buf.writeFloat(recipe.experience);
            },
            buf -> new KegRecipe(
                    CaskRecipeBookTab.findByName(buf.readUtf()),
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                    FluidStack.OPTIONAL_STREAM_CODEC.decode(buf),
                    FluidStack.OPTIONAL_STREAM_CODEC.decode(buf),
                    ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    buf.readFloat()));

    public static final RecipeSerializer<KegRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
