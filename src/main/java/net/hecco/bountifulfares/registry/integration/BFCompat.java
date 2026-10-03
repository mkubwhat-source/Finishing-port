package net.hecco.bountifulfares.registry.integration;

import net.hecco.bountifulfares.platform.*;

import static net.hecco.bountifulfares.BountifulFares.COMPAT_MANAGER;
import static net.hecco.bountifulfares.BountifulFares.EVERY_COMPAT_MOD_ID;

public class BFCompat {
    public static void register() {
        COMPAT_MANAGER.addIntegration(new NaturesSpiritIntegration());
        COMPAT_MANAGER.addIntegration(new FrontiersIntegration());
        COMPAT_MANAGER.addIntegration(new FarmersDelightIntegration());
        COMPAT_MANAGER.addIntegration(new AmendmentsIntegration());
        COMPAT_MANAGER.addIntegration(new NoMansLandIntegration());
        COMPAT_MANAGER.addIntegration(new ArtsAndCraftsIntegration());
        COMPAT_MANAGER.addIntegration(new AppledogIntegration());
        COMPAT_MANAGER.addIntegration(new NetherExpIntegration());
        COMPAT_MANAGER.addIntegration(new DelicateDyesIntegration());
        COMPAT_MANAGER.addIntegration(new DungeonsDelightIntegration());

        COMPAT_MANAGER.registerCompatContent();

        // EveryCompat/Selene (moonlight-lib) integration is disabled for this 26.3 port: neither
        // mod has published a 26.3 build yet (checked CurseForge directly - EveryCompat's newest
        // listed file is still tagged "1.21"), so their API classes aren't available to compile
        // against at all right now. The two source files are preserved, unmodified, under
        // _disabled_pending_26.3_deps/everycompat/ - restore them (and the
        // curse.maven:every-compat-628539 / curse.maven:selene-499980 compileOnlyApi dependencies
        // that used to be in common/build.gradle) once a 26.3-compatible build exists.
        // if (BFPlatformCompat.isModLoaded(EVERY_COMPAT_MOD_ID)) {
        //     EveryCompatIntegration.register();
        // }
    }
}
