package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class FlavoredEffects {
    public static final Holder<MobEffect> SUGAR_RUSH = register("sugar_rush", new SugarRushEffect(MobEffectCategory.BENEFICIAL, 0xffeadb)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("sugar_rush"), 0.3f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("sugar_rush"), 0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> SUGAR_CRAVE = register("sugar_crave", new SugarCraveEffect(MobEffectCategory.HARMFUL, 0x4e281d));
    public static final Holder<MobEffect> HEAT = register("heat", new HeatEffect(MobEffectCategory.NEUTRAL, 0xd72710));
    public static final Holder<MobEffect> POPPED = register("popped", new PoppedEffect(MobEffectCategory.BENEFICIAL, 0xedc62e));
    public static final Holder<MobEffect> BOOZED = register("boozed", new BoozedEffect(MobEffectCategory.BENEFICIAL, 0xbd334a)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("boozed"), 0.4f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> HANGOVER = register("hangover", new HangoverEffect(MobEffectCategory.BENEFICIAL, 0x42222d)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("hangover"), -0.3f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("hangover"), -0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(Flavored.MOD_ID, name);
    }

    private static Holder<MobEffect> register(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id(name), effect);
    }

    public static void init() {
    }

    private FlavoredEffects() {
    }
}
