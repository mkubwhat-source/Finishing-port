package net.hecco.bountifulfares.lib.publicBlocks;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** Ported in-tree from NexusLib as part of removing the NexusLib dependency during the 26.3 Fabric port. */
public class PublicButtonBlock extends ButtonBlock {
    public PublicButtonBlock(BlockSetType type, int ticksToStayPressed, Properties properties) {
        super(type, ticksToStayPressed, properties);
    }
}
