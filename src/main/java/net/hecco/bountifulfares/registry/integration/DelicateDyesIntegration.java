package net.hecco.bountifulfares.registry.integration;

import net.minecraft.core.HolderGetter;
import net.hecco.bountifulfares.platform.BFProperties;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.JackOStrawBlock;
import net.hecco.bountifulfares.definition.item.component.TiffinContents;
import net.hecco.bountifulfares.definition.item.custom.TiffinItem;
import net.hecco.bountifulfares.registry.content.BFComponents;
import net.hecco.bountifulfares.registry.content.BFItems;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

import static net.hecco.bountifulfares.BountifulFares.ARTS_AND_CRAFTS_MOD_ID;
import static net.hecco.bountifulfares.BountifulFares.DELICATE_DYES_MOD_ID;
import static net.hecco.bountifulfares.registry.content.BFBlocks.createLightLevelFromLitBlockState;

public class DelicateDyesIntegration implements BFIntegration {
    public static final ArrayList<String> DYES = new ArrayList<>(List.of("coral", "umber", "canary", "wasabi", "sacramento", "sky", "blurple", "lavender", "sangria", "rose"));

    @Override
    public List<String> modIds() {
        return List.of(DELICATE_DYES_MOD_ID);
    }

    public static final Map<String, Supplier<Block>> JACK_O_STRAWS = new HashMap<>();
    public static final Map<String, Supplier<Item>> TIFFINS = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void registerContent() {
        for (String color : DYES) {
            if (BFPlatformCompat.isDatagen()) {
                BFRegistryHelper.registerItem(DELICATE_DYES_MOD_ID, color + "_wool", () -> new Item(BFProperties.item()));
                BFRegistryHelper.registerItem(DELICATE_DYES_MOD_ID, color + "_shulker_tiffin_front", () -> new Item(BFProperties.item()));
                BFRegistryHelper.registerItem(DELICATE_DYES_MOD_ID, color + "_shulker_tiffin_back", () -> new Item(BFProperties.item()));
            }
            JACK_O_STRAWS.put(color, (Supplier<Block>)registerContent(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID, color + "_jack_o_straw"), BFRegistryHelper.registerBlockNoItem(DELICATE_DYES_MOD_ID, color + "_jack_o_straw", () -> new JackOStrawBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).lightLevel(createLightLevelFromLitBlockState(12)).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion().pushReaction(PushReaction.POPPED)))));
            registerContent(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID, color + "_jack_o_straw"), BFRegistryHelper.registerItem(DELICATE_DYES_MOD_ID, color + "_jack_o_straw", () -> new BlockItem(JACK_O_STRAWS.get(color).get(), BFProperties.blockItem(JACK_O_STRAWS.get(color).get()))));

            //Item testForExisting = BuiltInRegistries.ITEM.getValue(BountifulFares.id(color + "_shulker_tiffin"));
            //if (testForExisting != null) {
            //    TIFFINS.put(color, () -> testForExisting);
            //}
            //else {
            //    TIFFINS.put(color, (Supplier<Item>)registerContent(BFRegistryHelper.registerItem(DELICATE_DYES_MOD_ID,color + "_shulker_tiffin", () ->
            //            new Item(createTiffinProperties()))));
            //}

            TIFFINS.put(color, (Supplier<Item>)registerContent(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID,color + "_shulker_tiffin"), BFRegistryHelper.registerItem(DELICATE_DYES_MOD_ID,color + "_shulker_tiffin", () ->
                    new TiffinItem(DyeColor.byName(color, DyeColor.WHITE), createTiffinProperties()))));
        }
    }

    @Override
    public boolean shouldCreateDatapack() {
        return true;
    }

    @Override
    public @Nullable String getDatapackName() {
        return "Delicate Dyes x Bountiful Fares";
    }

    @Override
    public void recipeGeneration(RecipeOutput exporter, HolderGetter<Item> items) {
        for (String color : DYES) {
            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.DECORATIONS, JACK_O_STRAWS.get(color).get())
                    .requires(BFItems.SUN_HAT.get())
                    .requires(Items.CARVED_PUMPKIN)
                    .requires(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID, color + "_wool")))
                    .requires(Items.STICK)
                    .unlockedBy("has_"+color+"_wool", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID, color + "_wool"))).build()))))
                    .unlockedBy("has_carved_pumpkin", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, Items.CARVED_PUMPKIN).build()))))
                    .unlockedBy("has_sun_hat", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BFItems.SUN_HAT.get()).build()))))
                    .save(exporter, color + "_jack_o_straw_with_carved_pumpkin");
            ShapelessRecipeBuilder.shapeless(items, RecipeCategory.DECORATIONS, JACK_O_STRAWS.get(color).get())
                    .requires(BFItems.SUN_HAT.get())
                    .requires(Items.PUMPKIN)
                    .requires(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID, color + "_wool")))
                    .requires(Items.STICK)
                    .unlockedBy("has_"+color+"_wool", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(DELICATE_DYES_MOD_ID, color + "_wool"))).build()))))
                    .unlockedBy("has_pumpkin", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, Items.PUMPKIN).build()))))
                    .unlockedBy("has_sun_hat", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BFItems.SUN_HAT.get()).build()))))
                    .save(exporter);
        }
    }

    private static Item.Properties createTiffinProperties() {
        return BFProperties.item()
                .stacksTo(1)
                .component(BFComponents.TIFFIN_CONTENTS.get(), new TiffinContents());
    }
}
