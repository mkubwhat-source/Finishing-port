package com.sidden.flavored.block;

import com.sidden.flavored.registry.FlavoredParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The "melting in the sun" chocolate drip particles shared by the chocolate block, tiles, tile
 * stairs and tile slab (1.21.1 duplicated this code in each block class, and the stairs class was a
 * full copy of vanilla's StairBlock just to add it - the stairs now extend StairBlock).
 * <p>
 * 26.3: {@code Level.getDayTime()} is gone; the overworld time of day is now the default world
 * clock ({@code Level.getDefaultClockTime()}), same 24000-tick day.
 */
final class ChocolateDrips {
    private ChocolateDrips() {
    }

    static void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        long timeOfDay = level.getDefaultClockTime() % 24000;
        boolean isNightTime = timeOfDay >= 13000 && timeOfDay <= 23000;
        if (isNightTime) return;

        if (level.canSeeSky(above) && !level.isRainingAt(above)) {
            for (int i = 0; i < random.nextInt(1) + 1; i++) {
                if (random.nextInt(35) == 0) spawnParticle(level, pos, state.getCollisionShape(level, pos), pos.getY() - 0.1);
            }
        }
    }

    private static void spawnParticle(Level level, BlockPos pos, VoxelShape shape, double y) {
        double x1 = pos.getX() + shape.min(Direction.Axis.X);
        double x2 = pos.getX() + shape.max(Direction.Axis.X);
        double z1 = pos.getZ() + shape.min(Direction.Axis.Z);
        double z2 = pos.getZ() + shape.max(Direction.Axis.Z);
        level.addParticle(FlavoredParticles.FALLING_CHOCOLATE.get(),
                Mth.lerp(level.getRandom().nextDouble(), x1, x2), y, Mth.lerp(level.getRandom().nextDouble(), z1, z2),
                0.0, 0.0, 0.0);
    }
}
