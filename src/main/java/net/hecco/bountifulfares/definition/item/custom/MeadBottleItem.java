package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class MeadBottleItem extends LiquidBottleItem {
    public MeadBottleItem(Properties settings) {
        super(settings);
    }
    public MeadBottleItem(List<MobEffectInstance> effects, Properties settings) {
        super(settings);
        this.effects = effects;
    }
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user.getEffect(MobEffects.POISON) != null) {
            user.removeEffect(MobEffects.POISON);
        }
        return super.finishUsingItem(stack, world, user);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        if (Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("tooltip.bountifulfares.removes").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable(new MobEffectInstance(MobEffects.POISON).getDescriptionId().formatted(MobEffects.POISON.value().getCategory().getTooltipFormatting())).withStyle(ChatFormatting.RED));
        }
    }
}
