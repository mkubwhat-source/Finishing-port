package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class AirTimeIncreasingItem extends EffectFoodItem {
    public int airTickIncrease;
    public AirTimeIncreasingItem(int airTickIncrease, Properties settings) {
        super(List.of(), settings);
        this.airTickIncrease = airTickIncrease;
    }
    public AirTimeIncreasingItem(List<MobEffectInstance> effects, int airTickIncrease, Properties settings) {
        super(effects, settings);
        this.airTickIncrease = airTickIncrease;
    }



    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
        }
        int air = user.getAirSupply();
        int maxAir = user.getMaxAirSupply();
        if (air < maxAir - airTickIncrease){
            user.setAirSupply(air + airTickIncrease);
        } else {
            user.setAirSupply(maxAir);
        }
        return super.finishUsingItem(stack, world, user);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        if (Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("tooltip.bountifulfares.when_eaten").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("+" + airTickIncrease / 20 + " ").append(Component.translatable("tooltip.bountifulfares.air_time")).withStyle(ChatFormatting.BLUE));
        }
    }
}
