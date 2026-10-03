package com.sidden.flavored.item;

import com.sidden.flavored.entity.ThrownHotSauce;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Hold to wind up, release to throw a hot sauce bottle that sets everything around the impact on
 * fire; the longer the wind-up (capped), the further it flies.
 * <p>
 * 1.21.1 never overrode getUseDuration (0), which made the use run until released; the
 * vanilla "hold until release" duration (72000, as bows/tridents use) gives the same behavior, and
 * the charge time is computed from it.
 */
public class HotSauceItem extends Item {
    private static final int USE_DURATION = 72000;

    public HotSauceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.SPEAR;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) return false;
        int time = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (time < 5) return false;

        if (!level.isClientSide()) {
            ThrownHotSauce thrownHotSauce = new ThrownHotSauce(level, player, stack.copyWithCount(1));
            thrownHotSauce.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, (float) Math.clamp(time, 0, 12) / 10, 1.0F);
            thrownHotSauce.setThrowPower((float) time / 25);
            level.addFreshEntity(thrownHotSauce);
            level.playSound(null, thrownHotSauce, SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }
}
