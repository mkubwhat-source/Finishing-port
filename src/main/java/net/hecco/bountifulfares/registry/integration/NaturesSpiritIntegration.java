package net.hecco.bountifulfares.registry.integration;

import net.minecraft.core.HolderGetter;
import net.hecco.bountifulfares.platform.BFProperties;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.PicketsBlock;
import net.hecco.bountifulfares.definition.block.custom.TrellisBlock;
import net.hecco.bountifulfares.definition.item.custom.TrellisBlockItem;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFSoundTypes;
import net.hecco.bountifulfares.registry.integration.interfaces.HasWoodTypes;
import net.hecco.bountifulfares.lib.compat.CompatManager;
import net.hecco.bountifulfares.lib.compat.ModIntegration;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static net.hecco.bountifulfares.BountifulFares.NATURES_SPIRIT_MOD_ID;

public class NaturesSpiritIntegration implements BFIntegration, HasWoodTypes {
    private static final List<String> WOOD_TYPES = new ArrayList<>(List.of("aspen", "cedar", "coconut", "cypress", "fir", "ghaf", "joshua", "larch", "mahogany", "maple", "olive", "palo_verde", "sugi", "willow", "wisteria"));

    @Override
    public List<String> modIds() {
        return List.of(NATURES_SPIRIT_MOD_ID);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerContent() {
        for (String wood : WOOD_TYPES) {
            String key = NATURES_SPIRIT_MOD_ID + "_" + wood;
            BFBlocks.TRELLISES.put(key, (Supplier<Block>) registerContent(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_trellis"), BFRegistryHelper.registerBlockNoItem(NATURES_SPIRIT_MOD_ID, wood + "_trellis", () -> new TrellisBlock(BFProperties.block().noOcclusion().strength(1.0f).sound(BFSoundTypes.LIGHT_WOOD).mapColor(MapColor.NONE).instrument(NoteBlockInstrument.BASS).randomTicks().noOcclusion()))));
            registerContent(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_trellis"), BFRegistryHelper.registerItem(NATURES_SPIRIT_MOD_ID, wood + "_trellis", () -> new TrellisBlockItem(BFBlocks.TRELLISES.get(key).get(), BFProperties.blockItem(BFBlocks.TRELLISES.get(key).get()))));
            if (BFPlatformCompat.isDatagen()) {
                BFRegistryHelper.registerItem(NATURES_SPIRIT_MOD_ID, wood + "_planks", () -> new Item(BFProperties.item()));
            }
            BFBlocks.PICKETS.put(key, (Supplier<Block>) registerContent(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_pickets"), BFRegistryHelper.registerBlockNoItem(NATURES_SPIRIT_MOD_ID, wood + "_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()))));
            registerContent(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_pickets"), BFRegistryHelper.registerItem(NATURES_SPIRIT_MOD_ID, wood + "_pickets", () -> new BlockItem(BFBlocks.PICKETS.get(key).get(), BFProperties.blockItem(BFBlocks.PICKETS.get(key).get()))));
        }
    }

    @Override
    public boolean shouldCreateDatapack() {
        return true;
    }

    @Override
    public @Nullable String getDatapackName() {
        return "Nature's Spirit x Bountiful Fares";
    }

    @Override
    public void recipeGeneration(RecipeOutput exporter, HolderGetter<Item> items) {
        for (String wood : WOOD_TYPES) {
            ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BFBlocks.TRELLISES.get(NATURES_SPIRIT_MOD_ID + "_" + wood).get())
                    .pattern("# #")
                    .pattern(" P ")
                    .pattern("# #")
                    .define('#', Items.STICK)
                    .define('P', BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_planks")))
                    .unlockedBy("has_stick", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, Items.STICK).build()))))
                    .unlockedBy("has_planks", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_planks"))).build()))))
                    .group("trellis")
                    .save(exporter);
            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, BFBlocks.PICKETS.get(NATURES_SPIRIT_MOD_ID + "_" + wood).get(), 4).define('#', BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_planks"))).define('S', Items.STICK)
                    .pattern("#S#").unlockedBy("has_planks", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NATURES_SPIRIT_MOD_ID, wood + "_planks"))).build())))).save(exporter);
        }
    }

    @Override
    public List<String> getWoodTypes() {
        return WOOD_TYPES;
    }
}
