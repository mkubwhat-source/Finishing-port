package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class EdibleJarItem extends Item {
    public List<MobEffectInstance> effects;
    public Holder<SoundEvent> eatSound;

    public EdibleJarItem(Properties settings) {
        super(settings);
        this.eatSound = SoundEvents.GENERIC_EAT;
    }

    public EdibleJarItem(List<MobEffectInstance> effects, Holder<SoundEvent> eatSound, Properties settings) {
        super(settings);
        this.effects = effects;
        this.eatSound = eatSound;
    }

    public EdibleJarItem(List<MobEffectInstance> effects, Properties settings) {
        super(settings);
        this.effects = effects;
        this.eatSound = SoundEvents.GENERIC_EAT;
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
        }
        if (stack.isEmpty()) {
            return new ItemStack(BFItems.CUP.get());
        } else {
            if (user instanceof Player && !((Player)user).getAbilities().instabuild) {
                ItemStack itemStack = new ItemStack(BFItems.CUP.get());
                Player playerEntity = (Player)user;
                if (!playerEntity.getInventory().add(itemStack)) {
                    playerEntity.drop(itemStack, false, net.minecraft.util.Prediction.SERVER_ONLY);
                }
            }

            return stack;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
        if (!effects.isEmpty() && Services.PLATFORM.get().getBoolConfigValue("effectTooltips")) {
            PotionContents.addPotionTooltip(effects, tooltip, 1.0F, context.tickRate());
        }
    }
}
