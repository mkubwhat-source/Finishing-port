package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.function.Consumer;

public class EffectFoodItem extends Item {
    public final List<MobEffectInstance> effects;
    public EffectFoodItem(List<MobEffectInstance> effects, Properties settings) {
        super(settings);
        this.effects = effects;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        if (effects != null && !effects.isEmpty() && Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            PotionContents.addPotionTooltip(effects, tooltip, 1.0F, context.tickRate());
        }
    }
}
