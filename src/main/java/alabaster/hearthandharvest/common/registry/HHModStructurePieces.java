package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.worldgen.structure.corn_maze.CornMazeStructurePiece;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.function.Supplier;

public class HHModStructurePieces {
    public static final Supplier<StructurePieceType> CORN_MAZE_PIECE =
            BFRegistryHelper.registerStructurePiece(HearthAndHarvest.MODID, "corn_maze_piece", () -> (StructurePieceType) CornMazeStructurePiece::new);

    public static void init() {
    }
}
