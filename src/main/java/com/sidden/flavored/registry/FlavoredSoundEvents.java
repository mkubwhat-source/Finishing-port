package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public final class FlavoredSoundEvents {
    public static final Holder<SoundEvent> CHOCOLATE_BREAK = register("block.chocolate.break");
    public static final Holder<SoundEvent> CHOCOLATE_STEP = register("block.chocolate.step");
    public static final Holder<SoundEvent> CHOCOLATE_PLACE = register("block.chocolate.place");
    public static final Holder<SoundEvent> CHOCOLATE_HIT = register("block.chocolate.hit");
    public static final Holder<SoundEvent> CHOCOLATE_FALL = register("block.chocolate.fall");
    public static final Holder<SoundEvent> OVEN_BAKE = register("block.oven.bake");
    public static final Holder<SoundEvent> KEG_FERMENT = register("block.keg.ferment");

    private static Holder<SoundEvent> register(String name) {
        return BFRegistryHelper.registerSoundReference(Flavored.MOD_ID, name);
    }

    public static void init() {
    }

    private FlavoredSoundEvents() {
    }
}
