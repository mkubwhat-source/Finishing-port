package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.common.block.entity.StompingBasinBlockEntity;
import alabaster.hearthandharvest.common.item.JugBlockItem;
import alabaster.hearthandharvest.common.registry.HHDataMaps;
import alabaster.hearthandharvest.common.registry.HHModBlockEntities;
import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import alabaster.hearthandharvest.platform.fluid.FluidUtil;
import alabaster.hearthandharvest.platform.fluid.IFluidHandler;
import alabaster.hearthandharvest.platform.fluid.IFluidHandlerItem;
import alabaster.hearthandharvest.platform.fluid.ItemComponentFluidHandler;
import alabaster.hearthandharvest.common.registry.HHModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

/**
 * Hearth and Harvest's item and fluid handlers. 1.21.1 registered them as NeoForge capabilities;
 * here item inventories go to Fabric's {@code ItemStorage.SIDED} and fluid tanks to
 * {@link FluidUtil} (which also exposes them through Fabric's {@code FluidStorage}).
 */
public class CapabilityRegistration {
    public static void register() {
        // Nest
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> be.getInventory(), HHModBlockEntities.NEST.get());

        // Stomping basin (a 2x2 multiblock shares its controller's inventory and tank)
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> {
            StompingBasinBlockEntity controller = be.getControllerBE();
            return controller != null ? controller.getItemHandler() : be.getItemHandler();
        }, HHModBlockEntities.STOMPING_BASIN.get());
        FluidUtil.registerBlockEntity(HHModBlockEntities.STOMPING_BASIN.get(), (be, side) -> be.getFluidHandlerForCapability());

        FluidUtil.registerBlockEntity(HHModBlockEntities.TROUGH.get(), (be, side) -> be.getFluidTank());
        FluidUtil.registerBlockEntity(HHModBlockEntities.TREE_TAPPER.get(), (be, side) -> be.tank);
        FluidUtil.registerBlockEntity(HHModBlockEntities.BASIN.get(), (be, side) -> be.tank);
        FluidUtil.registerBlockEntity(HHModBlockEntities.SPRINKLER.get(), (be, side) -> be.tank);
        FluidUtil.registerBlockEntity(HHModBlockEntities.JUG.get(), (be, side) -> be.getFluidTank());

        // Jug item: its contents are the jug_fluid component.
        FluidUtil.registerItem(stack -> new ItemComponentFluidHandler(HHModDataComponents.JUG_FLUID.get(), stack, JugBlockItem.JUG_CAPACITY),
                HHModItems.JUG.get());
        // Bottled drinks (any item in the fluid_bottle data map, including other bundled mods' bottles).
        FluidUtil.registerItemProvider(CapabilityRegistration::bottleHandler);
        FluidUtil.registerItem(EmptyBottleFluidHandler::new, Items.GLASS_BOTTLE);

        // Buckets of HH fluids, for Fabric fluid transfer (pipes, other mods' tanks). Empty buckets
        // fill with them through the fluids' getBucket(). 1.21.1 enabled NeoForge's milk fluid and
        // let the vanilla milk bucket hold it; here that is hearthandharvest:milk.
        fullBucket(HHModItems.SAP_BUCKET.get(), HHModFluids.SAP.source().get());
        fullBucket(HHModItems.GOAT_MILK_BUCKET.get(), HHModFluids.GOAT_MILK.source().get());
        fullBucket(Items.MILK_BUCKET, HHModFluids.MILK.source().get());
    }

    private static void fullBucket(Item bucket, Fluid fluid) {
        FluidStorage.ITEM.registerForItems((stack, context) ->
                new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(fluid), FluidConstants.BUCKET), bucket);
    }

    @Nullable
    private static IFluidHandlerItem bottleHandler(ItemStack stack) {
        Fluid fluid = HHDataMaps.getFluidForBottle(stack.getItem());
        if (fluid == null) return null;
        return new BottleFluidHandler(stack, fluid);
    }

    private static class EmptyBottleFluidHandler implements IFluidHandlerItem {
        private ItemStack container;

        EmptyBottleFluidHandler(ItemStack stack) {
            this.container = stack;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return HHDataMaps.BOTTLE_VOLUME;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return HHDataMaps.getBottleForFluid(stack.getFluid()) != null;
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (container.getCount() != 1 || resource.getAmount() < HHDataMaps.BOTTLE_VOLUME) return 0;

            Item bottle = HHDataMaps.getBottleForFluid(resource.getFluid());
            if (bottle == null) return 0;

            if (action.execute()) {
                container = new ItemStack(bottle);
            }
            return HHDataMaps.BOTTLE_VOLUME;
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public ItemStack getContainer() {
            return container;
        }
    }

    private static class BottleFluidHandler implements IFluidHandlerItem {
        private final Item bottleItem;
        private final Fluid containedFluid;
        private ItemStack container;

        BottleFluidHandler(ItemStack stack, Fluid containedFluid) {
            this.bottleItem = stack.getItem();
            this.containedFluid = containedFluid;
            this.container = stack;
        }

        private boolean isFullBottle() {
            return container.getCount() == 1
                    && container.getItem() == bottleItem
                    && !container.has(HHModDataComponents.SERVINGS.get());
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!isFullBottle()) return FluidStack.EMPTY;
            return new FluidStack(containedFluid, HHDataMaps.BOTTLE_VOLUME);
        }

        @Override
        public int getTankCapacity(int tank) {
            return HHDataMaps.BOTTLE_VOLUME;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack f) {
            return f.getFluid().isSame(containedFluid);
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!resource.getFluid().isSame(containedFluid)) return FluidStack.EMPTY;
            return drain(resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (!isFullBottle()) return FluidStack.EMPTY;
            if (maxDrain < HHDataMaps.BOTTLE_VOLUME) return FluidStack.EMPTY;
            if (action.execute()) {
                container = new ItemStack(Items.GLASS_BOTTLE);
            }
            return new FluidStack(containedFluid, HHDataMaps.BOTTLE_VOLUME);
        }

        @Override
        public ItemStack getContainer() {
            return container;
        }
    }
}