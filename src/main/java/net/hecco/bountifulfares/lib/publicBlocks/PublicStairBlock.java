package net.hecco.bountifulfares.lib.publicBlocks;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Ported in-tree from NexusLib as part of removing the NexusLib dependency during the 26.3 Fabric port. */
public class PublicStairBlock extends StairBlock {
    public PublicStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }
}
