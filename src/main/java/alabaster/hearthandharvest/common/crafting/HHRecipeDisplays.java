package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.function.Supplier;

/**
 * Recipe displays (26.3's client-side description of a recipe, used by the recipe book and recipe
 * viewers) for Hearth and Harvest's machines: the keg (items + an input fluid -> a fluid and/or an
 * item), the cask (items -> item, over time) and the stomping basin (items -> fluid).
 */
public final class HHRecipeDisplays {
    private HHRecipeDisplays() {
    }

    public record FluidMachineDisplay(List<SlotDisplay> ingredients, FluidStack inputFluid, FluidStack resultFluid,
                                      SlotDisplay result, SlotDisplay craftingStation, int duration) implements RecipeDisplay {
        public static final MapCodec<FluidMachineDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(FluidMachineDisplay::ingredients),
                FluidStack.OPTIONAL_CODEC.fieldOf("input_fluid").forGetter(FluidMachineDisplay::inputFluid),
                FluidStack.OPTIONAL_CODEC.fieldOf("result_fluid").forGetter(FluidMachineDisplay::resultFluid),
                SlotDisplay.CODEC.fieldOf("result").forGetter(FluidMachineDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FluidMachineDisplay::craftingStation),
                com.mojang.serialization.Codec.INT.fieldOf("duration").forGetter(FluidMachineDisplay::duration)
        ).apply(i, FluidMachineDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, FluidMachineDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), FluidMachineDisplay::ingredients,
                FluidStack.OPTIONAL_STREAM_CODEC, FluidMachineDisplay::inputFluid,
                FluidStack.OPTIONAL_STREAM_CODEC, FluidMachineDisplay::resultFluid,
                SlotDisplay.STREAM_CODEC, FluidMachineDisplay::result,
                SlotDisplay.STREAM_CODEC, FluidMachineDisplay::craftingStation,
                ByteBufCodecs.VAR_INT, FluidMachineDisplay::duration,
                FluidMachineDisplay::new);
        public static final Type<FluidMachineDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public Type<? extends RecipeDisplay> type() {
            return TYPE;
        }
    }

    public static final Supplier<RecipeDisplay.Type<FluidMachineDisplay>> FLUID_MACHINE = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID,
            "fluid_machine", BuiltInRegistries.RECIPE_DISPLAY, () -> FluidMachineDisplay.TYPE);

    /** Farmer's Delight cooking pot recipe display. */
    public static final Supplier<RecipeDisplay.Type<alabaster.hearthandharvest.common.fd.crafting.display.CookingPotRecipeDisplay>> COOKING = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID,
            "cooking", BuiltInRegistries.RECIPE_DISPLAY, () -> alabaster.hearthandharvest.common.fd.crafting.display.CookingPotRecipeDisplay.TYPE);

    public static void init() {
    }
}
