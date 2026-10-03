package alabaster.hearthandharvest.common.item;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHDataMaps;
import alabaster.hearthandharvest.common.data.VintageStyle;
import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import java.util.List;

public final class VintageHelper {
    public static final int MAX_VINTAGE = 3;
    public static final String[] STAGE_NAMES = {"aged", "fine", "reserve"};

    private static final float DURATION_BONUS_PER_VINTAGE = 0.5F;

    private VintageHelper() {
    }

    public static int getVintage(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(HHModDataComponents.VINTAGE.get(), 0), 0, MAX_VINTAGE);
    }

    public static ItemStack withVintage(ItemStack stack, int vintage) {
        if (vintage <= 0) {
            stack.remove(HHModDataComponents.VINTAGE.get());
        } else {
            stack.set(HHModDataComponents.VINTAGE.get(), Math.min(vintage, MAX_VINTAGE));
        }
        return stack;
    }

    @Nullable
    public static String stageName(ItemStack stack) {
        int vintage = getVintage(stack);
        return vintage >= 1 && vintage <= MAX_VINTAGE ? STAGE_NAMES[vintage - 1] : null;
    }

    @Nullable
    public static VintageStyle getStyle(ItemStack stack) {
        return HHDataMaps.getVintageStyle(stack.getItem());
    }

    public static boolean isAgeable(ItemStack stack) {
        VintageStyle style = getStyle(stack);
        return style != null && style.ageable();
    }

    public static boolean canAgeFurther(ItemStack stack) {
        return isAgeable(stack) && getVintage(stack) < MAX_VINTAGE;
    }

    @Nullable
    public static String overlayStage(ItemStack stack) {
        VintageStyle style = getStyle(stack);
        if (style == null || !style.hasOverlay()) return null;

        String stage = stageName(stack);
        if (stage == null && style.showFresh() && style.ageable()) {
            return "fresh";
        }
        return stage;
    }

    @Nullable
    public static Identifier overlayModel(ItemStack stack) {
        VintageStyle style = getStyle(stack);
        if (style == null || !style.drawModel()) return null;

        String stage = overlayStage(stack);
        return stage == null ? null : style.modelFor(stage);
    }

    public static float durationFactor(ItemStack stack) {
        return 1.0F + DURATION_BONUS_PER_VINTAGE * getVintage(stack);
    }

    public static ItemStack aged(ItemStack stack) {
        return withVintage(stack.copyWithCount(1), getVintage(stack) + 1);
    }

    /** Nutrition stays; saturation gains {@code saturationBonusPerVintage} per vintage (jars). */
    public static FoodProperties scaleFood(FoodProperties food, int vintage, float saturationBonusPerVintage) {
        float saturation = food.saturation() + food.nutrition() * 2.0F * saturationBonusPerVintage * vintage;
        return new FoodProperties(food.nutrition(), saturation, food.canAlwaysEat());
    }

    /**
     * Status effects last 50% longer per vintage and gain a level at the top vintage. Effects of
     * {@code excluded} are dropped (wine handles Drunk itself). Other consume effects are kept as is.
     */
    public static Consumable scaleConsumable(Consumable consumable, int vintage, @Nullable Holder<MobEffect> excluded) {
        List<ConsumeEffect> effects = new ArrayList<>();
        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (!(effect instanceof ApplyStatusEffectsConsumeEffect apply)) {
                effects.add(effect);
                continue;
            }
            List<MobEffectInstance> scaled = new ArrayList<>();
            for (MobEffectInstance base : apply.effects()) {
                if (excluded != null && base.getEffect().value() == excluded.value()) continue;
                int duration = base.getEffect().value().isInstantaneous()
                        ? base.getDuration()
                        : Math.round(base.getDuration() * (1.0F + DURATION_BONUS_PER_VINTAGE * vintage));
                int amplifier = base.getAmplifier() + (vintage >= MAX_VINTAGE ? 1 : 0);
                scaled.add(new MobEffectInstance(base.getEffect(), duration, amplifier, base.isAmbient(), base.isVisible(), base.showIcon()));
            }
            if (!scaled.isEmpty()) effects.add(new ApplyStatusEffectsConsumeEffect(scaled, apply.probability()));
        }
        return new Consumable(consumable.consumeSeconds(), consumable.animation(), consumable.sound(), consumable.hasConsumeParticles(), effects);
    }

    /**
     * Applies a vintage to a single serving: the returned copy carries the scaled food and consumable
     * components, so consuming it gives the vintage's effects.
     */
    public static ItemStack scaledServing(ItemStack stack, float saturationBonusPerVintage, @Nullable Holder<MobEffect> excluded) {
        ItemStack serving = stack.copyWithCount(1);
        int vintage = getVintage(stack);
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food != null) serving.set(DataComponents.FOOD, scaleFood(food, vintage, saturationBonusPerVintage));
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable != null) serving.set(DataComponents.CONSUMABLE, scaleConsumable(consumable, vintage, excluded));
        return serving;
    }

    public static void appendTooltip(ItemStack stack, Consumer<Component> tooltip) {
        int vintage = getVintage(stack);
        if (vintage > 0) {
            tooltip.accept(Component.translatable("tooltip.hearthandharvest.vintage." + vintage).withStyle(ChatFormatting.GOLD));
        }
    }
}
