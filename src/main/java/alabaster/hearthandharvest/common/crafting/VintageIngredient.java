package alabaster.hearthandharvest.common.crafting;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.item.AgeableItem;
import alabaster.hearthandharvest.common.item.VintageHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.stream.Stream;

/**
 * Matches an item of a given vintage (the {@code hearthandharvest:vintage} component) that can
 * still age further. Used by cask aging recipes. 1.21.1: a NeoForge custom ingredient
 * ({@code "type": "hearthandharvest:vintage"}); now a Fabric one ({@code "fabric:type"}), same fields.
 */
public class VintageIngredient implements CustomIngredient {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, "vintage");
    public static final MapCodec<VintageIngredient> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("base").forGetter(ingredient -> ingredient.base),
                    Codec.INT.fieldOf("vintage").forGetter(ingredient -> ingredient.vintage)
            ).apply(instance, VintageIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, VintageIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ingredient -> ingredient.base,
            ByteBufCodecs.VAR_INT, ingredient -> ingredient.vintage,
            VintageIngredient::new);
    public static final CustomIngredientSerializer<VintageIngredient> SERIALIZER = new CustomIngredientSerializer<>() {
        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<VintageIngredient> getCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, VintageIngredient> getStreamCodec() {
            return STREAM_CODEC;
        }
    };

    private final Ingredient base;
    private final int vintage;

    public VintageIngredient(Ingredient base, int vintage) {
        this.base = base;
        this.vintage = vintage;
    }

    public VintageIngredient(Item item, int vintage) {
        this(Ingredient.of(item), vintage);
    }

    public static void init() {
        CustomIngredientSerializer.register(SERIALIZER);
    }

    public int vintage() {
        return vintage;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (!base.test(stack) || VintageHelper.getVintage(stack) != vintage) return false;
        return !(stack.getItem() instanceof AgeableItem ageable) || ageable.canAgeFurther(stack);
    }

    @Override
    public Stream<Holder<Item>> items() {
        return base.items();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    /** Recipe viewers show the base items carrying this vintage. */
    @Override
    public SlotDisplay display() {
        List<SlotDisplay> stacks = base.items()
                .map(item -> (SlotDisplay) new SlotDisplay.ItemStackSlotDisplay(
                        ItemStackTemplate.fromNonEmptyStack(VintageHelper.withVintage(new ItemStack(item), vintage))))
                .toList();
        return new SlotDisplay.Composite(stacks);
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public Ingredient toVanilla() {
        return CustomIngredient.super.toVanilla();
    }
}
