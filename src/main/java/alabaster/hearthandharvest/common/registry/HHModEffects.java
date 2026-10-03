package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.effect.*;
import alabaster.hearthandharvest.common.fd.effect.ComfortEffect;
import alabaster.hearthandharvest.common.fd.effect.NourishmentEffect;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class HHModEffects {
    public static final Holder<MobEffect> PUNGENT = register("pungent", PungentEffect::new);
    public static final Holder<MobEffect> TEMPTING = register("tempting", TemptingEffect::new);
    public static final Holder<MobEffect> DRUNK = register("drunk", DrunkEffect::new);
    public static final Holder<MobEffect> PRICKLY = register("prickly", PricklyEffect::new);
    public static final Holder<MobEffect> CLARITY = register("clarity", ClarityEffect::new);
    public static final Holder<MobEffect> PINNED = register("pinned", PinnedEffect::new);
    public static final Holder<MobEffect> HORSE_BOOST = register("horse_boost", HorseBoostEffect::new);

    // From Farmer's Delight (FarmersDelightRefabricated 26.3, MIT, vectorwing): HH foods use these,
    // and the bundle no longer depends on Farmer's Delight.
    public static final Holder<MobEffect> NOURISHMENT = register("nourishment", NourishmentEffect::new);
    public static final Holder<MobEffect> COMFORT = register("comfort", ComfortEffect::new);

    private static Holder<MobEffect> register(String name, Supplier<MobEffect> effect) {
        return BFRegistryHelper.registerForHolder(HearthAndHarvest.MODID, name, BuiltInRegistries.MOB_EFFECT, effect);
    }

    public static void init() {
    }
}
