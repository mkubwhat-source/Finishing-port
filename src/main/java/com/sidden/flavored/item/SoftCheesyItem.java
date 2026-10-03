package com.sidden.flavored.item;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/** Soft-cheese foods: eating one has a 1-in-5 chance per harmful effect to cure it. */
public class SoftCheesyItem extends Item {
    public SoftCheesyItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide()) {
            // copy: removing effects while iterating the live collection would throw
            for (MobEffectInstance effect : List.copyOf(livingEntity.getActiveEffects())) {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && livingEntity.getRandom().nextInt(5) == 0) {
                    livingEntity.removeEffect(effect.getEffect());
                }
            }
        }
        return result;
    }
}
