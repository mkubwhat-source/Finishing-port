package net.hecco.bountifulfares.registry.misc;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.platform.*;

import java.util.Calendar;

public class BFResourcePacks {
    public static void registerBuiltinResourcePacks() {

        BFRegistryHelper.registerBuiltInResourcepack(BountifulFares.MOD_ID, "vanilla_item_override", "Vanilla Item Overrides", false, true);


        if ((Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1)) {
            BFRegistryHelper.registerBuiltInResourcepack(BountifulFares.MOD_ID, "lime", "Lime", true, true);
        }
    }
}
