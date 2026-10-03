package net.hecco.bountifulfares.definition.block.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;

/**
 * Render state for {@code TrellisRenderer} (26.3 render-state redesign, project checkpoint doc
 * part B). Holds only what {@code submit(...)} needs to pick a model and a texture - the actual
 * geometry decision ({@code defaultModel} vs {@code invertedModel}) and the per-plant/crop
 * texture lookup stay in {@code extractRenderState(...)}, mirroring the old {@code render(...)}
 * body's logic exactly, just split across the two lifecycle methods.
 */
public class TrellisRenderState extends BlockEntityRenderState {
    public boolean hasPlant;
    public boolean inverted;
    public Identifier texture;
    public float yRot;
}
