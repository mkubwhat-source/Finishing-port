package net.hecco.bountifulfares.definition.data.trellis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record TrellisPlantDefinition(Item plant, Identifier texture, String model, boolean canDuplicate) {
    public static final Codec<TrellisPlantDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("plant")
                    .xmap(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey)
                    .forGetter(TrellisPlantDefinition::plant),
            Identifier.CODEC.fieldOf("texture")
                    .forGetter(TrellisPlantDefinition::texture),
            Codec.STRING.fieldOf("model")
                    .forGetter(TrellisPlantDefinition::model),
            Codec.BOOL.fieldOf("can_duplicate")
                    .forGetter(TrellisPlantDefinition::canDuplicate)
    ).apply(instance, TrellisPlantDefinition::new));

    public TrellisPlantDefinition(Item plant, Identifier texture, String model, boolean canDuplicate) {
        this.plant = plant;
        this.texture = texture;
        this.model = model;
        this.canDuplicate = canDuplicate;
    }
}
