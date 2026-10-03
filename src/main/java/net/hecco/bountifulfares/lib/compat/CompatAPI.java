package net.hecco.bountifulfares.lib.compat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Ported in-tree from NexusLib's {@code NLCompatAPI} as part of removing the NexusLib
 * dependency during the 26.3 Fabric port. {@link net.hecco.bountifulfares.mixin.util.FeatureElementMixin}
 * reads {@link #DISABLED_CACHE} to actually enforce this at runtime.
 */
public class CompatAPI {
    private static final Map<String, CompatManager> MANAGERS = new HashMap<>();

    public static CompatManager createCompatManager(String modId) {
        CompatManager manager = new CompatManager(modId);
        MANAGERS.put(modId, manager);
        return manager;
    }

    public static Map<String, CompatManager> getManagers() {
        return MANAGERS;
    }

    public static final Set<Identifier> DISABLED_CACHE = new ObjectOpenHashSet<>();

    public static final Map<FeatureElement, Identifier> ID_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    public static Identifier getId(FeatureElement element) {
        if (ID_CACHE.containsKey(element)) {
            return ID_CACHE.get(element);
        } else {
            Identifier id = resolveId(element);
            ID_CACHE.put(element, id);
            return id;
        }
    }

    public static Identifier resolveId(FeatureElement element) {
        if (element instanceof Item item)
            return BuiltInRegistries.ITEM.getKey(item);
        if (element instanceof Block block)
            return BuiltInRegistries.BLOCK.getKey(block);
        return null;
    }
}
