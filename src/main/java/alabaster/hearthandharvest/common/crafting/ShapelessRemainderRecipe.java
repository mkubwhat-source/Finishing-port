package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.common.registry.HHModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;
import java.util.Optional;

/**
 * Shapeless recipe with an explicit list of remainders: the n-th non-empty input slot leaves the
 * n-th remainder behind. JSON: vanilla shapeless fields plus {@code remainders} (list of item stacks,
 * {@code minecraft:air} / empty entries leave nothing).
 */
public class ShapelessRemainderRecipe extends ShapelessRecipe {
    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;
    private final List<Optional<ItemStackTemplate>> remainders;

    public ShapelessRemainderRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result,
                                    List<Ingredient> ingredients, List<Optional<ItemStackTemplate>> remainders) {
        super(commonInfo, bookInfo, result, ingredients);
        this.result = result;
        this.ingredients = ingredients;
        this.remainders = remainders;
    }

    public List<Optional<ItemStackTemplate>> getRemainderItems() {
        return remainders;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        int remainderIndex = 0;
        for (int i = 0; i < input.size(); i++) {
            if (!input.getItem(i).isEmpty()) {
                if (remainderIndex < remainders.size()) {
                    remaining.set(i, remainders.get(remainderIndex).map(ItemStackTemplate::create).orElse(ItemStack.EMPTY));
                }
                remainderIndex++;
            }
        }
        return remaining;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return (RecipeSerializer) HHModRecipeSerializers.SHAPELESS_REMAINDER.get();
    }

    /**
     * A remainder entry: an item stack, or {@code {}} for "nothing" (1.21.1's ItemStack.OPTIONAL_CODEC
     * format). Decoded as an ItemStackTemplate: recipes load before item components are bound, so no
     * ItemStack may be created here.
     */
    private static final com.mojang.serialization.Codec<Optional<ItemStackTemplate>> REMAINDER_CODEC =
            net.minecraft.util.ExtraCodecs.optionalEmptyMap(ItemStackTemplate.CODEC);

    public static final MapCodec<ShapelessRemainderRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(r -> r.commonInfo),
            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(r -> r.bookInfo),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
            Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(r -> r.ingredients),
            REMAINDER_CODEC.listOf().fieldOf("remainders").forGetter(ShapelessRemainderRecipe::getRemainderItems)
    ).apply(inst, ShapelessRemainderRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRemainderRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
            ItemStackTemplate.STREAM_CODEC, r -> r.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).apply(ByteBufCodecs.list()), ShapelessRemainderRecipe::getRemainderItems,
            ShapelessRemainderRecipe::new);

    public static final RecipeSerializer<ShapelessRemainderRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
