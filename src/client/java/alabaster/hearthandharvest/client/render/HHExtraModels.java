package alabaster.hearthandharvest.client.render;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standalone models drawn by HH's block entity and item renderers (1.21.1: ModelEvent.RegisterAdditional):
 * every {@code models/display/*.json} (bottle rack / crate / jar display models, except templates),
 * and the big stomping basin quarters.
 */
public final class HHExtraModels {
    private static final Map<Identifier, ExtraModelKey<BlockStateModel>> KEYS = new ConcurrentHashMap<>();
    private static final Set<Identifier> FIXED = Set.of(
            HearthAndHarvest.id("block/big_stomping_basin_nw"),
            HearthAndHarvest.id("block/big_stomping_basin_ne"),
            HearthAndHarvest.id("block/big_stomping_basin_sw"),
            HearthAndHarvest.id("block/big_stomping_basin_se"));

    private HHExtraModels() {}

    public static void register() {
        PreparableModelLoadingPlugin.register((sharedState, executor) -> CompletableFuture.supplyAsync(() -> {
            var resourceManager = sharedState.resourceManager();
            Set<Identifier> ids = new HashSet<>(FIXED);
            resourceManager.listResources("models/display", path -> path.getPath().endsWith(".json") && !path.getPath().contains("/template_"))
                    .keySet().forEach(file -> {
                        String path = file.getPath();
                        ids.add(Identifier.fromNamespaceAndPath(file.getNamespace(), path.substring("models/".length(), path.length() - ".json".length())));
                    });
            return ids;
        }, executor), (ids, context) -> {
            for (Identifier id : ids) {
                ExtraModelKey<BlockStateModel> key = KEYS.computeIfAbsent(id, i -> ExtraModelKey.create(i::toString));
                context.addModel(key, SimpleUnbakedExtraModel.blockStateModel(id));
            }
        });
    }

    /** The baked standalone model with this id (e.g. {@code hearthandharvest:display/oak_bottle_rack_wine}), or null. */
    public static @Nullable BlockStateModel get(Identifier id) {
        ExtraModelKey<BlockStateModel> key = KEYS.get(id);
        if (key == null) return null;
        return ((FabricModelManager) Minecraft.getInstance().getModelManager()).getModel(key);
    }
}
