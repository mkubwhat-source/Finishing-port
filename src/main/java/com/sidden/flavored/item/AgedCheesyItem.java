package com.sidden.flavored.item;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/** Aged-cheese foods: eating one cures every harmful effect. */
public class AgedCheesyItem extends Item {
    public AgedCheesyItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide()) {
            for (MobEffectInstance effect : List.copyOf(livingEntity.getActiveEffects())) {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                    livingEntity.removeEffect(effect.getEffect());
                }
            }
        }
        return result;
    }
}
