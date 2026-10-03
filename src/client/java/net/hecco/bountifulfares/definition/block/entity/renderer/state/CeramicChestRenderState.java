package net.hecco.bountifulfares.definition.block.entity.renderer.state;

import net.hecco.bountifulfares.definition.block.entity.CeramicChestBlockEntity;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Render state for {@code CeramicChestRenderer} (26.3 render-state redesign, project checkpoint
 * doc part B). Mirrors what vanilla's own {@code ChestRenderer.extractRenderState(...)} computes
 * (confirmed via {@code javap -c}): the resolved {@code ChestType} (single/left/right), the
 * block's facing (for the yaw rotation {@code submit(...)} applies), the neighbor-combined
 * openness angle (already eased, {@code 1 - (1-t)^3}, same as the old {@code render(...)} body),
 * and the neighbor-combined packed light (only actually differing from the base-extracted value
 * for a double chest - see {@code extractRenderState}). {@code color} is the dish's dye tint,
 * pre-opacified via {@code FastColor.ARGB32.opaque(...)} exactly as the old code did.
 */
public class CeramicChestRenderState extends BlockEntityRenderState {
    public ChestType type = ChestType.SINGLE;
    public Direction facing = Direction.SOUTH;
    public float open;
    public int color = CeramicChestBlockEntity.DEFAULT_COLOR;
}
