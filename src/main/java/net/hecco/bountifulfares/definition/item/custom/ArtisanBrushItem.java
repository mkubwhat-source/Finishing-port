package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.entity.DyeableBlockEntity;
import net.hecco.bountifulfares.definition.networking.payload.UseArtisanBrushPayload;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class ArtisanBrushItem extends Item {
    public static int DEFAULT_COLOR = DyeableBlockEntity.DEFAULT_COLOR;
    public ArtisanBrushItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockState current = world.getBlockState(pos);
        int oldColor = DyeableBlockEntity.getColor(world, pos);
        DyedItemColor component = context.getItemInHand().get(DataComponents.DYED_COLOR);
        if (BFBlocks.CERAMIC_TO_CHECKERED_CERAMIC.containsKey(current.getBlock()) && DyeableBlockEntity.getColor(world, pos) != DyeableBlockEntity.DEFAULT_COLOR) {
            if ((component != null ? component.rgb() : DEFAULT_COLOR) == DyeableBlockEntity.getColor(world, pos)) {
                    world.setBlockAndUpdate(pos, BFBlocks.CERAMIC_TO_CHECKERED_CERAMIC.get(current.getBlock()).withPropertiesOf(current));
                    world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (world.getBlockEntity(pos) instanceof DyeableBlockEntity ceramicTilesBlockEntity) {
                    ceramicTilesBlockEntity.color = oldColor;
                    ceramicTilesBlockEntity.setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (DyeableBlockEntity.getColor(world, pos) != DyeableBlockEntity.DEFAULT_COLOR) {
            if (DyedItemColor.getOrDefault(context.getItemInHand(), DEFAULT_COLOR) != DyeableBlockEntity.getColor(world, pos)) {
                context.getItemInHand().set(DataComponents.DYED_COLOR, new DyedItemColor(DyeableBlockEntity.getColor(world, pos)));
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
//        if (ModBlocks.CERAMIC_TO_CHECKERED_CERAMIC.containsKey(current.getBlock()) && Objects.requireNonNull(context.getPlayer()).isSneaking()) {
//            if (world.getBlockEntity(pos) instanceof DyeableBlockEntity ceramicTilesBlockEntity && ceramicTilesBlockEntity.color != DyeableBlockEntity.DEFAULT_COLOR) {
//                int oldColor = DyeableBlockEntity.getColor(world, pos);
//                world.setBlockState(pos, ModBlocks.CERAMIC_TO_CHECKERED_CERAMIC.get(current.getBlock()).getStateWithProperties(current));
//                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0F, 0.8F + world.getRandom().nextFloat());
//                ceramicTilesBlockEntity.color = oldColor;
//                ceramicTilesBlockEntity.markDirty();
//                return ActionResult.SUCCESS;
//            }
//        }
//        return super.useOnBlock(context);
//    }


    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack other = slot.getItem();
            // ArmorItem (and its per-material getMaterial()) was removed entirely in 26.3 - armor is
            // now just a plain Item carrying an Equippable data component, with no ArmorMaterial
            // reference on the component itself (confirmed via javap). The item prototype's default
            // component map still declares a DYED_COLOR component for anything dyeable (leather/wolf
            // armor), so checking that default-components map is the direct, non-hardcoded
            // replacement for the old "is this leather or armadillo armor" material check.
            if ((other.has(DataComponents.DYED_COLOR) || other.getItem().components().has(DataComponents.DYED_COLOR) || other.is(BFItemTags.DYEABLE_CERAMIC_BLOCKS)) && stack.has(DataComponents.DYED_COLOR)) {
                other.set(DataComponents.DYED_COLOR, stack.get(DataComponents.DYED_COLOR));
                player.playSound(SoundEvents.DYE_USE, 0.9F, 1.0f);
                if (other.is(BFItemTags.DYEABLE_CERAMIC_BLOCKS)) {
                    BFNetworkingHelper.sendToServer(new UseArtisanBrushPayload());
                }
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (other.getItem() instanceof DyeItem dyeItem) {
                stack.set(DataComponents.DYED_COLOR, DyedItemColor.applyDyes(stack, List.of(dyeColorOf(dyeItem))).get(DataComponents.DYED_COLOR));
                if (!player.hasInfiniteMaterials()) {
                    other.shrink(1);
                }
                player.playSound(SoundEvents.DYE_USE, 0.9F, 1.0f);
                return true;
            }
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    /** DyeItem lost its own getDyeColor() - dyes are now a single generic DyeItem class registered
     * per color through the Items.DYE ColorCollection (confirmed via javap: DyeItem has no color
     * field/accessor at all anymore), so recovering the DyeColor for a given dye Item means
     * reverse-searching that ColorCollection (same pattern already established in
     * TiffinColoringRecipe). */
    private static DyeColor dyeColorOf(DyeItem dyeItem) {
        for (DyeColor color : DyeColor.values()) {
            if (Items.DYE.pick(color) == dyeItem) {
                return color;
            }
        }
        return DyeColor.WHITE;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag type) {
        if (!stack.getComponents().has(DataComponents.DYED_COLOR)) {
            tooltip.accept(Component.translatable("tooltip." + BountifulFares.MOD_ID + ".dyeable").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, type);
    }
}
