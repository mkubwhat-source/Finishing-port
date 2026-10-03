package alabaster.hearthandharvest.platform.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;
import java.util.Optional;

/**
 * A fluid and an amount in millibuckets: the part of NeoForge's {@code FluidStack} that Hearth and
 * Harvest used (its keg, jug, stomping basin, tree tapper and basin all measure fluids in mB,
 * 250 mB a bottle, 1000 mB a bucket). HH never put data components on fluids, so this has none.
 * <p>
 * The codec reads and writes NeoForge's JSON shape ({@code {"id": ..., "amount": ...}}), so HH's
 * recipe files are unchanged. Conversion to Fabric's droplet-based transfer API is
 * {@value #DROPLETS_PER_MB} droplets per mB.
 */
public final class FluidStack {
    public static final long DROPLETS_PER_MB = 81;
    /** NeoForge FluidType.BUCKET_VOLUME. */
    public static final int BUCKET_VOLUME = 1000;
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private static final Codec<Holder<Fluid>> FLUID_NON_EMPTY = BuiltInRegistries.FLUID.holderByNameCodec()
            .validate(f -> f.value() == Fluids.EMPTY ? DataResult.error(() -> "Fluid must not be minecraft:empty") : DataResult.success(f));

    public static final MapCodec<FluidStack> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FLUID_NON_EMPTY.fieldOf("id").forGetter(FluidStack::getFluidHolder),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStack::getAmount)
    ).apply(i, FluidStack::new));
    public static final Codec<FluidStack> CODEC = MAP_CODEC.codec();
    /** Like {@link #CODEC} but also accepts/writes the empty stack (as {@code {}}), as NeoForge's did. */
    public static final Codec<FluidStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(CODEC)
            .xmap(o -> o.orElse(EMPTY), s -> s.isEmpty() ? Optional.empty() : Optional.of(s));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> OPTIONAL_STREAM_CODEC = StreamCodec.of(
            (buf, stack) -> {
                ByteBufCodecs.VAR_INT.encode(buf, stack.getAmount());
                if (!stack.isEmpty()) {
                    ByteBufCodecs.holderRegistry(Registries.FLUID).encode(buf, stack.getFluidHolder());
                }
            },
            buf -> {
                int amount = ByteBufCodecs.VAR_INT.decode(buf);
                if (amount <= 0) return EMPTY;
                return new FluidStack(ByteBufCodecs.holderRegistry(Registries.FLUID).decode(buf), amount);
            });
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = OPTIONAL_STREAM_CODEC;

    private final Fluid fluid;
    private int amount;

    public FluidStack(Fluid fluid, int amount) {
        this.fluid = fluid == null ? Fluids.EMPTY : fluid;
        this.amount = amount;
    }

    public FluidStack(Holder<Fluid> fluid, int amount) {
        this(fluid.value(), amount);
    }

    public Fluid getFluid() {
        return isEmpty() ? Fluids.EMPTY : fluid;
    }

    public Holder<Fluid> getFluidHolder() {
        return getFluid().builtInRegistryHolder();
    }

    public int getAmount() {
        return isEmpty() ? 0 : amount;
    }

    public void setAmount(int amount) {
        if (this == EMPTY) throw new IllegalStateException("Can't modify the empty stack");
        this.amount = amount;
    }

    public void grow(int amount) {
        setAmount(this.amount + amount);
    }

    public void shrink(int amount) {
        setAmount(this.amount - amount);
    }

    public boolean isEmpty() {
        return this == EMPTY || fluid == Fluids.EMPTY || amount <= 0;
    }

    public boolean is(Fluid other) {
        return getFluid() == other;
    }

    public boolean is(TagKey<Fluid> tag) {
        return getFluidHolder().is(tag);
    }

    public FluidStack copy() {
        return isEmpty() ? EMPTY : new FluidStack(fluid, amount);
    }

    public FluidStack copyWithAmount(int amount) {
        return isEmpty() || amount <= 0 ? EMPTY : new FluidStack(fluid, amount);
    }

    public Component getHoverName() {
        return FluidUtil.getName(getFluid());
    }

    /** NeoForge semantics: same fluid and same components (HH fluids carry none). */
    public static boolean isSameFluidSameComponents(FluidStack a, FluidStack b) {
        return a.getFluid() == b.getFluid();
    }

    public static boolean isSameFluid(FluidStack a, FluidStack b) {
        return a.getFluid() == b.getFluid();
    }

    public static boolean matches(FluidStack a, FluidStack b) {
        return a == b || (a.getAmount() == b.getAmount() && isSameFluidSameComponents(a, b));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FluidStack other && matches(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFluid(), getAmount());
    }

    @Override
    public String toString() {
        return getAmount() + " mB " + BuiltInRegistries.FLUID.getKey(getFluid());
    }
}
