package alabaster.hearthandharvest.common.worldgen;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.block.NestBlock;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.chunk.ChunkGenerator;

/** 26.3: a configuration-less feature record (registered by codec in HHModFeatures). */
public record NestFeature() implements Feature {
    public static final MapCodec<NestFeature> CODEC = MapCodec.unit(NestFeature::new);

    @Override
    public MapCodec<NestFeature> codec() {
        return CODEC;
    }


    @Override
    public boolean place(WorldGenLevel ctxLevel, ChunkGenerator ctxGenerator, RandomSource ctxRandom, BlockPos ctxOrigin) {
        if (!Config.GENERATE_NESTS.get()) return false;

        WorldGenLevel level = ctxLevel;
        BlockPos pos = ctxOrigin;
        BlockState existing = level.getBlockState(pos);
        if (!existing.canBeReplaced() || !existing.getFluidState().isEmpty()) return false;

        BlockState nest = HHModBlocks.NEST.get().defaultBlockState().setValue(NestBlock.GENERATED, true);
        if (!nest.canSurvive(level, pos)) return false;

        level.setBlock(pos, nest, 2);
        return true;
    }
}