package com.sidden.flavored;

import com.sidden.flavored.registry.FlavoredDataComponents;
import com.sidden.flavored.registry.FlavoredEffects;
import com.sidden.flavored.registry.FlavoredItemTags;
import com.sidden.flavored.util.ChocolateAddictionManager;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Flavored's gameplay event handlers (1.21.1: FlavoredGameBusEvents on the NeoForge game bus),
 * mapped onto Fabric API events. The wandering trader trades it also added are data now
 * ({@code data/flavored/villager_trade/wandering_trader/*} + the vanilla trade tags), and the
 * spiciness tooltip is registered client-side (FlavoredClient).
 */
public final class FlavoredEvents {
    public static void register() {
        // Sugar Crave: only chocolaty foods can be eaten (PlayerInteractEvent.RightClickItem).
        UseItemCallback.EVENT.register((player, level, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (player.hasEffect(FlavoredEffects.SUGAR_CRAVE) && !stack.is(FlavoredItemTags.CHOCOLATY) && stack.has(DataComponents.FOOD)) {
                if (!level.isClientSide()) {
                    player.sendOverlayMessage(Component.translatable("message.flavored.sugar_crave"));
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // Heat: attacks set the target on fire (AttackEntityEvent).
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide() && player.hasEffect(FlavoredEffects.HEAT)) {
                entity.igniteForSeconds(3f);
            }
            return InteractionResult.PASS;
        });

        // Chocolate addiction drain (PlayerTickEvent.Post; the manager is server-only anyway).
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ChocolateAddictionManager.tickAddiction(player);
            }
        });

        // Every Sugar Rush raises chocolate addiction (MobEffectEvent.Added).
        ServerMobEffectEvents.AFTER_ADD.register((effect, entity, context) -> {
            if (effect.is(FlavoredEffects.SUGAR_RUSH) && entity instanceof Player player) {
                ChocolateAddictionManager.increaseAddiction(player);
            }
        });

        // Booze wears off into a one minute hangover (MobEffectEvent.Expired). Fabric reports an
        // expiry as a removal; removals by command (/effect clear) are skipped like 1.21.1 did.
        ServerMobEffectEvents.AFTER_REMOVE.register((effect, entity, context) -> {
            if (effect.is(FlavoredEffects.BOOZED) && !context.isFromCommand()) {
                entity.addEffect(new MobEffectInstance(FlavoredEffects.HANGOVER, 20 * 60));
            }
        });

        // Milk does not cure Sugar Crave, Booze or Hangover (MobEffectEvent.Remove with the MILK
        // cure). 26.3 has no cure types; every non-command early removal (milk, totem, ...) of
        // these three is refused.
        ServerMobEffectEvents.ALLOW_EARLY_REMOVE.register((effect, entity, context) ->
                context.isFromCommand() || !(effect.is(FlavoredEffects.SUGAR_CRAVE) || effect.is(FlavoredEffects.BOOZED) || effect.is(FlavoredEffects.HANGOVER)));
    }

    /**
     * Eating spicy food gives Heat for 30s per spiciness level (LivingEntityUseItemEvent.Finish);
     * called from ConsumableMixin when a food is consumed.
     */
    public static void onConsumed(LivingEntity entity, ItemStack stack) {
        Integer spiciness = stack.get(FlavoredDataComponents.SPICINESS.get());
        if (spiciness != null && stack.has(DataComponents.FOOD) && !entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(FlavoredEffects.HEAT, 20 * 30 * spiciness));
        }
    }

    private FlavoredEvents() {
    }
}
