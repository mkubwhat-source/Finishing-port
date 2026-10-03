package com.sidden.flavored.menu;

import com.sidden.flavored.block.entity.KegBlockEntity;
import com.sidden.flavored.recipe.FermentingRecipe;
import com.sidden.flavored.recipe.input.FermentingRecipeInput;
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
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Keg menu. The recipe book works like a furnace's: the ingredient is placed in its slot, the
 * fermenter is shown as a ghost (like furnace fuel). 26.3 has a fixed set of recipe book types;
 * the keg shares the furnace's book settings (open/filtering).
 */
public class KegMenu extends RecipeBookMenu {
    private static final int TE_SLOT_COUNT = 3;
    private static final int INV_START = TE_SLOT_COUNT;
    private static final int INV_END = INV_START + 36;

    private final Container container;
    private final ContainerData data;
    private final Level level;

    public KegMenu(int containerId, Inventory inv) {
        this(containerId, inv, new SimpleContainer(3), new SimpleContainerData(2));
    }

    public KegMenu(int containerId, Inventory inv, Container container, ContainerData data) {
        super(FlavoredMenus.KEG.get(), containerId);
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 2);
        this.container = container;
        this.data = data;
        this.level = inv.player.level();

        this.addSlot(new Slot(container, KegBlockEntity.INPUT_SLOT, 63, 35));
        this.addSlot(new Slot(container, KegBlockEntity.FERMENTING_SLOT, 36, 35));
        this.addSlot(new KegResultSlot(inv.player, container, KegBlockEntity.OUTPUT_SLOT, 123, 35));
        this.addStandardInventorySlots(inv, 8, 84);
        this.addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public float getProgress() {
        int progress = this.data.get(0);
        int totalTime = this.data.get(1);
        return totalTime != 0 && progress != 0 ? Mth.clamp((float) progress / totalTime, 0.0F, 1.0F) : 0.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index < TE_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, INV_START, INV_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < INV_END) {
            if (!moveItemStackTo(sourceStack, 0, TE_SLOT_COUNT - 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents contents) {
        if (this.container instanceof StackedContentsCompatible compatible) {
            compatible.fillStackedContents(contents);
        }
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.FURNACE;
    }

    public Slot getIngredientSlot() {
        return this.slots.get(KegBlockEntity.INPUT_SLOT);
    }

    public Slot getFermenterSlot() {
        return this.slots.get(KegBlockEntity.FERMENTING_SLOT);
    }

    public Slot getResultSlot() {
        return this.slots.get(KegBlockEntity.OUTPUT_SLOT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level, Inventory inventory) {
        List<Slot> slotsToClear = List.of(getIngredientSlot(), getResultSlot());
        return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<FermentingRecipe>() {
            @Override
            public void fillCraftSlotsStackedContents(StackedItemContents contents) {
                KegMenu.this.fillCraftSlotsStackedContents(contents);
            }

            @Override
            public void clearCraftingContent() {
                slotsToClear.forEach(slot -> slot.set(ItemStack.EMPTY));
            }

            @Override
            public boolean recipeMatches(RecipeHolder<FermentingRecipe> holder) {
                return holder.value().matches(new FermentingRecipeInput(container.getItem(0), container.getItem(1)), level);
            }
        }, 1, 1, List.of(getIngredientSlot()), slotsToClear, inventory, (RecipeHolder<FermentingRecipe>) recipe, useMaxItems, allowDroppingItemsToClear);
    }
}
