package alabaster.hearthandharvest.client;

import alabaster.hearthandharvest.HearthAndHarvest;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

/**
 * Hearth and Harvest key bindings. 1.21.1 bound "poop" to Shift+P through NeoForge's key modifiers;
 * vanilla/Fabric key mappings have no modifiers, so the binding is P and HearthAndHarvestClient
 * additionally requires Shift to be held.
 */
public final class HHKeyBindings {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(HearthAndHarvest.id("main"));

    public static final KeyMapping POOP = new KeyMapping(
            "key.hearthandharvest.poop",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_P,
            CATEGORY);

    private HHKeyBindings() {}
}
