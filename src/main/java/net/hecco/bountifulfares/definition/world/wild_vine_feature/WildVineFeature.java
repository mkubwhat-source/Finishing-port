package net.hecco.bountifulfares.definition.world.wild_vine_feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Collection;

// In 26.3 the old generic `Feature<FC extends FeatureConfiguration>` split is gone: a Feature is
// now a self-contained record that carries its own configuration fields directly and implements
// `Feature` (no more `FeaturePlaceContext<FC>` wrapper either - `place` takes the level/generator/
// random/pos directly). This merges what used to be WildVineFeatureConfig straight into this class,
// matching how vanilla's own record-based features (e.g. CoralTreeFeature, TreeFeature) are shaped.
public record WildVineFeature(BlockState block, TagKey<Block> canPlaceOn, int patchSize) implements Feature {

    public static final MapCodec<WildVineFeature> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    BlockState.CODEC
                            .fieldOf("block")
                            .forGetter(WildVineFeature::block),
                    TagKey.hashedCodec(BuiltInRegistries.BLOCK.key())
                            .fieldOf("can_place_on")
                            .forGetter(WildVineFeature::canPlaceOn),
                    com.mojang.serialization.Codec.intRange(1, 16)
                            .fieldOf("patch_size")
                            .forGetter(WildVineFeature::patchSize)
            ).apply(instance, WildVineFeature::new));

    @Override
    public MapCodec<WildVineFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(net.minecraft.world.level.WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos startPos) {
        for (int i = -patchSize; i <= patchSize; i++) {
            for (int j = -patchSize; j <= patchSize; j++) {
                for (int k = -patchSize; k <= patchSize; k++) {
                    if (random.nextFloat() < 0.25f) {
                        placeVine(random, world, startPos.offset(i, j, k));
                    }
                }
            }
        }
        return true;
    }

    private void placeVine(RandomSource random, net.minecraft.world.level.WorldGenLevel world, BlockPos pos) {
        Collection<Direction> dirs = Direction.allShuffled(random);
        dirs.remove(Direction.UP);
        dirs.remove(Direction.DOWN);
        if (world.getLevel().structureManager().getAllStructuresAt(pos).isEmpty() && (world.isEmptyBlock(pos) || world.getBlockState(pos).is(Blocks.VINE))) {
            for (Direction direction : dirs) {
                if (world.getBlockState(pos.relative(direction.getOpposite())).is(canPlaceOn)) {
                    world.setBlock(pos, block.setValue(BlockStateProperties.HORIZONTAL_FACING, direction), 2);
                }
            }
        }
    }
}
