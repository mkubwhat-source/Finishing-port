package alabaster.hearthandharvest.common;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import alabaster.hearthandharvest.common.fd.item.component.consumable.HealConsumeEffect;
import alabaster.hearthandharvest.common.fd.item.component.consumable.RemoveRandomStatusEffectsConsumeEffect;
import alabaster.hearthandharvest.common.fd.FDTags;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A Hearth and Harvest food value, in 26.3 terms.
 * <p>
 * 1.21.1 kept everything in {@code FoodProperties}. 26.3 split it: nutrition, saturation and
 * always-edible stay in {@code FoodProperties}; eat time ({@code fast()}), animation, sound and the
 * on-eat effects moved to the {@code minecraft:consumable} component; {@code usingConvertsTo} became
 * the {@code use_remainder} component. Whether a value is eaten or drunk is chosen when it is
 * applied to an item ({@link #eat} / {@link #drink}), matching the 1.21.1 item class that used it.
 * Effects are suppliers (as in 1.21.1), resolved when the item is registered.
 */
public final class HHFood {
    /** 1.21.1 {@code FoodProperties.Builder.fast()} = 16 ticks. */
    private static final float FAST_SECONDS = 0.8F;
    /** 1.21.1 default eat/drink time = 32 ticks. */
    private static final float EAT_SECONDS = 1.6F;

    private record Effect(Supplier<MobEffectInstance> effect, float chance) {
    }

    private final int nutrition;
    private final float saturation;
    private boolean alwaysEdible;
    private boolean fast;
    private Item convertsTo;
    private final List<Effect> effects = new ArrayList<>();
    private final List<Supplier<ConsumeEffect>> extraEffects = new ArrayList<>();

    private HHFood(int nutrition, float saturation) {
        this.nutrition = nutrition;
        this.saturation = saturation;
    }

    public static HHFood of(int nutrition, float saturation) {
        return new HHFood(nutrition, saturation);
    }

    public HHFood alwaysEdible() {
        this.alwaysEdible = true;
        return this;
    }

    public HHFood fast() {
        this.fast = true;
        return this;
    }

    public HHFood convertsTo(Item item) {
        this.convertsTo = item;
        return this;
    }

    public HHFood effect(Supplier<MobEffectInstance> effect, float chance) {
        this.effects.add(new Effect(effect, chance));
        return this;
    }

    /**
     * Farmer's Delight's milk bottle: removes one random active effect (not in
     * {@code #hearthandharvest:milk_bottle_ignored}). Returns a copy so shared values stay unchanged.
     */
    public HHFood milk() {
        return copy().consumeEffect(() -> new RemoveRandomStatusEffectsConsumeEffect(FDTags.MobEffects.MILK_BOTTLE_IGNORED));
    }

    /** Farmer's Delight's hot cocoa: removes one random harmful effect. */
    public HHFood hotCocoa() {
        return copy().consumeEffect(() -> new RemoveRandomStatusEffectsConsumeEffect(FDTags.MobEffects.HOT_COCOA_IGNORED, true));
    }

    /** Farmer's Delight's melon juice: heals {@code amount} health. */
    public HHFood heal(float amount) {
        return copy().consumeEffect(() -> new HealConsumeEffect(amount));
    }

    public HHFood consumeEffect(Supplier<ConsumeEffect> effect) {
        this.extraEffects.add(effect);
        return this;
    }

    private HHFood copy() {
        HHFood copy = new HHFood(nutrition, saturation);
        copy.alwaysEdible = alwaysEdible;
        copy.fast = fast;
        copy.convertsTo = convertsTo;
        copy.effects.addAll(effects);
        copy.extraEffects.addAll(extraEffects);
        return copy;
    }

    public List<MobEffectInstance> effectInstances() {
        return effects.stream().map(e -> e.effect().get()).toList();
    }

    public FoodProperties foodProperties() {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation);
        if (alwaysEdible) builder.alwaysEdible();
        return builder.build();
    }

    public Consumable consumable(boolean drink) {
        return consumable(drink, null);
    }

    private Consumable consumable(boolean drink, @Nullable Holder<SoundEvent> sound) {
        Consumable.Builder builder = Consumable.builder().consumeSeconds(fast ? FAST_SECONDS : EAT_SECONDS);
        if (drink) builder.animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK);
        if (sound != null) builder.sound(sound);
        for (Effect effect : effects) {
            builder.onConsume(new ApplyStatusEffectsConsumeEffect(effect.effect().get(), effect.chance()));
        }
        for (Supplier<ConsumeEffect> effect : extraEffects) {
            builder.onConsume(effect.get());
        }
        return builder.build();
    }

    /** Eaten food (1.21.1 {@code Item.Properties.food(food)}). */
    public Item.Properties eat(Item.Properties properties) {
        return apply(properties, false);
    }

    /** Drunk food (1.21.1 drink item classes that returned {@code ItemUseAnimation.DRINK}). */
    public Item.Properties drink(Item.Properties properties) {
        return apply(properties, true);
    }

    /** Drunk food with its own drinking sound (HH wine bottles use {@code hearthandharvest:item.wine.drink}). */
    public Item.Properties drink(Item.Properties properties, SoundEvent sound) {
        properties.food(foodProperties(), consumable(true, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound)));
        if (convertsTo != null) properties.usingConvertsTo(convertsTo);
        return properties;
    }

    private Item.Properties apply(Item.Properties properties, boolean drink) {
        properties.food(foodProperties(), consumable(drink));
        if (convertsTo != null) properties.usingConvertsTo(convertsTo);
        return properties;
    }
}
