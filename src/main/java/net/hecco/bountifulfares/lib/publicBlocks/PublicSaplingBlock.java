package net.hecco.bountifulfares.lib.publicBlocks;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;

/** Ported in-tree from NexusLib as part of removing the NexusLib dependency during the 26.3 Fabric port. */
public class PublicSaplingBlock extends SaplingBlock {
    public PublicSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }
}
