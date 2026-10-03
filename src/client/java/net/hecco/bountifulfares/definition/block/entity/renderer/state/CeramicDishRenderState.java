package net.hecco.bountifulfares.definition.block.entity.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

/**
 * Render state for {@code CeramicDishRenderer}, part of the 26.3 block-entity-renderer "render
 * state" redesign (see the project checkpoint doc, part B). Mirrors the shape confirmed via
 * javap against vanilla's {@code ShelfRenderState} (the closest vanilla analog - a block entity
 * that displays a single held {@code ItemStack} in-world): a mutable {@code ItemStackRenderState}
 * allocated once and repopulated every frame by {@code ItemModelResolver.updateForTopItem(...)},
 * plus whatever extra per-instance data {@code submit(...)} needs (here, the dish's facing, for
 * the same Y-rotation this renderer's old {@code render(...)} body applied).
 */
public class CeramicDishRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    public Direction facing = Direction.NORTH;
}
