package alabaster.hearthandharvest.platform.fluid;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

/**
 * NeoForge's {@code FluidHandlerItemStack}: a single-tank item whose fluid is stored in a data
 * component (HH's jug). Same rules: only a stack of one can be filled/drained, and the component is
 * removed when it empties.
 */
public class ItemComponentFluidHandler implements IFluidHandlerItem {
    protected final DataComponentType<FluidStack> componentType;
    protected ItemStack container;
    protected int capacity;

    public ItemComponentFluidHandler(DataComponentType<FluidStack> componentType, ItemStack container, int capacity) {
        this.componentType = componentType;
        this.container = container;
        this.capacity = capacity;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    public FluidStack getFluid() {
        return container.getOrDefault(componentType, FluidStack.EMPTY);
    }

    protected void setFluid(FluidStack fluid) {
        if (fluid.isEmpty()) {
            container.remove(componentType);
        } else {
            container.set(componentType, fluid.copy());
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction doFill) {
        if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(0, resource)) return 0;
        FluidStack contained = getFluid();
        if (contained.isEmpty()) {
            int fillAmount = Math.min(capacity, resource.getAmount());
            if (doFill.execute()) setFluid(resource.copyWithAmount(fillAmount));
            return fillAmount;
        }
        if (FluidStack.isSameFluidSameComponents(contained, resource)) {
            int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());
            if (doFill.execute() && fillAmount > 0) {
                FluidStack grown = contained.copy();
                grown.grow(fillAmount);
                setFluid(grown);
            }
            return fillAmount;
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) return FluidStack.EMPTY;
        FluidStack contained = getFluid();
        if (contained.isEmpty() || !isFluidValid(0, contained)) return FluidStack.EMPTY;
        int drainAmount = Math.min(contained.getAmount(), maxDrain);
        FluidStack drained = contained.copyWithAmount(drainAmount);
        if (action.execute()) {
            FluidStack rest = contained.copy();
            rest.shrink(drainAmount);
            setFluid(rest.getAmount() <= 0 ? FluidStack.EMPTY : rest);
        }
        return drained;
    }
}
