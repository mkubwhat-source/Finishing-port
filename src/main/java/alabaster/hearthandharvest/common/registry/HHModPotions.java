package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

import java.util.function.Supplier;

public class HHModPotions {
    // 26.3 potions take their translation name explicitly. 1.21.1 HH passed none, so the name was
    // the registry path; the same path is passed here so the existing
    // item.minecraft.potion.effect.<path> lang keys keep working.
    public static final Holder<Potion> PUNGENT_POTION = register("pungent_potion", () -> new MobEffectInstance(HHModEffects.PUNGENT, 3600, 0));
    public static final Holder<Potion> LONG_PUNGENT_POTION = register("long_pungent_potion", () -> new MobEffectInstance(HHModEffects.PUNGENT, 9600, 0));
    public static final Holder<Potion> STRONG_PUNGENT_POTION = register("strong_pungent_potion", () -> new MobEffectInstance(HHModEffects.PUNGENT, 1800, 1));
    public static final Holder<Potion> TEMPTING_POTION = register("tempting_potion", () -> new MobEffectInstance(HHModEffects.TEMPTING, 3600, 0));
    public static final Holder<Potion> LONG_TEMPTING_POTION = register("long_tempting_potion", () -> new MobEffectInstance(HHModEffects.TEMPTING, 9600, 0));
    public static final Holder<Potion> STRONG_TEMPTING_POTION = register("strong_tempting_potion", () -> new MobEffectInstance(HHModEffects.TEMPTING, 1800, 1));
    public static final Holder<Potion> BAD_OMEN_POTION = register("bad_omen_potion", () -> new MobEffectInstance(MobEffects.BAD_OMEN, 3600, 0));
    public static final Holder<Potion> LONG_BAD_OMEN_POTION = register("long_bad_omen_potion", () -> new MobEffectInstance(MobEffects.BAD_OMEN, 9600, 0));
    public static final Holder<Potion> STRONG_BAD_OMEN_POTION = register("strong_bad_omen_potion", () -> new MobEffectInstance(MobEffects.BAD_OMEN, 1800, 1));

    private static Holder<Potion> register(String name, Supplier<MobEffectInstance> effect) {
        return BFRegistryHelper.registerForHolder(HearthAndHarvest.MODID, name, BuiltInRegistries.POTION, () -> new Potion(name, effect.get()));
    }

    public static void init() {
    }
}
