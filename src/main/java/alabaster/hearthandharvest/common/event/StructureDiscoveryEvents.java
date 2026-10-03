package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureDiscoveryEvents {
    private static final int CHECK_INTERVAL = 40;
    private static final ResourceKey<Structure> CORN_MAZE = ResourceKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "corn_maze"));
    private static final Identifier CORN_MAZE_ADVANCEMENT = Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "farming/a_maize_ing");

    public static void register() {
        HHEvents.PLAYER_TICK_POST.register(StructureDiscoveryEvents::onPlayerTick);
    }

    private static void onPlayerTick(net.minecraft.world.entity.player.Player entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (player.tickCount % CHECK_INTERVAL != 0 || player.isSpectator()) return;
        if (hasAdvancement(player, CORN_MAZE_ADVANCEMENT)) return;

        ServerLevel level = player.level();
        if (level.structureManager().getStructureWithPieceAt(player.blockPosition(), structure -> structure.is(CORN_MAZE)).isValid()) {
            HHModTriggers.FOUND_CORN_MAZE.trigger(player);
        }
    }

    private static boolean hasAdvancement(ServerPlayer player, Identifier id) {
        AdvancementHolder holder = player.level().getServer().getAdvancements().get(id);
        return holder == null || player.getAdvancements().getOrStartProgress(holder).isDone();
    }
}