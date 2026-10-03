package net.hecco.bountifulfares.registry.integration;

import net.minecraft.core.HolderGetter;
import net.hecco.bountifulfares.platform.BFProperties;

import net.hecco.bountifulfares.definition.block.custom.PicketsBlock;
import net.hecco.bountifulfares.definition.block.custom.TrellisBlock;
import net.hecco.bountifulfares.definition.item.custom.TrellisBlockItem;
import net.hecco.bountifulfares.definition.item.integration.MapleMeadBottleItem;
import net.hecco.bountifulfares.definition.recipe.FermentingRecipeBuilder;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.hecco.bountifulfares.registry.content.BFSoundTypes;
import net.hecco.bountifulfares.registry.integration.interfaces.HasWoodTypes;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static net.hecco.bountifulfares.BountifulFares.NO_MANS_LAND_MOD_ID;

public class NoMansLandIntegration implements BFIntegration, HasWoodTypes {
    private static final List<String> WOOD_TYPES = new ArrayList<>(List.of("pine", "maple", "walnut", "willow"));

    @Override
    public List<String> modIds() {
        return List.of(NO_MANS_LAND_MOD_ID);
    }

    public static Supplier<Item> CANDIED_PEAR;
    public static Supplier<Item> MAPLE_MEAD_BOTTLE;

    @Override
    @SuppressWarnings("unchecked")
    public void registerContent() {
        for (String wood : WOOD_TYPES) {
            String key = NO_MANS_LAND_MOD_ID + "_" + wood;
            BFBlocks.TRELLISES.put(key, (Supplier<Block>) registerContent(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_trellis"), BFRegistryHelper.registerBlockNoItem(NO_MANS_LAND_MOD_ID, wood + "_trellis", () -> new TrellisBlock(BFProperties.block().noOcclusion().strength(1.0f).sound(BFSoundTypes.LIGHT_WOOD).mapColor(MapColor.NONE).instrument(NoteBlockInstrument.BASS).randomTicks().noOcclusion()))));
            registerContent(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_trellis"), BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID, wood + "_trellis", () -> new TrellisBlockItem(BFBlocks.TRELLISES.get(key).get(), BFProperties.blockItem(BFBlocks.TRELLISES.get(key).get()))));
            if (BFPlatformCompat.isDatagen()) {
                BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID, wood + "_planks", () -> new Item(BFProperties.item()));
            }
            BFBlocks.PICKETS.put(key, (Supplier<Block>) registerContent(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_pickets"), BFRegistryHelper.registerBlockNoItem(NO_MANS_LAND_MOD_ID, wood + "_pickets", () -> new PicketsBlock(BFProperties.block().ignitedByLava().mapColor(MapColor.NONE).strength(0.5F).sound(BFSoundTypes.LIGHT_WOOD).instrument(NoteBlockInstrument.BASS).forceSolidOff().noOcclusion()))));
            registerContent(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_pickets"), BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID, wood + "_pickets", () -> new BlockItem(BFBlocks.PICKETS.get(key).get(), BFProperties.blockItem(BFBlocks.PICKETS.get(key).get()))));
        }

        CANDIED_PEAR = (Supplier<Item>) registerContent(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "candied_pear"), BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID, "candied_pear", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F).build()))));
        // FoodProperties.Builder lost .effect(...) in 26.3 - on-eat status effects moved to the new
        // minecraft:consumable data component, set via the Item.Properties.food(FoodProperties,
        // Consumable) overload (same fix already established in BFItems.java's withEffect() helper).
        MAPLE_MEAD_BOTTLE = (Supplier<Item>) registerContent(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "maple_mead_bottle"), BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID, "maple_mead_bottle", () -> new MapleMeadBottleItem(BFProperties.item().craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.4f).alwaysEdible().build(),
                Consumable.builder()
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1))
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 600, 0), 0.3f))
                        .build()).stacksTo(16))));


        //DATAGEN DUMMY ITEMS
        if (BFPlatformCompat.isDatagen()) {
            BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID,  "pear", () -> new Item(BFProperties.item()));
            BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID,  "maple_syrup_bottle", () -> new Item(BFProperties.item()));
            BFRegistryHelper.registerItem(NO_MANS_LAND_MOD_ID,  "grilled_mushrooms", () -> new Item(BFProperties.item()));
        }
    }

    @Override
    public boolean shouldCreateDatapack() {
        return true;
    }

    @Override
    public @Nullable String getDatapackName() {
        return "No Man's Land x Bountiful Fares";
    }

    @Override
    public void recipeGeneration(RecipeOutput exporter, HolderGetter<Item> items) {
        for (String wood : WOOD_TYPES) {
            ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, BFBlocks.TRELLISES.get(NO_MANS_LAND_MOD_ID + "_" + wood).get())
                    .pattern("# #")
                    .pattern(" P ")
                    .pattern("# #")
                    .define('#', Items.STICK)
                    .define('P', BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_planks")))
                    .unlockedBy("has_stick", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, Items.STICK).build()))))
                    .unlockedBy("has_planks", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_planks"))).build()))))
                    .group("trellis")
                    .save(exporter);
            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, BFBlocks.PICKETS.get(NO_MANS_LAND_MOD_ID + "_" + wood).get(), 4).define('#', BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_planks"))).define('S', Items.STICK)
                    .pattern("#S#").unlockedBy("has_planks", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, wood + "_planks"))).build())))).save(exporter);
        }

        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.FOOD, CANDIED_PEAR.get(), 1)
                .requires(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "pear")))
                .requires(BFItemTags.SUGAR_INGREDIENTS)
                .unlockedBy("has_pear", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "pear"))).build()))))
                .save(exporter);

        FermentingRecipeBuilder.create(Ingredient.of(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "maple_syrup_bottle"))), MAPLE_MEAD_BOTTLE.get(), 1, 13529674)
                .unlockedBy("has_maple_syrup", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "maple_syrup_bottle"))).build()))))
                .save(exporter);


        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.FOOD, BFItems.MUSHROOM_STUFFED_POTATO.get())
                .requires(Items.BAKED_POTATO)
                .requires(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "grilled_mushrooms")))
                .requires(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "grilled_mushrooms")))
                .unlockedBy("has_baked_potato", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, Items.BAKED_POTATO).build()))))
                .unlockedBy("has_grilled_mushrooms", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(items, BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(NO_MANS_LAND_MOD_ID, "grilled_mushrooms"))).build()))))
                .save(exporter, "mushroom_stuffed_potato_from_grilled_mushrooms");
    }

    @Override
    public List<String> getWoodTypes() {
        return WOOD_TYPES;
    }
}
