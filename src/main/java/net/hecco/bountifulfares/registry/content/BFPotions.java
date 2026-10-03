package net.hecco.bountifulfares.registry.content;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class BFPotions {
    // Potion's constructor gained a leading String name in 26.3; PotionContents.getName builds the
    // "item.minecraft.<potion|splash_potion|...>.effect.<name>" key from it. In 1.21.1 (no name)
    // that suffix was the registry path, e.g. "bountifulfares.long_acidic", and every lang file
    // (en_us datagen + 6 hand-translated ones) uses those per-variant keys, so the name is the
    // same per-variant string rather than vanilla's shared base name.
    public static final Holder<Potion> ACIDIC = BFRegistryHelper.registerForHolder(BountifulFares.MOD_ID, "bountifulfares.acidic", BuiltInRegistries.POTION,
            () -> new Potion("bountifulfares.acidic", new MobEffectInstance(BFEffects.ACIDIC, 2000, 0)));

    public static final Holder<Potion> LONG_ACIDIC = BFRegistryHelper.registerForHolder(BountifulFares.MOD_ID, "bountifulfares.long_acidic", BuiltInRegistries.POTION,
            () -> new Potion("bountifulfares.long_acidic", new MobEffectInstance(BFEffects.ACIDIC, 3600, 0)));

    public static final Holder<Potion> STRONG_ACIDIC = BFRegistryHelper.registerForHolder(BountifulFares.MOD_ID, "bountifulfares.strong_acidic", BuiltInRegistries.POTION,
            () -> new Potion("bountifulfares.strong_acidic", new MobEffectInstance(BFEffects.ACIDIC, 1000, 1)));

    public static final Holder<Potion> STUPOR = BFRegistryHelper.registerForHolder(BountifulFares.MOD_ID, "bountifulfares.stupor", BuiltInRegistries.POTION,
            () -> new Potion("bountifulfares.stupor", new MobEffectInstance(BFEffects.STUPOR, 2000, 0)));

    public static final Holder<Potion> LONG_STUPOR = BFRegistryHelper.registerForHolder(BountifulFares.MOD_ID, "bountifulfares.long_stupor", BuiltInRegistries.POTION,
            () -> new Potion("bountifulfares.long_stupor", new MobEffectInstance(BFEffects.STUPOR, 3600, 0)));

    public static void registerPotions() {
    }
}
