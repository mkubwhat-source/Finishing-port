package net.hecco.bountifulfares.mixin.util;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Read access to an {@link ItemStackRenderState}'s layer list, so {@code TiffinItemModel} can
 * post-transform the layers appended for a tiffin's contents (the 26.3 replacement for the old
 * GuiGraphics mixin's "pushPose / scale / translate / render contents / popPose" corner icon).
 */
@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {
    @Accessor("layers")
    ItemStackRenderState.LayerRenderState[] bountifulfares$getLayers();

    @Accessor("activeLayerCount")
    int bountifulfares$getActiveLayerCount();
}
