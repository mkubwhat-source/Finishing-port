package net.hecco.bountifulfares.definition.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hecco.bountifulfares.registry.misc.BFCriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class PlantOnTrellisTrigger extends SimpleCriterionTrigger<PlantOnTrellisTrigger.TriggerInstance> {
    public Codec<TriggerInstance> codec() {
        return PlantOnTrellisTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack plant) {
        this.trigger(player, (triggerInstance) -> triggerInstance.matches(plant));
    }

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<ItemPredicate> plant) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<PlantOnTrellisTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(PlantOnTrellisTrigger.TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("plant").forGetter(PlantOnTrellisTrigger.TriggerInstance::plant)).apply(instance, PlantOnTrellisTrigger.TriggerInstance::new));

        public boolean matches(ItemStack plant) {
            return this.plant.isEmpty() || this.plant.get().test(plant);
        }

        public static Criterion<PlantOnTrellisTrigger.TriggerInstance> plantedAnyPlant() {
            return ((PlantOnTrellisTrigger)BFCriteriaTriggers.PLANT_ON_TRELLIS.get()).createCriterion(new PlantOnTrellisTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<PlantOnTrellisTrigger.TriggerInstance> plantedPlant(ItemStack plant) {
            return ((PlantOnTrellisTrigger)BFCriteriaTriggers.PLANT_ON_TRELLIS.get()).createCriterion(new PlantOnTrellisTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, plant.getItem()).build())));
        }
    }
}
