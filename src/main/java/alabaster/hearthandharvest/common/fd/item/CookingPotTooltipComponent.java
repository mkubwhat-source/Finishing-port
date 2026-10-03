package alabaster.hearthandharvest.common.fd.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/** Tooltip data for a cooking pot item's stored meal (rendered by the client's {@code CookingPotTooltip}). */
public record CookingPotTooltipComponent(ItemStack mealStack) implements TooltipComponent {
}
