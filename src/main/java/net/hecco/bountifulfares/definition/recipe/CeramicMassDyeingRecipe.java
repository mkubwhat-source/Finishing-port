package net.hecco.bountifulfares.definition.recipe;

import com.google.common.collect.Lists;
import net.hecco.bountifulfares.definition.block.entity.DyeableCeramicBlockEntity;
import net.hecco.bountifulfares.definition.item.custom.CeramicDishBlockItem;
import net.hecco.bountifulfares.definition.item.custom.DyeableCeramicBlockItem;
import net.hecco.bountifulfares.registry.misc.BFRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class CeramicMassDyeingRecipe extends CustomRecipe {
    public CeramicMassDyeingRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput inventory, Level world) {
        boolean difference = false;
        Item dyeItem = null;
        int dyeCount = 0;
        ItemStack ceramicItemStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack2 = inventory.getItem(i);
            if (itemStack2.isEmpty()) {
                continue;
            }
            if (itemStack2.getItem() instanceof DyeItem
            && !BuiltInRegistries.ITEM.getKey(itemStack2.getItem()).equals(Identifier.fromNamespaceAndPath("unidye", "custom_dye"))) {
                dyeCount++;
                if (dyeItem == null) {
                    dyeItem = itemStack2.getItem();
                } else if (dyeItem != itemStack2.getItem()) {
                    difference = true;
                }
                continue;
            }
            if (itemStack2.getItem() instanceof DyeableCeramicBlockItem
                || itemStack2.getItem() instanceof CeramicDishBlockItem) {
                if (ceramicItemStack.isEmpty()) {
                    ceramicItemStack = itemStack2;
                    continue;
                } else if (ceramicItemStack.is(itemStack2.getItem())){
                    if(DyedItemColor.getOrDefault(ceramicItemStack, DyeableCeramicBlockEntity.DEFAULT_COLOR)
                    == DyedItemColor.getOrDefault(itemStack2, DyeableCeramicBlockEntity.DEFAULT_COLOR)){
                        continue;
                    }else{
                        return false;
                    }
                }
            }
            return false;
        }
        return difference || dyeCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ArrayList<DyeItem> list = Lists.newArrayList();
        ItemStack ceramicStack = ItemStack.EMPTY;
        int ceramicCount = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack itemStack2 = input.getItem(i);
            if (itemStack2.isEmpty()) continue;
            Item item = itemStack2.getItem();
            if (item instanceof DyeItem) {
                list.add((DyeItem) item);
            }
            if (item instanceof DyeableCeramicBlockItem
                    || item instanceof CeramicDishBlockItem){
                ceramicCount++;
                if(ceramicStack.isEmpty()){
                    ceramicStack = itemStack2;
                }
            }
        }
        if (list.isEmpty() || ceramicStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        java.util.List<DyeColor> dyeColors = list.stream().map(CeramicMassDyeingRecipe::dyeColorOf).toList();
        ItemStack itemStack = DyedItemColor.applyDyes(ceramicStack, dyeColors);
        itemStack.setCount(ceramicCount);
        return itemStack;
    }

    /** DyeItem lost its own getDyeColor() - dyes are now a single generic DyeItem class
     * registered per color through the Items.DYE ColorCollection (see the identical note on
     * TiffinColoringRecipe), so recovering the DyeColor for a given dye Item means
     * reverse-searching that ColorCollection. */
    private static DyeColor dyeColorOf(DyeItem dyeItem) {
        for (DyeColor color : DyeColor.values()) {
            if (Items.DYE.pick(color) == dyeItem) {
                return color;
            }
        }
        return DyeColor.WHITE;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return BFRecipes.CERAMIC_MASS_DYEING.get();
    }
}
