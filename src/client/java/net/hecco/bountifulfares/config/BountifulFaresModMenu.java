package net.hecco.bountifulfares.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Mod Menu integration. The config screen is built with Cloth Config, which fabric.mod.json only
 * "suggests" (as in 1.21.1, the mod runs without it): without Cloth Config installed Mod Menu
 * gets its default no-screen factory instead of a button that would crash on click.
 */
public class BountifulFaresModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ClothConfigScreen::buildConfigScreen;
        }
        return ModMenuApi.super.getModConfigScreenFactory();
    }
}
