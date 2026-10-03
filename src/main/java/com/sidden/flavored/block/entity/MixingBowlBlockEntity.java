package com.sidden.flavored.block.entity;

import com.sidden.flavored.menu.MixingBowlMenu;
import com.sidden.flavored.recipe.MixingRecipe;
import com.sidden.flavored.recipe.input.MixingRecipeInput;
import com.sidden.flavored.registry.FlavoredBlockEntities;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import com.sidden.flavored.registry.FlavoredStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The mixing bowl: six ingredient slots, a vessel slot (6) and a liquid slot (7). Right-clicking it
 * with a whisk advances mixing; after 10 whisks the result pops out on top and one of each
 * ingredient plus the vessel (if the recipe has one) is consumed. The liquid is not consumed.
 */
public class MixingBowlBlockEntity extends BaseContainerBlockEntity implements StackedContentsCompatible {
    public static final int[] INGREDIENT_SLOTS = new int[]{0, 1, 2, 3, 4, 5};
    public static final int VESSEL_SLOT = 6;
    public static final int LIQUID_SLOT = 7;
    public static final int MAX_MIX_PROGRESS = 10;

    protected final ContainerData dataAccess;
    private NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private int mixProgress = 0;
    /** Server-side "has a valid recipe" flag, synced to the menu (the client cannot look recipes up). */
    private boolean validRecipe = false;
    public int wiggleTime = 0;

    public MixingBowlBlockEntity(BlockPos pos, BlockState blockState) {
        super(FlavoredBlockEntities.MIXING_BOWL.get(), pos, blockState);
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MixingBowlBlockEntity.this.mixProgress;
                    case 1 -> MixingBowlBlockEntity.this.validRecipe ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) MixingBowlBlockEntity.this.mixProgress = value;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.mixProgress = input.getIntOr("mix_progress", 0);
        this.wiggleTime = input.getIntOr("wiggle_time", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items, true);
        output.putInt("mix_progress", mixProgress);
        output.putInt("wiggle_time", wiggleTime);
    }

    @Override
    public void fillStackedContents(StackedItemContents contents) {
        for (ItemStack stack : this.items) {
            contents.accountStack(stack);
        }
    }

    public void mix(ServerLevel level, int amount, Player player) {
        if (getCurrentRecipe(level).isPresent()) {
            this.mixProgress += amount;
            this.wiggleTime = 5;
            this.setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        if (this.mixProgress >= MAX_MIX_PROGRESS) {
            mixItem(level);
            player.awardStat(FlavoredStats.MIX_ITEM);
            resetProgress();
        }
    }

    public void resetProgress() {
        this.mixProgress = 0;
        this.setChanged();
    }

    private void mixItem(ServerLevel level) {
        Optional<RecipeHolder<MixingRecipe>> recipeOpt = getCurrentRecipe(level);
        if (recipeOpt.isEmpty()) return;
        MixingRecipe recipe = recipeOpt.get().value();
        ItemStack output = recipe.assemble(createInput());

        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), output);
        for (int i = 0; i < INGREDIENT_SLOTS.length; i++) {
            ContainerHelper.removeItem(this.items, i, 1);
        }
        if (recipe.vesselInput().isPresent()) {
            ContainerHelper.removeItem(this.items, VESSEL_SLOT, 1);
        }
        this.setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MixingBowlBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.validRecipe = blockEntity.getCurrentRecipe(serverLevel).isPresent();
            if (!blockEntity.validRecipe && blockEntity.mixProgress != 0) {
                blockEntity.resetProgress();
            }
            return;
        }
        if (blockEntity.wiggleTime > 0) {
            blockEntity.wiggleTime--;
        }
    }

    public boolean hasValidRecipe(ServerLevel level) {
        return getCurrentRecipe(level).isPresent();
    }

    public MixingRecipeInput createInput() {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < INGREDIENT_SLOTS.length; i++) {
            ingredients.add(this.items.get(i));
        }
        return new MixingRecipeInput(ingredients, this.items.get(VESSEL_SLOT), this.items.get(LIQUID_SLOT));
    }

    public Optional<RecipeHolder<MixingRecipe>> getCurrentRecipe(ServerLevel level) {
        return level.recipeAccess().getRecipeFor(FlavoredRecipeTypes.MIXING_BOWL_TYPE.get(), createInput(), level);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new MixingBowlMenu(containerId, inventory, this, this.dataAccess);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.flavored.mixing_bowl");
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack stackAtIndex = this.items.get(slot);
        boolean sameItem = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stackAtIndex, stack);
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        if (!sameItem) {
            this.mixProgress = 0;
            this.wiggleTime = 0;
        }
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = super.removeItem(slot, amount);
        // the bowl renders its contents, so every change is synced to clients
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
        return removed;
    }

    @Override
    public int getContainerSize() {
        return 8;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
