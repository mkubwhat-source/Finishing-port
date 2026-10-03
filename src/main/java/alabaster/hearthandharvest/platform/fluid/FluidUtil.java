package alabaster.hearthandharvest.platform.fluid;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * NeoForge's {@code FluidUtil} (1.21.1) and its fluid capabilities, for Hearth and Harvest on
 * Fabric. The transfer helpers keep NeoForge's exact rules.
 * <ul>
 *   <li>Blocks: {@link #BLOCK} holds HH's own tanks (registered as they were with
 *   {@code Capabilities.FluidHandler.BLOCK}); other mods' blocks are reached through Fabric's
 *   {@code FluidStorage.SIDED}, and HH tanks are registered there too ({@link #registerBlockEntity}).</li>
 *   <li>Items: HH's own handlers (bottled drinks, glass bottle, jug) come first; anything else
 *   (buckets, other mods' containers) goes through Fabric's {@code FluidStorage.ITEM}.</li>
 * </ul>
 */
public final class FluidUtil {
    public static final BlockApiLookup<IFluidHandler, @Nullable Direction> BLOCK = BlockApiLookup.get(
            Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "fluid_handler"), IFluidHandler.class, Direction.class);

    private static final Map<Item, Function<ItemStack, @Nullable IFluidHandlerItem>> ITEM_HANDLERS = new IdentityHashMap<>();
    private static final List<Function<ItemStack, @Nullable IFluidHandlerItem>> ITEM_PROVIDERS = new ArrayList<>();

    private FluidUtil() {
    }

    /** Registers a block entity's tank(s) for HH's lookups and, for {@link FluidTank}s, Fabric's transfer API. */
    public static <T extends BlockEntity> void registerBlockEntity(BlockEntityType<T> type, BiFunction<T, @Nullable Direction, @Nullable IFluidHandler> provider) {
        BLOCK.registerForBlockEntity(provider, type);
        FluidStorage.SIDED.registerForBlockEntity((be, side) -> {
            IFluidHandler handler = provider.apply(be, side);
            return handler instanceof FluidTank tank ? new FluidTankStorage(tank) : null;
        }, type);
    }

    public static void registerItem(Function<ItemStack, @Nullable IFluidHandlerItem> handler, Item... items) {
        for (Item item : items) ITEM_HANDLERS.put(item, handler);
    }

    /** A handler factory tried for every item (returns null for items it doesn't handle). */
    public static void registerItemProvider(Function<ItemStack, @Nullable IFluidHandlerItem> provider) {
        ITEM_PROVIDERS.add(provider);
    }

    public static Optional<IFluidHandlerItem> getFluidHandler(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        Function<ItemStack, IFluidHandlerItem> direct = ITEM_HANDLERS.get(stack.getItem());
        if (direct != null) {
            IFluidHandlerItem handler = direct.apply(stack);
            if (handler != null) return Optional.of(handler);
        }
        for (Function<ItemStack, IFluidHandlerItem> provider : ITEM_PROVIDERS) {
            IFluidHandlerItem handler = provider.apply(stack);
            if (handler != null) return Optional.of(handler);
        }
        return Optional.ofNullable(FabricItemFluidHandler.of(stack));
    }

    public static Optional<IFluidHandler> getFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
        IFluidHandler handler = BLOCK.find(level, pos, side);
        if (handler != null) return Optional.of(handler);
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
        return storage == null ? Optional.empty() : Optional.of(new FabricFluidStorageHandler(storage));
    }

    public static Optional<FluidStack> getFluidContained(ItemStack container) {
        if (container.isEmpty()) return Optional.empty();
        return getFluidHandler(container.copyWithCount(1))
                .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE));
    }

    public static FluidStack tryFluidTransfer(IFluidHandler destination, IFluidHandler source, int maxAmount, boolean doTransfer) {
        FluidStack drainable = source.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty()) return tryFluidTransferInternal(destination, source, drainable, doTransfer);
        return FluidStack.EMPTY;
    }

    public static FluidStack tryFluidTransfer(IFluidHandler destination, IFluidHandler source, FluidStack resource, boolean doTransfer) {
        FluidStack drainable = source.drain(resource, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty() && FluidStack.isSameFluidSameComponents(resource, drainable)) {
            return tryFluidTransferInternal(destination, source, drainable, doTransfer);
        }
        return FluidStack.EMPTY;
    }

    private static FluidStack tryFluidTransferInternal(IFluidHandler destination, IFluidHandler source, FluidStack drainable, boolean doTransfer) {
        int fillableAmount = destination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount > 0) {
            drainable = drainable.copyWithAmount(fillableAmount);
            if (!doTransfer) return drainable;
            FluidStack drained = source.drain(drainable, IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                drained.setAmount(destination.fill(drained, IFluidHandler.FluidAction.EXECUTE));
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    public static FluidActionResult tryFillContainer(ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable Player player, boolean doFill) {
        ItemStack containerCopy = container.copyWithCount(1);
        return getFluidHandler(containerCopy).map(containerHandler -> {
            FluidStack simulated = tryFluidTransfer(containerHandler, fluidSource, maxAmount, false);
            if (simulated.isEmpty()) return FluidActionResult.FAILURE;
            if (doFill) {
                tryFluidTransfer(containerHandler, fluidSource, maxAmount, true);
                if (player != null) {
                    player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), getFillSound(simulated), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                containerHandler.fill(simulated, IFluidHandler.FluidAction.EXECUTE);
            }
            return new FluidActionResult(containerHandler.getContainer());
        }).orElse(FluidActionResult.FAILURE);
    }

    public static FluidActionResult tryEmptyContainer(ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable Player player, boolean doDrain) {
        ItemStack containerCopy = container.copyWithCount(1);
        return getFluidHandler(containerCopy).map(containerHandler -> {
            if (doDrain) {
                FluidStack transfer = tryFluidTransfer(fluidDestination, containerHandler, maxAmount, true);
                if (transfer.isEmpty()) return FluidActionResult.FAILURE;
                if (player != null) {
                    player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), getEmptySound(transfer), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return new FluidActionResult(containerHandler.getContainer());
            }
            FluidStack simulated = tryFluidTransfer(fluidDestination, containerHandler, maxAmount, false);
            if (simulated.isEmpty()) return FluidActionResult.FAILURE;
            containerHandler.drain(simulated, IFluidHandler.FluidAction.EXECUTE);
            return new FluidActionResult(containerHandler.getContainer());
        }).orElse(FluidActionResult.FAILURE);
    }

    /** NeoForge's {@code interactWithFluidHandler(player, hand, level, pos, side)}. */
    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side) {
        return getFluidHandler(level, pos, side).map(handler -> interactWithFluidHandler(player, hand, handler)).orElse(false);
    }

    /** Fills the held container from the handler, or else empties it into the handler. */
    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, IFluidHandler handler) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return false;
        FluidActionResult result = andStow(held, player, tryFillContainer(held, handler, Integer.MAX_VALUE, player, true));
        if (!result.isSuccess()) {
            result = andStow(held, player, tryEmptyContainer(held, handler, Integer.MAX_VALUE, player, true));
        }
        if (result.isSuccess()) {
            player.setItemInHand(hand, result.getResult());
            return true;
        }
        return false;
    }

    /** NeoForge's ...AndStow: creative keeps the container; one item is swapped; else stow the result. */
    private static FluidActionResult andStow(ItemStack container, Player player, FluidActionResult result) {
        if (!result.isSuccess()) return result;
        if (player.getAbilities().instabuild) return new FluidActionResult(container);
        if (container.getCount() == 1) return result;
        ItemStack out = result.getResult();
        if (!out.isEmpty() && !player.getInventory().add(out)) player.drop(out, false, net.minecraft.util.Prediction.SERVER_ONLY);
        ItemStack rest = container.copy();
        rest.shrink(1);
        return new FluidActionResult(rest);
    }

    public static SoundEvent getFillSound(FluidStack fluid) {
        SoundEvent sound = FluidVariantAttributes.getFillSound(FluidVariant.of(fluid.getFluid()));
        return sound != null ? sound : SoundEvents.BUCKET_FILL;
    }

    public static SoundEvent getEmptySound(FluidStack fluid) {
        SoundEvent sound = FluidVariantAttributes.getEmptySound(FluidVariant.of(fluid.getFluid()));
        return sound != null ? sound : SoundEvents.BUCKET_EMPTY;
    }

    public static Component getName(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return Component.empty();
        return FluidVariantAttributes.getName(FluidVariant.of(fluid));
    }
}
