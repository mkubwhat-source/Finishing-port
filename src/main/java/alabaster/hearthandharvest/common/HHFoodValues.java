package alabaster.hearthandharvest.common;

import alabaster.hearthandharvest.common.registry.HHModEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;

public class HHFoodValues {

    // Duration Constants
    public static final int BRIEF_DURATION = 600;    // 30 seconds
    public static final int SHORT_DURATION = 1200;   // 1 minute
    public static final int MEDIUM_DURATION = 3600;  // 3 minutes
    public static final int LONG_DURATION = 6000;    // 5 minutes

    // Raw Ingredients
    public static final HHFood BLUEBERRIES = food(1, 0.3f).fast();
    public static final HHFood RASPBERRY = food(2, 0.4f).fast();
    public static final HHFood CHERRY = food(3, 0.5f).fast();
    public static final HHFood GRAPES = food(3, 0.3f).fast();
    public static final HHFood PEANUT = food(2, 0.3f).fast();
    public static final HHFood CORN = food(3, 0.5f);
    public static final HHFood RAISINS = food(2, 0.3f).fast();
    public static final HHFood SUNFLOWER_SEEDS = food(1, 0.3f).fast();

    // Drinks
    public static final HHFood CHOCOLATE_MILK_BOTTLE = food(0, 0.0f).effect(() -> new MobEffectInstance(MobEffects.SPEED, BRIEF_DURATION, 0), 1.0F);
    public static final HHFood GOAT_MILK_BOTTLE = food(0, 0.0f);

    // Juices
    public static final HHFood BLUEBERRY_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.LUCK, BRIEF_DURATION, 0), 1.0F);
    public static final HHFood RASPBERRY_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.PRICKLY, BRIEF_DURATION, 0), 1.0F);
    public static final HHFood CHERRY_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.HASTE, BRIEF_DURATION, 0), 1.0F);
    public static final HHFood RED_GRAPE_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.RESISTANCE, BRIEF_DURATION, 0), 1.0F);
    public static final HHFood GREEN_GRAPE_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.SPEED, BRIEF_DURATION, 0), 1.0F);
    public static final HHFood SWEET_BERRY_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.TEMPTING, SHORT_DURATION, 0), 1.0F);
    public static final HHFood GLOW_BERRY_JUICE = food(0, 0.0f).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, BRIEF_DURATION, 0), 1.0F);

    // Alcoholic Beverages

    // --- Light Drinks ---
    public static final HHFood ROOT_BEER = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, SHORT_DURATION, 0), 0.5F).effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, BRIEF_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.HASTE, BRIEF_DURATION, 0), 1.0F);

    public static final HHFood HARD_CIDER = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, SHORT_DURATION, 0), 0.5F).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, SHORT_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.RESISTANCE, BRIEF_DURATION, 0), 1.0F);

    // --- Medium Drinks (Wines & Mead) ---
    public static final HHFood MEAD = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.SLOW_FALLING, SHORT_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, BRIEF_DURATION, 0), 1.0F)
            // Merged into bountifulfares:mead_bottle, which keeps Bountiful Fares' poison cure.
            .consumeEffect(() -> new net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect(MobEffects.POISON));

    public static final HHFood BLUEBERRY_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.LUCK, BRIEF_DURATION, 1), 0.75F);

    public static final HHFood CHERRY_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.HASTE, BRIEF_DURATION, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.STRENGTH, BRIEF_DURATION, 0), 1.0F);

    public static final HHFood GREEN_GRAPE_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.SPEED, SHORT_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.JUMP_BOOST, BRIEF_DURATION, 0), 1.0F);

    public static final HHFood RASPBERRY_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(HHModEffects.PRICKLY, SHORT_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.RESISTANCE, SHORT_DURATION, 0), 0.5F);

    public static final HHFood RED_GRAPE_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.RESISTANCE, BRIEF_DURATION, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, BRIEF_DURATION, 0), 0.5F);

    public static final HHFood SWEET_BERRY_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.INSTANT_HEALTH, 0, 0), 1.0F).effect(() -> new MobEffectInstance(HHModEffects.TEMPTING, MEDIUM_DURATION, 0), 1.0F);

    public static final HHFood GLOW_BERRY_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(HHModEffects.CLARITY, SHORT_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, SHORT_DURATION, 0), 0.5F);

    public static final HHFood MELON_WINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 0), 0.75F).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, SHORT_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, SHORT_DURATION, 0), 1.0F);

    // --- Strong Drinks ---
    public static final HHFood MOONSHINE = food(1, 0.2f).alwaysEdible().effect(() -> new MobEffectInstance(HHModEffects.DRUNK, MEDIUM_DURATION, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.SATURATION, MEDIUM_DURATION, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.STRENGTH, SHORT_DURATION, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.SLOWNESS, SHORT_DURATION, 1), 1.0F);

    // Jams & Spreads (per serving)
    public static final HHFood RASPBERRY_JAM = jam(MobEffects.SPEED, BRIEF_DURATION);
    public static final HHFood BLUEBERRY_JAM = jam(MobEffects.HASTE, BRIEF_DURATION);
    public static final HHFood CHERRY_JAM = jam(MobEffects.REGENERATION, 160);
    public static final HHFood GRAPE_JAM = jam(MobEffects.RESISTANCE, BRIEF_DURATION);
    public static final HHFood APPLE_JAM = jam(MobEffects.ABSORPTION, BRIEF_DURATION);
    public static final HHFood SWEET_BERRY_JAM = jam(MobEffects.JUMP_BOOST, BRIEF_DURATION);
    public static final HHFood GLOW_BERRY_JAM = jam(MobEffects.NIGHT_VISION, SHORT_DURATION);
    public static final HHFood MELON_JAM = jam(MobEffects.SATURATION, 1);
    public static final HHFood PEANUT_BUTTER = food(4, 0.5f).fast().effect(() -> new MobEffectInstance(MobEffects.STRENGTH, BRIEF_DURATION, 0), 1.0F);

    // Pickled Foods (per serving)
    public static final HHFood PICKLED_BEETROOTS = pickle();
    public static final HHFood PICKLED_CABBAGE = pickle().effect(() -> new MobEffectInstance(HHModEffects.COMFORT, SHORT_DURATION, 0), 1.0F);
    public static final HHFood PICKLED_CARROTS = pickle().effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, SHORT_DURATION, 0), 1.0F);
    public static final HHFood PICKLED_ONIONS = pickle().effect(() -> new MobEffectInstance(HHModEffects.PUNGENT, MEDIUM_DURATION, 0), 1.0F);
    public static final HHFood PICKLED_POTATOES = pickle().effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, SHORT_DURATION, 0), 1.0F);

    // Sweets & Snacks
    public static final HHFood CARAMEL = food(1, 0.3f);
    public static final HHFood CARAMEL_APPLE = food(4, 0.3f);
    public static final HHFood CHOCOLATE_BAR = food(3, 0.3f);
    public static final HHFood COTTON_CANDY = food(2, 0.3f);
    public static final HHFood BLUEBERRY_MUFFIN = food(6, 0.3f);
    public static final HHFood PEANUT_BUTTER_COOKIE = food(3, 0.3f).fast();
    public static final HHFood MAPLE_COOKIE = food(3, 0.3f).fast();
    public static final HHFood RAISIN_COOKIE = food(2, 0.2f).fast();
    public static final HHFood TRAIL_MIX = food(5, 0.5f).fast().effect(() -> new MobEffectInstance(MobEffects.SPEED, MEDIUM_DURATION, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, SHORT_DURATION, 0), 1.0F);
    public static final HHFood ROASTED_PEANUTS = food(4, 0.5f).fast();
    public static final HHFood POPCORN = food(3, 0.3f).fast();
    public static final HHFood CANDY_CORN = food(4, 0.6f).fast();
    public static final HHFood MARSHMALLOW_STICK = food(2, 0.3f).alwaysEdible().fast().convertsTo(Items.STICK);
    public static final HHFood ROASTED_MARSHMALLOW_STICK = food(4, 0.6f).alwaysEdible().fast().convertsTo(Items.STICK).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, SHORT_DURATION, 0), 1.0F);
    public static final HHFood CHARRED_MARSHMALLOW_STICK = food(1, 0.1f).alwaysEdible().fast().convertsTo(Items.STICK).effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 0), 1.0F);
    public static final HHFood SMORE = food(5, 0.5f).alwaysEdible().fast().effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, 200, 0), 1.0F);
    public static final HHFood SUGAR_CUBES = food(1, 0.1f).alwaysEdible().fast().effect(() -> new MobEffectInstance(MobEffects.SPEED, 100, 0), 1.0F).effect(() -> new MobEffectInstance(MobEffects.HASTE, 100, 0), 1.0F);

    // Baked Goods
    public static final HHFood RASPBERRY_PIE_SLICE = food(3, 0.3f);
    public static final HHFood BLUEBERRY_PIE_SLICE = food(3, 0.3f);
    public static final HHFood CHERRY_PIE_SLICE = food(3, 0.3f);
    public static final HHFood GRAPE_PIE_SLICE = food(3, 0.3f);
    public static final HHFood PEANUT_BUTTER_PIE_SLICE = food(3, 0.3f);
    public static final HHFood CHICKEN_POT_PIE_SLICE = food(5, 0.3f);
    public static final HHFood CARROT_CAKE_SLICE = food(4, 0.3f);
    public static final HHFood CHOCOLATE_CAKE_SLICE = food(6, 0.3f);
    public static final HHFood WAFFLE = food(6, 0.3f);
    public static final HHFood PANCAKE = food(5, 0.4f);
    public static final HHFood CIDER_DONUT = food(5, 0.5f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, 200, 0), 1.0F);
    public static final HHFood CORN_BREAD = food(6, 0.5f);

    // Pizzas
    public static final HHFood PIZZA_SLICE = food(5, 0.5f);
    public static final HHFood MEAT_PIZZA_SLICE = food(6, 0.5f);
    public static final HHFood VEGGIE_PIZZA_SLICE = food(5, 0.5f);
    public static final HHFood CHEESE_PIZZA_SLICE = food(3, 0.5f);

    // Dairy, Meat, and Savory Dishes
    public static final HHFood CHEESE_SLICE = food(3, 0.3f);
    public static final HHFood GOAT_CHEESE_SLICE = food(3, 0.3f);
    public static final HHFood RAW_SAUSAGE = food(2, 0.3f);
    public static final HHFood COOKED_SAUSAGE = food(3, 0.5f);
    public static final HHFood RAW_SKEWERED_SAUSAGE = food(2, 0.3f);
    public static final HHFood SKEWERED_SAUSAGE = food(3, 0.5f);
    public static final HHFood HOT_DOG = food(10, 0.7f);
    public static final HHFood UNCOOKED_CORN_ON_THE_COB = food(6, 0.6f);
    public static final HHFood COOKED_CORN_ON_THE_COB = food(8, 0.8f);
    public static final HHFood TORTILLA = food(5, 0.6f);
    public static final HHFood TACO = food(11, 0.8f);
    public static final HHFood JERKY = food(4, 0.3f);
    public static final HHFood MACARONI_AND_CHEESE = food(11, 0.5f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, SHORT_DURATION, 1), 1.0F);
    public static final HHFood MASHED_POTATOES = food(9, 0.5f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, SHORT_DURATION, 1), 1.0F);
    public static final HHFood PEANUT_BUTTER_AND_JELLY_SANDWICH = food(8, 0.3f);
    public static final HHFood BISCUITS_AND_GRAVY = food(10, 0.5f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, MEDIUM_DURATION, 1), 1.0F);
    public static final HHFood GLAZED_CARROTS = food(10, 0.5f);
    public static final HHFood CORN_STEW = food(8, 0.6f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, MEDIUM_DURATION, 1), 1.0F);
    public static final HHFood STREET_CORN = food(10, 0.8f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, SHORT_DURATION, 1), 1.0F).effect(() -> new MobEffectInstance(MobEffects.HASTE, MEDIUM_DURATION, 1), 1.0F);
    public static final HHFood TAMALE = food(7, 0.6f).effect(() -> new MobEffectInstance(HHModEffects.NOURISHMENT, MEDIUM_DURATION, 1), 1.0F);

    // From Farmer's Delight (FarmersDelightRefabricated 26.3 FoodValues, same numbers)
    public static final HHFood CABBAGE = food(2, 0.4f);
    public static final HHFood CABBAGE_LEAF = food(1, 0.4f);
    public static final HHFood ONION = food(2, 0.4f);
    public static final HHFood PIE_CRUST = food(2, 0.2f);
    public static final HHFood TOMATO_SAUCE = food(4, 0.4f);
    // FD's milk bottle, hot cocoa and melon juice are drinks with no hunger value.
    public static final HHFood MILK_BOTTLE = food(0, 0.0f).alwaysEdible();
    public static final HHFood HOT_COCOA = food(0, 0.0f).alwaysEdible();
    public static final HHFood MELON_JUICE = food(0, 0.0f).alwaysEdible();

    private static HHFood jam(Holder<MobEffect> effect, int duration) {
        return food(3, 0.3f).fast().effect(() -> new MobEffectInstance(effect, duration, 0), 1.0F);
    }

    private static HHFood pickle() {
        return food(4, 0.5f);
    }

    private static HHFood food(int nutrition, float saturation) {
        return HHFood.of(nutrition, saturation);
    }
}
