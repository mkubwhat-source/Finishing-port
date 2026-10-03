package net.hecco.bountifulfares.mixin.util;

import net.hecco.bountifulfares.lib.compat.CompatAPI;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes vanilla's own {@code FeatureElement.isEnabled} report our own compat-integration
 * content as disabled when the integration it belongs to isn't active. Ported in-tree from
 * NexusLib's {@code FeatureElementMixin} as part of removing the NexusLib dependency during
 * the 26.3 Fabric port; this is what actually enforces
 * {@link net.hecco.bountifulfares.lib.compat.CompatManager}'s disabled-content cache at
 * runtime, not just at datagen time.
 */
@Mixin(FeatureElement.class)
public interface FeatureElementMixin {
    @Inject(
            method = "isEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void bountifulfares$addCompatLogic(FeatureFlagSet enabledFeatures, CallbackInfoReturnable<Boolean> cir) {
        FeatureElement self = (FeatureElement)(Object)this;
        Identifier id = CompatAPI.getId(self);
        if (id != null && !id.getNamespace().equals("minecraft")) {
            cir.setReturnValue(!CompatAPI.DISABLED_CACHE.contains(id));
        }
    }
}
