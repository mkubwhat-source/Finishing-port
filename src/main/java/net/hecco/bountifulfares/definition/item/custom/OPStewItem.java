package net.hecco.bountifulfares.definition.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;

public class OPStewItem extends StackableBowlFoodItem{
    public OPStewItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        tooltip.accept(Component.literal("Perfectly balanced!").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.ITALIC));
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.GRAVEL_HIT;
    }
}
