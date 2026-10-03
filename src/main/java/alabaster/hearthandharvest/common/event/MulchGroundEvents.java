package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.common.tag.HHModTags;
import alabaster.hearthandharvest.platform.event.HHEvents;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

public class MulchGroundEvents {

    /**
     * Trees that convert the ground around them (podzol under mega spruces) leave mulch alone.
     * 1.21.1 wrapped NeoForge's AlterGroundEvent state provider; 26.3's decorator already picks the
     * ground block itself, so this keeps the existing block when it is mulch.
     */
    public static void register() {
        HHEvents.ALTER_GROUND.register((level, random, pos, replacement) -> {
            BlockState existing = level.getBlockState(pos);
            return existing.is(HHModTags.MULCH) ? existing : replacement;
        });
    }
}
