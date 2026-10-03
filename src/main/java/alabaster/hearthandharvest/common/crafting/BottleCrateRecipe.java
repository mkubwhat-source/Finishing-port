package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.common.registry.HHModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

/**
 * Nine identical bottles (3x3) pack into a bottle crate. The bottles are consumed (no glass bottle
 * remainders). JSON format unchanged: {@code category}, {@code input}, {@code result}.
 */
public class BottleCrateRecipe implements CraftingRecipe {
    private final CraftingBookCategory category;
    private final Ingredient input;
    private final ItemStackTemplate result;
    private PlacementInfo placementInfo;

    public BottleCrateRecipe(CraftingBookCategory category, Ingredient input, ItemStackTemplate result) {
        this.category = category;
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        if (container.width() != 3 || container.height() != 3) return false;
        for (int index = 0; index < container.size(); ++index) {
            ItemStack stack = container.getItem(index);
            if (stack.isEmpty() || !input.test(stack)) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput container) {
        return new ItemStack(result.item());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
        return NonNullList.withSize(container.size(), ItemStack.EMPTY);
    }

    @Override
    public CraftingBookCategory category() {
        return category;
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
        if (placementInfo == null) placementInfo = PlacementInfo.create(Collections.nCopies(9, input));
        return placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapedCraftingRecipeDisplay(3, 3, Collections.nCopies(9, input.display()),
                new SlotDisplay.ItemStackSlotDisplay(result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    @Override
    public RecipeSerializer<BottleCrateRecipe> getSerializer() {
        return HHModRecipeSerializers.BOTTLE_CRATE.get();
    }

    public static final MapCodec<BottleCrateRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(BottleCrateRecipe::category),
            Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result)
    ).apply(instance, BottleCrateRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BottleCrateRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, BottleCrateRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
            ItemStackTemplate.STREAM_CODEC, r -> r.result,
            BottleCrateRecipe::new);

    public static final RecipeSerializer<BottleCrateRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
