package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.item.component.ItemStackWrapper;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import net.hecco.bountifulfares.platform.BFRegistryHelper;

import alabaster.hearthandharvest.common.item.component.SeedPouchContents;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class HHModDataComponents {

    public static final Supplier<DataComponentType<Integer>> COOK_TIME =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "cook_time", builder ->
                    builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
            );

    public static final Supplier<DataComponentType<Integer>> WATER_LEVEL =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "water_level", builder ->
                    builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
            );

    public static final Supplier<DataComponentType<Integer>> BONEMEAL_LEVEL =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "bonemeal_level", builder ->
                    builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
            );

    public static final Supplier<DataComponentType<Integer>> SERVINGS =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "servings", builder ->
                    builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final Supplier<DataComponentType<Integer>> VINTAGE =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "vintage", builder ->
                    builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    public static final Supplier<DataComponentType<Boolean>> SALTED =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "salted", builder ->
                    builder.persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
            );

    public static final Supplier<DataComponentType<Item>> FERTILIZER_ITEM =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "fertilizer_item", builder ->
                    builder.persistent(BuiltInRegistries.ITEM.byNameCodec())
                            .networkSynchronized(ByteBufCodecs.registry(Registries.ITEM))
            );

    public static final Supplier<DataComponentType<FluidStack>> JUG_FLUID =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "jug_fluid", builder ->
                    builder.persistent(FluidStack.OPTIONAL_CODEC)
                            .networkSynchronized(FluidStack.OPTIONAL_STREAM_CODEC)
                            .cacheEncoding()
            );

    public static final Supplier<DataComponentType<SeedPouchContents>> SEED_POUCH_CONTENTS =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "seed_pouch_contents", builder ->
                    builder.persistent(SeedPouchContents.CODEC)
                            .networkSynchronized(SeedPouchContents.STREAM_CODEC)
            );

    // Farmer's Delight cooking pot item: the meal inside and its serving container.
    public static final Supplier<DataComponentType<ItemStackWrapper>> MEAL =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "meal", builder ->
                    builder.persistent(ItemStackWrapper.CODEC).networkSynchronized(ItemStackWrapper.STREAM_CODEC).cacheEncoding());
    public static final Supplier<DataComponentType<ItemStackWrapper>> CONTAINER =
            BFRegistryHelper.registerComponentType(HearthAndHarvest.MODID, "container", builder ->
                    builder.persistent(ItemStackWrapper.CODEC).networkSynchronized(ItemStackWrapper.STREAM_CODEC).cacheEncoding());

    public static void init() {
    }
}
