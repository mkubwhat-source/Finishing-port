package alabaster.hearthandharvest.common.item;

import alabaster.hearthandharvest.common.entity.cleaver.ThrownCleaver;
import alabaster.hearthandharvest.common.fd.item.KnifeItem;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Hearth and Harvest's cleaver: a heavy knife (cuts on the cutting board, butchers mobs for extra
 * meat, see CleaverEvents) that can be charged and thrown, and loses 1 durability when used as a
 * crafting tool. Accepts Loyalty.
 */
public class CleaverItem extends KnifeItem {
    private static final int MIN_THROW_TICKS = 5;

    public CleaverItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.hearthandharvest.cleaver.butchering").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
    }

    /** 1.21.1 also excluded FD's Backstabbing, which isn't part of this bundle. */
    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        if (enchantment.is(Enchantments.LOYALTY)) return true;
        return super.canBeEnchantedWith(stack, enchantment, context);
    }

    /** Used as a crafting ingredient, the cleaver comes back with 1 damage (gone when it breaks). */
    @Override
    public ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        ItemStack remainder = stack.copyWithCount(1);
        if (remainder.isDamageableItem()) {
            remainder.setDamageValue(remainder.getDamageValue() + 1);
            if (remainder.getDamageValue() >= remainder.getMaxDamage()) return null;
        }
        return ItemStackTemplate.fromNonEmptyStack(remainder);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TRIDENT; // 1.21.1 "SPEAR" = 26.3 TRIDENT; 26.3 SPEAR is the kinetic spear animation
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) return InteractionResult.FAIL;
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return false;
        if (getUseDuration(stack, entity) - timeLeft < MIN_THROW_TICKS) return false;
        if (!(level instanceof ServerLevel)) return true;
        EquipmentSlot slot = player.getOffhandItem() == stack ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        stack.hurtAndBreak(1, player, slot);
        ThrownCleaver thrown = new ThrownCleaver(level, player, stack);
        thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 2.0f, 1.0f);
        level.addFreshEntity(thrown);
        level.playSound(null, thrown, HHModSounds.CLEAVER_THROW.get(), SoundSource.PLAYERS, 1.0f, 1.2f);
        if (!player.getAbilities().instabuild) player.getInventory().removeItem(stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }
}
