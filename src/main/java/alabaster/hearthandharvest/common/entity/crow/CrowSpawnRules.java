package alabaster.hearthandharvest.common.entity.crow;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.block.NestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CrowSpawnRules {

    public static boolean canSpawnCrow(EntityType<? extends PathfinderMob> type, ServerLevelAccessor level, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {

        if (EntitySpawnReason.isSpawner(spawnType)) return true;

        if (!PathfinderMob.checkMobSpawnRules(type, level, spawnType, pos, random))
            return false;

        if (!level.getBlockState(pos.below()).isSolid())
            return false;

        int radius = Config.CROW_SPAWN_RADIUS.get();
        int cropRequirement = Config.CROW_SPAWN_NUMBER_OF_CROPS.get();

        // 26.3: spawns during chunk generation may only read the chunk being generated (1.21.1 let
        // them read neighbours), so the search area is clamped to that chunk there.
        boolean worldgen = spawnType == EntitySpawnReason.CHUNK_GENERATION;
        if (Config.CROW_SPAWN_NEAR_NESTS.get() && hasNearbyGeneratedNest(level, pos, radius, worldgen)) return true;
        if (cropRequirement == 0) return false;

        return countNearbyCrops(level, pos, radius, worldgen) >= cropRequirement;
    }

    private static boolean outsideChunk(BlockPos origin, int x, int z, boolean worldgen) {
        return worldgen && ((x >> 4) != (origin.getX() >> 4) || (z >> 4) != (origin.getZ() >> 4));
    }

    private static boolean hasNearbyGeneratedNest(ServerLevelAccessor level, BlockPos pos, int radius, boolean worldgen) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (outsideChunk(pos, pos.getX() + dx, pos.getZ() + dz, worldgen)) continue;
                for (int dy = -4; dy <= 8; dy++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState state = level.getBlockState(cursor);

                    if (state.getBlock() instanceof NestBlock &&
                            state.getValue(NestBlock.GENERATED)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static int countNearbyCrops(ServerLevelAccessor level, BlockPos pos, int radius, boolean worldgen) {
        int count = 0;
        int needed = Config.CROW_SPAWN_NUMBER_OF_CROPS.get();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (outsideChunk(pos, pos.getX() + dx, pos.getZ() + dz, worldgen)) continue;
                for (int dy = -2; dy <= 2; dy++) {
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);

                    if (level.getBlockState(cursor).is(BlockTags.CROPS)) {
                        count++;
                        if (count >= needed) return count;
                    }
                }
            }
        }
        return count;
    }
}