package net.hecco.bountifulfares.registry.integration;

import net.minecraft.core.HolderGetter;
import net.hecco.bountifulfares.platform.BFProperties;

import net.hecco.bountifulfares.definition.block.integration.AppledogBlock;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static net.hecco.bountifulfares.BountifulFares.*;

public class AppledogIntegration implements BFIntegration {

    @Override
    public List<String> modIds() {
        return List.of(APPLEDOG_MOD_ID);
    }

    public static Supplier<Block> APPLEDOG_BLOCK;

    @Override
    public void registerContent() {
        APPLEDOG_BLOCK = registerBlock("appledog_block", () -> new AppledogBlock(BFProperties.blockCopy(BFBlocks.APPLE_BLOCK.get()).strength(1f, 1000f)));

        //DATAGEN DUMMY ITEMS
        if (BFPlatformCompat.isDatagen()) {
            BFRegistryHelper.registerItem(APPLEDOG_MOD_ID,  "dogapple", () -> new Item(BFProperties.item()));
        }
    }

    @SuppressWarnings("unchecked")
    private Supplier<net.minecraft.world.level.block.Block> registerBlock(String id, Supplier<Block> supplier) {
        Supplier<Block> block = (Supplier<Block>) registerContent(Identifier.fromNamespaceAndPath(APPLEDOG_MOD_ID, id), BFRegistryHelper.registerBlockNoItem(APPLEDOG_MOD_ID, id, supplier));
        registerContent(Identifier.fromNamespaceAndPath(APPLEDOG_MOD_ID, id), BFRegistryHelper.registerItem(APPLEDOG_MOD_ID, id, () -> new BlockItem(block.get(), BFProperties.blockItem(block.get()).rarity(Rarity.EPIC))));
        return block;
    }

    @Override
    public boolean shouldCreateDatapack() {
        return true;
    }

    @Override
    public String getDatapackName() {
        return "Bountiful Appledogs";
    }

    @Override
    public void recipeGeneration(RecipeOutput output, HolderGetter<Item> items) {
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, APPLEDOG_BLOCK.get()).requires(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(APPLEDOG_MOD_ID, "dogapple")), 9).unlockedBy("has_dogapple",
                CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(APPLEDOG_MOD_ID, "dogapple"))).build())))
        ).save(output);
    }
}
