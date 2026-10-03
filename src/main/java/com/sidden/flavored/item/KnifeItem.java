package com.sidden.flavored.item;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;

import java.util.List;

/**
 * The knife: +1 attack damage, 1 durability per hit, 2 per block (26.3: the per-hit damage that
 * 1.21.1's hurtEnemy/postHurtEnemy overrides did is the {@code minecraft:weapon} component, set in
 * FlavoredItems; enchantability 14 is {@code minecraft:enchantable}).
 */
public class KnifeItem extends Item {
    public KnifeItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 1.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 2, true);
    }
}
