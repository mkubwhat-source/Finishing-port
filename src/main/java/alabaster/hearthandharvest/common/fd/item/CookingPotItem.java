package alabaster.hearthandharvest.common.fd.item;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import alabaster.hearthandharvest.common.fd.block.entity.CookingPotBlockEntity;

import java.util.Optional;

public class CookingPotItem extends BlockItem
{
	private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.4F, 0.4F, 1.0F);

	public CookingPotItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getServingCount(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.min(1 + 12 * getServingCount(stack) / 64, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BAR_COLOR;
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		ItemStack mealStack = CookingPotBlockEntity.getMealFromItem(stack);
		return Optional.of(new CookingPotTooltipComponent(mealStack));
	}

	private static int getServingCount(ItemStack stack) {
		ItemStack mealStack = CookingPotBlockEntity.getMealFromItem(stack);
		return mealStack.getCount();
	}
}
