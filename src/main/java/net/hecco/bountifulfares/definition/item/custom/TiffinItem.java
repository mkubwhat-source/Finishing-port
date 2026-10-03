package net.hecco.bountifulfares.definition.item.custom;

import net.hecco.bountifulfares.definition.item.component.TiffinContents;
import net.hecco.bountifulfares.definition.item.component.TiffinTooltip;
import net.hecco.bountifulfares.definition.networking.payload.TiffinFillPayload;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.definition.trigger.CraftFoodInTiffinTrigger;
import net.hecco.bountifulfares.registry.content.BFComponents;
import net.hecco.bountifulfares.registry.content.BFSounds;
import net.hecco.bountifulfares.registry.misc.BFCriteriaTriggers;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TiffinItem extends Item {
    private static final Map<DyeColor, TiffinItem> DYE_TO_TIFFIN = new HashMap<>();
    public TiffinItem(DyeColor color, Properties properties) {
        super(properties);
        DYE_TO_TIFFIN.put(color, this);
    }

    public static TiffinItem getItemFromDye(DyeColor color) {
        return DYE_TO_TIFFIN.get(color);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        FoodProperties foodproperties = null;
        if (itemstack.getComponents().has(BFComponents.TIFFIN_CONTENTS.get())) {
            ItemStack stack = itemstack.get(BFComponents.TIFFIN_CONTENTS.get()).getItemStack();
            if (stack.is(Items.PUMPKIN_PIE) && Services.PLATFORM.get().getBoolConfigValue("enablePlaceablePumpkinPie")){
                return InteractionResult.FAIL;
            }
            if (!stack.isEmpty() && !(stack.getItem() instanceof TiffinItem) && stack.has(DataComponents.FOOD)) {
                foodproperties = stack.get(DataComponents.FOOD);
            }
        }
        if (foodproperties != null) {
            if (player.canEat(foodproperties.canAlwaysEat())) {
                player.startUsingItem(usedHand);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.FAIL;
        }
    }

    public static Item getRemainderItem(Item item) {
        if (item.getCraftingRemainder() != null) {
            return item.getCraftingRemainder().item().value();
        }
        UseRemainder useRemainder = item.getDefaultInstance().get(DataComponents.USE_REMAINDER);
        if (useRemainder != null) {
            return useRemainder.convertInto().item().value();
        }
        return null;
    }

    public static boolean canInsertStack(ItemStack stack, TiffinContents contents) {
        if (stack.is(Items.PUMPKIN_PIE) && Services.PLATFORM.get().getBoolConfigValue("enablePlaceablePumpkinPie")) return false;
        return !(stack.getItem() instanceof TiffinItem) && stack.has(DataComponents.FOOD) && (getRemainderItem(stack.getItem()) == null || getRemainderItem(stack.getItem()).getDefaultInstance().is(BFItemTags.FOOD_CONTAINERS_TIFFINS_CAN_HOLD)) && (ItemStack.isSameItemSameComponents(contents.getItemStack(), stack) || contents.getItemStack().isEmpty());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack itemStack;
        if (stack.getComponents().has(BFComponents.TIFFIN_CONTENTS.get())) {
            ItemStack item = stack.get(BFComponents.TIFFIN_CONTENTS.get()).getItemStack();
            if (!item.isEmpty() && !(item.getItem() instanceof TiffinItem) && item.has(DataComponents.FOOD)) {
                itemStack = item.copy();
                FoodProperties existingProperties = itemStack.get(DataComponents.FOOD);
                itemStack.set(DataComponents.FOOD, new FoodProperties(existingProperties.nutrition(), existingProperties.saturation(), existingProperties.canAlwaysEat()));
                // 1.21.1 cleared FoodProperties.usingConvertsTo on this copy so eating from a tiffin
                // never handed out the food's container; in 26.3 that remainder lives in the
                // separate USE_REMAINDER component (applied by ItemStack.finishUsingItem ->
                // applyAfterUseComponentSideEffects), so it is removed here instead.
                itemStack.remove(DataComponents.USE_REMAINDER);
                itemStack.finishUsingItem(level, livingEntity);
                if (livingEntity == null || !livingEntity.hasInfiniteMaterials()) {
                    if (item.getCount() == 1) {
                        stack.set(BFComponents.TIFFIN_CONTENTS.get(), new TiffinContents());
                    } else {
                        item.shrink(1);
                    }
                }
            }
        }
        // 26.3: the tiffin carries a CONSUMABLE component (needed so the eating sound/particles
        // play while it is being used - LivingEntity.triggerItemUseEffects is Consumable-driven
        // now), and Item.finishUsingItem would hand the tiffin itself to Consumable.onConsume,
        // which ends in stack.consume(1, entity) - deleting the tiffin. In 1.21.1 the tiffin had no
        // FOOD component so super.finishUsingItem was a no-op, and LivingEntityTiffinMixin guarded
        // LivingEntity.eat(...) (removed in 26.3) against consuming a tiffin. The food copy above
        // already performs the whole eat (nutrition, effects, sound, EAT game event), so the tiffin
        // stack is returned unchanged - replacing both the super call and that mixin.
        return stack;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        if (stack.getComponents().has(BFComponents.TIFFIN_CONTENTS.get())) {
            ItemStack item = stack.get(BFComponents.TIFFIN_CONTENTS.get()).getItemStack();
            // FoodProperties.eatDurationTicks() is gone - eat duration now lives on the
            // Consumable component instead (confirmed via javap: FoodProperties is just
            // nutrition/saturation/canAlwaysEat now, and Consumable.consumeTicks() is the
            // replacement for the old eatDurationTicks()).
            if (!item.isEmpty() && !(item.getItem() instanceof TiffinItem) && item.has(DataComponents.FOOD) && item.has(DataComponents.CONSUMABLE)) {
                return item.get(DataComponents.CONSUMABLE).consumeTicks();
            }
        }
        return 0;
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        // HIDE_TOOLTIP/HIDE_ADDITIONAL_TOOLTIP were folded into the single TooltipDisplay
        // component (confirmed via javap: TooltipDisplay has hideTooltip() and a
        // shows(DataComponentType) check for individual hidden components).
        TooltipDisplay tooltipDisplay = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        return !tooltipDisplay.hideTooltip() && tooltipDisplay.shows(BFComponents.TIFFIN_CONTENTS.get()) ? Optional.ofNullable(stack.get(BFComponents.TIFFIN_CONTENTS.get())).map(TiffinTooltip::new) : Optional.empty();
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        ItemStack other = slot.getItem();
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            TiffinContents contents = stack.getComponents().get(BFComponents.TIFFIN_CONTENTS.get());
            if (contents == null) {
                return false;
            } else {
                TiffinContents.Mutable mutable = new TiffinContents.Mutable(contents);
                if (!contents.getItemStack().isEmpty() && ((getRemainderItem(contents.getItemStack().getItem()) == null && other.isEmpty()) || other.is(getRemainderItem(contents.getItemStack().getItem())))) {
                    boolean i = mutable.tryRemove(other, slot, player);
                    if (i) {
                        player.playSound(BFSounds.TIFFIN_REMOVE.get(), 0.9F, (Fraction.getFraction(contents.getCount(), contents.CAPACITY).floatValue() / 2) + 0.8f);
                    }
                    stack.set(BFComponents.TIFFIN_CONTENTS.get(), mutable.toImmutable());
                    return true;
                } else if (canInsertStack(other, contents)) {
                    int i = mutable.tryFill(other, slot, player);
                    if (i > 0) {
                        player.playSound(BFSounds.TIFFIN_INSERT.get(), 0.9F, (Fraction.getFraction(contents.getCount(), contents.CAPACITY).floatValue() / 2) + 0.8f);
                        if (player.level().isClientSide()) {
                            BFNetworkingHelper.sendToServer(new TiffinFillPayload((double) mutable.getCount() / mutable.getCapacity()));
                        }
                    }
                    stack.set(BFComponents.TIFFIN_CONTENTS.get(), mutable.toImmutable());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            TiffinContents contents = stack.getComponents().get(BFComponents.TIFFIN_CONTENTS.get());
            if (contents == null) {
                return false;
            } else {
                TiffinContents.Mutable mutable = new TiffinContents.Mutable(contents);
                if (!contents.getItemStack().isEmpty() && ((getRemainderItem(contents.getItemStack().getItem()) == null && other.isEmpty()) || other.is(getRemainderItem(contents.getItemStack().getItem())))) {
                    boolean i = mutable.tryRemove(other, access, player);
                    if (i) {
                        player.playSound(BFSounds.TIFFIN_REMOVE.get(), 0.9F, (Fraction.getFraction(contents.getCount(), contents.CAPACITY).floatValue() / 2) + 0.8f);
                    }
                    stack.set(BFComponents.TIFFIN_CONTENTS.get(), mutable.toImmutable());
                    return true;
                } else if (canInsertStack(other, contents)) {
                    int i = mutable.tryFill(other, access, player);
                    if (i > 0) {
                        player.playSound(BFSounds.TIFFIN_INSERT.get(), 0.9F, (Fraction.getFraction(contents.getCount(), contents.CAPACITY).floatValue() / 2) + 0.8f);
                        if (player.level().isClientSide()) {
                            BFNetworkingHelper.sendToServer(new TiffinFillPayload((double) mutable.getCount() / mutable.getCapacity()));
                        }
                    }
                    stack.set(BFComponents.TIFFIN_CONTENTS.get(), mutable.toImmutable());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        if (player instanceof ServerPlayer serverPlayer && stack.get(BFComponents.TIFFIN_CONTENTS.get()) != null) {
            ((CraftFoodInTiffinTrigger) BFCriteriaTriggers.CRAFT_FOOD_IN_TIFFIN.get()).trigger(serverPlayer, stack.get(BFComponents.TIFFIN_CONTENTS.get()).getItemStack());
        }
        super.onCraftedBy(stack, player);
    }

    public boolean isBarVisible(ItemStack stack) {
        TiffinContents contents = stack.getOrDefault(BFComponents.TIFFIN_CONTENTS.get(), new TiffinContents());
        return contents.getCount() > 0;
    }

    public int getBarWidth(ItemStack stack) {
        TiffinContents contents = stack.getOrDefault(BFComponents.TIFFIN_CONTENTS.get(), new TiffinContents());
        return Math.min(1 + Mth.mulAndTruncate(Fraction.getFraction(contents.getCount(), contents.CAPACITY), 12), 13);
    }

    public int getBarColor(ItemStack stack) {
        return net.minecraft.util.ARGB.colorFromFloat(1.0F, 0.9F, 0.6F, 0.2F);
    }
}
