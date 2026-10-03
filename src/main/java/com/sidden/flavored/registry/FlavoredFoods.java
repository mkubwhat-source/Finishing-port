package com.sidden.flavored.registry;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

/**
 * Flavored's food values.
 * <p>
 * 26.3 split 1.21.1's {@code FoodProperties} in three: nutrition/saturation/always-edible stay in
 * {@code FoodProperties}; eat speed ({@code .fast()}), animation, sound and on-eat effects are the
 * {@code minecraft:consumable} component; {@code usingConvertsTo} is the {@code use_remainder}
 * component ({@code Item.Properties.usingConvertsTo}). Each constant here is an
 * {@code Item.Properties} decorator that applies all three, with 1.21.1's exact values.
 */
public final class FlavoredFoods {
    /** 1.21.1 {@code FoodProperties.Builder.fast()} = 16 ticks. */
    private static final float FAST_SECONDS = 0.8F;
    /** 1.21.1 {@code DrinkItem.DRINK_DURATION} = 40 ticks. */
    private static final float DRINK_SECONDS = 2.0F;

    @FunctionalInterface
    public interface Food {
        Item.Properties apply(Item.Properties properties);
    }

    private static FoodProperties nutrition(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    private static FoodProperties alwaysEdible(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).alwaysEdible().build();
    }

    private static Consumable.Builder eat() {
        return Consumable.builder();
    }

    private static Consumable.Builder fast() {
        return Consumable.builder().consumeSeconds(FAST_SECONDS);
    }

    private static Consumable.Builder drink() {
        return Consumable.builder().consumeSeconds(DRINK_SECONDS).animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK);
    }

    private static Consumable.Builder effect(Consumable.Builder builder, Holder<MobEffect> effect, int duration, float chance) {
        return builder.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(effect, duration, 0), chance));
    }

    private static Food food(FoodProperties food, Consumable.Builder consumable) {
        return p -> p.food(food, consumable.build());
    }

    private static Food bowl(FoodProperties food, Consumable.Builder consumable) {
        return p -> p.food(food, consumable.build()).usingConvertsTo(Items.BOWL);
    }

    private static Food bottle(FoodProperties food, Consumable.Builder consumable) {
        return p -> p.food(food, consumable.build()).usingConvertsTo(Items.GLASS_BOTTLE);
    }

    public static final Food CHOCOLATE = food(nutrition(3, 0.1f), effect(fast(), FlavoredEffects.SUGAR_RUSH, 1200, 1F));
    public static final Food CHOCOLATE_EGG = food(nutrition(3, 0.1f), effect(fast(), FlavoredEffects.SUGAR_RUSH, 1200, 1F));
    public static final Food RED_TOMATO = food(nutrition(2, 0.2f), eat());
    public static final Food YELLOW_TOMATO = food(nutrition(2, 0.1f), eat());
    public static final Food GREEN_TOMATO = food(nutrition(1, 0.1f), eat());
    public static final Food GARLIC = food(nutrition(1, 0.1f), effect(eat(), MobEffects.NAUSEA, 300, 1F));
    public static final Food SOFT_CHEESE_SLICE = food(alwaysEdible(2, 0.2f), eat());
    public static final Food AGED_CHEESE_SLICE = food(alwaysEdible(5, 0.5f), eat());
    public static final Food GROUND_BEEF = food(nutrition(2, 0.1f), eat());
    public static final Food COOKED_GROUND_BEEF = food(nutrition(5, 0.7f), eat());
    public static final Food CHICKEN_DRUMSTICK = food(nutrition(1, 0.1f), effect(eat(), MobEffects.HUNGER, 300, 0.3F));
    public static final Food COOKED_CHICKEN_DRUMSTICK = food(nutrition(3, 0.7f), eat());
    public static final Food MUTTON_SHANK = food(nutrition(2, 0.1f), eat());
    public static final Food COOKED_MUTTON_SHANK = food(nutrition(5, 0.7f), eat());
    public static final Food PORK_JOWL = food(nutrition(2, 0.1f), eat());
    public static final Food COOKED_PORK_JOWL = food(nutrition(5, 0.7f), eat());
    public static final Food PEPPER = food(nutrition(2, 0.1f), effect(eat(), FlavoredEffects.HEAT, 600, 1F));
    public static final Food DRIED_PEPPER = food(nutrition(3, 0.3f), effect(eat(), FlavoredEffects.HEAT, 1200, 1F));
    public static final Food SPINACH = food(nutrition(1, 0.1f), eat());
    public static final Food COOKIE_DOUGH = food(nutrition(1, 0.1f), eat());
    public static final Food PIZZA_SLICE = food(nutrition(8, 0.9f), eat());
    public static final Food GRILLED_CORN = food(nutrition(5, 0.7f), effect(eat(), FlavoredEffects.POPPED, 2400, 1F));
    public static final Food HAMBURGER = food(nutrition(10, 0.9f), eat());
    public static final Food CHEESE_SANDWICH = food(nutrition(7, 0.9f), eat());
    public static final Food HAM_SANDWICH = food(nutrition(8, 0.9f), eat());
    public static final Food CHICKEN_SANDWICH = food(nutrition(8, 0.9f), eat());
    public static final Food SHAWARMA = food(nutrition(10, 0.9f), eat());
    public static final Food GARLIC_BREAD = food(nutrition(8, 0.7f), eat());
    public static final Food BUTTER_PASTRY = food(nutrition(6, 0.7f), eat());
    public static final Food CINNAMON_PASTRY = food(nutrition(6, 0.7f), eat());
    public static final Food HONEY_PASTRY = food(nutrition(6, 0.7f), eat());
    public static final Food CHOCOLATE_PASTRY = food(nutrition(6, 0.7f), effect(eat(), FlavoredEffects.SUGAR_RUSH, 1200, 1F));
    public static final Food SALAD = bowl(nutrition(6, 0.3f), eat());
    public static final Food CEREAL = bowl(nutrition(7, 0.1f), eat());
    public static final Food TOMATO_PASTA = bowl(nutrition(8, 0.7f), eat());
    public static final Food PESTO_PASTA = bowl(nutrition(8, 0.7f), eat());
    public static final Food CARBONARA_PASTA = bowl(nutrition(9, 0.9f), eat());
    public static final Food CREAM_PASTA = bowl(nutrition(9, 0.7f), eat());
    public static final Food RAGU_PASTA = bowl(nutrition(10, 0.9f), eat());
    public static final Food PORRIDGE = bowl(nutrition(6, 0.4f), eat());
    public static final Food POLENTA = bowl(nutrition(7, 0.4f), effect(eat(), FlavoredEffects.POPPED, 3600, 1F));
    public static final Food OSSOBUCO = bowl(nutrition(9, 0.9f), eat());
    public static final Food SHAKSHOUKA = bowl(nutrition(6, 0.8f), effect(eat(), FlavoredEffects.HEAT, 1200, 1F));
    public static final Food SWEET_BERRY_JUICE = bottle(nutrition(4, 0.3f), drink());
    public static final Food GLOW_BERRY_JUICE = bottle(nutrition(4, 0.3f), effect(drink(), MobEffects.GLOWING, 300, 1F));
    public static final Food WORT = bottle(nutrition(1, 0.1f), drink());
    public static final Food APPLE_JUICE = bottle(nutrition(5, 0.3f), drink());
    public static final Food SWEET_BERRY_WINE = bottle(nutrition(6, 0.3f), effect(drink(), FlavoredEffects.BOOZED, 2400, 1F));
    public static final Food GLOW_BERRY_WINE = bottle(nutrition(6, 0.3f), effect(effect(drink(), FlavoredEffects.BOOZED, 2400, 1F), MobEffects.GLOWING, 1200, 1F));
    public static final Food BEER = bottle(nutrition(5, 0.3f), effect(drink(), FlavoredEffects.BOOZED, 2400, 1F));
    public static final Food CIDER = bottle(nutrition(7, 0.5f), effect(drink(), FlavoredEffects.BOOZED, 2400, 1F));

    private FlavoredFoods() {
    }
}
