package alabaster.hearthandharvest.common.fd.block.entity.dispenser;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import alabaster.hearthandharvest.common.fd.block.CuttingBoardBlock;
import alabaster.hearthandharvest.common.fd.block.entity.CuttingBoardBlockEntity;

import java.util.HashMap;

/**
 * Uses the given item as a tool when facing a Cutting Board.
 */
public class CuttingBoardDispenseBehavior implements DispenseItemBehavior
{
	// Hearth and Harvest: 26.3 made DefaultDispenseItemBehavior#dispense final, so this implements the
	// interface directly with OptionalDispenseItemBehavior's success flag, sound and animation.
	private boolean success;

	private void setSuccess(boolean success) {
		this.success = success;
	}

	private void playSound(BlockSource source) {
		source.level().levelEvent(success ? 1000 : 1001, source.pos(), 0);
	}

	private void playAnimation(BlockSource source, net.minecraft.core.Direction direction) {
		source.level().levelEvent(2000, source.pos(), direction.get3DDataValue());
	}

	private static final HashMap<Item, DispenseItemBehavior> DISPENSE_ITEM_BEHAVIOR_HASH_MAP = new HashMap<>();
	public static final CuttingBoardDispenseBehavior INSTANCE = new CuttingBoardDispenseBehavior();

	public static void registerBehaviour(Item item, CuttingBoardDispenseBehavior behavior) {
		DISPENSE_ITEM_BEHAVIOR_HASH_MAP.put(item, DispenserBlock.DISPENSER_REGISTRY.get(item)); // Save the old behaviours so they can be used later
		DispenserBlock.registerBehavior(item, behavior);
	}

	@Override
	public ItemStack dispense(BlockSource source, ItemStack stack) {
		if (tryDispenseStackOnCuttingBoard(source, stack)) {
			this.playSound(source); // I added this because I completely overrode the super implementation which had the sounds.
			this.playAnimation(source, source.state().getValue(DispenserBlock.FACING)); // see above, same reasoning
			return stack;
		}
		return DISPENSE_ITEM_BEHAVIOR_HASH_MAP.get(stack.getItem()).dispense(source, stack); // Not targeted on cutting board, use vanilla/other mods behaviour
	}

	public boolean tryDispenseStackOnCuttingBoard(BlockSource source, ItemStack stack) {
		setSuccess(false);
		Level level = source.level();
		BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof CuttingBoardBlock && level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
			if (!cuttingBoard.isEmpty() && cuttingBoard.processStoredItemUsingTool(stack, null)) {
				setSuccess(true);
			}
			return true;
		}
		return false;
	}
}
