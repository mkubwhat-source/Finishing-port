package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EffectClearingItem extends Item {
    protected List<MobEffectInstance> removedEffects;

    public EffectClearingItem(List<MobEffectInstance> removedEffects, Properties settings) {
        super(settings);
        this.removedEffects = removedEffects;
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
            for (Holder<MobEffect> effect : getStatusEffectsToRemove()) {
                user.removeEffect(effect);
            }
        }
        return stack;
    }

    public ArrayList<Holder<MobEffect>> getStatusEffectsToRemove() {
        return new ArrayList<>();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        if (Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("tooltip.bountifulfares.removes").withStyle(ChatFormatting.GRAY));
            for (MobEffectInstance effect : removedEffects) {
                tooltip.accept(Component.translatable(effect.getDescriptionId().formatted(effect.getEffect().value().getCategory().getTooltipFormatting())).withStyle(ChatFormatting.RED));
            }
        }
    }
}
