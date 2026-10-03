package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class CoconutMilkBottleItem extends LiquidBottleItem {
    public CoconutMilkBottleItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!world.isClientSide()) {
            user.removeAllEffects();
        }

        if (user instanceof Player playerEntity) {
            return ItemUtils.createFilledResult(stack, playerEntity, new ItemStack(Items.GLASS_BOTTLE), false);
        } else {
            stack.consume(1, user);
            return stack;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        if (Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("tooltip.bountifulfares.when_drunk").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.bountifulfares.removes_all_effects").withStyle(ChatFormatting.BLUE));
        }
    }
}
