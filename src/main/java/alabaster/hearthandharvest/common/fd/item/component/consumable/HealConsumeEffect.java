package alabaster.hearthandharvest.common.fd.item.component.consumable;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public record HealConsumeEffect(float amount) implements ConsumeEffect {
	public static final MapCodec<HealConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("amount").forGetter(HealConsumeEffect::amount)
			).apply(instance, HealConsumeEffect::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, HealConsumeEffect> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, HealConsumeEffect::amount,
			HealConsumeEffect::new
	);

	@Override
	public Type<HealConsumeEffect> getType() {
		return HHModConsumeEffects.HEAL.get();
	}

	@Override
	public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
		entity.heal(amount);
		return true;
	}
}