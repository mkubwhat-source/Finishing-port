package com.sidden.flavored.menu;

import com.sidden.flavored.block.entity.OvenBlockEntity;
import com.sidden.flavored.recipe.BakingRecipe;
import com.sidden.flavored.registry.FlavoredMenus;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Oven menu: 3x3 grid, fuel and result. The recipe book places baking recipes into the grid
 * exactly like a crafting table's shaped recipes (26.3 has a fixed set of recipe book types; the
 * oven shares the crafting book's open/filtering settings).
 */
public class OvenMenu extends RecipeBookMenu {
    public static final int INPUT_SLOT_START = 0;
    public static final int INPUT_SLOT_END = 9;
    public static final int FUEL_SLOT = 9;
    public static final int RESULT_SLOT = 10;
    public static final int SLOT_COUNT = 11;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = 11;
    private static final int INV_SLOT_END = 38;
    private static final int USE_ROW_SLOT_START = 38;
    private static final int USE_ROW_SLOT_END = 47;
    private final Container container;
    private final ContainerData data;

    public OvenMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public OvenMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(FlavoredMenus.OVEN.get(), containerId);
        checkContainerSize(container, SLOT_COUNT);
        checkContainerDataCount(data, DATA_COUNT);
        this.container = container;
        this.data = data;
        for (int slot = INPUT_SLOT_START; slot < INPUT_SLOT_END; slot++) {
            this.addSlot(new Slot(container, slot, 30 + 18 * (slot % 3), 17 + 18 * (slot / 3)));
        }
        this.addSlot(new Slot(container, FUEL_SLOT, 126, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return OvenBlockEntity.isFuel(stack);
            }
        });
        this.addSlot(new OvenResultSlot(playerInventory.player, container, RESULT_SLOT, 126, 17));
        this.addStandardInventorySlots(playerInventory, 8, 84);
        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(slotStack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
            } else if (index >= INV_SLOT_START) {
                // fuel goes to the fuel slot first (1.21.1 tried the grid first, so fuel never
                // reached its slot by shift-click while the grid had room)
                if (!(OvenBlockEntity.isFuel(slotStack) && this.moveItemStackTo(slotStack, FUEL_SLOT, FUEL_SLOT + 1, false))
                        && !this.moveItemStackTo(slotStack, INPUT_SLOT_START, INPUT_SLOT_END, false)) {
                    if (index < INV_SLOT_END) {
                        if (!this.moveItemStackTo(slotStack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(slotStack, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(slotStack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return itemstack;
    }

    public float getBurnProgress() {
        int progress = this.data.get(OvenBlockEntity.DATA_BAKING_PROGRESS);
        int totalTime = this.data.get(OvenBlockEntity.DATA_BAKING_TOTAL_TIME);
        return totalTime != 0 && progress != 0 ? Mth.clamp((float) progress / totalTime, 0.0F, 1.0F) : 0.0F;
    }

    public float getLitProgress() {
        int litDuration = this.data.get(OvenBlockEntity.DATA_LIT_DURATION);
        if (litDuration == 0) {
            litDuration = 200;
        }
        return Mth.clamp((float) this.data.get(OvenBlockEntity.DATA_LIT_TIME) / litDuration, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return this.data.get(OvenBlockEntity.DATA_LIT_TIME) > 0;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents contents) {
        for (int i = INPUT_SLOT_START; i < INPUT_SLOT_END; i++) {
            contents.accountStack(this.container.getItem(i));
        }
    }

    public List<Slot> getInputGridSlots() {
        return this.slots.subList(INPUT_SLOT_START, INPUT_SLOT_END);
    }

    public Slot getResultSlot() {
        return this.slots.get(RESULT_SLOT);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level, Inventory inventory) {
        List<Slot> slotsToClear = new ArrayList<>(getInputGridSlots());
        slotsToClear.add(getResultSlot());
        return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<BakingRecipe>() {
            @Override
            public void fillCraftSlotsStackedContents(StackedItemContents contents) {
                OvenMenu.this.fillCraftSlotsStackedContents(contents);
            }

            @Override
            public void clearCraftingContent() {
                getInputGridSlots().forEach(slot -> slot.set(ItemStack.EMPTY));
            }

            @Override
            public boolean recipeMatches(RecipeHolder<BakingRecipe> holder) {
                List<ItemStack> grid = new ArrayList<>();
                for (int i = INPUT_SLOT_START; i < INPUT_SLOT_END; i++) {
                    grid.add(container.getItem(i));
                }
                return holder.value().matches(CraftingInput.of(3, 3, grid), level);
            }
        }, 3, 3, getInputGridSlots(), slotsToClear, inventory, (RecipeHolder<BakingRecipe>) recipe, useMaxItems, allowDroppingItemsToClear);
    }
}
