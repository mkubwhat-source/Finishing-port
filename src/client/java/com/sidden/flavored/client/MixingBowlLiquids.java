package com.sidden.flavored.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sidden.flavored.Flavored;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Which texture the mixing bowl renders as its liquid surface for a given liquid-slot item.
 * <p>
 * 1.21.1 kept this in a synced NeoForge item data map ({@code flavored:mixing_bowl}, field
 * {@code textureLocation}). It only affects rendering, so it is now client resource-pack data:
 * {@code assets/<item namespace>/flavored_mixing_bowl_liquids/<item path>.json} with the same
 * field (e.g. {@code assets/minecraft/flavored_mixing_bowl_liquids/water_bucket.json}).
 */
public class MixingBowlLiquids extends SimpleJsonResourceReloadListener<MixingBowlLiquids.Liquid> implements IdentifiableResourceReloadListener {
    public record Liquid(Identifier textureLocation) {
        public static final Codec<Liquid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("textureLocation").forGetter(Liquid::textureLocation)
        ).apply(instance, Liquid::new));
    }

    private static final Map<Item, Identifier> TEXTURES = new HashMap<>();

    public MixingBowlLiquids() {
        super(Liquid.CODEC, FileToIdConverter.json("flavored_mixing_bowl_liquids"));
    }

    @Nullable
    public static Identifier textureFor(Item item) {
        return TEXTURES.get(item);
    }

    @Override
    protected void apply(Map<Identifier, Liquid> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        TEXTURES.clear();
        map.forEach((itemId, liquid) -> BuiltInRegistries.ITEM.getOptional(itemId)
                .ifPresentOrElse(item -> TEXTURES.put(item, liquid.textureLocation()),
                        () -> Flavored.LOGGER.warn("Mixing bowl liquid for unknown item {}", itemId)));
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "mixing_bowl_liquids");
    }
}
