package com.sidden.flavored.menu;

import com.sidden.flavored.block.entity.MixingBowlBlockEntity;
import com.sidden.flavored.recipe.MixingRecipe;
import com.sidden.flavored.recipe.input.MixingRecipeInput;
import com.sidden.flavored.registry.FlavoredMenus;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import net.hecco.bountifulfares.platform.BFRecipeLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Mixing bowl menu: 3x2 ingredient grid, vessel and liquid slots. The recipe book places a
 * recipe's ingredients into the grid; its vessel and liquid are shown as ghost items to fill in
 * (26.3's recipe placement only fills one grid). Shares the crafting book's open/filtering settings.
 */
public class MixingBowlMenu extends RecipeBookMenu {
    public static final int INGREDIENT_SLOT_START = 0;
    public static final int INGREDIENT_SLOT_END = 6;
    public static final int VESSEL_SLOT = 6;
    public static final int LIQUID_SLOT = 7;
    public static final int SLOT_COUNT = 8;
    public static final int INV_SLOT_START = 8;
    public static final int INV_SLOT_END = 35;
    public static final int USE_ROW_SLOT_START = 35;
    public static final int USE_ROW_SLOT_END = 44;
    public static final int MIX_PROGRESS_ARROW_SIZE = 27;

    private final Container container;
    private final ContainerData data;
    private final Level level;

    public MixingBowlMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(2));
    }

    public MixingBowlMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(FlavoredMenus.MIXING_BOWL.get(), containerId);
        checkContainerSize(container, SLOT_COUNT);
        checkContainerDataCount(data, 2);
        this.container = container;
        this.data = data;
        this.level = playerInventory.player.level();

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(this.container, x + y * 3, 44 + x * 18, 18 + y * 18));
            }
        }
        this.addSlot(new Slot(this.container, VESSEL_SLOT, 135, 25));
        this.addSlot(new Slot(this.container, LIQUID_SLOT, 62, 60));
        this.addStandardInventorySlots(playerInventory, 8, 84);
        this.addDataSlots(data);
    }

    public int getMixProgress() {
        int progress = this.data.get(0);
        return progress != 0 ? progress * MIX_PROGRESS_ARROW_SIZE / MixingBowlBlockEntity.MAX_MIX_PROGRESS : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();
            if (index >= INV_SLOT_START) {
                // 1.21.1 returned early whenever a vessel/liquid item could not go to its own
                // slot; such items now fall through to the grid / inventory like anything else
                boolean moved = (this.isVessel(stackInSlot) && this.moveItemStackTo(stackInSlot, VESSEL_SLOT, VESSEL_SLOT + 1, false))
                        || (this.isLiquid(stackInSlot) && this.moveItemStackTo(stackInSlot, LIQUID_SLOT, LIQUID_SLOT + 1, false))
                        || this.moveItemStackTo(stackInSlot, INGREDIENT_SLOT_START, INGREDIENT_SLOT_END, false);
                if (!moved) {
                    if (index < INV_SLOT_END) {
                        if (!this.moveItemStackTo(stackInSlot, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackInSlot);
        }
        return stack;
    }

    private boolean anyRecipe(ItemStack stack, Function<MixingRecipe, Optional<Ingredient>> part) {
        return BFRecipeLookup.getAllOfType(this.level, FlavoredRecipeTypes.MIXING_BOWL_TYPE.get()).stream()
                .anyMatch(holder -> part.apply(holder.value()).map(i -> i.test(stack)).orElse(false));
    }

    public boolean isVessel(ItemStack stack) {
        return anyRecipe(stack, MixingRecipe::vesselInput);
    }

    public boolean isLiquid(ItemStack stack) {
        return anyRecipe(stack, MixingRecipe::liquidInput);
    }

    public boolean shouldDisplayCheckmark() {
        return this.data.get(1) == 1;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents contents) {
        if (this.container instanceof StackedContentsCompatible compatible) {
            compatible.fillStackedContents(contents);
        }
    }

    public MixingRecipeInput createInput() {
        NonNullList<ItemStack> ingredients = NonNullList.withSize(INGREDIENT_SLOT_END, ItemStack.EMPTY);
        for (int i = 0; i < INGREDIENT_SLOT_END; i++) {
            ingredients.set(i, this.container.getItem(i));
        }
        return new MixingRecipeInput(ingredients, this.container.getItem(VESSEL_SLOT), this.container.getItem(LIQUID_SLOT));
    }

    public List<Slot> getIngredientSlots() {
        return this.slots.subList(INGREDIENT_SLOT_START, INGREDIENT_SLOT_END);
    }

    public Slot getVesselSlot() {
        return this.slots.get(VESSEL_SLOT);
    }

    public Slot getLiquidSlot() {
        return this.slots.get(LIQUID_SLOT);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level, Inventory inventory) {
        List<Slot> slotsToClear = new ArrayList<>(getIngredientSlots());
        return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<MixingRecipe>() {
            @Override
            public void fillCraftSlotsStackedContents(StackedItemContents contents) {
                MixingBowlMenu.this.fillCraftSlotsStackedContents(contents);
            }

            @Override
            public void clearCraftingContent() {
                getIngredientSlots().forEach(slot -> slot.set(ItemStack.EMPTY));
            }

            @Override
            public boolean recipeMatches(RecipeHolder<MixingRecipe> holder) {
                return holder.value().matches(createInput(), level);
            }
        }, 3, 2, getIngredientSlots(), slotsToClear, inventory, (RecipeHolder<MixingRecipe>) recipe, useMaxItems, allowDroppingItemsToClear);
    }
}
