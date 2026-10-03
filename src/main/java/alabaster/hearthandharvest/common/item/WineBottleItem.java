package alabaster.hearthandharvest.common.item;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import alabaster.hearthandharvest.common.registry.HHModEffects;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import alabaster.hearthandharvest.common.HHFood;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;

import java.util.List;
import java.util.function.Supplier;

public class WineBottleItem extends Item implements AgeableItem {
    private static final int DRUNK_DURATION = 2400;
    private static final int MAX_DRUNK_AMPLIFIER = 4;
    private static final float DRUNK_REDUCTION_PER_VINTAGE = 0.2F;
    private static final int BAR_COLOR = 0x9C2A4A;

    private final boolean hasFoodEffectTooltip;
    private final boolean hasCustomTooltip;
    private final Supplier<Fluid> fluid;
    private int glasses = 1;

    public WineBottleItem(Supplier<Fluid> fluid, Properties properties, boolean hasFoodEffectTooltip, boolean hasCustomTooltip) {
        super(properties);
        this.fluid = fluid;
        this.hasFoodEffectTooltip = hasFoodEffectTooltip;
        this.hasCustomTooltip = hasCustomTooltip;
    }

    public WineBottleItem glasses(int glasses) {
        this.glasses = Math.max(1, glasses);
        return this;
    }

    public Fluid getFluid() {
        return this.fluid.get();
    }

    public int getMaxGlasses() {
        return glasses;
    }

    public int getGlasses(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(HHModDataComponents.SERVINGS.get(), glasses), 1, glasses);
    }

    @Override
    public boolean canAgeFurther(ItemStack stack) {
        return !isOpened(stack) && VintageHelper.canAgeFurther(stack);
    }

    public boolean isOpened(ItemStack stack) {
        return glasses > 1 && getGlasses(stack) < glasses;
    }

    private ItemStack withGlasses(ItemStack stack, int remaining) {
        if (remaining >= glasses) {
            stack.remove(HHModDataComponents.SERVINGS.get());
        } else {
            stack.set(HHModDataComponents.SERVINGS.get(), remaining);
        }
        return stack;
    }

    private ItemStack leftoverAfterGlass(ItemStack stack) {
        int remaining = getGlasses(stack) - 1;
        return remaining > 0
                ? withGlasses(stack.copyWithCount(1), remaining)
                : new ItemStack(Items.GLASS_BOTTLE);
    }

    // Drinking: the consumable component (eat time, DRINK animation, wine sound) drives use; 26.3's
    // default Item#use starts it when the drinker can drink (always-edible wines can always be drunk).

    /** Item properties for a wine bottle: an HH drink with the wine drinking sound, stacking to 16. */
    public static Item.Properties properties(Item.Properties properties, HHFood food) {
        return food.drink(properties, HHModSounds.WINE_DRINK.get()).stacksTo(16);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        Player player = consumer instanceof Player p ? p : null;
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable == null) {
            return super.finishUsingItem(stack, level, consumer);
        }

        int vintage = VintageHelper.getVintage(stack);
        if (!level.isClientSide()) {
            applyDrunk(consumable, vintage, level, consumer);
        }
        // Drink one serving with the vintage's effects (Drunk is handled above, scaled by vintage).
        ItemStack serving = VintageHelper.scaledServing(stack, 0.0F, HHModEffects.DRUNK);
        serving.get(DataComponents.CONSUMABLE).onConsume(level, consumer, serving);

        if (player != null && player.getAbilities().instabuild) {
            return stack;
        }

        ItemStack leftover = leftoverAfterGlass(stack);
        stack.shrink(1);
        return giveBack(stack, leftover, player);
    }

    private static ItemStack giveBack(ItemStack stack, ItemStack leftover, Player player) {
        if (stack.isEmpty()) {
            return leftover;
        }
        if (player != null && !player.getAbilities().instabuild && !player.getInventory().add(leftover)) {
            player.drop(leftover, false, net.minecraft.util.Prediction.SERVER_ONLY);
        }
        return stack;
    }

    private static void applyDrunk(Consumable consumable, int vintage, Level level, LivingEntity consumer) {
        float drunkProbability = 0.0F;
        int baseAmplifier = 0;
        search:
        for (ConsumeEffect consumeEffect : consumable.onConsumeEffects()) {
            if (!(consumeEffect instanceof ApplyStatusEffectsConsumeEffect apply)) continue;
            for (MobEffectInstance effect : apply.effects()) {
                if (effect.getEffect().value() == HHModEffects.DRUNK.value()) {
                    drunkProbability = apply.probability();
                    baseAmplifier = effect.getAmplifier();
                    break search;
                }
            }
        }
        drunkProbability *= Math.max(0.0F, 1.0F - DRUNK_REDUCTION_PER_VINTAGE * vintage);
        if (drunkProbability <= 0.0F || level.getRandom().nextFloat() >= drunkProbability) return;

        MobEffectInstance existing = consumer.getEffect(HHModEffects.DRUNK);
        int escalated = (existing != null ? existing.getAmplifier() : -1) + 1;
        int amplifier = Math.min(Math.max(escalated, baseAmplifier), MAX_DRUNK_AMPLIFIER);
        consumer.addEffect(new MobEffectInstance(HHModEffects.DRUNK, DRUNK_DURATION, amplifier, false, true));
    }

    /** An opened bottle leaves the rest of the wine behind when used in crafting; the last glass leaves the bottle. */
    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        return ItemStackTemplate.fromNonEmptyStack(leftoverAfterGlass(stack));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isOpened(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getGlasses(stack) / glasses);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag isAdvanced) {
        VintageHelper.appendTooltip(stack, tooltip);
        if (glasses > 1) {
            tooltip.accept(Component.translatable("tooltip.hearthandharvest.glasses", getGlasses(stack), glasses).withStyle(ChatFormatting.GRAY));
        }
        if (Config.ENABLE_FOOD_EFFECT_TOOLTIP.get()) {
            if (this.hasCustomTooltip) {
                MutableComponent textEmpty = TextUtils.getTranslation("tooltip." + BuiltInRegistries.ITEM.getKey(this).getPath());
                tooltip.accept(textEmpty.withStyle(ChatFormatting.BLUE));
            }
            if (this.hasFoodEffectTooltip) {
                TextUtils.addFoodEffectTooltip(stack, tooltip, VintageHelper.durationFactor(stack), context.tickRate());
            }
        }
    }
}