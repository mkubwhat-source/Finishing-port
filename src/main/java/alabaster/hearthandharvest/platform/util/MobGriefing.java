package alabaster.hearthandharvest.platform.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

/** Replacement for NeoForge's {@code EventHooks.canEntityGrief}: the vanilla mobGriefing game rule. */
public final class MobGriefing {
    private MobGriefing() {}

    public static boolean canEntityGrief(Level level, Entity entity) {
        return level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.MOB_GRIEFING);
    }
}
