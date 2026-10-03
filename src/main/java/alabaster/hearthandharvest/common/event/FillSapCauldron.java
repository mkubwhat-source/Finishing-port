package alabaster.hearthandharvest.common.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.SapCauldronBlock;
import alabaster.hearthandharvest.common.registry.HHModBlocks;
import alabaster.hearthandharvest.common.registry.HHModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FillSapCauldron {

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            ItemStack heldItem = player.getItemInHand(hand);

        if (level.getBlockState(pos).getBlock() == Blocks.CAULDRON && heldItem.getItem() == HHModItems.SAP_BUCKET.get()) {
            if (!level.isClientSide()) {
                level.setBlock(pos, HHModBlocks.SAP_CAULDRON.get().defaultBlockState().setValue(SapCauldronBlock.SAP_LEVEL, 3), 3);
                heldItem.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (!player.getInventory().add(bucket)) {
                    player.drop(bucket, false, net.minecraft.util.Prediction.SERVER_ONLY);
                }
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }
}
