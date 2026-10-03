package com.sidden.flavored.entity;

import com.sidden.flavored.registry.FlavoredEntities;
import com.sidden.flavored.registry.FlavoredItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** A thrown chocolate egg: like a vanilla egg, 1 in 8 hatches a baby chocken (1 in 32 of those, four). */
public class ThrownChocolateEgg extends ThrowableItemProjectile {
    private static final EntityDimensions ZERO_SIZED_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

    public ThrownChocolateEgg(EntityType<? extends ThrownChocolateEgg> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownChocolateEgg(Level level, LivingEntity shooter, ItemStack stack) {
        super(FlavoredEntities.CHOCOLATE_EGG.get(), shooter, level, stack);
    }

    public ThrownChocolateEgg(Level level, double x, double y, double z, ItemStack stack) {
        super(FlavoredEntities.CHOCOLATE_EGG.get(), x, y, z, level, stack);
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
            if (this.random.nextInt(8) == 0) {
                int count = this.random.nextInt(32) == 0 ? 4 : 1;
                for (int j = 0; j < count; ++j) {
                    Chocken chocken = FlavoredEntities.CHOCKEN.get().create(this.level(), EntitySpawnReason.TRIGGERED);
                    if (chocken != null) {
                        chocken.setAge(-24000);
                        chocken.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        if (!chocken.fudgePositionAfterSizeChange(ZERO_SIZED_DIMENSIONS)) {
                            break;
                        }
                        this.level().addFreshEntity(chocken);
                    }
                }
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return FlavoredItems.CHOCOLATE_EGG.get();
    }
}
