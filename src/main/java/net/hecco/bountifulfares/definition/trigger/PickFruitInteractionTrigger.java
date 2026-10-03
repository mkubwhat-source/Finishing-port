package net.hecco.bountifulfares.definition.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hecco.bountifulfares.registry.misc.BFCriteriaTriggers;
import net.minecraft.world.phys.Vec3;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Optional;

// ContextAwarePredicate is gone in 26.3 - its job (a LootContext-testable predicate that can be
// attached to an advancement criterion) is now just `Holder<LootItemCondition>` directly, since
// LootItemCondition already implements Predicate<LootContext>. Both the "player" and "location"
// fields below are that Holder now, tested with `.value().test(context)` instead of `.matches(context)`.
public class PickFruitInteractionTrigger extends SimpleCriterionTrigger<PickFruitInteractionTrigger.TriggerInstance> {
    public Codec<PickFruitInteractionTrigger.TriggerInstance> codec() {
        return PickFruitInteractionTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, BlockPos pos) {
        // ServerPlayer.serverLevel() was removed in 26.3; its own level() override (covariantly
        // returning ServerLevel) is the direct replacement (confirmed via javap).
        ServerLevel serverLevel = player.level();
        BlockState blockState = serverLevel.getBlockState(pos);
        LootParams lootParams = (new LootParams.Builder(serverLevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.BLOCK_STATE, blockState).create(LootContextParamSets.BLOCK_USE);
        LootContext lootContext = (new LootContext.Builder(lootParams)).create(Optional.empty());
        this.trigger(player, (triggerInstance) -> triggerInstance.matches(lootContext));
    }

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<Holder<LootItemCondition>> location) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<PickFruitInteractionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(PickFruitInteractionTrigger.TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("location").forGetter(PickFruitInteractionTrigger.TriggerInstance::location)).apply(instance, PickFruitInteractionTrigger.TriggerInstance::new));

        public boolean matches(LootContext context) {
            return this.location.isEmpty() || this.location.get().value().test(context);
        }

        public static Criterion<PickFruitInteractionTrigger.TriggerInstance> pickedAnyFruit() {
            return ((PickFruitInteractionTrigger)BFCriteriaTriggers.PICK_FRUIT.get()).createCriterion(new PickFruitInteractionTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<PickFruitInteractionTrigger.TriggerInstance> pickedFruit(LocationPredicate.Builder location) {
            Holder<LootItemCondition> locationCondition = Holder.direct(LocationCheck.checkLocation(location).build());
            return ((PickFruitInteractionTrigger)BFCriteriaTriggers.PICK_FRUIT.get()).createCriterion(new PickFruitInteractionTrigger.TriggerInstance(Optional.empty(), Optional.of(locationCondition)));
        }

        public void validate(ValidationContextSource validator) {
            SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
            this.location.ifPresent((holder) -> validator.context(LootContextParamSets.BLOCK_USE).validateContextUsage(holder.value()));
        }
    }
}
