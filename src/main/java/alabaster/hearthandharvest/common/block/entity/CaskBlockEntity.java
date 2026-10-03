package alabaster.hearthandharvest.common.block.entity;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.CaskBlock;
import alabaster.hearthandharvest.common.block.entity.container.CaskMenu;
import alabaster.hearthandharvest.common.block.entity.inventory.CaskItemHandler;
import alabaster.hearthandharvest.common.crafting.CaskRecipe;
import alabaster.hearthandharvest.common.registry.HHModRecipeTypes;
import alabaster.hearthandharvest.common.registry.HHModBlockEntities;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import alabaster.hearthandharvest.platform.inventory.IItemHandler;
import alabaster.hearthandharvest.platform.inventory.ItemStackHandler;
import alabaster.hearthandharvest.platform.inventory.RecipeWrapper;
import alabaster.hearthandharvest.common.fd.block.entity.SyncedBlockEntity;
import alabaster.hearthandharvest.common.fd.item.component.ItemStackWrapper;
import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import alabaster.hearthandharvest.common.fd.utility.ItemUtils;

import org.jspecify.annotations.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class CaskBlockEntity extends SyncedBlockEntity implements ExtendedMenuProvider<BlockPos>, Nameable, RecipeCraftingHolder, Clearable
{
    public static final int INPUT_SLOTS = 4;
    public static final int OUTPUT_SLOTS = 4;
    public static final int FIRST_OUTPUT_SLOT = INPUT_SLOTS;
    public static final int INVENTORY_SIZE = INPUT_SLOTS + OUTPUT_SLOTS;

    private final ItemStackHandler inventory;
    private final IItemHandler inputHandler;
    private final IItemHandler outputHandler;
    private final Map<Direction, IItemHandler> sideHandlers = new EnumMap<>(Direction.class);

    private int ageTime;
    private int ageTimeTotal;
    private boolean sealed;
    private boolean aging;
    private int lastComparatorOutput;
    private Component customName;

    protected final ContainerData cookingPotData;
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    private final Object2IntOpenHashMap<ResourceKey<Recipe<?>>> usedRecipeTracker;

    private final RecipeManager.CachedCheck<RecipeWrapper, CaskRecipe> quickCheck;

    public CaskBlockEntity(BlockPos pos, BlockState state) {
        super(HHModBlockEntities.CASK.get(), pos, state);
        this.inventory = createHandler();
        this.inputHandler = new CaskItemHandler(inventory, Direction.UP, this::isSealed);
        this.outputHandler = new CaskItemHandler(inventory, Direction.DOWN, this::isSealed);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            this.sideHandlers.put(d, new CaskItemHandler(inventory, d, this::isSealed));
        }
        this.cookingPotData = createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.quickCheck = RecipeManager.createCheck(HHModRecipeTypes.AGING.get());
    }

    public static void init() {
        ItemStorage.SIDED.registerForBlockEntity((be, context) -> {
            if (context == null || context == Direction.UP) return be.inputHandler;
            if (context == Direction.DOWN) return be.outputHandler;
            return be.sideHandlers.get(context);
        }, HHModBlockEntities.CASK.get());
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventory.deserialize(input.childOrEmpty("Inventory"));
        ageTime = input.getIntOr("AgeTime", 0);
        ageTimeTotal = input.getIntOr("AgeTimeTotal", 0);
        sealed = input.getBooleanOr("Sealed", false);
        aging = input.getBooleanOr("Aging", false);
        customName = input.read("CustomName", ComponentSerialization.CODEC).orElse(null);
        usedRecipeTracker.clear();
        usedRecipeTracker.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Collections.emptyMap()));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("AgeTime", ageTime);
        output.putInt("AgeTimeTotal", ageTimeTotal);
        output.putBoolean("Sealed", sealed);
        output.putBoolean("Aging", aging);
        output.storeNullable("CustomName", ComponentSerialization.CODEC, customName);
        inventory.serialize(output.child("Inventory"));
        output.store("RecipesUsed", RECIPES_USED_CODEC, usedRecipeTracker);
    }

    private void writeItems(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output.child("Inventory"));
        output.putBoolean("Sealed", sealed);
        output.putBoolean("Aging", aging);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            // A sealed cask keeps its contents in the dropped item (see CaskBlock#getDrops).
            if (!isSealed()) Containers.dropContents(level, pos, getDroppableInventory());
            getUsedRecipesAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
        super.preRemoveSideEffects(pos, state);
    }

    public static void cookingTick(Level level, BlockPos pos, BlockState state, CaskBlockEntity caskBlock) {
        if (level.isClientSide()) return;

        caskBlock.syncSealedState();

        boolean didInventoryChange = false;
        int previousAgeTime = caskBlock.ageTime;
        int previousAgeTimeTotal = caskBlock.ageTimeTotal;

        if (!caskBlock.isSealed()) {
            caskBlock.ageTime = 0;
        } else if (caskBlock.hasInput()) {
            Optional<RecipeHolder<CaskRecipe>> recipe = caskBlock.getMatchingRecipe(new RecipeWrapper(caskBlock.inventory));
            if (recipe.isPresent() && caskBlock.canCook(recipe.get().value())) {
                didInventoryChange = caskBlock.processCooking(recipe.get(), caskBlock);
            } else {
                caskBlock.ageTime = 0;
            }
        } else {
            caskBlock.ageTime = 0;
        }

        boolean nowAging = caskBlock.ageTime > previousAgeTime;
        if (nowAging != caskBlock.aging) {
            caskBlock.aging = nowAging;
            didInventoryChange = true;
        }

        if (didInventoryChange) {
            caskBlock.inventoryChanged();
        } else if (caskBlock.ageTime != previousAgeTime || caskBlock.ageTimeTotal != previousAgeTimeTotal) {
            caskBlock.setChanged();
        }

        int comparator = caskBlock.getComparatorOutput();
        if (comparator != caskBlock.lastComparatorOutput) {
            caskBlock.lastComparatorOutput = comparator;
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
    }

    private Optional<RecipeHolder<CaskRecipe>> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (!(level instanceof ServerLevel serverLevel)) return Optional.empty();
        return hasInput() ? quickCheck.getRecipeFor(inventoryWrapper, serverLevel) : Optional.empty();
    }

    private boolean hasInput() {
        for (int i = 0; i < INPUT_SLOTS; ++i) {
            if (!inventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    protected boolean canCook(CaskRecipe recipe) {
        if (hasInput()) {
            ItemStack resultStack = recipe.getOutput();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                return findOutputSlot(resultStack) >= 0;
            }
        } else {
            return false;
        }
    }

    public boolean isSealed() {
        return sealed || isPowered();
    }

    public boolean isPowered() {
        return level != null && level.hasNeighborSignal(worldPosition);
    }

    public void setSealed(boolean sealed) {
        if (!sealed && isPowered()) return;
        if (!sealed) this.ageTime = 0;
        this.sealed = sealed;
        syncSealedState();
        setChanged();
    }

    private void syncSealedState() {
        if (level == null || level.isClientSide()) return;
        BlockState state = getBlockState();
        boolean shouldSeal = isSealed();
        if (state.hasProperty(CaskBlock.SEALED) && state.getValue(CaskBlock.SEALED) != shouldSeal) {
            level.setBlock(worldPosition, state.setValue(CaskBlock.SEALED, shouldSeal), Block.UPDATE_ALL);
        }
    }

    public boolean isAging() {
        return aging;
    }

    public int getRemainingSeconds() {
        if (ageTimeTotal <= 0 || ageTime <= 0) return 0;
        int speed = agingSpeed();
        return (int) Math.min(Short.MAX_VALUE, ((long) (ageTimeTotal - ageTime) + speed - 1) / speed / 20L);
    }

    public int getComparatorOutput() {
        if (ageTimeTotal <= 0 || ageTime <= 0) return 0;
        return 1 + (int) Math.min(14L, (long) ageTime * 14L / ageTimeTotal);
    }

    public boolean isProcessingRecipe() {
        return ageTime > 0 && hasInput();
    }

    public int getCurrentLightLevel() {
        return level != null ? level.getBrightness(LightLayer.SKY, worldPosition) : 7;
    }

    public boolean processCooking(RecipeHolder<CaskRecipe> recipe, CaskBlockEntity cask) {
        if (level == null) return false;

        if (!advanceAging(recipe.value().getCookTime())) {
            return false;
        }

        ItemStack resultStack = recipe.value().getOutput();
        int slot = findOutputSlot(resultStack);
        if (slot >= 0) {
            ItemStack stored = inventory.getStackInSlot(slot);
            if (stored.isEmpty()) {
                inventory.setStackInSlot(slot, resultStack.copy());
            } else {
                stored.grow(resultStack.getCount());
            }
        }
        cask.setRecipeUsed(recipe);

        for (int i = 0; i < INPUT_SLOTS; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (!slotStack.isEmpty())
                slotStack.shrink(1);
        }
        return true;
    }

    private static final int PROGRESS_UNITS_PER_BASE_TICK = 2;

    private int agingSpeed() {
        int lightLevel = getCurrentLightLevel();
        if (lightLevel <= 5) return 4;
        if (lightLevel <= 10) return 2;
        return 1;
    }

    private boolean advanceAging(int baseTime) {
        ageTimeTotal = Math.max(1, baseTime) * PROGRESS_UNITS_PER_BASE_TICK;
        ageTime += agingSpeed();
        if (ageTime < ageTimeTotal) {
            return false;
        }
        ageTime = 0;
        return true;
    }

    protected void ejectIngredientRemainder(ItemStack remainderStack) {
        Direction direction = getBlockState().getValue(CaskBlock.FACING).getCounterClockWise();
        double x = worldPosition.getX() + 0.5 + (direction.getStepX() * 0.25);
        double y = worldPosition.getY() + 0.7;
        double z = worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.25);
        ItemUtils.spawnItemEntity(level, remainderStack, x, y, z,
                direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F);
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceKey<Recipe<?>> recipeID = recipe.id();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> items) {
        if (player instanceof ServerPlayer serverPlayer) {
            List<RecipeHolder<?>> usedRecipes = getUsedRecipesAndPopExperience(serverPlayer.level(), player.position());
            serverPlayer.awardRecipes(usedRecipes);
        }
        usedRecipeTracker.clear();
    }

    public List<RecipeHolder<?>> getUsedRecipesAndPopExperience(ServerLevel serverLevel, Vec3 pos) {
        List<RecipeHolder<?>> list = Lists.newArrayList();
        for (Object2IntMap.Entry<ResourceKey<Recipe<?>>> entry : usedRecipeTracker.object2IntEntrySet()) {
            serverLevel.recipeAccess().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience(serverLevel, pos, entry.getIntValue(), ((CaskRecipe) recipe.value()).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack getMeal() {
        for (int slot = FIRST_OUTPUT_SLOT; slot < INVENTORY_SIZE; ++slot) {
            ItemStack stored = inventory.getStackInSlot(slot);
            if (!stored.isEmpty()) return stored;
        }
        return ItemStack.EMPTY;
    }

    private int findOutputSlot(ItemStack result) {
        for (int slot = FIRST_OUTPUT_SLOT; slot < INVENTORY_SIZE; ++slot) {
            ItemStack stored = inventory.getStackInSlot(slot);
            if (stored.isEmpty()) return slot;
            if (ItemStack.isSameItemSameComponents(stored, result)
                    && stored.getCount() + result.getCount() <= Math.min(inventory.getSlotLimit(slot), result.getMaxStackSize())) {
                return slot;
            }
        }
        return -1;
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.add(inventory.getStackInSlot(i));
        }
        return drops;
    }

    @Override
    public Component getName() {
        return customName != null ? customName : Component.translatable("container.hearthandharvest.cask");
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return customName;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
        return new CaskMenu(id, player, this, cookingPotData);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return writeTag(registries, this::writeItems);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return getBlockPos();
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        this.customName = componentInput.get(DataComponents.CUSTOM_NAME);
        getInventory().setStackInSlot(FIRST_OUTPUT_SLOT, componentInput.getOrDefault(HHModDataComponents.MEAL.get(), ItemStackWrapper.EMPTY).getStack());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.customName);
        if (!getMeal().isEmpty()) {
            components.set(HHModDataComponents.MEAL.get(), new ItemStackWrapper(getMeal()));
        }
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("CustomName");
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(INVENTORY_SIZE)
        {
            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }
        };
    }

    public static final int PROGRESS_SCALE = 1000;

    private ContainerData createIntArray() {
        return new ContainerData()
        {
            @Override
            public int get(int index) {
                int total = CaskBlockEntity.this.ageTimeTotal;
                return switch (index) {
                    case 0 -> total <= 0 ? 0 : (int) Math.min(PROGRESS_SCALE, (long) CaskBlockEntity.this.ageTime * PROGRESS_SCALE / total);
                    case 1 -> total <= 0 ? 0 : PROGRESS_SCALE;
                    case 2 -> CaskBlockEntity.this.ageTime % Short.MAX_VALUE;
                    case 3 -> CaskBlockEntity.this.getRemainingSeconds();
                    case 4 -> CaskBlockEntity.this.isSealed() ? 1 : 0;
                    case 5 -> CaskBlockEntity.this.isPowered() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CaskBlockEntity.this.ageTime = value;
                    case 1 -> CaskBlockEntity.this.ageTimeTotal = value;
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventory.getSlotCount(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}