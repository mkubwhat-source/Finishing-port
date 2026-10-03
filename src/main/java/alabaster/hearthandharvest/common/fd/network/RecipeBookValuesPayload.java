package alabaster.hearthandharvest.common.fd.network;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.refabricated.HHRecipeBookTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;

import java.util.ArrayList;
import java.util.List;

/** Open/filtering flags for each of {@link HHRecipeBookTypes#ALL}, in order. */
public record RecipeBookValuesPayload(List<Boolean> open, List<Boolean> filtering) implements CustomPacketPayload {
    public static final Type<RecipeBookValuesPayload> TYPE = new Type<>(HearthAndHarvest.id("recipe_book_values"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeBookValuesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), RecipeBookValuesPayload::open,
            ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), RecipeBookValuesPayload::filtering,
            RecipeBookValuesPayload::new);

    public static RecipeBookValuesPayload of(RecipeBookSettings settings) {
        List<Boolean> open = new ArrayList<>();
        List<Boolean> filtering = new ArrayList<>();
        for (RecipeBookType type : HHRecipeBookTypes.ALL) {
            open.add(settings.isOpen(type));
            filtering.add(settings.isFiltering(type));
        }
        return new RecipeBookValuesPayload(open, filtering);
    }

    public void applyTo(RecipeBook book) {
        for (int i = 0; i < HHRecipeBookTypes.ALL.size() && i < open.size() && i < filtering.size(); i++) {
            book.setOpen(HHRecipeBookTypes.ALL.get(i), open.get(i));
            book.setFiltering(HHRecipeBookTypes.ALL.get(i), filtering.get(i));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
