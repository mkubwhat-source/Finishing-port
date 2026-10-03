package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.common.item.VintageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Display models: how bottles, jars etc. look when placed in racks, crates and nests
 * ({@code models/display/<item>.json}). 1.21.1 baked them as standalone models and drew them with the
 * item renderer; 26.3 draws them through the item model system, so each has an item model definition
 * {@code items/display/<item>.json} and the renderers draw a copy of the stack with its
 * {@code minecraft:item_model} pointed at it (keeping the model's display transforms).
 */
public final class DisplayModels {
    public static final String FOLDER = "display";
    private static final Map<Identifier, Boolean> EXISTS = new ConcurrentHashMap<>();

    private DisplayModels() {
    }

    /** Forget cached lookups (resource reload). */
    public static void clearCache() {
        EXISTS.clear();
    }

    private static boolean exists(Identifier modelId) {
        return EXISTS.computeIfAbsent(modelId, id -> Minecraft.getInstance().getResourceManager()
                .getResource(id.withPath(path -> "items/" + path + ".json")).isPresent());
    }

    /** {@code <namespace>:display/<item path>} if the item has a display model. */
    public static @Nullable Identifier get(ItemStack stack) {
        if (stack.isEmpty()) return null;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return get(itemId.withPath(path -> FOLDER + "/" + path));
    }

    public static @Nullable Identifier get(Identifier modelId) {
        return exists(modelId) ? modelId : null;
    }

    /** The vintage overlay model for an aged stack, if it has one. */
    public static @Nullable Identifier vintageOverlay(ItemStack stack) {
        Identifier model = VintageHelper.overlayModel(stack);
        return model == null ? null : get(model);
    }

    /** Resolves {@code stack} drawn with the display model {@code modelId} into {@code state}. */
    public static void resolve(ItemModelResolver resolver, ItemStackRenderState state, ItemStack stack, Identifier modelId,
                               ItemDisplayContext context, @Nullable Level level, int seed) {
        ItemStack display = stack.copyWithCount(1);
        display.set(DataComponents.ITEM_MODEL, modelId);
        resolver.updateForTopItem(state, display, context, level, null, seed);
    }
}
