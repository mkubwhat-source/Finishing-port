package alabaster.hearthandharvest.common.fd.item;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import java.util.List;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import alabaster.hearthandharvest.common.fd.utility.ItemUtils;
import alabaster.hearthandharvest.common.fd.refabricated.ItemAbility;

import java.util.Set;

public class KnifeItem extends Item
{
	/**
	 * This action is used on cutting recipes which need a knife.
	 */
	public static final ItemAbility KNIFE_DIG = ItemAbility.KNIFE_DIG;
	/**
	 * This action is used in gameplay interactions where something is harvested.
	 */
	public static final ItemAbility KNIFE_HARVEST = ItemAbility.KNIFE_HARVEST;

	public static final Set<ItemAbility> KNIFE_ACTIONS = Set.of(ItemAbility.SHEARS_CARVE, ItemAbility.SWORD_DIG, KNIFE_DIG, KNIFE_HARVEST);

    public KnifeItem(Properties properties) {
        super(properties);
    }

    /** Farmer's Delight's flint tool material (flint cleaver). */
    public static final ToolMaterial FLINT = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 131, 4.0F, 1.0F, 5, FDTags.Items.FLINT_TOOL_MATERIALS);

    /**
     * FarmersDelightRefabricated's knife properties (durability/repair/enchantability of the material,
     * knife mining rules), with the given attack damage and speed. HH's cleavers use 2.0 / -3.0.
     * The cake-slicing interaction is not ported (FD's cake slice isn't part of this bundle).
     */
    public static Item.Properties knifeProperties(Item.Properties properties, ToolMaterial material, float attackDamage, float attackSpeed) {
        HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return properties
                .durability(material.durability())
                .repairable(material.repairItems())
                .enchantable(material.enchantmentValue())
                .attributes(createAttributes(material, attackDamage, attackSpeed))
                .component(DataComponents.TOOL, new Tool(List.of(
                        Tool.Rule.deniesDrops(blocks.getOrThrow(material.incorrectBlocksForDrops())),
                        Tool.Rule.minesAndDrops(blocks.getOrThrow(FDTags.Blocks.MINEABLE_WITH_KNIFE), material.speed()),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(FDTags.Blocks.KNIFE_INSTANTLY_MINES), Float.MAX_VALUE)
                ), 1.0F, 1, false))
                // FarmersDelightRefabricated 26.3: knives are weapons (2 durability per hit, like 1.21.1's
                // DiggerItem#hurtEnemy); without it attacks never wear the cleaver down.
                .component(DataComponents.WEAPON, new net.minecraft.world.item.component.Weapon(2));
    }

    public static ItemAttributeModifiers createAttributes(ToolMaterial material, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        if (enchantment.is(Enchantments.SWEEPING_EDGE)) {
            return false;
        }
        return super.canBeEnchantedWith(stack, enchantment, context);
    }

    public static class KnifeEvents
    {
        public static double onKnifeKnockback(double strength, LivingEntity entity) {
            LivingEntity attacker = entity.getKillCredit();
            ItemStack toolStack = attacker != null ? attacker.getItemInHand(InteractionHand.MAIN_HAND) : ItemStack.EMPTY;
            if (toolStack.getItem() instanceof KnifeItem) {
                strength = strength - 0.1F;
            }
            return strength;
        }

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack toolStack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Direction facing = context.getClickedFace();

        if (state.getBlock() == Blocks.PUMPKIN && toolStack.is(FDTags.Items.KNIVES)) {
            Player player = context.getPlayer();
            if (player != null && !level.isClientSide()) {
                Direction direction = facing.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : facing;
                level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), 11);
                ItemEntity itemEntity = new ItemEntity(level, (double) pos.getX() + 0.5D + (double) direction.getStepX() * 0.65D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D + (double) direction.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
                itemEntity.setDeltaMovement(0.05D * (double) direction.getStepX() + level.getRandom().nextDouble() * 0.02D, 0.05D, 0.05D * (double) direction.getStepZ() + level.getRandom().nextDouble() * 0.02D);
                level.addFreshEntity(itemEntity);
                toolStack.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        } else {
            return InteractionResult.PASS;
        }
    }
}
