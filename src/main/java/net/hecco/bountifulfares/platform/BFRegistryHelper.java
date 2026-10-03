package net.hecco.bountifulfares.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.hecco.bountifulfares.lib.util.ItemGroupAddition;
import net.hecco.bountifulfares.mixin.util.FireBlockSetFlammableInvoker;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Fabric-only registration helper, ported in-tree from NexusLib's
 * {@code NLServices.REGISTRY}/{@code FabricRegistryHelper} (and the interface's default
 * methods) as part of removing the NexusLib dependency during the 26.3 Fabric port. Since
 * this mod now targets Fabric exclusively, there is no more need for the
 * interface+ServiceLoader indirection NexusLib used to support multiple loaders - these are
 * plain static methods.
 */
public class BFRegistryHelper {

    public static <T> Supplier<T> register(String modid, String id, Registry<T> registry, Supplier<T> supplier) {
        // 26.3: blocks/items must be constructed from properties that already carry their id
        // (see BFProperties) - the supplier runs inside a registration scope for this key.
        ResourceKey<T> key = ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(modid, id));
        T value = Registry.register(registry, key, BFProperties.withScope(key, supplier));
        return () -> value;
    }

    /**
     * Like {@link #register(String, String, Registry, Supplier)} for registries of wildcard types
     * (menus, particle types, consume effect types, recipe display types), keeping the precise type.
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Supplier<T> registerTyped(String modid, String id, Registry<R> registry, Supplier<T> supplier) {
        return (Supplier<T>) register(modid, id, (Registry<Object>) (Registry<?>) registry, (Supplier<Object>) (Supplier<?>) supplier);
    }

    public static <T> Holder<T> registerForHolder(String modid, String id, Registry<T> registry, Supplier<T> holder) {
        return Registry.registerForHolder(
                registry,
                Identifier.fromNamespaceAndPath(modid, id),
                holder.get()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> Supplier<T> registerBlock(String modid, String id, Supplier<T> block) {
        Supplier<T> registeredBlock = register(modid, id, (Registry<T>) BuiltInRegistries.BLOCK, block);
        register(modid, id, BuiltInRegistries.ITEM, () -> new BlockItem(registeredBlock.get(), BFProperties.blockItem(registeredBlock.get())));
        return registeredBlock;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> Supplier<T> registerBlockNoItem(String modid, String id, Supplier<T> block) {
        return register(modid, id, (Registry<T>) BuiltInRegistries.BLOCK, block);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> Supplier<T> registerBlock(String modid, String id, Supplier<T> block, Item.Properties itemProperties) {
        Supplier<T> registeredBlock = register(modid, id, (Registry<T>) BuiltInRegistries.BLOCK, block);
        // The caller's (fresh) Item.Properties gets the item id and the block's description id,
        // exactly what BFProperties.blockItem does for the default case above.
        register(modid, id, BuiltInRegistries.ITEM, () -> new BlockItem(registeredBlock.get(),
                itemProperties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(modid, id)))
                        .overrideDescription(registeredBlock.get().getDescriptionId())));
        return registeredBlock;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Item> Supplier<T> registerItem(String modid, String id, Supplier<T> item) {
        return register(modid, id, (Registry<T>) BuiltInRegistries.ITEM, item);
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(String modid, String id, Supplier<BlockEntityType<T>> supplier) {
        BlockEntityType<T> register = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(modid, id), supplier.get());
        return () -> register;
    }

    @FunctionalInterface
    public interface BlockEntitySupplier<T extends BlockEntity> {
        @NotNull T create(net.minecraft.core.BlockPos pos, BlockState state);
    }

    @SafeVarargs
    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(BlockEntitySupplier<T> supplier, Supplier<Block>... blocks) {
        return FabricBlockEntityTypeBuilder.create(supplier::create, Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new)).build();
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(BlockEntitySupplier<T> supplier, List<Supplier<Block>> blocks) {
        return FabricBlockEntityTypeBuilder.create(supplier::create, blocks.stream().map(Supplier::get).toArray(Block[]::new)).build();
    }

    public static <T extends EntityType<?>> Supplier<T> registerEntityType(String modid, String id, Supplier<T> supplier) {
        var registered = Registry.register(BuiltInRegistries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(modid, id), supplier.get());
        return () -> registered;
    }

    public static Holder<SoundEvent> registerSoundReference(String modId, String id) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(modId, id), SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(modId, id)));
    }

    public static <T> Supplier<DataComponentType<T>> registerComponentType(String modId, String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> instance = builder.apply(DataComponentType.builder()).build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(modId, name), instance);
        return () -> instance;
    }

    public static Supplier<SimpleParticleType> registerParticleType(String modid, String id) {
        var register = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(modid, id), FabricParticleTypes.simple());
        return () -> register;
    }

    public static <T extends ParticleOptions> Supplier<ParticleType<T>> registerParticleType(String modId, String id, Function<ParticleType<T>, MapCodec<T>> codecGetter, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        var register = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(modId, id), new ParticleType<T>(false) {
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
        return () -> register;
    }

    @FunctionalInterface
    public interface MenuSupplier<T extends AbstractContainerMenu> {
        @NotNull T create(int var1, Inventory var2);
    }

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String modId, String id, MenuSupplier<T> factory) {
        MenuType<T> registered = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(modId, id), new MenuType<>(factory::create, FeatureFlags.DEFAULT_FLAGS));
        return () -> registered;
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String modId, String id) {
        var registered = Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(modId, id), new RecipeType<T>() {});
        return () -> registered;
    }

    public static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> registerRecipeSerializer(String modId, String id, RecipeSerializer<T> serializer) {
        var registered = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(modId, id), serializer);
        return () -> registered;
    }

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerCommandArgumentType(String modId, String id, Class<A> clazz, ArgumentTypeInfo<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(modId, id), clazz, serializer);
    }

    public static Supplier<PoiType> registerPoiType(String modId, String id, Set<BlockState> matchingStates, int maxTickets, int validRange) {
        var registered = PoiHelper.register(Identifier.fromNamespaceAndPath(modId, id), maxTickets, validRange, matchingStates);
        return () -> registered;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Structure> Supplier<StructureType<T>> registerStructureType(String modId, String id, Supplier<MapCodec<T>> pieceType) {
        var registered = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Identifier.fromNamespaceAndPath(modId, id), () -> (MapCodec<Structure>) pieceType.get());
        return () -> (StructureType<T>) registered;
    }

    public static <T extends StructurePieceType> Supplier<T> registerStructurePiece(String modId, String id, Supplier<T> pieceType) {
        var registered = Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Identifier.fromNamespaceAndPath(modId, id), pieceType.get());
        return () -> registered;
    }

    // `StructureProcessorType` is no longer a generic per-processor wrapper in 26.3 - it's just a
    // marker interface with a couple of static codecs now. The actual static "type registry" (what
    // used to be a registry of StructureProcessorType<P> instances) is BuiltInRegistries.STRUCTURE_PROCESSOR,
    // a registry of the processor's own MapCodec directly - matching the same type-merge pattern as
    // Feature/BlockStateProvider elsewhere in 26.3.
    public static <P extends StructureProcessor> Supplier<MapCodec<P>> registerStructureProcessor(String modId, String name, Supplier<MapCodec<P>> codec) {
        var registered = Registry.register((Registry<MapCodec<P>>) (Registry<?>) BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath(modId, name), codec.get());
        return () -> registered;
    }

    public static void setFlammable(Block block, int encouragement, int flammability) {
        ((FireBlockSetFlammableInvoker) Blocks.FIRE).bountifulfares$setFlammable(block, encouragement, flammability);
    }

    public static void setFlammable(TagKey<Block> block, int encouragement, int flammability) {
        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(block)) {
            ((FireBlockSetFlammableInvoker) Blocks.FIRE).bountifulfares$setFlammable(holder.value(), encouragement, flammability);
        }
    }

    // `ItemGroupEvents`/`ItemGroupEntries` is gone in 26.3's fabric-api (matching Mojang's own
    // ItemGroup -> CreativeModeTab rename); the replacement is `CreativeModeTabEvents.modifyOutputEvent`
    // with a `FabricCreativeModeTabOutput`, whose `insertAfter`/`insertBefore`/`accept` methods take
    // the place of the old entries.addAfter/addBefore/accept.
    public static void addItemsToItemGroup(ResourceKey<CreativeModeTab> tab, java.util.function.Supplier<List<ItemGroupAddition>> items) {
        // 26.3: item stacks can't be created during mod init anymore (item components are bound
        // later - "Components not bound yet"), so the additions are produced lazily, when the tab's
        // contents are actually being built.
        CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> applyAdditions(output, items.get()));
    }

    public static void applyAdditions(FabricCreativeModeTabOutput output, List<ItemGroupAddition> items) {
        items.forEach(addition -> {
            switch (addition.type()) {
                case ADD_AFTER -> {
                    if (addition.origin().isEmpty()) throw new NullPointerException("Origin item for ADD_AFTER addition type cannot be empty!");
                    output.insertAfter(addition.origin().get().asItem().getDefaultInstance(), addition.stack());
                }
                case ADD_BEFORE -> {
                    if (addition.origin().isEmpty()) throw new NullPointerException("Origin item for ADD_BEFORE addition type cannot be empty!");
                    output.insertBefore(addition.origin().get().asItem().getDefaultInstance(), addition.stack());
                }
                case ADD_LAST -> output.accept(addition.stack());
            }
        });
    }

    public static void registerBuiltInResourcepack(String modId, String packId, String displayName, boolean required, boolean enabledByDefault) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
        if (container.isPresent()) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath(modId, packId),
                    container.get(),
                    Component.literal(displayName),
                    required ? ResourcePackActivationType.ALWAYS_ENABLED : enabledByDefault ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL
            );
        }
    }

    public static void registerBuiltInDatapack(String modId, String packId, String displayName, boolean required, boolean enabledByDefault) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
        if (container.isPresent()) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath(modId, packId),
                    container.get(),
                    Component.literal(displayName),
                    required ? ResourcePackActivationType.ALWAYS_ENABLED : enabledByDefault ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL
            );
        }
    }
}
