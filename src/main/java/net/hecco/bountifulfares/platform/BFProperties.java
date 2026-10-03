package net.hecco.bountifulfares.platform;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * Factory for block/item {@code Properties} that carry their registry id.
 * <p>
 * Since 1.21.2 (and so in 26.3) a {@code Block}/{@code Item} can no longer be constructed from
 * properties without an id: {@code BlockBehaviour.<init>} immediately resolves the loot table and
 * description id from {@code Properties.setId(ResourceKey)} ("Block id not set" otherwise), and
 * {@code Item.<init>} does the same for the description id and model. Vanilla registers with
 * {@code Blocks.register(id, factory, properties)} and sets the id before calling the factory.
 * <p>
 * This mod builds its properties inside the registration suppliers (hundreds of call sites), so
 * {@link BFRegistryHelper} opens a "registration scope" around each supplier call and these
 * factories - drop-in replacements for {@code BlockBehaviour.Properties.of()}/{@code ofFullCopy(..)}
 * and {@code new Item.Properties()} - stamp the scope's key onto the new properties at
 * construction, exactly where vanilla's {@code setId} happens. Using them outside a scope throws,
 * so a missed id can never slip through silently.
 * <p>
 * {@link #blockItem(Block)} reproduces 1.21.1's {@code BlockItem.getDescriptionId()} (which
 * returned the block's own description id) via {@code overrideDescription(block.getDescriptionId())};
 * item-named block items (old {@code ItemNameBlockItem}, e.g. seeds) use {@link #item()}.
 */
public final class BFProperties {
    private static final ThreadLocal<Deque<ResourceKey<?>>> SCOPE = ThreadLocal.withInitial(ArrayDeque::new);

    static <T> T withScope(ResourceKey<?> key, Supplier<T> supplier) {
        Deque<ResourceKey<?>> stack = SCOPE.get();
        stack.push(key);
        try {
            return supplier.get();
        } finally {
            stack.pop();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T> currentKey(ResourceKey<? extends net.minecraft.core.Registry<T>> registry) {
        for (ResourceKey<?> key : SCOPE.get()) {
            if (key.isFor(registry)) {
                return (ResourceKey<T>) key;
            }
        }
        throw new IllegalStateException("No " + registry.identifier() + " registration in progress - "
                + "BFProperties must be used inside a BFRegistryHelper registration supplier");
    }

    public static BlockBehaviour.Properties block() {
        return BlockBehaviour.Properties.of().setId(currentKey(Registries.BLOCK));
    }

    public static BlockBehaviour.Properties blockCopy(BlockBehaviour source) {
        return BlockBehaviour.Properties.ofFullCopy(source).setId(currentKey(Registries.BLOCK));
    }

    public static Item.Properties item() {
        return new Item.Properties().setId(currentKey(Registries.ITEM));
    }

    public static Item.Properties blockItem(Block block) {
        return item().overrideDescription(block.getDescriptionId());
    }

    private BFProperties() {
    }
}
