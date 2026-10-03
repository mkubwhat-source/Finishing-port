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
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CraftFoodInTiffinTrigger extends SimpleCriterionTrigger<CraftFoodInTiffinTrigger.TriggerInstance> {
    public Codec<CraftFoodInTiffinTrigger.TriggerInstance> codec() {
        return CraftFoodInTiffinTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack food) {
        this.trigger(player, (p_43166_) -> p_43166_.matches(food));
    }

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<ItemStack> food) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<CraftFoodInTiffinTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(CraftFoodInTiffinTrigger.TriggerInstance::player), ItemStack.CODEC.optionalFieldOf("food").forGetter(CraftFoodInTiffinTrigger.TriggerInstance::food)).apply(instance, CraftFoodInTiffinTrigger.TriggerInstance::new));

        public boolean matches(ItemStack food) {
            return this.food.map(stack -> ItemStack.isSameItemSameComponents(stack, food)).orElse(true);
        }

        public static Criterion<CraftFoodInTiffinTrigger.TriggerInstance> crafted(ItemStack food) {
            return ((CraftFoodInTiffinTrigger)BFCriteriaTriggers.CRAFT_FOOD_IN_TIFFIN.get()).createCriterion(new CraftFoodInTiffinTrigger.TriggerInstance(Optional.empty(), Optional.of(food)));
        }

        public static Criterion<CraftFoodInTiffinTrigger.TriggerInstance> crafted() {
            return ((CraftFoodInTiffinTrigger)BFCriteriaTriggers.CRAFT_FOOD_IN_TIFFIN.get()).createCriterion(new CraftFoodInTiffinTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }
    }
}
