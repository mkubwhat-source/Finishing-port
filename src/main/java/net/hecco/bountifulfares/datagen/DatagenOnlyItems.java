package net.hecco.bountifulfares.datagen;

import net.hecco.bountifulfares.platform.BFProperties;
import net.hecco.bountifulfares.platform.BFRegistryHelper;

import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Arrays;

import static net.hecco.bountifulfares.BountifulFares.*;


public class DatagenOnlyItems {
    /** Ids of the placeholder items registered here (datagen runs only; never exist in game). */
    public static final java.util.Set<Identifier> IDS = new java.util.HashSet<>();

    private static void registerDatagenOnlyItem(String modId, String name) {
        if (BFPlatformCompat.isDatagen()) {
            IDS.add(Identifier.fromNamespaceAndPath(modId, name));
            BFRegistryHelper.registerItem(modId, name, () -> new Item(BFProperties.item()));
        }
    }
    public static void registerDatagenItems() {
        for (DyeColor color : Arrays.stream(DyeColor.values()).limit(16).toList()) {
            registerDatagenOnlyItem(MOD_ID, color.getName() + "_shulker_tiffin_back");
            registerDatagenOnlyItem(MOD_ID, color.getName() + "_shulker_tiffin_front");
        }
    }

}
