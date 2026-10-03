package alabaster.hearthandharvest.common.fd.block.entity.container;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import alabaster.hearthandharvest.common.fd.block.entity.CookingPotBlockEntity;
import alabaster.hearthandharvest.platform.inventory.SlotItemHandler;
import alabaster.hearthandharvest.platform.inventory.ItemStackHandler;

public class CookingPotResultSlot extends SlotItemHandler
{
	public final CookingPotBlockEntity cookingPot;
	private final Player player;
	private int removeCount;

	public CookingPotResultSlot(Player player, CookingPotBlockEntity blockEntity, ItemStackHandler inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.cookingPot = blockEntity;
		this.player = player;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	@NonNull
	public ItemStack remove(int amount) {
		if (this.hasItem()) {
			this.removeCount += Math.min(amount, this.getItem().getCount());
		}

		return super.remove(amount);
	}

	@Override
	public void onTake(Player thePlayer, ItemStack stack) {
		this.checkTakeAchievements(stack);
		super.onTake(thePlayer, stack);
	}

	@Override
	protected void onQuickCraft(ItemStack stack, int amount) {
		this.removeCount += amount;
		this.checkTakeAchievements(stack);
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(this.player, this.removeCount);

		if (!this.player.level().isClientSide()) {
            cookingPot.awardUsedRecipes(this.player, cookingPot.getDroppableInventory());
		}

		this.removeCount = 0;
	}
}
