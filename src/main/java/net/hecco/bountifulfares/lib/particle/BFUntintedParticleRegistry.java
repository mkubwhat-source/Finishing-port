package net.hecco.bountifulfares.lib.particle;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Ported in-tree from NexusLib's {@code NLUntintedParticleRegistry} as part of removing
 * the NexusLib dependency during the 26.3 Fabric port.
 */
public class BFUntintedParticleRegistry {
    private static final List<Block> BLOCKS = new ArrayList<>();

    public static void add(Block block) {
        BLOCKS.add(block);
    }

    public static List<Block> getBlocks() {
        return BLOCKS;
    }
}
