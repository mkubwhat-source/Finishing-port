package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.common.block.entity.StompingBasinBlockEntity;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModRecipeSerializers;
import alabaster.hearthandharvest.common.registry.HHModRecipeTypes;
import alabaster.hearthandharvest.common.registry.HHRecipeBookCategories;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import alabaster.hearthandharvest.platform.inventory.RecipeWrapper;
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

import java.util.List;
import java.util.Optional;

/**
 * A stomping basin recipe: the listed ingredients (one item each, any slots) are stomped into a
 * fluid and/or an item. JSON unchanged from 1.21.1; 26.3 recipe API.
 */
public class StompingBasinRecipe implements Recipe<RecipeWrapper> {
    private final List<Ingredient> ingredients;
    private final FluidStack resultFluid;
    private final Optional<ItemStackTemplate> resultItem;
    private PlacementInfo placementInfo;

    public StompingBasinRecipe(List<Ingredient> ingredients, FluidStack resultFluid, Optional<ItemStackTemplate> resultItem) {
        this.ingredients = List.copyOf(ingredients);
        this.resultFluid = resultFluid;
        this.resultItem = resultItem;
    }

    @Override
    public boolean matches(RecipeWrapper wrapper, Level level) {
        return findSlotAssignment(wrapper) != null;
    }

    /** For each ingredient, the slot it takes an item from (or null if the recipe doesn't fit). */
    @Nullable
    public int[] findSlotAssignment(RecipeWrapper wrapper) {
        if (ingredients.isEmpty()) return null;
        int slots = Math.min(wrapper.size(), StompingBasinBlockEntity.ITEM_SLOTS);
        ItemStack[] stacks = new ItemStack[slots];
        int[] remaining = new int[slots];
        for (int slot = 0; slot < slots; slot++) {
            stacks[slot] = wrapper.getItem(slot);
            remaining[slot] = stacks[slot].getCount();
        }
        int[] assignment = new int[ingredients.size()];
        return assign(0, stacks, remaining, assignment) ? assignment : null;
    }

    private boolean assign(int index, ItemStack[] stacks, int[] remaining, int[] assignment) {
        if (index >= ingredients.size()) return true;
        Ingredient ingredient = ingredients.get(index);
        for (int slot = 0; slot < stacks.length; slot++) {
            if (remaining[slot] <= 0 || !ingredient.test(stacks[slot])) continue;
            remaining[slot]--;
            assignment[index] = slot;
            if (assign(index + 1, stacks, remaining, assignment)) return true;
            remaining[slot]++;
        }
        return false;
    }

    @Override
    public ItemStack assemble(RecipeWrapper wrapper) {
        return getResultItem();
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
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
        if (placementInfo == null) placementInfo = PlacementInfo.create(ingredients);
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        SlotDisplay result = resultItem.<SlotDisplay>map(SlotDisplay.ItemStackSlotDisplay::new).orElse(SlotDisplay.Empty.INSTANCE);
        return List.of(new HHRecipeDisplays.FluidMachineDisplay(ingredients.stream().map(Ingredient::display).toList(),
                FluidStack.EMPTY, resultFluid, result, new SlotDisplay.ItemSlotDisplay(HHModBlocks.STOMPING_BASIN.get().asItem()), 0));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return HHRecipeBookCategories.FERMENTING_MISC.get();
    }

    @Override
    public RecipeSerializer<StompingBasinRecipe> getSerializer() {
        return HHModRecipeSerializers.STOMPING.get();
    }

    @Override
    public RecipeType<StompingBasinRecipe> getType() {
        return HHModRecipeTypes.STOMPING.get();
    }

    public static final MapCodec<StompingBasinRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(StompingBasinRecipe::getIngredients),
            FluidStack.CODEC.optionalFieldOf("result_fluid", FluidStack.EMPTY).forGetter(StompingBasinRecipe::getResultFluid),
            ItemStackTemplate.CODEC.optionalFieldOf("result_item").forGetter(StompingBasinRecipe::getResultTemplate)
    ).apply(instance, StompingBasinRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StompingBasinRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), StompingBasinRecipe::getIngredients,
            FluidStack.OPTIONAL_STREAM_CODEC, StompingBasinRecipe::getResultFluid,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), StompingBasinRecipe::getResultTemplate,
            StompingBasinRecipe::new);

    public static final RecipeSerializer<StompingBasinRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
