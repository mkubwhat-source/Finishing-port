package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.common.registry.HHModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PitchforkEventHandler {

    /**
     * Breaking grass or crops with the pitchfork drops 2-4 straw instead of the usual drops.
     * 1.21.1 replaced them in NeoForge's BlockDropsEvent; here the block's loot is edited as it is rolled.
     */
    public static void register() {
        LootTableEvents.MODIFY_DROPS.register((table, context, drops) -> {
            BlockState state = context.getOptional(LootContextParams.BLOCK_STATE);
            net.minecraft.world.item.ItemInstance tool = context.getOptional(LootContextParams.TOOL);
            if (state == null || tool == null || !tool.is(HHModItems.PITCHFORK.get())) return;
            if (!isGrassOrCrop(state)) return;
            drops.clear();
            int count = 2 + context.getRandom().nextInt(3);
            drops.add(new ItemStack(HHModItems.STRAW.get(), count));
        });
    }

    private static boolean isGrassOrCrop(BlockState state) {
        return state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.TALL_GRASS)
                || state.is(BlockTags.CROPS)
                || state.is(Blocks.WHEAT);
    }
}