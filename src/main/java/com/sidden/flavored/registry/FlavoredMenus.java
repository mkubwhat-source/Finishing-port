package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.menu.KegMenu;
import com.sidden.flavored.menu.MixingBowlMenu;
import com.sidden.flavored.menu.OvenMenu;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

/**
 * 1.21.1's keg menu was a NeoForge "extended" menu that read the block position from the open
 * packet to reach the block entity on the client; it is now a plain container menu like the
 * other two (the client side works on a local container, synced by the slot/data packets).
 */
public final class FlavoredMenus {
    public static final Supplier<MenuType<KegMenu>> KEG = BFRegistryHelper.registerMenu(Flavored.MOD_ID, "keg", KegMenu::new);
    public static final Supplier<MenuType<MixingBowlMenu>> MIXING_BOWL = BFRegistryHelper.registerMenu(Flavored.MOD_ID, "mixing_bowl", MixingBowlMenu::new);
    public static final Supplier<MenuType<OvenMenu>> OVEN = BFRegistryHelper.registerMenu(Flavored.MOD_ID, "oven", OvenMenu::new);

    public static void init() {
    }

    private FlavoredMenus() {
    }
}
