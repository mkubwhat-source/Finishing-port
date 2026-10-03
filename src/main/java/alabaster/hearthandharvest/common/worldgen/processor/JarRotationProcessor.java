package alabaster.hearthandharvest.common.worldgen.processor;

import alabaster.hearthandharvest.common.block.JarBlock;
import alabaster.hearthandharvest.common.block.entity.JarBlockEntity;
import alabaster.hearthandharvest.common.registry.HHModStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.jspecify.annotations.Nullable;

public class JarRotationProcessor implements StructureProcessor {
    public static final JarRotationProcessor INSTANCE = new JarRotationProcessor();
    public static final MapCodec<JarRotationProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    private JarRotationProcessor() {
    }

    @Override
    public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        if (relativeBlockInfo.nbt() == null || !(relativeBlockInfo.state().getBlock() instanceof JarBlock)) return relativeBlockInfo;
        if (settings.getMirror() == Mirror.NONE && settings.getRotation() == Rotation.NONE) return relativeBlockInfo;
        return new StructureTemplate.StructureBlockInfo(
                relativeBlockInfo.pos(),
                relativeBlockInfo.state(),
                JarBlockEntity.transformTag(relativeBlockInfo.nbt(), settings.getMirror(), settings.getRotation())
        );
    }

    @Override
    public MapCodec<JarRotationProcessor> codec() {
        return CODEC;
    }
}