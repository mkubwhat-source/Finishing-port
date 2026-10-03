package alabaster.hearthandharvest.common.crafting;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import alabaster.hearthandharvest.common.registry.HHModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/** Salt + any food (not already salted): +25% nutrition (rounded up), -10% saturation, marked salted. */
public class SaltingRecipe extends CustomRecipe {

    private final CraftingBookCategory category;
    private final Ingredient saltIngredient;

    public SaltingRecipe(CraftingBookCategory category, Ingredient saltIngredient) {
        this.category = category;
        this.saltIngredient = saltIngredient;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) nonEmpty.add(stack);
        }
        if (nonEmpty.size() != 2) return false;

        boolean hasSalt = false;
        boolean hasFood = false;
        for (ItemStack stack : nonEmpty) {
            if (saltIngredient.test(stack)) {
                hasSalt = true;
            } else if (stack.is(ConventionalItemTags.FOODS)
                    && !stack.has(HHModDataComponents.SALTED.get())) {
                hasFood = true;
            }
        }
        return hasSalt && hasFood;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack foodStack = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && !saltIngredient.test(stack) && stack.is(ConventionalItemTags.FOODS)) {
                foodStack = stack;
                break;
            }
        }
        if (foodStack.isEmpty()) return ItemStack.EMPTY;

        FoodProperties original = foodStack.get(net.minecraft.core.component.DataComponents.FOOD);
        if (original == null) return ItemStack.EMPTY;

        FoodProperties modified = new FoodProperties(
                Math.max(1, (int) Math.ceil(original.nutrition() * 1.25f)),
                original.saturation() * 0.9f,
                original.canAlwaysEat()
        );

        ItemStack result = foodStack.copyWithCount(1);
        result.set(net.minecraft.core.component.DataComponents.FOOD, modified);
        result.set(HHModDataComponents.SALTED.get(), true);
        return result;
    }

    @Override
    public RecipeSerializer<SaltingRecipe> getSerializer() {
        return HHModRecipeSerializers.SALTING.get();
    }

    public static final MapCodec<SaltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(SaltingRecipe::category),
            Ingredient.CODEC.fieldOf("salt").forGetter(r -> r.saltIngredient)
    ).apply(inst, SaltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaltingRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, SaltingRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.saltIngredient,
            SaltingRecipe::new);

    public static final RecipeSerializer<SaltingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);
}
