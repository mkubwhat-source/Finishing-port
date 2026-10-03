package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class FlavoredBlockTags {
    public static final TagKey<Block> HEAT_SOURCES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "heat_sources"));

    private FlavoredBlockTags() {
    }
}
