package alabaster.hearthandharvest.common.item;

import alabaster.hearthandharvest.common.entity.pitchfork.ThrownPitchfork;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import alabaster.hearthandharvest.common.tag.HHModTags;

public class PitchforkItem extends Item {

    private static final int MIN_THROW_TICKS = 10;

    public PitchforkItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.6, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TRIDENT; // 1.21.1 "SPEAR" = 26.3 TRIDENT
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return false;
        int chargedTicks = getUseDuration(stack, entity) - timeLeft;
        if (chargedTicks < MIN_THROW_TICKS) return false;
        if (level.isClientSide()) return false;

        stack.hurtAndBreak(1, player, player.getUsedItemHand().asEquipmentSlot());
        ThrownPitchfork thrown = new ThrownPitchfork(level, player, stack);
        thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 2.5f, 1.0f);
        level.addFreshEntity(thrown);
        level.playSound(null, thrown, HHModSounds.PITCHFORK_THROW.get(), SoundSource.PLAYERS, 1.0f, .75f);
        if (!player.getAbilities().instabuild) {
            player.getInventory().removeItem(stack);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(HHModTags.MINEABLE_WITH_PITCHFORK)) return Float.MAX_VALUE;
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, net.fabricmc.fabric.api.item.v1.EnchantingContext context) {
        if (enchantment.is(Enchantments.LOYALTY)) return true;
        return super.canBeEnchantedWith(stack, enchantment, context);
    }
}