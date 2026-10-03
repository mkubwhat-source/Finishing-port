package net.hecco.bountifulfares.mixin.util;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Read access to a layer's display transform and (mutable) local transform - see
 * {@link ItemStackRenderStateAccessor}. At submit time a layer draws with
 * {@code pose * itemTransform(displayContext) * localTransform} (confirmed via {@code javap -c}
 * on {@code LayerRenderState.applyTransform}).
 */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface LayerRenderStateAccessor {
    @Accessor("itemTransform")
    ItemTransform bountifulfares$getItemTransform();

    @Accessor("localTransform")
    Matrix4f bountifulfares$getLocalTransform();
}
