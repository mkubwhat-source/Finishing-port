package com.sidden.flavored.entity;

import com.sidden.flavored.registry.FlavoredEntities;
import com.sidden.flavored.registry.FlavoredItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownTomato extends ThrowableItemProjectile {
    public ThrownTomato(EntityType<? extends ThrownTomato> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownTomato(Level level, LivingEntity shooter, ItemStack stack) {
        super(FlavoredEntities.TOMATO.get(), shooter, level, stack);
    }

    public ThrownTomato(Level level, double x, double y, double z, ItemStack stack) {
        super(FlavoredEntities.TOMATO.get(), x, y, z, level, stack);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3 && !this.getItem().isEmpty()) {
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(this.getItem()));
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(),
                        (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08, (this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return FlavoredItems.RED_TOMATO.get();
    }
}
