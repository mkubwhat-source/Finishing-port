package com.sidden.flavored.entity;

import com.sidden.flavored.registry.FlavoredEntities;
import com.sidden.flavored.registry.FlavoredItems;
import com.sidden.flavored.registry.FlavoredParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/** A thrown hot sauce bottle: sets the entity it hits (and every living entity around the impact) on fire. */
public class ThrownHotSauce extends ThrowableItemProjectile {
    public static final int BURN_ATTACK_RANGE = 2;
    public static final float DEFAULT_THROW_POWER = 1;
    private static final EntityDataAccessor<Float> DATA_THROW_POWER = SynchedEntityData.defineId(ThrownHotSauce.class, EntityDataSerializers.FLOAT);

    public ThrownHotSauce(EntityType<? extends ThrownHotSauce> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownHotSauce(Level level, LivingEntity shooter, ItemStack stack) {
        super(FlavoredEntities.HOT_SAUCE.get(), shooter, level, stack);
    }

    public ThrownHotSauce(Level level, double x, double y, double z, ItemStack stack) {
        super(FlavoredEntities.HOT_SAUCE.get(), x, y, z, level, stack);
    }

    public void setThrowPower(float throwPower) {
        this.getEntityData().set(DATA_THROW_POWER, throwPower);
    }

    public float getThrowPower() {
        return this.getEntityData().get(DATA_THROW_POWER);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                int randomX = level().getRandom().nextInt(-1, 1);
                int randomZ = level().getRandom().nextInt(-1, 1);
                this.level().addParticle(ParticleTypes.FLAME, getX() + randomX, getY() + 1, getZ() + randomZ, 0.05f * randomX, 0.1f, 0.05f * randomZ);
            }
            for (int i = 0; i < 3; ++i) {
                this.level().addParticle(FlavoredParticles.FLAME_BUNCH.get(), getX(), getY(), getZ(), 0, 0.2f, 0);
            }
            for (int i = 0; i < 6; ++i) {
                int randomX = level().getRandom().nextInt(-1, 1);
                int randomZ = level().getRandom().nextInt(-1, 1);
                this.level().addParticle(ParticleTypes.SMOKE, getX() + randomX, getY(), getZ() + randomZ, 0, 0f, 0);
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05f;
    }

    public List<Entity> getBurntEntities(Level level) {
        BlockPos pos = getOnPos();
        BlockPos start = new BlockPos(pos.getX() - BURN_ATTACK_RANGE, pos.getY(), pos.getZ() - BURN_ATTACK_RANGE);
        BlockPos end = new BlockPos(pos.getX() + BURN_ATTACK_RANGE, pos.getY() + BURN_ATTACK_RANGE, pos.getZ() + BURN_ATTACK_RANGE);
        return level.getEntities(this, new AABB(net.minecraft.world.phys.Vec3.atCenterOf(start), net.minecraft.world.phys.Vec3.atCenterOf(end)));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 5F);
        result.getEntity().igniteForSeconds(10);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.playSound(SoundEvents.SPLASH_POTION_BREAK);
            for (Entity target : getBurntEntities(level())) {
                if (target instanceof LivingEntity living) {
                    living.igniteForSeconds(8);
                }
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("throw_power", getThrowPower());
        super.addAdditionalSaveData(output);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setThrowPower(input.getFloatOr("throw_power", DEFAULT_THROW_POWER));
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_THROW_POWER, DEFAULT_THROW_POWER);
        super.defineSynchedData(builder);
    }

    @Override
    protected Item getDefaultItem() {
        return FlavoredItems.HOT_SAUCE.get();
    }
}
