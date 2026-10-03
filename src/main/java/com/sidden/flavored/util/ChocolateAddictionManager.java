package com.sidden.flavored.util;

import com.sidden.flavored.registry.FlavoredDataAttachments;
import com.sidden.flavored.registry.FlavoredEffects;
import com.sidden.flavored.registry.FlavoredStats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

/**
 * Chocolate addiction: every Sugar Rush a player gets raises their addiction; at 5 they get Sugar
 * Crave (they can only eat chocolaty food) for 5 minutes. Addiction drains by one per minute.
 */
public final class ChocolateAddictionManager {
    private static final int THRESHOLD = 5;
    private static final int DRAIN_INTERVAL = 20 * 60;

    public static void tickAddiction(Player player) {
        if (player.level().isClientSide()) return;

        long lastTick = player.getAttachedOrCreate(FlavoredDataAttachments.LAST_DRAIN_TICK);
        long currentTick = player.level().getGameTime();

        if (currentTick - lastTick >= DRAIN_INTERVAL) {
            int currentAddiction = player.getAttachedOrCreate(FlavoredDataAttachments.CHOCOLATE_ADDICTION);
            if (currentAddiction > 0) {
                player.setAttached(FlavoredDataAttachments.CHOCOLATE_ADDICTION, currentAddiction - 1);
            }
            if (player.hasEffect(FlavoredEffects.SUGAR_CRAVE)) {
                player.awardStat(FlavoredStats.CRAVE_CHOCOLATE, 1);
            }
            player.setAttached(FlavoredDataAttachments.LAST_DRAIN_TICK, currentTick);
        }
    }

    public static void increaseAddiction(Player player) {
        int newValue = player.getAttachedOrCreate(FlavoredDataAttachments.CHOCOLATE_ADDICTION) + 1;
        player.setAttached(FlavoredDataAttachments.CHOCOLATE_ADDICTION, newValue);
        if (newValue >= THRESHOLD) {
            player.addEffect(new MobEffectInstance(FlavoredEffects.SUGAR_CRAVE, 20 * 60 * 5));
        }
    }

    private ChocolateAddictionManager() {
    }
}
