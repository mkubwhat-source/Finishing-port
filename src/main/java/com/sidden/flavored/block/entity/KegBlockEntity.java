package com.sidden.flavored.block.entity;

import com.sidden.flavored.block.KegBlock;
import com.sidden.flavored.menu.KegMenu;
import com.sidden.flavored.recipe.FermentingRecipe;
import com.sidden.flavored.recipe.input.FermentingRecipeInput;
import com.sidden.flavored.registry.FlavoredBlockEntities;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import com.sidden.flavored.registry.FlavoredSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The keg: slot 0 ingredient, slot 1 fermenter, slot 2 result. Ferments over 2496 ticks, four
 * times faster when the recipe's fermenter is in slot 1 (which is consumed with the ingredient).
 * <p>
 * Port notes (26.3): 1.21.1 used a NeoForge ItemStackHandler plus two sided IItemHandler
 * capabilities (top: ingredient in/out; other sides: fermenter in, result out). The keg is now a
 * vanilla {@code WorldlyContainer} with exactly those per-face rules, which hoppers (and Fabric's
 * transfer API) use directly. Contents are dropped by the vanilla container removal hook.
 */
public class KegBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    public static final int INPUT_SLOT = 0;
    public static final int FERMENTING_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int MAX_PROGRESS = 2496;
    private static final int[] SLOTS_FOR_UP = {INPUT_SLOT};
    private static final int[] SLOTS_FOR_OTHER = {FERMENTING_SLOT, OUTPUT_SLOT};

    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = MAX_PROGRESS;
    private int particleColor = 0xFFFFFF;

    public KegBlockEntity(BlockPos pos, BlockState state) {
        super(FlavoredBlockEntities.KEG.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> KegBlockEntity.this.progress;
                    case 1 -> KegBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> KegBlockEntity.this.progress = value;
                    case 1 -> KegBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.flavored.keg");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new KegMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot != OUTPUT_SLOT;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_OTHER;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (side == null || side == Direction.UP) {
            return slot == INPUT_SLOT;
        }
        return slot == FERMENTING_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (side == Direction.UP) {
            return slot == INPUT_SLOT;
        }
        return slot == OUTPUT_SLOT;
    }

    @Override
    public void fillStackedContents(StackedItemContents contents) {
        for (ItemStack stack : this.items) {
            contents.accountStack(stack);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("keg.progress", progress);
        output.putInt("keg.particle_color", particleColor);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.progress = input.getIntOr("keg.progress", 0);
        this.particleColor = input.getIntOr("keg.particle_color", 0xFFFFFF);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        Optional<RecipeHolder<FermentingRecipe>> recipe = getCurrentRecipe(serverLevel);
        boolean canFerment = recipe.isPresent() && canOutput(recipe.get().value());

        if (canFerment && !state.getValue(KegBlock.FERMENTING)) {
            this.particleColor = hexToRGB(recipe.get().value().particleColor());
            level.setBlock(pos, state.setValue(KegBlock.FERMENTING, true), 3);
            setChanged(level, pos, state);
        } else if (!canFerment && state.getValue(KegBlock.FERMENTING)) {
            level.setBlock(pos, state.setValue(KegBlock.FERMENTING, false), 3);
            setChanged(level, pos, state);
        }

        if (canFerment) {
            ItemStack fermenterStack = this.items.get(FERMENTING_SLOT);
            this.progress += recipe.get().value().fermenter().test(fermenterStack) ? 4 : 1;
            setChanged(level, pos, state);
            if (this.progress >= this.maxProgress) {
                fermentItem(serverLevel, recipe.get().value());
                this.progress = 0;
            }
        } else {
            this.progress = 0;
        }
    }

    private static int hexToRGB(String hex) {
        try {
            return Integer.parseInt(hex.replace("#", ""), 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    private void fermentItem(ServerLevel level, FermentingRecipe recipe) {
        ItemStack output = recipe.assemble(new FermentingRecipeInput(this.items.get(INPUT_SLOT), this.items.get(FERMENTING_SLOT)));
        ItemStack input = this.items.get(INPUT_SLOT);
        // 26.3: the milk bucket is a plain consumable item now (MilkBucketItem is gone)
        if (input.getItem() instanceof BucketItem || input.is(Items.MILK_BUCKET)) {
            this.items.set(INPUT_SLOT, new ItemStack(Items.BUCKET));
        } else {
            input.shrink(1);
        }
        this.items.get(FERMENTING_SLOT).shrink(1);

        ItemStack current = this.items.get(OUTPUT_SLOT);
        if (current.isEmpty()) {
            this.items.set(OUTPUT_SLOT, output);
        } else {
            current.grow(output.getCount());
        }
        level.playSound(null, getBlockPos(), FlavoredSoundEvents.KEG_FERMENT.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public Optional<RecipeHolder<FermentingRecipe>> getCurrentRecipe(ServerLevel level) {
        return level.recipeAccess().getRecipeFor(FlavoredRecipeTypes.KEG_TYPE.get(),
                new FermentingRecipeInput(this.items.get(INPUT_SLOT), this.items.get(FERMENTING_SLOT)), level);
    }

    private boolean canOutput(FermentingRecipe recipe) {
        ItemStack result = recipe.result();
        ItemStack current = this.items.get(OUTPUT_SLOT);
        // 1.21.1 compared items only and rebuilt the output stack from the item, dropping any
        // result components; results with components now stack only with identical stacks.
        return current.isEmpty() || (ItemStack.isSameItemSameComponents(current, result)
                && current.getCount() + result.getCount() <= current.getMaxStackSize());
    }

    public int getParticleColor() {
        return particleColor;
    }
}
