package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import java.util.List;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.item.CleaverItem;
import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import alabaster.hearthandharvest.common.entity.cleaver.ThrownCleaver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CleaverEvents {

    private static final Identifier CLEAVER_SPEED_ID =
            Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "cleaver_charge_speed");

    public static void register() {
        LootTableEvents.MODIFY_DROPS.register((table, context, drops) -> onDrops(context, drops));
        HHEvents.PLAYER_TICK_PRE.register(CleaverEvents::onPlayerTick);
    }

    /**
     * Cleaver butchering: a mob in {@code #hearthandharvest:can_be_butchered} killed by a player's
     * cleaver (held or thrown) drops only its meat, plus half as much again. 1.21.1 did this in
     * NeoForge's LivingDeathEvent + LivingDropsEvent; here it edits the death loot as it is rolled.
     */
    private static void onDrops(LootContext context, List<ItemStack> drops) {
        if (!(context.getOptional(LootContextParams.THIS_ENTITY) instanceof LivingEntity target)) return;
        DamageSource source = context.getOptional(LootContextParams.DAMAGE_SOURCE);
        if (source == null || !(source.getEntity() instanceof Player player)) return;
        if (!context.hasParameter(LootContextParams.DIRECT_ATTACKING_ENTITY) && source.getDirectEntity() == null) return;
        Entity direct = source.getDirectEntity();
        boolean cleaverKill = direct instanceof ThrownCleaver
                || (direct == player && player.getMainHandItem().getItem() instanceof CleaverItem);
        if (!cleaverKill) return;
        if (!target.typeHolder().is(HHModTags.CAN_BE_BUTCHERED)) return;

        // Remove non-meat drops
        drops.removeIf(drop -> !drop.is(ItemTags.MEAT));
        if (drops.isEmpty()) return;

        // Add bonus meat
        int meatCount = drops.stream().mapToInt(ItemStack::getCount).sum();
        int bonus = Math.max(0, (int) Math.ceil(meatCount * 0.5));
        ItemStack firstMeat = drops.getFirst().copy();
        if (!firstMeat.isEmpty() && bonus > 0) {
            firstMeat.setCount(bonus);
            drops.add(firstMeat);
        }
    }

    private static void onPlayerTick(Player player) {
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        boolean compensating = player.isUsingItem()
                && player.getUseItem().getItem() instanceof CleaverItem
                && !player.isPassenger()
                && !player.isInWater()
                && !player.isSwimming();

        if (compensating) {
            if (!attr.hasModifier(CLEAVER_SPEED_ID))
                attr.addTransientModifier(new AttributeModifier(CLEAVER_SPEED_ID, 4.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else {
            attr.removeModifier(CLEAVER_SPEED_ID);
        }
    }
}
