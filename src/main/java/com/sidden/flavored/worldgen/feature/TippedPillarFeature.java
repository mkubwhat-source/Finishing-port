package com.sidden.flavored.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * A pillar of {@code pillar_provider} blocks ({@code height} tall) on grass, topped with one
 * {@code tip_provider} block - Flavored's wild cinnamon stalks.
 * <p>
 * 26.3: {@code Feature} is now an interface whose instances carry their own configuration (a
 * codec-registered record, like vanilla's {@code BlockColumnFeature}), so 1.21.1's
 * {@code Feature<TippedPillarConfiguration>} + configuration class became this one record. The JSON
 * fields are unchanged; they simply sit directly in {@code worldgen/feature/*.json} now.
 */
public record TippedPillarFeature(Holder<BlockStateProvider> pillarProvider, Holder<BlockStateProvider> tipProvider, IntProvider height) implements Feature {
    public static final MapCodec<TippedPillarFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("pillar_provider").forGetter(TippedPillarFeature::pillarProvider),
            BlockStateProvider.CODEC.fieldOf("tip_provider").forGetter(TippedPillarFeature::tipProvider),
            net.minecraft.util.valueproviders.IntProviders.codec(1, 16).fieldOf("height").forGetter(TippedPillarFeature::height)
    ).apply(instance, TippedPillarFeature::new));

    @Override
    public MapCodec<TippedPillarFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        BlockPos pos = origin;
        if (!level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) return false;

        int h = this.height.sample(random);
        for (int i = 0; i < h; i++) {
            level.setBlock(pos, this.pillarProvider.value().getState(level, random, pos), 2);
            pos = pos.above();
        }
        level.setBlock(pos, this.tipProvider.value().getState(level, random, pos), 2);
        return true;
    }
}
