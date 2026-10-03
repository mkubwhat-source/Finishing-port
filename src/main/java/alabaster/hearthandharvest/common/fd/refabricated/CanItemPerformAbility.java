package alabaster.hearthandharvest.common.fd.refabricated;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;

public record CanItemPerformAbility(ItemAbility ability) implements LootItemCondition {
    public static final MapCodec<CanItemPerformAbility> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemAbility.CODEC.fieldOf("ability").forGetter(CanItemPerformAbility::ability)
    ).apply(inst, CanItemPerformAbility::new));

    public static void init() {
		net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.LOOT_CONDITION_TYPE, HearthAndHarvest.id("can_item_perform_ability"), CODEC);
    }

	public static LootItemCondition.Builder canItemPerformAbility(ItemAbility ability) {
		return () -> new CanItemPerformAbility(ability);
	}

    @Override
    public boolean test(LootContext context) {
        ItemInstance stack = context.getOptional(LootContextParams.TOOL);
        return ability.canPerformAction(stack);
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.TOOL);
    }

	@Override
	public MapCodec<? extends LootItemCondition> codec() {
		return CODEC;
	}
}
