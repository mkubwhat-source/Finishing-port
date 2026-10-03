package net.hecco.bountifulfares.definition.recipe;

import net.hecco.bountifulfares.definition.item.component.TiffinContents;
import net.hecco.bountifulfares.definition.item.custom.TiffinItem;
import net.hecco.bountifulfares.registry.content.BFComponents;
import net.hecco.bountifulfares.registry.misc.BFRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TiffinFoodCraftingRecipe extends CustomRecipe {
    public TiffinFoodCraftingRecipe() {
        super();
    }

    @Nullable
    private ItemStack matchedFoodItem;

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int tiffins = 0;
        ItemStack tiffin = ItemStack.EMPTY;
        for(int k = 0; k < input.size(); ++k) {
            ItemStack itemstack = input.getItem(k);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof TiffinItem) {
                    ++tiffins;
                    if (tiffins > 1) {
                        return false;
                    } else {
                        tiffin = itemstack;
                    }
                }
            }
        }
        if (!tiffin.isEmpty() && tiffin.has(BFComponents.TIFFIN_CONTENTS.get())) {
            TiffinContents contents = tiffin.get(BFComponents.TIFFIN_CONTENTS.get());
            // RecipeManager.getAllRecipesFor(RecipeType) is gone entirely in 26.3 (confirmed via
            // javap - only the untyped getRecipes(), returning Collection<RecipeHolder<?>>,
            // remains), so the type filtering that used to happen inside getAllRecipesFor is done
            // by hand here instead.
            // Only the server can enumerate recipes in 26.3 (and only it computes crafting results).
            if (!(level instanceof ServerLevel serverLevel)) {
                return false;
            }
            for (RecipeHolder<?> recipe : serverLevel.recipeAccess().getRecipes()) {
                if (recipe.value() instanceof ShapelessRecipe shapelessRecipe) {
                    ItemStack resultStack = shapelessRecipe.assemble(input);
                    if (contents.getItemStack().getCount() + resultStack.getCount() > contents.CAPACITY) {
                        return false;
                    }
                    if (contents.getItemStack().isEmpty() || contents.getItemStack().is(resultStack.getItem())) {
                        if (resultStack.has(DataComponents.FOOD)) {
                            List<ItemStack> newItemList = new ArrayList<>(input.items().size());
                            for (ItemStack i : input.items()) {
                                newItemList.add(i.copy());
                            }

                            for (int i = 0; i < newItemList.size(); i++) {
                                if (ItemStack.matches(newItemList.get(i), tiffin)) {
                                    newItemList.set(i, ItemStack.EMPTY);
                                    break;
                                }
                            }
                            Item remainder = TiffinItem.getRemainderItem(resultStack.getItem());
                            if (remainder != null) {
                                for (int i = 0; i < newItemList.size(); i++) {
                                    if (newItemList.get(i).isEmpty()) {
                                        newItemList.set(i, remainder.getDefaultInstance());
                                        break;
                                    }
                                }
                            }
                            CraftingInput newInput = CraftingInput.of(input.width(), input.height(), newItemList);
                            if (shapelessRecipe.matches(newInput, level)) {
                                matchedFoodItem = resultStack;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack tiffin = ItemStack.EMPTY;
        for(ItemStack itemStack : input.items()) {
            if (itemStack.getItem() instanceof TiffinItem) {
                tiffin = itemStack;
            }
        }
        if (!tiffin.isEmpty() && tiffin.has(BFComponents.TIFFIN_CONTENTS.get()) && matchedFoodItem != null) {
            TiffinContents.Mutable contents = new TiffinContents.Mutable(tiffin.get(BFComponents.TIFFIN_CONTENTS.get()));
            contents.item = matchedFoodItem.copyWithCount(contents.item.getCount());
            contents.item.grow(matchedFoodItem.getCount());
            ItemStack newTiffin = tiffin.copy();
            newTiffin.set(BFComponents.TIFFIN_CONTENTS.get(), contents.toImmutable());
            return newTiffin;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return BFRecipes.TIFFIN_FOOD_CRAFTING.get();
    }
}
