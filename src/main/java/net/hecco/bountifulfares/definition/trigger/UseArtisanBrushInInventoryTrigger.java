package net.hecco.bountifulfares.definition.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hecco.bountifulfares.registry.misc.BFCriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class UseArtisanBrushInInventoryTrigger extends SimpleCriterionTrigger<UseArtisanBrushInInventoryTrigger.TriggerInstance> {
    public Codec<UseArtisanBrushInInventoryTrigger.TriggerInstance> codec() {
        return UseArtisanBrushInInventoryTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (p_43166_) -> true);
    }

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<UseArtisanBrushInInventoryTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(UseArtisanBrushInInventoryTrigger.TriggerInstance::player)).apply(instance, UseArtisanBrushInInventoryTrigger.TriggerInstance::new));

        public static Criterion<UseArtisanBrushInInventoryTrigger.TriggerInstance> use() {
            return ((UseArtisanBrushInInventoryTrigger)BFCriteriaTriggers.USE_ARTISAN_BRUSH_IN_INVENTORY.get()).createCriterion(new UseArtisanBrushInInventoryTrigger.TriggerInstance(Optional.empty()));
        }
    }
}
