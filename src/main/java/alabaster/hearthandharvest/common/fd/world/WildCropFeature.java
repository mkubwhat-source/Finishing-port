package alabaster.hearthandharvest.common.fd.world;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;

/**
 * Farmer's Delight's 1.21.1 {@code wild_crop} feature (FarmersDelightRefabricated 1.21.1, MIT,
 * vectorwing), which Hearth and Harvest's wild grape/peanut/cotton and berry bush patches use, now
 * {@code hearthandharvest:wild_crop}. Same fields and placement: {@code tries} attempts of the
 * optional floor feature and the secondary feature within {@code xz_spread}/{@code y_spread}, and of
 * the primary (crop) feature within a spread 2 blocks tighter.
 * <p>
 * 26.3: features are codec-registered records holding their configuration. FarmersDelightRefabricated
 * 26.3 replaced this feature with a different one, so it is ported from the 1.21.1 source.
 */
public record WildCropFeature(int tries, int xzSpread, int ySpread, Holder<PlacedFeature> primaryFeature,
                              Holder<PlacedFeature> secondaryFeature, Optional<Holder<PlacedFeature>> floorFeature) implements Feature {
    public static final MapCodec<WildCropFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(64).forGetter(WildCropFeature::tries),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(4).forGetter(WildCropFeature::xzSpread),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(WildCropFeature::ySpread),
            PlacedFeature.CODEC.fieldOf("primary_feature").forGetter(WildCropFeature::primaryFeature),
            PlacedFeature.CODEC.fieldOf("secondary_feature").forGetter(WildCropFeature::secondaryFeature),
            PlacedFeature.CODEC.optionalFieldOf("floor_feature").forGetter(WildCropFeature::floorFeature)
    ).apply(instance, WildCropFeature::new));

    @Override
    public MapCodec<WildCropFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int placed = 0;
        int xz = xzSpread + 1;
        int y = ySpread + 1;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        if (floorFeature.isPresent()) {
            for (int j = 0; j < tries; ++j) {
                pos.setWithOffset(origin, random.nextInt(xz) - random.nextInt(xz), random.nextInt(y) - random.nextInt(y), random.nextInt(xz) - random.nextInt(xz));
                if (floorFeature.get().value().place(level, generator, random, pos)) ++placed;
            }
        }
        for (int k = 0; k < tries; ++k) {
            int shorterXZ = xz - 2;
            pos.setWithOffset(origin, random.nextInt(shorterXZ) - random.nextInt(shorterXZ), random.nextInt(y) - random.nextInt(y), random.nextInt(shorterXZ) - random.nextInt(shorterXZ));
            if (primaryFeature.value().place(level, generator, random, pos)) ++placed;
        }
        for (int l = 0; l < tries; ++l) {
            pos.setWithOffset(origin, random.nextInt(xz) - random.nextInt(xz), random.nextInt(y) - random.nextInt(y), random.nextInt(xz) - random.nextInt(xz));
            if (secondaryFeature.value().place(level, generator, random, pos)) ++placed;
        }
        return placed > 0;
    }
}
