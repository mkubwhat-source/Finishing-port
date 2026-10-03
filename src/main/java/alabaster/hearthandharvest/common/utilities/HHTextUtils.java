package alabaster.hearthandharvest.common.utilities;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HHTextUtils
{
    public static MutableComponent getTranslation(String key, Object... args) {
        return Component.translatable(HearthAndHarvest.MODID + "." + key, args);
    }
}