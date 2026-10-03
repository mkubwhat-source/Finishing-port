package net.hecco.bountifulfares.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.trigger.*;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.hecco.bountifulfares.registry.content.BFPotions;
import net.hecco.bountifulfares.registry.tags.BFBlockTags;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.minecraft.advancements.*;
import net.minecraft.advancements.triggers.*;
import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BFAdvancementProvider extends FabricAdvancementProvider {


    public BFAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        // ItemPredicate.Builder.item().of(...)/BlockPredicate.Builder.block().of(...)/
        // EntityPredicate.Builder.entity().of(...) all gained a leading HolderGetter<T> parameter
        // in 26.3 (confirmed via javap) - resolve them once here from the registryLookup this
        // method is already handed, rather than threading FabricAdvancementProvider's own
        // registryLookupFuture through by hand.
        HolderGetter<Item> items = registryLookup.lookupOrThrow(Registries.ITEM);
        HolderGetter<Block> blocks = registryLookup.lookupOrThrow(Registries.BLOCK);
        HolderGetter<EntityType<?>> entityTypes = registryLookup.lookupOrThrow(Registries.ENTITY_TYPE);
        // 26.3 validates that only root advancements carry a background texture ("Only
        // advancement roots can have background"); 1.21.1 silently ignored it on children, so
        // every child's copy of the root's farmland_moist background is dropped - no visual change.
        AdvancementHolder root_advancement = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.PASSION_FRUIT.get()),
                        Component.translatable("advancement.bountifulfares.bountiful_fares"),
                        Component.translatable("advancement.bountifulfares.bountiful_fares.description"), Optional.of(new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("block/farmland_moist"))), AdvancementType.TASK,
                        false,
                        false,
                        false))
                .addCriterion("consume_item", ConsumeItemTrigger.TriggerInstance.usedItem())
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":bountiful_fares"));
        AdvancementHolder make_first_food = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.LEEK_STEW.get()),
                        Component.translatable("advancement.bountifulfares.make_first_food"),
                        Component.translatable("advancement.bountifulfares.make_first_food.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("make_first_food", ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(items, BFItemTags.MEALS)))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":make_first_food"));

        Advancement.Builder eat_all_food = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.CRUSTED_BEEF.get()),
                        Component.translatable("advancement.bountifulfares.eat_all_food"),
                        Component.translatable("advancement.bountifulfares.eat_all_food.description"), Optional.empty(), AdvancementType.GOAL,
                        true,
                        true,
                        false))
                .parent(make_first_food);
        // 26.3: an Item's default components are no longer built in its constructor - they are
        // produced by BuiltInRegistries.DATA_COMPONENT_INITIALIZERS against a registry lookup and
        // bound later during resource loading, which datagen never does ("Components not bound
        // yet" from Item.components()). Compute them the same way vanilla's own
        // RegistryComponentsReport does - DataComponentInitializers.build(lookup) - without
        // binding anything globally.
        Map<Item, DataComponentMap> itemComponents = computeItemComponents(registryLookup);
        for (Item i : BuiltInRegistries.ITEM) {
            DataComponentMap components = itemComponents.getOrDefault(i, DataComponentMap.EMPTY);
            if (BuiltInRegistries.ITEM.getKey(i).getNamespace().equals(BountifulFares.MOD_ID) && components.has(DataComponents.FOOD) && i != BFItems.DIRT_STEW.get()) {
                if (!BuiltInRegistries.ITEM.getKey(i).getPath().contains("tiffin")) {
                    eat_all_food.addCriterion(BuiltInRegistries.ITEM.getKey(i).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(items, i)));
                }
            }
        }
        eat_all_food.save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":eat_all_food"));

        Advancement.Builder eat_all_bad_foods = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(Items.CHICKEN),
                        Component.translatable("advancement.bountifulfares.eat_all_bad_foods"),
                        Component.translatable("advancement.bountifulfares.eat_all_bad_foods.description"), Optional.empty(), AdvancementType.CHALLENGE,
                        true,
                        true,
                        true))
                .parent(make_first_food);
        for (Item i : BuiltInRegistries.ITEM) {
            DataComponentMap components = itemComponents.getOrDefault(i, DataComponentMap.EMPTY);
            if (
                    (BuiltInRegistries.ITEM.getKey(i).getNamespace().equals(BountifulFares.MOD_ID) ||
                    BuiltInRegistries.ITEM.getKey(i).getNamespace().equals("minecraft")) &&
                    components.has(DataComponents.FOOD) && i != BFItems.DIRT_STEW.get()) {
                // FoodProperties no longer carries its consume effects (its effects() accessor
                // is gone) - the harmful-effect status list moved onto the item's separate
                // DataComponents.CONSUMABLE component, as a list of ConsumeEffect entries; the
                // status-effect ones are ApplyStatusEffectsConsumeEffect (confirmed via javap).
                Consumable consumable = components.get(DataComponents.CONSUMABLE);
                if (consumable != null && consumable.onConsumeEffects().stream()
                        .filter(effect -> effect instanceof ApplyStatusEffectsConsumeEffect)
                        .flatMap(effect -> ((ApplyStatusEffectsConsumeEffect) effect).effects().stream())
                        .anyMatch((effect) -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)) {
                    eat_all_bad_foods.addCriterion(BuiltInRegistries.ITEM.getKey(i).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(items, i)));
                }
            }
        }
        eat_all_bad_foods.save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":eat_all_bad_foods"));
        AdvancementHolder pick_fruit = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(Items.APPLE),
                        Component.translatable("advancement.bountifulfares.pick_fruit"),
                        Component.translatable("advancement.bountifulfares.pick_fruit.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("pick_fruit",
                        PickFruitInteractionTrigger.TriggerInstance.pickedAnyFruit()
                )
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":pick_fruit"));

        AdvancementHolder obtain_all_fruit = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.PLUM.get()),
                        Component.translatable("advancement.bountifulfares.obtain_all_fruit"),
                        Component.translatable("advancement.bountifulfares.obtain_all_fruit.description"), Optional.empty(), AdvancementType.CHALLENGE,
                        true,
                        true,
                        false))
                .parent(pick_fruit)
                .addCriterion("apple", InventoryChangeTrigger.TriggerInstance.hasItems(Items.APPLE))
                .addCriterion("orange", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.ORANGE.get()))
                .addCriterion("lemon", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.LEMON.get()))
                .addCriterion("plum", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.PLUM.get()))
                .addCriterion("hoary_apple", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.HOARY_APPLE.get()))
                .addCriterion("golden_apple", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLDEN_APPLE))
                .addCriterion("coconut", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.COCONUT.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_all_fruit"));

        AdvancementHolder obtain_lemon_block = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.LEMON.get()),
                        Component.translatable("advancement.bountifulfares.how_easy"),
                        Component.translatable("advancement.bountifulfares.how_easy.description"), Optional.empty(), AdvancementType.CHALLENGE,
                        true,
                        true,
                        true))
                .parent(pick_fruit)
                .addCriterion("obtain_lemon_block", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.LEMON_BLOCK.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_lemon_block"));

        AdvancementHolder plant_on_trellis = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.TRELLISES.get("oak").get().asItem()),
                        Component.translatable("advancement.bountifulfares.plant_on_trellis"),
                        Component.translatable("advancement.bountifulfares.plant_on_trellis.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("plant_on_trellis", PlantOnTrellisTrigger.TriggerInstance.plantedAnyPlant())
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":plant_on_trellis"));

        AdvancementHolder place_gristmill = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.GRISTMILL.get().asItem()),
                        Component.translatable("advancement.bountifulfares.place_gristmill"),
                        Component.translatable("advancement.bountifulfares.place_gristmill.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("place_gristmill", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.GRISTMILL.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":place_gristmill"));

        AdvancementHolder obtain_feldspar = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.FELDSPAR.get()),
                        Component.translatable("advancement.bountifulfares.obtain_feldspar"),
                        Component.translatable("advancement.bountifulfares.obtain_feldspar.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(place_gristmill)
                .addCriterion("obtain_feldspar", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.FELDSPAR.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_feldspar"));

        AdvancementHolder obtain_ceramic_clay = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.CERAMIC_CLAY.get()),
                        Component.translatable("advancement.bountifulfares.obtain_ceramic_clay"),
                        Component.translatable("advancement.bountifulfares.obtain_ceramic_clay.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_feldspar)
                .addCriterion("obtain_ceramic_clay", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.CERAMIC_CLAY.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_ceramic_clay"));

        AdvancementHolder obtain_flour = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.FLOUR.get()),
                        Component.translatable("advancement.bountifulfares.obtain_flour"),
                        Component.translatable("advancement.bountifulfares.obtain_flour.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(place_gristmill)
                .addCriterion("obtain_flour", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.FLOUR.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_flour"));

        AdvancementHolder throw_flour_as_cover = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.FLOUR.get()),
                        Component.translatable("advancement.bountifulfares.throw_flour_as_cover"),
                        Component.translatable("advancement.bountifulfares.throw_flour_as_cover.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_flour)
                .addCriterion("throw_flour_as_cover", CriteriaTriggers.USING_ITEM.createCriterion(new UsingItemTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(items, BFItems.FLOUR.get()).build()))))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":throw_flour_as_cover"));

        AdvancementHolder obtain_artisan_brush = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.ARTISAN_BRUSH.get()),
                        Component.translatable("advancement.bountifulfares.obtain_artisan_brush"),
                        Component.translatable("advancement.bountifulfares.obtain_artisan_brush.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_ceramic_clay)
                .addCriterion("obtain_artisan_brush", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.ARTISAN_BRUSH.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_artisan_brush"));

        AdvancementHolder dye_ceramic_block = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.CERAMIC_TILES.get().asItem()),
                        Component.translatable("advancement.bountifulfares.dye_ceramic_block"),
                        Component.translatable("advancement.bountifulfares.dye_ceramic_block.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_artisan_brush)
                .addCriterion("use_on_ceramic_block", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, BFBlockTags.DYEABLE_CERAMIC_BLOCKS)), ItemPredicate.Builder.item().of(items, BFItems.ARTISAN_BRUSH.get())))
                .addCriterion("click_on_ceramic_item", UseArtisanBrushInInventoryTrigger.TriggerInstance.use())
                .requirements(AdvancementRequirements.anyOf(List.of("use_on_ceramic_block", "click_on_ceramic_item")))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":dye_ceramic_block"));

        AdvancementHolder dye_leather_armor_on_armor_stand = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(Items.LEATHER_LEGGINGS),
                        Component.translatable("advancement.bountifulfares.dye_leather_armor_on_armor_stand"),
                        Component.translatable("advancement.bountifulfares.dye_leather_armor_on_armor_stand.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_artisan_brush)
                .addCriterion("dye_leather_armor_on_armor_stand", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(items, BFItems.ARTISAN_BRUSH.get()), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityTypes.ARMOR_STAND)))))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":dye_leather_armor_on_armor_stand"));

        AdvancementHolder obtain_fermentation_vessel = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.FERMENTATION_VESSEL.get().asItem()),
                        Component.translatable("advancement.bountifulfares.obtain_fermentation_vessel"),
                        Component.translatable("advancement.bountifulfares.obtain_fermentation_vessel.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_ceramic_clay)
                .addCriterion("obtain_fermentation_vessel", InventoryChangeTrigger.TriggerInstance.hasItems(BFBlocks.FERMENTATION_VESSEL.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_fermentation_vessel"));
        AdvancementHolder eat_ancient_fruit = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.HOARY_APPLE.get()),
                        Component.translatable("advancement.bountifulfares.eat_ancient_fruit"),
                        Component.translatable("advancement.bountifulfares.eat_ancient_fruit.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(pick_fruit)
                .addCriterion("eat_ancient_fruit", ConsumeItemTrigger.TriggerInstance.usedItem(ItemPredicate.Builder.item().of(items, BFItems.HOARY_APPLE.get(), BFItems.LAPISBERRIES.get())))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":eat_ancient_fruit"));
        AdvancementHolder place_all_baked_goods = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.ARTISAN_BREAD.get().asItem()),
                        Component.translatable("advancement.bountifulfares.place_all_baked_goods"),
                        Component.translatable("advancement.bountifulfares.place_all_baked_goods.description"), Optional.empty(), AdvancementType.CHALLENGE,
                        true,
                        true,
                        false))
                .parent(obtain_flour)
                .addCriterion("cake", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, Blocks.CAKE))
                .addCriterion("cocoa_cake", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.COCOA_CAKE.get()))
                .addCriterion("artisan_bread", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.ARTISAN_BREAD.get()))
                .addCriterion("artisan_cookie", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.ARTISAN_COOKIE.get()))
                .addCriterion("apple_pie", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.APPLE_PIE.get()))
                .addCriterion("orange_pie", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.ORANGE_PIE.get()))
                .addCriterion("lemon_pie", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.LEMON_PIE.get()))
                .addCriterion("plum_pie", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.PLUM_PIE.get()))
                .addCriterion("hoary_pie", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.HOARY_PIE.get()))
                .addCriterion("passion_fruit_tart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.PASSION_FRUIT_TART.get()))
                .addCriterion("elderberry_tart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.ELDERBERRY_TART.get()))
                .addCriterion("glow_berry_tart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.GLOW_BERRY_TART.get()))
                .addCriterion("sweet_berry_tart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.SWEET_BERRY_TART.get()))
                .addCriterion("lapisberry_tart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.LAPISBERRY_TART.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":place_all_baked_goods"));
        AdvancementHolder eat_citrus_essence = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.CITRUS_ESSENCE.get()),
                        Component.translatable("advancement.bountifulfares.eat_citrus_essence"),
                        Component.translatable("advancement.bountifulfares.eat_citrus_essence.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false
                ))
                .parent(obtain_fermentation_vessel)
                .addCriterion("eat_citrus_essence", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.CITRUS_ESSENCE.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":eat_citrus_essence"));

        AdvancementHolder acidify_effect_2_levels = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(Items.POTION, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(BFPotions.ACIDIC)).build()),
                        Component.translatable("advancement.bountifulfares.acidify_effect_2_levels"),
                        Component.translatable("advancement.bountifulfares.acidify_effect_2_levels.description"), Optional.empty(), AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                ))
                .parent(eat_citrus_essence)
                .addCriterion("acidify_effect_2_levels", AcidifyEffectTrigger.TriggerInstance.acidified(2))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":acidify_effect_2_levels"));

        AdvancementHolder obtain_sun_hat = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.SUN_HAT.get()),
                        Component.translatable("advancement.bountifulfares.obtain_sun_hat"),
                        Component.translatable("advancement.bountifulfares.obtain_sun_hat.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("obtain_sun_hat", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.SUN_HAT.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_sun_hat"));
        AdvancementHolder eat_all_candy = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.CANDY.get()),
                        Component.translatable("advancement.bountifulfares.eat_all_candy"),
                        Component.translatable("advancement.bountifulfares.eat_all_candy.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(make_first_food)
                .addCriterion("candy", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.CANDY.get()))
                .addCriterion("piquant", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.PIQUANT_CANDY.get()))
                .addCriterion("sour", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.SOUR_CANDY.get()))
                .addCriterion("bitter", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.BITTER_CANDY.get()))
                .addCriterion("strange", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.STRANGE_CANDY.get()))
                .addCriterion("candied_apple", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.CANDIED_APPLE.get()))
                .addCriterion("candied_orange", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.CANDIED_ORANGE.get()))
                .addCriterion("candied_lemon", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.CANDIED_LEMON.get()))
                .addCriterion("candied_plum", ConsumeItemTrigger.TriggerInstance.usedItem(items, BFItems.CANDIED_PLUM.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":eat_all_candy"));
//        AdvancementEntry gorge = Advancement.Builder.create()
//                .display(new AdvancementDisplay(new ItemStackTemplate(ModItems.SOUR_CANDY),
//                        Text.translatable("advancement.bountifulfares.gorge"),
//                        Text.translatable("advancement.bountifulfares.gorge.description"), Optional.of(Identifier.of("minecraft:textures/block/farmland_moist.png")), AdvancementFrame.TASK,
//                        true,
//                        true,
//                        false))
//                .parent(eat_all_candy)
//                .criterion("gorge", EffectsChangedCriterion.Conditions.create(EntityEffectPredicate.create().withEffect(ModEffects.GORGING)))
//                .build(consumer, BountifulFares.MOD_ID + ":gorge");
        AdvancementHolder obtain_tea_cups = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.TEA_LEAVES.get()),
                        Component.translatable("advancement.bountifulfares.obtain_tea_blends"),
                        Component.translatable("advancement.bountifulfares.obtain_tea_blends.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_ceramic_clay)
                .addCriterion("green", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.GREEN_TEA_CUP.get()))
                .addCriterion("black", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.BLACK_TEA_CUP.get()))
                .addCriterion("chamomile", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.CHAMOMILE_TEA_CUP.get()))
                .addCriterion("honeysuckle", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.HONEYSUCKLE_TEA_CUP.get()))
                .addCriterion("bellflower", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.BELLFLOWER_TEA_CUP.get()))
                .addCriterion("torchflower", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.TORCHFLOWER_TEA_CUP.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_tea_blends"));
        AdvancementHolder place_all_tea_candles = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.GREEN_TEA_CANDLE.get().asItem()),
                        Component.translatable("advancement.bountifulfares.place_all_tea_candles"),
                        Component.translatable("advancement.bountifulfares.place_all_tea_candles.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_tea_cups)
                .addCriterion("green", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.GREEN_TEA_CANDLE.get()))
                .addCriterion("black", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.BLACK_TEA_CANDLE.get()))
                .addCriterion("chamomile", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.CHAMOMILE_CANDLE.get()))
                .addCriterion("honeysuckle", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.HONEYSUCKLE_CANDLE.get()))
                .addCriterion("bellflower", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.BELLFLOWER_CANDLE.get()))
                .addCriterion("torchflower", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.TORCHFLOWER_CANDLE.get()))
                .addCriterion("walnut", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(blocks, BFBlocks.WALNUT_CANDLE.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":place_all_tea_candles"));
        AdvancementHolder obtain_walnut = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.WALNUT.get()),
                        Component.translatable("advancement.bountifulfares.obtain_walnut"),
                        Component.translatable("advancement.bountifulfares.obtain_walnut.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("obtain_walnut", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.WALNUT.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_walnut"));
        AdvancementHolder obtain_spongekin_seeds = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.SPONGEKIN_SEEDS.get()),
                        Component.translatable("advancement.bountifulfares.obtain_spongekin_seeds"),
                        Component.translatable("advancement.bountifulfares.obtain_spongekin_seeds.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("obtain_spongekin_seeds", InventoryChangeTrigger.TriggerInstance.hasItems(BFItems.SPONGEKIN_SEEDS.get()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_spongekin_seeds"));
        AdvancementHolder obtain_spongekin = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.SPONGEKIN.get().asItem()),
                        Component.translatable("advancement.bountifulfares.obtain_spongekin"),
                        Component.translatable("advancement.bountifulfares.obtain_spongekin.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_spongekin_seeds)
                .addCriterion("obtain_spongekin", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, BFItems.SPONGEKIN_SLICE.get(), BFBlocks.SPONGEKIN.get()).build()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_spongekin"));
        AdvancementHolder obtain_prismarine_blossom = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.PRISMARINE_BLOSSOM.get().asItem()),
                        Component.translatable("advancement.bountifulfares.obtain_prismarine_blossom"),
                        Component.translatable("advancement.bountifulfares.obtain_prismarine_blossom.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_spongekin)
                .addCriterion("obtain_prismarine_blossom", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, BFBlocks.PRISMARINE_BLOSSOM.get()).build()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_prismarine_blossom"));

        AdvancementHolder obtain_golden_apple_sapling = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFBlocks.GOLDEN_APPLE_SAPLING.get().asItem()),
                        Component.translatable("advancement.bountifulfares.obtain_golden_apple_sapling"),
                        Component.translatable("advancement.bountifulfares.obtain_golden_apple_sapling.description"), Optional.empty(), AdvancementType.CHALLENGE,
                        true,
                        true,
                        false))
                .parent(pick_fruit)
                .addCriterion("obtain_golden_apple_sapling", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, BFBlocks.GOLDEN_APPLE_SAPLING.get()).build()))
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_golden_apple_sapling"));
        AdvancementHolder obtain_golden_apple = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(Items.GOLDEN_APPLE),
                        Component.translatable("advancement.bountifulfares.obtain_golden_apple"),
                        Component.translatable("advancement.bountifulfares.obtain_golden_apple.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_golden_apple_sapling)
                .addCriterion("obtain_golden_apple",
                        PickFruitInteractionTrigger.TriggerInstance.pickedFruit(LocationPredicate.Builder.location()
                                .setBlock(BlockPredicate.Builder.block()
                                        .of(blocks, BFBlocks.HANGING_GOLDEN_APPLE.get())))
                )
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_golden_apple"));
        AdvancementHolder obtain_tiffin = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.TIFFINS.get(null).get()),
                        Component.translatable("advancement.bountifulfares.obtain_tiffin"),
                        Component.translatable("advancement.bountifulfares.obtain_tiffin.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(root_advancement)
                .addCriterion("obtain_tiffin",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, BFItemTags.TIFFINS))
                )
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":obtain_tiffin"));
        AdvancementHolder craft_food_in_tiffin = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.TIFFINS.get(DyeColor.WHITE).get()),
                        Component.translatable("advancement.bountifulfares.craft_food_in_tiffin"),
                        Component.translatable("advancement.bountifulfares.craft_food_in_tiffin.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_tiffin)
                .addCriterion("craft_food_in_tiffin",
                        CraftFoodInTiffinTrigger.TriggerInstance.crafted()
                )
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":craft_food_in_tiffin"));
        AdvancementHolder fill_tiffin = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStackTemplate(BFItems.TIFFINS.get(DyeColor.PURPLE).get()),
                        Component.translatable("advancement.bountifulfares.fill_tiffin"),
                        Component.translatable("advancement.bountifulfares.fill_tiffin.description"), Optional.empty(), AdvancementType.TASK,
                        true,
                        true,
                        false))
                .parent(obtain_tiffin)
                .addCriterion("fill_tiffin",
                        FillTiffinTrigger.TriggerInstance.filledTo(MinMaxBounds.Doubles.atLeast(0.999d))
                )
                .save(consumer, Identifier.parse(BountifulFares.MOD_ID + ":fill_tiffin"));
//        AdvancementEntry breedWolvesWithMulch = Advancement.Builder.create()
//                .display(new AdvancementDisplay(new ItemStackTemplate(ModBlocks.WALNUT_MULCH),
//                        Text.translatable("advancement.bountifulfares.breed_wolves_with_mulch"),
//                        Text.translatable("advancement.bountifulfares.breed_wolves_with_mulch.description"), Optional.of(Identifier.of("minecraft:textures/block/farmland_moist.png")), AdvancementFrame.CHALLENGE,
//                        true,
//                        true,
//                        false))
//                .parent(obtain_feldspar)
//                .criterion("breed_wolves_with_mulch", PlayerInteractedWithEntityCriterion.Conditions.create(ItemPredicate.Builder.create().items(ModBlocks.WALNUT_MULCH), LootContextPredicate.create()))
//                .build(consumer, BountifulFares.MOD_ID + ":breed_wolves_with_mulch");
    }

    @SuppressWarnings("unchecked")
    private static Map<Item, DataComponentMap> computeItemComponents(HolderLookup.Provider registryLookup) {
        Map<Item, DataComponentMap> result = new HashMap<>();
        for (DataComponentInitializers.PendingComponents<?> pending : BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup)) {
            if (pending.key().equals(Registries.ITEM)) {
                ((DataComponentInitializers.PendingComponents<Item>) pending).forEach((holder, map) -> result.put(holder.value(), map));
            }
        }
        return result;
    }
}
