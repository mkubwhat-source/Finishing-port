package alabaster.hearthandharvest.common.worldgen.processor;

import alabaster.hearthandharvest.common.registry.HHModStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import org.jspecify.annotations.Nullable;

public class PreserveFromAirProcessor implements StructureProcessor {
    public static final MapCodec<PreserveFromAirProcessor> CODEC = TagKey.hashedCodec(Registries.BLOCK)
            .xmap(PreserveFromAirProcessor::new, processor -> processor.preserved)
            .fieldOf("preserved");

    private final TagKey<Block> preserved;

    public PreserveFromAirProcessor(TagKey<Block> preserved) {
        this.preserved = preserved;
    }

    @Override
    public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        if (!relativeBlockInfo.state().isAir()) return relativeBlockInfo;

        BoundingBox box = settings.getBoundingBox();
        if (box != null && !box.isInside(relativeBlockInfo.pos())) return relativeBlockInfo;

        return level.getBlockState(relativeBlockInfo.pos()).is(preserved) ? null : relativeBlockInfo;
    }

    @Override
    public MapCodec<PreserveFromAirProcessor> codec() {
        return CODEC;
    }
}