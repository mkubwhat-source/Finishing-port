package alabaster.hearthandharvest.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

/**
 * A bucket of one of Hearth and Harvest's fluids (sap). These fluids have no world block, so, as in
 * 1.21.1 (NeoForge refused to place block-less fluids), the bucket can't be emptied into the world;
 * it fills/empties HH's tanks (and any Fabric fluid storage: the fluid's {@code getBucket()} is this
 * item) and the sap cauldron.
 */
public class HHBucketItem extends Item {
    private final Supplier<? extends Fluid> fluid;

    public HHBucketItem(Supplier<? extends Fluid> fluid, Properties properties) {
        super(properties);
        this.fluid = fluid;
    }

    public Fluid getFluid() {
        return fluid.get();
    }
}
