package alabaster.hearthandharvest.common.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.IHarvestable;
import alabaster.hearthandharvest.common.registry.HHModEnchantments;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FarmersHatEvents {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            onBlockBreak(world, player, pos, state);
            return true;
        });
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            onRightClickBlock(player, level, hand, hit);
            return InteractionResult.PASS;
        });
    }

    private static void onBlockBreak(Level world, Player player, BlockPos pos, BlockState state) {
        if (player == null) return;
        if (!(world instanceof ServerLevel level)) return;
        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(HHModItems.FARMERS_HAT.get())) return;

        if ((state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state))
                || (state.getBlock() instanceof IHarvestable h && h.isHarvestReady(state))) {
            dropXp(level, pos);
            damageHat(player);
        }
    }

    private static void onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return;
        if (level.isClientSide()) return;
        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(HHModItems.FARMERS_HAT.get())) return;

        BlockState state = level.getBlockState(hit.getBlockPos());
        boolean harvestable = (state.getBlock() instanceof IHarvestable h && h.isHarvestReady(state))
                || (state.is(HHModTags.RIGHT_CLICK_HARVESTABLE)
                && state.getBlock() instanceof CropBlock crop
                && crop.isMaxAge(state));
        if (!harvestable) return;

        var tool = player.getItemInHand(hand);
        if (tool.is(net.minecraft.tags.ItemTags.HOES)) {
            int lvl = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .get(HHModEnchantments.HARVESTING)
                    .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, tool))
                    .orElse(0);
            if (lvl > 0) return;
        }

        dropXp((ServerLevel) level, hit.getBlockPos());
        damageHat(player);
    }

    public static void dropXp(ServerLevel level, BlockPos pos) {
        ExperienceOrb.award(level, Vec3.atCenterOf(pos), 1 + level.getRandom().nextInt(2));
    }

    public static void damageHat(Player player) {
        ItemStack hat = player.getItemBySlot(EquipmentSlot.HEAD);
        if (hat.is(HHModItems.FARMERS_HAT.get())) {
            hat.hurtAndBreak(1, player, EquipmentSlot.HEAD);
            if (hat.isEmpty()) HHSimpleTrigger.trigger(HHModTriggers.FARMERS_HAT_WORN_OUT, player);
        }
    }
}