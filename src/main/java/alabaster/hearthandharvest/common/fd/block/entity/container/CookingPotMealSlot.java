package alabaster.hearthandharvest.common.fd.block.entity.container;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import alabaster.hearthandharvest.platform.inventory.SlotItemHandler;
import alabaster.hearthandharvest.platform.inventory.ItemStackHandler;

public class CookingPotMealSlot extends SlotItemHandler
{
	public CookingPotMealSlot(ItemStackHandler inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return false;
	}
}
