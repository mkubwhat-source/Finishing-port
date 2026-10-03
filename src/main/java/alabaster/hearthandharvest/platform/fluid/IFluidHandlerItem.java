package alabaster.hearthandharvest.platform.fluid;

import net.minecraft.world.item.ItemStack;

/** NeoForge's {@code IFluidHandlerItem}: a fluid handler for one item, which may change the item. */
public interface IFluidHandlerItem extends IFluidHandler {
    ItemStack getContainer();
}
