package net.hecco.bountifulfares.mixin.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Exposes {@code FireBlock.setFlammable}, which is otherwise package-private. Ported
 * in-tree from NexusLib's {@code FireBlockSetFlammableInvoker} as part of removing the
 * NexusLib dependency during the 26.3 Fabric port; backs
 * {@link net.hecco.bountifulfares.platform.BFRegistryHelper#setFlammable}.
 */
@Mixin(FireBlock.class)
public interface FireBlockSetFlammableInvoker {
    @Invoker("setFlammable")
    void bountifulfares$setFlammable(Block block, int encouragement, int flammability);
}
