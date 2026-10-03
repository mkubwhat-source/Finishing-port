package net.hecco.bountifulfares.registry.content;

import net.hecco.bountifulfares.platform.BFProperties;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.BountifulFaresUtil;
import net.hecco.bountifulfares.definition.item.component.TiffinContents;
import net.hecco.bountifulfares.definition.item.custom.*;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Blocks;

import java.util.*;
import java.util.function.Supplier;

// Items that were ItemNameBlockItem in 1.21.1 (seeds, coconut, artisan cookie) are plain
// BlockItems with the default item description prefix in 26.3 (ItemNameBlockItem was removed in
// favor of Item.Properties.useItemDescriptionPrefix(), the default) - so they use
// BFProperties.item(); block-named BlockItems use BFProperties.blockItem(block).
public class BFItems {
    public static final Map<DyeColor, Supplier<Item>> TIFFINS = new HashMap<>();

    // `FoodProperties.Builder` lost `.fast()` and `.effect(...)` in 26.3 - eating speed and on-eat
    // status effects moved to the new `minecraft:consumable` data component (a `Consumable` record),
    // set via the `Item.Properties.food(FoodProperties, Consumable)` overload. These two helpers
    // reproduce the old `.fast()` (0.8s eat time, vanilla's FAST_FOOD constant) and multi-effect
    // behavior without cutting anything - see Consumable/ApplyStatusEffectsConsumeEffect via javap.
    private static final float FAST_EAT_SECONDS = 0.8f;

    private static Consumable.Builder consumable() {
        return Consumable.builder();
    }

    private static Consumable.Builder fastConsumable() {
        return Consumable.builder().consumeSeconds(FAST_EAT_SECONDS);
    }

    // LiquidBottleItem/LiquidJarItem/TeaBottleItem/WaterCupItem/TiffinItem all used DRINK/EAT
    // animation with a fixed vanilla sound via the now-removed Item.getUseAnimation()+getEatingSound()/
    // getDrinkingSound() overrides; that behavior now lives on the Consumable data component instead.
    private static Consumable.Builder drinkConsumable() {
        return Consumable.builder().animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK);
    }

    private static Consumable.Builder eatConsumable() {
        return Consumable.builder().animation(ItemUseAnimation.EAT).sound(SoundEvents.GENERIC_EAT);
    }

    private static Consumable.Builder withEffect(Consumable.Builder builder, MobEffectInstance effect, float probability) {
        return builder.onConsume(new ApplyStatusEffectsConsumeEffect(effect, probability));
    }

    public static final Supplier<Item> PASSION_FRUIT = registerItem("passion_fruit", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build(), fastConsumable().build())));
    public static final Supplier<Item> ELDERBERRIES = registerItem("elderberries", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build(), withEffect(fastConsumable(), new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 0, true, false), 0.3f).build())));
    public static final Supplier<Item> LAPISBERRY_SEEDS = registerItem("lapisberry_seeds", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> LAPISBERRIES = registerItem("lapisberries", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.1f).build(), fastConsumable().build())));

    public static final Supplier<Item> ORANGE = registerItem("orange", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())));
    public static final Supplier<Item> LEMON = registerItem("lemon", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())));
    public static final Supplier<Item> PLUM = registerItem("plum", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())));
    public static final Supplier<Item> HOARY_APPLE = registerItem("hoary_apple", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F).build())));
    public static final Supplier<Item> HOARY_SEEDS = registerItem("hoary_seeds", () -> new BlockItem(BFBlocks.HOARY_APPLE_SAPLING_CROP.get(), BFProperties.item()));
    public static final Supplier<Item> HOARY_SIGN = registerItem("hoary_sign", () -> new StandingAndWallBlockItem(BFBlocks.HOARY_SIGN.get(), BFBlocks.HOARY_WALL_SIGN.get(), Direction.DOWN, BFProperties.blockItem(BFBlocks.HOARY_SIGN.get()).stacksTo(16)));
    public static final Supplier<Item> HOARY_HANGING_SIGN = registerItem("hoary_hanging_sign", () -> new HangingSignItem(BFBlocks.HOARY_HANGING_SIGN.get(), BFBlocks.HOARY_WALL_HANGING_SIGN.get(), BFProperties.blockItem(BFBlocks.HOARY_HANGING_SIGN.get()).stacksTo(16)));
    public static final Supplier<Item> HOARY_BOAT = registerItem("hoary_boat", () -> new BoatItem(BFBoats.HOARY_BOAT.get(), BFProperties.item().stacksTo(1)));
    public static final Supplier<Item> HOARY_CHEST_BOAT = registerItem("hoary_chest_boat", () -> new BoatItem(BFBoats.HOARY_CHEST_BOAT.get(), BFProperties.item().stacksTo(1)));

    public static final Supplier<Item> SWEET_BERRY_PIPS = registerItem("sweet_berry_pips", () -> new SweetBerryPipsItem(Blocks.SWEET_BERRY_BUSH, BFProperties.item()));

    public static final Supplier<Item> WALNUT = registerItem("walnut", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0).build(), fastConsumable().build())));
    public static final Supplier<Item> WALNUT_SIGN = registerItem("walnut_sign", () -> new StandingAndWallBlockItem(BFBlocks.WALNUT_SIGN.get(), BFBlocks.WALNUT_WALL_SIGN.get(), Direction.DOWN, BFProperties.blockItem(BFBlocks.WALNUT_SIGN.get()).stacksTo(16)));
    public static final Supplier<Item> WALNUT_HANGING_SIGN = registerItem("walnut_hanging_sign", () -> new HangingSignItem(BFBlocks.WALNUT_HANGING_SIGN.get(), BFBlocks.WALNUT_WALL_HANGING_SIGN.get(), BFProperties.blockItem(BFBlocks.WALNUT_HANGING_SIGN.get()).stacksTo(16)));
    public static final Supplier<Item> WALNUT_BOAT = registerItem("walnut_boat", () -> new BoatItem(BFBoats.WALNUT_BOAT.get(), BFProperties.item().stacksTo(1)));
    public static final Supplier<Item> WALNUT_CHEST_BOAT = registerItem("walnut_chest_boat", () -> new BoatItem(BFBoats.WALNUT_CHEST_BOAT.get(), BFProperties.item().stacksTo(1)));
    public static final Supplier<Item> PALM_FROND = registerItem("palm_frond", () -> new StandingAndWallBlockItem(BFBlocks.PALM_FROND.get(), BFBlocks.WALL_PALM_FROND.get(), Direction.DOWN, BFProperties.blockItem(BFBlocks.PALM_FROND.get())));
    public static final Supplier<Item> COCONUT = registerItem("coconut", () -> new BlockItem(BFBlocks.PALM_SAPLING.get(), BFProperties.item()));
    public static final Supplier<Item> COCONUT_COIR = registerItem("coconut_coir", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> COCONUT_HALF = registerItem("coconut_half", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.1f).build())));
    public static final Supplier<Item> COCONUT_MILK_BOTTLE = registerItem("coconut_milk_bottle", () -> new CoconutMilkBottleItem(BFProperties.item().food(new FoodProperties.Builder().nutrition(3).saturationModifier(1f).build()).craftRemainder(Items.GLASS_BOTTLE)));
    public static final Supplier<Item> CITRUS_ESSENCE = registerItem("citrus_essence", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ACIDIC, 300, 0)), BFProperties.item().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1f).alwaysEdible().build(), withEffect(fastConsumable(), new MobEffectInstance(BFEffects.ACIDIC, 300, 0), 1f).build())));
    public static final Supplier<Item> CANDIED_ORANGE = registerItem("candied_orange", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build(), fastConsumable().build())));
    public static final Supplier<Item> CANDIED_LEMON = registerItem("candied_lemon", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build(), fastConsumable().build())));
    // Hearth and Harvest merge (user decision): alcoholic drinks give HH's Drunk (in place of the wines'
    // nausea chance, same duration and chance).
    public static final Supplier<Item> ELDERBERRY_WINE_BOTTLE = registerItem("elderberry_wine_bottle", () -> new LiquidBottleItem(BFProperties.item().craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).alwaysEdible().build(), withEffect(withEffect(drinkConsumable(), new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 1, true, false), 1f), new MobEffectInstance(alabaster.hearthandharvest.common.registry.HHModEffects.DRUNK, 600, 0), 0.3f).build()).stacksTo(16)));
    public static final Supplier<Item> LAPISBERRY_WINE_BOTTLE = registerItem("lapisberry_wine_bottle", () -> new LiquidBottleItem(List.of(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0)), BFProperties.item().craftRemainder(Items.GLASS_BOTTLE).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.4f).alwaysEdible().build(), withEffect(withEffect(drinkConsumable(), new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 1f), new MobEffectInstance(alabaster.hearthandharvest.common.registry.HHModEffects.DRUNK, 600, 0), 0.3f).build()).stacksTo(16)));
    // Hearth and Harvest merge: BF's mead is also HH's mead, so it is an HH wine bottle (3 glasses, cask
    // aging into vintages, Drunk scaled by vintage) with HH's effects plus BF's poison cure.
    public static final Supplier<Item> MEAD_BOTTLE = registerItem("mead_bottle", () -> new alabaster.hearthandharvest.common.item.WineBottleItem(
            () -> alabaster.hearthandharvest.common.registry.HHModFluids.MEAD.source().get(),
            alabaster.hearthandharvest.common.item.WineBottleItem.properties(BFProperties.item(), alabaster.hearthandharvest.common.HHFoodValues.MEAD), true, true).glasses(3));
    public static final Supplier<Item> FOUL_FLESH = registerItem("foul_flesh", () -> new FoulFleshItem(BFProperties.item().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build(), withEffect(consumable(), new MobEffectInstance(MobEffects.HUNGER, 300, 0), 1f).build())));
    public static final Supplier<Item> FELDSPAR = registerItem("feldspar", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> CERAMIC_CLAY = registerItem("ceramic_clay", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> CERAMIC_TILE = registerItem("ceramic_tile", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> CUP = registerItem("jar", () -> new CupItem(BFProperties.item()));
    public static final Supplier<Item> APPLE_COMPOTE_JAR = registerItem("apple_compote_jar", () -> new EdibleJarItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0)), SoundEvents.HONEY_DRINK, BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0), 1f).sound(SoundEvents.HONEY_DRINK).build()).craftRemainder(BFItems.CUP.get())));
    public static final Supplier<Item> ORANGE_COMPOTE_JAR = registerItem("orange_compote_jar", () -> new EdibleJarItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0)), SoundEvents.HONEY_DRINK, BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0), 1f).sound(SoundEvents.HONEY_DRINK).build()).craftRemainder(BFItems.CUP.get())));
    public static final Supplier<Item> LEMON_COMPOTE_JAR = registerItem("lemon_compote_jar", () -> new EdibleJarItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0)), SoundEvents.HONEY_DRINK, BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0), 1f).sound(SoundEvents.HONEY_DRINK).build()).craftRemainder(BFItems.CUP.get())));
    public static final Supplier<Item> PLUM_COMPOTE_JAR = registerItem("plum_compote_jar", () -> new EdibleJarItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0)), SoundEvents.HONEY_DRINK, BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0), 1f).sound(SoundEvents.HONEY_DRINK).build()).craftRemainder(BFItems.CUP.get())));
    public static final Supplier<Item> HOARY_COMPOTE_JAR = registerItem("hoary_compote_jar", () -> new EdibleJarItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0)), SoundEvents.HONEY_DRINK, BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0), 1f).sound(SoundEvents.HONEY_DRINK).build()).craftRemainder(BFItems.CUP.get())));
    public static final Supplier<Item> APPLE_CIDER_JAR = registerItem("apple_cider_jar", () -> new LiquidJarItem(BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6f).build(), drinkConsumable().build())));
    public static final Supplier<Item> PLUM_CIDER_JAR = registerItem("plum_cider_jar", () -> new LiquidJarItem(BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6f).build(), drinkConsumable().build())));
    public static final Supplier<Item> HOARY_CIDER_JAR = registerItem("hoary_cider_jar", () -> new LiquidJarItem(BFProperties.item().stacksTo(16).craftRemainder(CUP.get()).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6f).build(), drinkConsumable().build())));
    public static final Supplier<Item> TEA_BERRIES = registerItem("tea_berries", () -> new TeaBerriesItem(BFBlocks.TEA_SHRUB.get(), BFProperties.item()));
    public static final Supplier<Item> TEA_LEAVES = registerItem("tea_leaves", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> DRIED_TEA_LEAVES = registerItem("dried_tea_leaves", () -> new Item(BFProperties.item()));
    public static final Supplier<Item> WATER_CUP = registerItem("water_cup", () -> new WaterCupItem(BFProperties.item().stacksTo(16).component(DataComponents.CONSUMABLE, drinkConsumable().build())));
    public static final Supplier<Item> GREEN_TEA_CUP = registerItem("green_tea_bottle", () -> new GreenTeaBottleItem(BFProperties.item().stacksTo(16).craftRemainder(BFItems.CUP.get()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(1f).alwaysEdible().build(), withEffect(drinkConsumable(), new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1f).build())));
    public static final Supplier<Item> BLACK_TEA_CUP = registerItem("black_tea_bottle", () -> new BlackTeaBottleItem(BFProperties.item().stacksTo(16).craftRemainder(BFItems.CUP.get()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(1f).alwaysEdible().build(), withEffect(drinkConsumable(), new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1f).build())));
    public static final Supplier<Item> CHAMOMILE_TEA_CUP = registerItem("chamomile_tea_bottle", () -> new ChamomileTeaBottleItem(BFProperties.item().stacksTo(16).craftRemainder(BFItems.CUP.get()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(1f).alwaysEdible().build(), withEffect(drinkConsumable(), new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1f).build())));
    public static final Supplier<Item> HONEYSUCKLE_TEA_CUP = registerItem("honeysuckle_tea_bottle", () -> new HoneysuckleTeaBottleItem(BFProperties.item().stacksTo(16).craftRemainder(BFItems.CUP.get()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(1f).alwaysEdible().build(), withEffect(drinkConsumable(), new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1f).build())));
    public static final Supplier<Item> BELLFLOWER_TEA_CUP = registerItem("bellflower_tea_bottle", () -> new BellflowerTeaBottleItem(BFProperties.item().stacksTo(16).craftRemainder(BFItems.CUP.get()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(1f).alwaysEdible().build(), withEffect(drinkConsumable(), new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1f).build())));
    public static final Supplier<Item> TORCHFLOWER_TEA_CUP = registerItem("torchflower_tea_bottle", () -> new TorchflowerTeaBottleItem(BFProperties.item().stacksTo(16).craftRemainder(BFItems.CUP.get()).food(new FoodProperties.Builder().nutrition(4).saturationModifier(1f).alwaysEdible().build(), withEffect(drinkConsumable(), new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1f).build())));
    public static final Supplier<Item> MAIZE = registerItem("maize", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.6f).build())));
    public static final Supplier<Item> GRASS_SEEDS = registerItem("grass_seeds", () -> new GrassSeedsItem(BFProperties.item()));
    public static final Supplier<Item> MAIZE_SEEDS = registerItem("maize_seeds", () -> new BlockItem(BFBlocks.MAIZE_CROP.get(), BFProperties.item()));
    public static final Supplier<Item> POPPED_MAIZE = registerItem("popped_maize", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build(), fastConsumable().build())));
    public static final Supplier<Item> COOKED_EGG = registerItem("cooked_egg", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(1.0f).build(), fastConsumable().build())));
    public static final Supplier<Item> PICKLED_BEETROOT = registerItem("pickled_beetroot", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.8f).build(), fastConsumable().build())));
    public static final Supplier<Item> LEEK = registerItem("leek", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.6f).build())));
    public static final Supplier<Item> LEEK_SEEDS = registerItem("leek_seeds", () -> new BlockItem(BFBlocks.LEEKS.get(), BFProperties.item()));
    public static final Supplier<Item> SPONGEKIN_SEEDS = registerItem("spongekin_seeds", () -> new BlockItem(BFBlocks.SPONGEKIN_STEM.get(), BFProperties.item()));
    public static final Supplier<Item> SPONGEKIN_SLICE = registerItem("spongekin_slice", () -> new AirTimeIncreasingItem( 150, BFProperties.item().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).alwaysEdible().build())));
    public static final Supplier<Item> PICKLED_SPONGEKIN = registerItem("pickled_spongekin", () -> new AirTimeIncreasingItem( 200, BFProperties.item().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.8f).alwaysEdible().build(), fastConsumable().build())));
    public static final Supplier<Item> SCORCHKIN_SEEDS = registerItem("scorchkin_seeds", () -> new BlockItem(BFBlocks.SCORCHKIN_STEM.get(), BFProperties.item()));
    public static final Supplier<Item> FLOUR = registerItem("flour", () -> new FlourItem(BFProperties.item()));
    public static final Supplier<Item> ARTISAN_COOKIE = registerItem("artisan_cookie", () -> new BlockItem(BFBlocks.ARTISAN_COOKIE.get(), BFProperties.item()));
    public static final Supplier<Item> SUN_HAT = registerItem("sun_hat", () -> new SunHatItem(BFProperties.item().stacksTo(1)
            .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).setEquipOnInteract(true).setSwappable(true).build())));



    public static final Supplier<Item> ARTISAN_BRUSH = registerItem("artisan_brush", () -> new ArtisanBrushItem(BFProperties.item().stacksTo(1)));
    public static final Supplier<Item> CANDY = registerItem("candy", () -> new EffectClearingItem(List.of(new MobEffectInstance(MobEffects.POISON)), BFProperties.item().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.4F).build(), fastConsumable().build())));
    public static final Supplier<Item> SOUR_CANDY = registerItem("sour_candy", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ACIDIC, 200)), BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), withEffect(fastConsumable(), new MobEffectInstance(BFEffects.ACIDIC, 200), 0.2f).build())));
    public static final Supplier<Item> PIQUANT_CANDY = registerItem("piquant_candy", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), fastConsumable().build())));
    public static final Supplier<Item> BITTER_CANDY = registerItem("bitter_candy", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), withEffect(fastConsumable(), new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 0, true, false), 0.75f).build())));
    public static final Supplier<Item> STRANGE_CANDY = registerItem("strange_candy", () -> new EffectFoodItem(List.of(new MobEffectInstance(MobEffects.NIGHT_VISION, 80, 0)), BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), withEffect(fastConsumable(), new MobEffectInstance(MobEffects.NIGHT_VISION, 20, 0, true, false), 1f).build())));



    public static final Supplier<Item> PASSION_GLAZED_SALMON = registerItem("passion_glazed_salmon", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.7F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> COCONUT_CRUSTED_COD = registerItem("coconut_crusted_cod", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.7F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> BOUNTIFUL_STEW = registerItem("bountiful_stew", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.RESTORATION, 2400, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> CRUSTED_BEEF = registerItem("crusted_beef", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.7F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 2400, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> LEEK_STEW = registerItem("leek_stew", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1800, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1800, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> FISH_STEW = registerItem("fish_stew", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> STONE_STEW = registerItem("stone_stew", () -> new StackableBowlFoodItem(BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.4F).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> APPLE_STEW = registerItem("apple_stew", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.5F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> COCONUT_STEW = registerItem("coconut_stew", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1f).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> SEA_SALAD = registerItem("sea_salad", () -> new StackableBowlFoodItem(BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> FOREST_MEDLEY = registerItem("forest_medley", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> ARID_MEDLEY = registerItem("arid_medley", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(9).saturationModifier(0.5F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> MEADOW_MEDLEY = registerItem("meadow_medley", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(9).saturationModifier(0.5F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> MIRE_MEDLEY = registerItem("mire_medley", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> COASTAL_MEDLEY = registerItem("coastal_medley", () -> new AirTimeIncreasingItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), 150, BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> TROPICAL_MEDLEY = registerItem("tropical_medley", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> STUFFED_HOARY_APPLE = registerItem("stuffed_hoary_apple", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.5F).build())));
    public static final Supplier<Item> CRIMSON_CHOW = registerItem("crimson_chow", () -> new StackableBowlFoodItem(BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.8F).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> WARPED_CHOW = registerItem("warped_chow", () -> new StackableBowlFoodItem(BFProperties.item().stacksTo(16).usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.7F).build()).craftRemainder(Items.BOWL)));

//    public static final Supplier<Item> CUSTARD = registerItem("custard", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(5).saturationModifier(1.2f).effect(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1).build()).craftRemainder(Items.BOWL)));
//    public static final Supplier<Item> PIQUANT_CUSTARD = registerItem("piquant_custard", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(7).saturationModifier(1.2f).effect(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1).build()).craftRemainder(Items.BOWL)));
//    public static final Supplier<Item> PASSION_CUSTARD = registerItem("passion_custard", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(7).saturationModifier(1.2f).effect(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1).build()).craftRemainder(Items.BOWL)));
//    public static final Supplier<Item> COCOA_CUSTARD = registerItem("cocoa_custard", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(1f).effect(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1).build()).craftRemainder(Items.BOWL)));
//    public static final Supplier<Item> ANCIENT_CUSTARD = registerItem("ancient_custard", () -> new StackableBowlFoodItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true)), BFProperties.item().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(1f).effect(new MobEffectInstance(BFEffects.RESTORATION, 1800, 0, true, true), 1).build()).craftRemainder(Items.BOWL)));
    public static final Supplier<Item> MUSHROOM_STUFFED_POTATO = registerItem("mushroom_stuffed_potato", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1800, 0, true, true)), BFProperties.item().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1800, 0, true, true), 1f).build())));
    public static final Supplier<Item> BERRY_STUFFED_POTATO = registerItem("berry_stuffed_potato", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.5F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> MAIZE_STUFFED_POTATO = registerItem("maize_stuffed_potato", () -> new EffectFoodItem(List.of(new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true)), BFProperties.item().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.6F).build(), withEffect(consumable(), new MobEffectInstance(BFEffects.ENRICHMENT, 1200, 0, true, true), 1f).build())));
    public static final Supplier<Item> MAIZE_BREAD = registerItem("maize_bread", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.5F).build())));
    public static final Supplier<Item> WALNUT_COOKIE = registerItem("walnut_cookie", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).build())));
    public static final Supplier<Item> CANDIED_APPLE = registerItem("candied_apple", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F).build())));
    public static final Supplier<Item> CANDIED_PLUM = registerItem("candied_plum", () -> new Item(BFProperties.item().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F).build())));
//    public static final Supplier<Item> YOGHURT = registerItem("yoghurt", () -> new YoghurtItem(List.of(), BFProperties.item().food(new FoodProperties.Builder().nutrition(4).saturationModifier(1.75F).build())));
//    public static final Supplier<Item> PASSION_YOGHURT = registerItem("passion_yoghurt", () -> new YoghurtItem(List.of(new MobEffectInstance(BFEffects.RESTORATION, 500)), BFProperties.item().food(new FoodProperties.Builder().nutrition(6).saturationModifier(1.5F).effect(new MobEffectInstance(BFEffects.RESTORATION, 500), 1f).build())));
    public static final Supplier<Item> DIRT_STEW = registerItem("dirt_stew", () -> new OPStewItem(BFProperties.item().stacksTo(99).food(new FoodProperties.Builder().nutrition(1000).saturationModifier(1000).build(),
            withEffect(
                withEffect(
                    withEffect(
                        withEffect(consumable(), new MobEffectInstance(BFEffects.RESTORATION, 72000, 10, true, true), 1f),
                        new MobEffectInstance(MobEffects.RESISTANCE, 72000, 10, true, true), 1f),
                    new MobEffectInstance(MobEffects.ABSORPTION, 72000, 10, true, true), 1f),
                new MobEffectInstance(MobEffects.HEALTH_BOOST, 72000, 25, true, true), 1f)
            .build())));

    private static void registerTiffins() {
        TIFFINS.put(null, registerItem("shulker_tiffin", () -> new TiffinItem(null, createTiffinProperties())));
        for (DyeColor color : Arrays.stream(DyeColor.values()).limit(16).toList()) {
            TIFFINS.put(color, registerItem(color.getName() + "_shulker_tiffin", () -> new TiffinItem(color, createTiffinProperties())));
        }
    }

    private static void registerTrellises() {
        for (String wood : BountifulFaresUtil.WOOD_TYPES) {
            if (!Objects.equals(wood, "oak")) {
                registerItem(wood + "_trellis", () -> new TrellisBlockItem(BFBlocks.TRELLISES.get(wood).get(), BFProperties.blockItem(BFBlocks.TRELLISES.get(wood).get())));
            } else {
                registerItem("trellis", () -> new TrellisBlockItem(BFBlocks.TRELLISES.get(wood).get(), BFProperties.blockItem(BFBlocks.TRELLISES.get(wood).get())));
            }
        }
    }

    private static Supplier<Item> registerItem(String id, Supplier<Item> registry) {
        return BFRegistryHelper.register(BountifulFares.MOD_ID, id, BuiltInRegistries.ITEM, registry);
    }

    private static Item.Properties createTiffinProperties() {
        return BFProperties.item()
                .stacksTo(1)
                .component(BFComponents.TIFFIN_CONTENTS.get(), new TiffinContents())
                .component(DataComponents.CONSUMABLE, eatConsumable().build());
    }

    public static void registerItems() {
        registerTiffins();
        registerTrellises();
    }
}
