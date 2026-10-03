package net.hecco.bountifulfares.definition.item.integration;

import net.hecco.bountifulfares.definition.item.custom.TeaBottleItem;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.registry.content.BFEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MapleMeadBottleItem extends TeaBottleItem {
    public MapleMeadBottleItem(Properties settings) {
        super(List.of(new MobEffectInstance(MobEffects.HUNGER), new MobEffectInstance(MobEffects.WEAKNESS), new MobEffectInstance(MobEffects.SLOWNESS)), settings);
    }

    public ArrayList<Holder<MobEffect>> getStatusEffectsToRemove() {
        ArrayList<Holder<MobEffect>> list = new ArrayList<>();
        list.add(MobEffects.HUNGER);
        list.add(MobEffects.WEAKNESS);
        list.add(MobEffects.SLOWNESS);
        return list;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        if (Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            PotionContents.addPotionTooltip(List.of(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, true, true)), tooltip, 1.0F, context.tickRate());
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("tooltip.bountifulfares.removes").withStyle(ChatFormatting.GRAY));
            for (MobEffectInstance effect : removedEffects) {
                tooltip.accept(Component.translatable(effect.getDescriptionId().formatted(effect.getEffect().value().getCategory().getTooltipFormatting())).withStyle(ChatFormatting.RED));
            }
        }
    }
}