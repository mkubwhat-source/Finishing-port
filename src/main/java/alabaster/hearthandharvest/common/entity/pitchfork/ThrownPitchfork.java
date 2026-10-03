package alabaster.hearthandharvest.common.entity.pitchfork;



import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import alabaster.hearthandharvest.common.registry.HHModEffects;
import alabaster.hearthandharvest.common.registry.HHModEntities;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ThrownPitchfork extends AbstractArrow {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(ThrownPitchfork.class, EntityDataSerializers.ITEM_STACK);

    private boolean dealtDamage;
    private boolean creativeThrown;

    public ThrownPitchfork(EntityType<? extends ThrownPitchfork> type, Level level) {
        super(type, level);
    }

    public ThrownPitchfork(Level level, LivingEntity shooter, ItemStack stack) {
        super(HHModEntities.THROWN_PITCHFORK.get(), shooter, level, stack.copyWithCount(1), stack.copy());
        this.entityData.set(DATA_ITEM, stack.copyWithCount(1));
        this.setBaseDamage(meleeDamage(stack) / 2.5);
        this.pickup = Pickup.ALLOWED;
        this.creativeThrown = shooter instanceof Player player && player.getAbilities().instabuild;
    }

    /** AbstractArrow keeps its base damage private in 26.3; mirrored here for the hit damage. */
    private double hhBaseDamage = 2.0;

    @Override
    public void setBaseDamage(double baseDamage) {
        super.setBaseDamage(baseDamage);
        this.hhBaseDamage = baseDamage;
    }

    private static double meleeDamage(ItemStack stack) {
        double damage = 2.0;
        for (ItemAttributeModifiers.Entry entry : stack.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
            if (entry.attribute().is(Attributes.ATTACK_DAMAGE)
                    && entry.slot().test(EquipmentSlot.MAINHAND)
                    && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE)
                damage += entry.modifier().amount();
        }
        return damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        int loyalty = loyaltyLevel(this.getPickupItem());
        if (loyalty > 0 && this.dealtDamage && owner instanceof Player player && player.isAlive()) {
            if (!this.level().isClientSide()) {
                this.setInGround(false);
                this.setNoPhysics(true);
                Vec3 toPlayer = player.getEyePosition().subtract(this.position());
                if (toPlayer.lengthSqr() < 4.0) {
                    returnTo(player);
                    return;
                }
                this.setDeltaMovement(this.getDeltaMovement()
                        .add(toPlayer.normalize().scale(0.05 * loyalty))
                        .scale(0.92));
            }
        }
        super.tick();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.dealtDamage = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.isNoPhysics()) {
            if (result.getEntity().equals(this.getOwner()) && this.getOwner() instanceof Player player)
                returnTo(player);
            return;
        }
        Entity entity = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source;
        if (owner instanceof Player player) source = this.damageSources().playerAttack(player);
        else if (owner instanceof LivingEntity living) source = this.damageSources().mobAttack(living);
        else source = this.damageSources().thrown(this, owner != null ? owner : this);
        float damage = (float)(this.getDeltaMovement().length() * this.hhBaseDamage);
        if (this.level() instanceof ServerLevel serverLevel)
            damage = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, source, damage);
        this.dealtDamage = true;
        if (entity.hurtOrSimulate(source, damage)) {
            if (entity instanceof LivingEntity livingEntity) {
                this.doKnockback(livingEntity, source);
                this.doPostHurtEffects(livingEntity);
                if (livingEntity.isDeadOrDying()) HHSimpleTrigger.trigger(HHModTriggers.PITCHFORK_KILL, owner);
            }
            if (this.level() instanceof ServerLevel serverLevel)
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, entity, source, this.getWeaponItem());
            if (entity instanceof LivingEntity target)
                target.addEffect(new MobEffectInstance(HHModEffects.PINNED, 200, 0));
        }
    }

    private void returnTo(Player player) {
        if (!player.getAbilities().instabuild) {
            ItemStack stack = getDefaultPickupItem();
            if (!stack.isEmpty() && !player.getInventory().add(stack))
                player.drop(stack, false, net.minecraft.util.Prediction.SERVER_ONLY);
        }
        this.discard();
    }

    @Override
    public ItemStack getWeaponItem() {
        return getPitchforkStack();
    }

    private int loyaltyLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(Enchantments.LOYALTY)
                .map(h -> net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(h, stack))
                .orElse(0);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.isNoPhysics() ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    protected void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED || loyaltyLevel(getPickupItem()) <= 0)
            super.tickDespawn();
    }

    @Nullable
    @Override
    protected ProjectileDeflection hitTargetOrDeflectSelf(HitResult hitResult) {
        onHit(hitResult);
        return null;
    }

    @Override
    public byte getPierceLevel() {
        return 2;
    }

    public ItemStack getPitchforkStack() {
        return this.entityData.get(DATA_ITEM);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        ItemStack stack = getPitchforkStack();
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    public boolean isStuck() {
        return isInGround();
    }

    @Override
    protected boolean tryPickup(Player player) {
        if (player.getAbilities().instabuild) return true;
        if (this.creativeThrown) return false;
        return super.tryPickup(player);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return HHModSounds.PITCHFORK_HIT.get();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        ItemStack stack = getPitchforkStack();
        if (!stack.isEmpty()) tag.store("PitchforkItem", ItemStack.CODEC, stack);
        tag.putBoolean("DealtDamage", this.dealtDamage);
        tag.putBoolean("CreativeThrown", this.creativeThrown);
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        this.hhBaseDamage = tag.getDoubleOr("damage", 2.0);
        tag.read("PitchforkItem", ItemStack.CODEC).ifPresent(stack -> entityData.set(DATA_ITEM, stack));
        this.dealtDamage = tag.getBooleanOr("DealtDamage", false);
        this.creativeThrown = tag.getBooleanOr("CreativeThrown", false);
    }
}