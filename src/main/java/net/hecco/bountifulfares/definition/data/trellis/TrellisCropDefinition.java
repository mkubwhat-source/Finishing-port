package net.hecco.bountifulfares.definition.data.trellis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * A crop grown on a trellis. {@code spreading} (optional, added for Hearth and Harvest's grapes) makes
 * a fully grown crop creep onto empty neighbouring trellises.
 */
public record TrellisCropDefinition(Item seeds, Item produce, Identifier texture, String model, int stages, float growChance, int minDrops, int maxDrops, Optional<Spreading> spreading) {
    public static final Codec<TrellisCropDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("seeds")
                    .xmap(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey)
                    .forGetter(TrellisCropDefinition::seeds),
            Identifier.CODEC.fieldOf("produce")
                    .xmap(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey)
                    .forGetter(TrellisCropDefinition::produce),
            Identifier.CODEC.fieldOf("texture")
                    .forGetter(TrellisCropDefinition::texture),
            Codec.STRING.fieldOf("model")
                    .forGetter(TrellisCropDefinition::model),
            Codec.intRange(1, 8).fieldOf("stages")
                    .forGetter(TrellisCropDefinition::stages),
            Codec.floatRange(0.0F, 1.0F).fieldOf("grow_chance")
                    .forGetter(TrellisCropDefinition::growChance),
            Codec.intRange(1, 64).fieldOf("min_drops")
                    .forGetter(TrellisCropDefinition::minDrops),
            Codec.intRange(1, 64).fieldOf("max_drops")
                    .forGetter(TrellisCropDefinition::maxDrops),
            Spreading.CODEC.optionalFieldOf("spreading")
                    .forGetter(TrellisCropDefinition::spreading)
    ).apply(instance, TrellisCropDefinition::new));

    public TrellisCropDefinition(Item seeds, Item produce, Identifier texture, String model, int stages, float growChance, int minDrops, int maxDrops) {
        this(seeds, produce, texture, model, stages, growChance, minDrops, maxDrops, Optional.empty());
    }

    /**
     * @param chance    chance per random tick that a fully grown crop spreads
     * @param maxHeight how many trellises tall a column of this crop may grow (counted from its lowest trellis)
     * @param soil      when present, the crop only grows while the block under its lowest trellis is in this tag
     */
    public record Spreading(float chance, int maxHeight, Optional<TagKey<Block>> soil) {
        public static final Codec<Spreading> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(Spreading::chance),
                Codec.intRange(1, 64).optionalFieldOf("max_height", 5).forGetter(Spreading::maxHeight),
                TagKey.codec(Registries.BLOCK).optionalFieldOf("soil").forGetter(Spreading::soil)
        ).apply(instance, Spreading::new));
    }
}
