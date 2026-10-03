package net.hecco.bountifulfares.platform;

import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Fabric-only client-side registration helper, ported in-tree from NexusLib's
 * {@code NLServices.client()}/{@code FabricClientHelper} as part of removing the NexusLib
 * dependency during the 26.3 Fabric port. Only ever call these methods from client-side
 * code - there's no more runtime "client helper requested on server" guard since callers are
 * responsible for that themselves now (same as every other client-only class in the mod).
 */
public class BFClientHelper {
    // NOTE (26.3 render-type redesign): setBlockRenderType(Block, RenderType) was removed - both
    // RenderType's block-render-type static factories (solid()/cutout()/translucent()) and
    // Fabric's net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap (which this method
    // used to delegate to) are gone entirely from 26.3, with no Java-side replacement found.
    // Block render type is now resolved from the block's own model/texture JSON instead - see
    // the comment in BountifulFaresClient.onInitializeClient() and the project checkpoint doc's
    // part C (BFBlocks.TINGED_GLASS's model JSON sets "force_translucent": true).

    // NOTE (26.3 item-model redesign): registerItemModelPredicate(...) was removed - both
    // net.minecraft.client.renderer.item.ItemProperties and ClampedItemPropertyFunction (the old
    // imperative runtime "model predicate" registry) are gone entirely from 26.3, replaced by
    // item-model properties inside the item's own definition JSON (the artisan brush's "dyed"
    // predicate is now a minecraft:has_component condition in items/artisan_brush.json).

    public static <T extends ParticleOptions> void registerParticle(ParticleType<T> type, ParticleProvider<T> registration) {
        ParticleProviderRegistry.getInstance().register(type, registration);
    }
}
