package net.hecco.bountifulfares.lib.toolAction;

import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

import java.util.function.Supplier;

/**
 * Ported in-tree from NexusLib's {@code NLToolActions} as part of removing the NexusLib
 * dependency during the 26.3 Fabric port.
 *
 * <p>26.3 removed {@code AxeItem}/{@code HoeItem}/{@code ShovelItem} (and their static
 * strippable/tillable/flattenable maps) entirely - tool-block interactions are now driven by a
 * data-driven {@code net.minecraft.core.component.BlockTransformer} registry
 * ({@code Registries.BLOCK_TRANSFORMER}, with the three built-in {@code BlockTransformers.AXE}/
 * {@code HOE}/{@code SHOVEL} entries), confirmed via javap on {@code AxeItem}/{@code HoeItem}/
 * {@code ShovelItem} (gone) and {@code BlockTransformer}/{@code BlockTransformers} (present).
 * Fabric API 0.161.0+26.3 ships a purpose-built helper for exactly this - {@code
 * net.fabricmc.fabric.api.item.v1.BlockTransformerHelper} - with {@code registerStripping}/
 * {@code registerTilling}/{@code registerFlattening(Block, Block)} overloads that reproduce the
 * old simple "source block -> result block, preserving the RotatedPillarBlock axis" semantics
 * exactly (confirmed via javap/bytecode: it internally builds a
 * {@code BlockPredicate.matchesBlocks(source)} matched against
 * {@code BlockStateProvider.of(result)}, the same axis-preserving substitution vanilla's own axe/
 * hoe/shovel logic already does). This class keeps its own call sites (addStrippable/addPathable/
 * addTillable, used only with simple Block-to-Block pairs throughout this codebase - confirmed by
 * grepping every call site) as thin wrappers around that Fabric API helper, so nothing calling
 * into BFToolActions needed to change. The old runtime Map-backed registries and the
 * now-impossible AxeItemMixin/HoeItemMixin/ShovelItemMixin (which mixed into the deleted vanilla
 * classes) are gone; Fabric's own mixin into the registry-loaded BlockTransformer applies these
 * entries automatically.
 */
public class BFToolActions {
    /** Registers an axe-stripping pair. Fails loudly (via BountifulFares.LOGGER) if either
     * block is missing the RotatedPillarBlock AXIS property, matching the old validation. */
    public static void addStrippable(Block log, Block stripped) {
        boolean noLogAxis = !log.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS);
        boolean noStripAxis = !stripped.defaultBlockState().hasProperty(RotatedPillarBlock.AXIS);

        if (noLogAxis || noStripAxis) {
            String output;
            if (noLogAxis && noStripAxis) output = "both";
            else output = (noLogAxis) ? log.toString() : stripped.toString();

            BountifulFares.LOGGER.error("Could not register axe stripping behavior for {} and {} due to a missing axis property in {}!", log, stripped, output);
        } else {
            BlockTransformerHelper.registerStripping(log, stripped);
        }
    }

    /** Registers an axe-stripping pair. Uses suppliers. */
    public static void addStrippable(Supplier<Block> log, Supplier<Block> stripped) {
        addStrippable(log.get(), stripped.get());
    }

    /////////////////////////////////////////////////////////////////

    /** Registers a shovel-pathing pair. */
    public static void addPathable(Block block, Block path) {
        BlockTransformerHelper.registerFlattening(block, path);
    }

    /** Registers a shovel-pathing pair. Uses suppliers. */
    public static void addPathable(Supplier<Block> block, Supplier<Block> path) {
        addPathable(block.get(), path.get());
    }

    /////////////////////////////////////////////////////////////////

    /** Registers a hoe-tilling pair. */
    public static void addTillable(Block block, Block tilled) {
        BlockTransformerHelper.registerTilling(block, tilled);
    }

    /** Registers a hoe-tilling pair. Uses suppliers. */
    public static void addTillable(Supplier<Block> block, Supplier<Block> tilled) {
        addTillable(block.get(), tilled.get());
    }
}
