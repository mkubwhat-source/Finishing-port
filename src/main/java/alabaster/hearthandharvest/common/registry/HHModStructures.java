package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.worldgen.processor.JarRotationProcessor;
import alabaster.hearthandharvest.common.worldgen.processor.PreserveFromAirProcessor;
import alabaster.hearthandharvest.common.worldgen.structure.LilliputLaneStructure;
import alabaster.hearthandharvest.common.worldgen.structure.corn_maze.CornMazeStructure;
import com.mojang.serialization.MapCodec;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.function.Supplier;

public class HHModStructures {
    public static final Supplier<StructureType<CornMazeStructure>> CORN_MAZE =
            BFRegistryHelper.registerStructureType(HearthAndHarvest.MODID, "corn_maze", () -> CornMazeStructure.CODEC);
    public static final Supplier<StructureType<LilliputLaneStructure>> LILLIPUT_LANE =
            BFRegistryHelper.registerStructureType(HearthAndHarvest.MODID, "lilliput_lane", () -> LilliputLaneStructure.CODEC);
    // 26.3: processor types are their codecs.
    public static final Supplier<MapCodec<JarRotationProcessor>> JAR_ROTATION =
            BFRegistryHelper.registerStructureProcessor(HearthAndHarvest.MODID, "jar_rotation", () -> JarRotationProcessor.CODEC);
    public static final Supplier<MapCodec<PreserveFromAirProcessor>> PRESERVE_FROM_AIR =
            BFRegistryHelper.registerStructureProcessor(HearthAndHarvest.MODID, "preserve_from_air", () -> PreserveFromAirProcessor.CODEC);

    public static void init() {
    }
}
