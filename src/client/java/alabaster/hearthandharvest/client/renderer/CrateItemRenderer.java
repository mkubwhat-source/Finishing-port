package alabaster.hearthandharvest.client.renderer;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.CrateBlockEntity;
import alabaster.hearthandharvest.platform.util.BlockEntityItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The items inside a crate item (1.21.1: CrateItemRenderer, a BEWLR drawing the crate block plus
 * CrateRenderer's contents). In 26.3 the crate block model is a plain model layer of the crate's
 * item definition and this {@code minecraft:special} layer stands the stored items in it, laid out
 * like a placed bottom crate.
 */
public class CrateItemRenderer implements SpecialModelRenderer<CrateItemRenderer.Contents> {
    public static final Identifier ID = HearthAndHarvest.id("crate_contents");

    /** The stored items (one crate half); compared by item and components so GUI item caching works. */
    public record Contents(List<ItemStack> items) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Contents other) || other.items.size() != items.size()) return false;
            for (int i = 0; i < items.size(); i++) {
                if (!ItemStack.isSameItemSameComponents(items.get(i), other.items.get(i))) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return ItemStack.hashStackList(items);
        }
    }

    @Override
    public @Nullable Contents extractArgument(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(CrateBlockEntity.SLOTS_PER_HALF, ItemStack.EMPTY);
        if (!BlockEntityItems.readItems(stack, items, BlockEntityItems.currentRegistries())) return null;
        if (items.stream().allMatch(ItemStack::isEmpty)) return null;
        return new Contents(List.copyOf(items));
    }

    @Override
    public void submit(@Nullable Contents contents, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        if (contents == null) return;
        ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
        List<CrateRenderer.Entry> entries = new ArrayList<>();
        for (int i = 0; i < contents.items().size(); i++) {
            CrateRenderer.Entry entry = CrateRenderer.entry(resolver, contents.items().get(i), i, 0.5, Minecraft.getInstance().level, i);
            if (entry != null) entries.add(entry);
        }
        for (CrateRenderer.Entry entry : entries) {
            CrateRenderer.submitEntry(entry, poseStack, collector, light);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        // the bottom crate's box (contents stay inside it)
        output.accept(new Vector3f(0, 0, 0));
        output.accept(new Vector3f(1, 0.5F, 1));
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Contents> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<Contents> bake(SpecialModelRenderer.BakingContext context) {
            return new CrateItemRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
