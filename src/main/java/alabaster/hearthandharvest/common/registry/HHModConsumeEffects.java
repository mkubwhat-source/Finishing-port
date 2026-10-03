package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.item.component.consumable.HealConsumeEffect;
import alabaster.hearthandharvest.common.fd.item.component.consumable.RemoveRandomStatusEffectsConsumeEffect;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.function.Supplier;

/** Farmer's Delight's consume effects (milk bottle / hot cocoa, melon juice), ported in. */
public class HHModConsumeEffects {
    public static final Supplier<ConsumeEffect.Type<RemoveRandomStatusEffectsConsumeEffect>> REMOVE_RANDOM_EFFECTS = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID, "remove_random_effects",
            BuiltInRegistries.CONSUME_EFFECT_TYPE, () -> new ConsumeEffect.Type<>(RemoveRandomStatusEffectsConsumeEffect.CODEC, RemoveRandomStatusEffectsConsumeEffect.STREAM_CODEC));
    public static final Supplier<ConsumeEffect.Type<HealConsumeEffect>> HEAL = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID, "heal",
            BuiltInRegistries.CONSUME_EFFECT_TYPE, () -> new ConsumeEffect.Type<>(HealConsumeEffect.CODEC, HealConsumeEffect.STREAM_CODEC));

    public static void init() {
    }
}
