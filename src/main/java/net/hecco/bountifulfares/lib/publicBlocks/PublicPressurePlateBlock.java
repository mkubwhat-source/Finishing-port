package net.hecco.bountifulfares.lib.publicBlocks;

import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** Ported in-tree from NexusLib as part of removing the NexusLib dependency during the 26.3 Fabric port. */
public class PublicPressurePlateBlock extends PressurePlateBlock {
    public PublicPressurePlateBlock(BlockSetType type, Properties properties) {
        super(type, properties);
    }
}
