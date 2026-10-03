package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fluid.HHFluidType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Hearth and Harvest's fluids. They exist only inside its keg, jug, stomping basin, tree tapper and
 * sink (no world blocks, as in 1.21.1). Each source/flowing pair gets a Fabric attribute handler for
 * its name ({@code fluid_type.hearthandharvest.<name>}, the 1.21.1 NeoForge FluidType key).
 * <p>
 * {@code milk}: 1.21.1 used NeoForge's {@code minecraft:milk} fluid (enabled by HH). Fabric has no
 * milk fluid, so it is {@code hearthandharvest:milk} here; the vanilla milk bucket fills/empties it.
 */
public class HHModFluids {
    public record FluidEntry(String name, Supplier<HHFluidType> source, Supplier<HHFluidType> flowing) {
    }

    public static final List<FluidEntry> ALL = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private static FluidEntry register(String name, Supplier<? extends Item> bucket) {
        HHFluidType[] fluids = new HHFluidType[2];
        Supplier<HHFluidType> source = () -> fluids[0];
        Supplier<HHFluidType> flowing = () -> fluids[1];
        fluids[0] = Registry.register(BuiltInRegistries.FLUID, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, name),
                new HHFluidType(true, source, flowing, bucket));
        fluids[1] = Registry.register(BuiltInRegistries.FLUID, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "flowing_" + name),
                new HHFluidType(false, source, flowing, bucket));
        Component displayName = Component.translatable("fluid_type." + HearthAndHarvest.MODID + "." + name);
        FluidVariantAttributeHandler handler = new FluidVariantAttributeHandler() {
            @Override
            public Component getName(FluidVariant variant) {
                return displayName;
            }
        };
        FluidVariantAttributes.register(fluids[0], handler);
        FluidVariantAttributes.register(fluids[1], handler);
        FluidEntry entry = new FluidEntry(name, source, flowing);
        ALL.add(entry);
        return entry;
    }

    private static FluidEntry register(String name) {
        return register(name, null);
    }

    public static final FluidEntry GOAT_MILK = register("goat_milk", () -> HHModItems.GOAT_MILK_BUCKET.get());
    public static final FluidEntry MILK = register("milk", () -> net.minecraft.world.item.Items.MILK_BUCKET);
    public static final FluidEntry COOKING_OIL = register("cooking_oil");
    public static final FluidEntry SAP = register("sap", () -> HHModItems.SAP_BUCKET.get());
    public static final FluidEntry SYRUP = register("syrup");
    public static final FluidEntry APPLE_CIDER = register("apple_cider");
    public static final FluidEntry HARD_CIDER = register("hard_cider");
    public static final FluidEntry ROOT_BEER = register("root_beer");
    public static final FluidEntry MEAD = register("mead");
    public static final FluidEntry MOONSHINE = register("moonshine");
    public static final FluidEntry BLUEBERRY_JUICE = register("blueberry_juice");
    public static final FluidEntry CHERRY_JUICE = register("cherry_juice");
    public static final FluidEntry GREEN_GRAPE_JUICE = register("green_grape_juice");
    public static final FluidEntry RASPBERRY_JUICE = register("raspberry_juice");
    public static final FluidEntry RED_GRAPE_JUICE = register("red_grape_juice");
    public static final FluidEntry SWEET_BERRY_JUICE = register("sweet_berry_juice");
    public static final FluidEntry MELON_JUICE = register("melon_juice");
    public static final FluidEntry GLOW_BERRY_JUICE = register("glow_berry_juice");
    public static final FluidEntry BLUEBERRY_WINE = register("blueberry_wine");
    public static final FluidEntry CHERRY_WINE = register("cherry_wine");
    public static final FluidEntry GREEN_GRAPE_WINE = register("green_grape_wine");
    public static final FluidEntry RASPBERRY_WINE = register("raspberry_wine");
    public static final FluidEntry RED_GRAPE_WINE = register("red_grape_wine");
    public static final FluidEntry SWEET_BERRY_WINE = register("sweet_berry_wine");
    public static final FluidEntry GLOW_BERRY_WINE = register("glow_berry_wine");
    public static final FluidEntry MELON_WINE = register("melon_wine");

    public static void init() {
    }
}
