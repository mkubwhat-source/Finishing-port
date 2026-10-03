package com.sidden.flavored.block.entity;

import com.mojang.serialization.Codec;
import com.sidden.flavored.block.OvenBlock;
import com.sidden.flavored.menu.OvenMenu;
import com.sidden.flavored.recipe.BakingRecipe;
import com.sidden.flavored.registry.FlavoredBlockEntities;
import com.sidden.flavored.registry.FlavoredItemTags;
import com.sidden.flavored.registry.FlavoredRecipeTypes;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The oven: a furnace with a 3x3 shaped input grid (slots 0-8), fuel (9) and result (10). Uses
 * any furnace fuel (26.3: the {@code minecraft:cooking_fuel} item component, resolved exactly as
 * {@code AbstractFurnaceBlockEntity.getBurnDuration} does). Awards baking recipe experience like a
 * furnace when the result is taken (OvenResultSlot) or the oven is broken.
 */
public class OvenBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    protected static final int SLOT_INPUT_START = 0;
    protected static final int SLOT_INPUT_END = 9;
    public static final int SLOT_FUEL = 9;
    public static final int SLOT_RESULT = 10;
    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT, SLOT_FUEL};
    private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_FUEL};
    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_BAKING_PROGRESS = 2;
    public static final int DATA_BAKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    protected NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);
    int litTime;
    int litDuration;
    int bakingProgress;
    int bakingTotalTime;
    protected final ContainerData dataAccess;
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<CraftingInput, BakingRecipe> quickCheck = RecipeManager.createCheck(FlavoredRecipeTypes.OVEN_TYPE.get());

    public OvenBlockEntity(BlockPos pos, BlockState blockState) {
        super(FlavoredBlockEntities.OVEN.get(), pos, blockState);
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_LIT_TIME -> litDuration > 32767 ? Mth.floor((double) litTime / litDuration * 32767.0) : litTime;
                    case DATA_LIT_DURATION -> Math.min(litDuration, 32767);
                    case DATA_BAKING_PROGRESS -> bakingProgress;
                    case DATA_BAKING_TOTAL_TIME -> bakingTotalTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case DATA_LIT_TIME -> litTime = value;
                    case DATA_LIT_DURATION -> litDuration = value;
                    case DATA_BAKING_PROGRESS -> bakingProgress = value;
                    case DATA_BAKING_TOTAL_TIME -> bakingTotalTime = value;
                }
            }

            @Override
            public int getCount() {
                return NUM_DATA_VALUES;
            }
        };
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.litTime = input.getIntOr("BurnTime", 0);
        this.bakingProgress = input.getIntOr("BakeTime", 0);
        this.bakingTotalTime = input.getIntOr("BakeTimeTotal", 0);
        // 1.21.1 recomputed this from the fuel slot on load; saved now (it needs a server level).
        this.litDuration = input.getIntOr("BurnTimeTotal", this.litTime);
        this.recipesUsed.clear();
        this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("BurnTime", this.litTime);
        output.putInt("BurnTimeTotal", this.litDuration);
        output.putInt("BakeTime", this.bakingProgress);
        output.putInt("BakeTimeTotal", this.bakingTotalTime);
        ContainerHelper.saveAllItems(output, this.items);
        output.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.oven");
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, OvenBlockEntity oven) {
        boolean wasLit = oven.isLit();
        boolean changed = false;
        if (oven.isLit()) {
            --oven.litTime;
        }

        ItemStack fuelStack = oven.items.get(SLOT_FUEL);
        CraftingInput input = oven.createRecipeInput();
        boolean hasFuel = !fuelStack.isEmpty();
        if (!oven.isLit() && (!hasFuel || input.isEmpty())) {
            if (oven.bakingProgress > 0) {
                oven.bakingProgress = Mth.clamp(oven.bakingProgress - BURN_COOL_SPEED, 0, oven.bakingTotalTime);
            }
        } else {
            RecipeHolder<BakingRecipe> recipe = input.isEmpty() ? null : oven.quickCheck.getRecipeFor(input, level).orElse(null);
            int maxStackSize = oven.getMaxStackSize();
            if (!oven.isLit() && canBurn(recipe, oven, maxStackSize)) {
                oven.litTime = oven.getBurnDuration(level, fuelStack);
                oven.litDuration = oven.litTime;
                if (oven.isLit()) {
                    changed = true;
                    ItemStackTemplate remainder = fuelStack.getItem().getCraftingRemainder();
                    fuelStack.shrink(1);
                    if (fuelStack.isEmpty() && remainder != null) {
                        oven.items.set(SLOT_FUEL, remainder.create());
                    }
                }
            }

            if (oven.isLit() && canBurn(recipe, oven, maxStackSize)) {
                ++oven.bakingProgress;
                if (oven.bakingProgress == oven.bakingTotalTime) {
                    oven.bakingProgress = 0;
                    oven.bakingTotalTime = getTotalBakeTime(level, oven);
                    if (burn(recipe, oven, maxStackSize)) {
                        oven.setRecipeUsed(recipe);
                    }
                    changed = true;
                }
            } else {
                oven.bakingProgress = 0;
            }
        }

        if (wasLit != oven.isLit()) {
            changed = true;
            state = state.setValue(OvenBlock.LIT, oven.isLit());
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
        if (changed) {
            setChanged(level, pos, state);
        }
    }

    private static boolean canBurn(@Nullable RecipeHolder<BakingRecipe> recipe, OvenBlockEntity oven, int maxStackSize) {
        CraftingInput input = oven.createRecipeInput();
        if (input.isEmpty() || recipe == null) return false;
        ItemStack resultStack = recipe.value().assemble(input);
        if (resultStack.isEmpty()) return false;
        ItemStack outputStack = oven.items.get(SLOT_RESULT);
        if (outputStack.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) return false;
        int total = outputStack.getCount() + resultStack.getCount();
        return total <= maxStackSize && total <= outputStack.getMaxStackSize() || total <= resultStack.getMaxStackSize();
    }

    private static boolean burn(@Nullable RecipeHolder<BakingRecipe> recipe, OvenBlockEntity oven, int maxStackSize) {
        if (recipe == null || !canBurn(recipe, oven, maxStackSize)) return false;
        ItemStack resultStack = recipe.value().assemble(oven.createRecipeInput());
        ItemStack outputStack = oven.items.get(SLOT_RESULT);
        if (outputStack.isEmpty()) {
            oven.items.set(SLOT_RESULT, resultStack.copy());
        } else if (ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            outputStack.grow(resultStack.getCount());
        }

        for (int slot = SLOT_INPUT_START; slot < SLOT_INPUT_END; slot++) {
            ItemStack stack = oven.items.get(slot);
            if (stack.isEmpty()) continue;
            // 1.21.1 intended bowl-tagged ingredients (stews etc.) to leave their bowl behind, but
            // replaced the stack with a bowl and then shrank it away; the bowl is kept now.
            boolean leavesBowl = stack.is(FlavoredItemTags.BOWLS) && stack.getCount() == 1;
            stack.shrink(1);
            if (leavesBowl) {
                oven.items.set(slot, new ItemStack(Items.BOWL));
            }
        }
        return true;
    }

    protected int getBurnDuration(ServerLevel level, ItemStack fuel) {
        return fuel.isEmpty() ? 0 : ResolvableInt.getFromItem(fuel, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(level), 0);
    }

    public static boolean isFuel(ItemStack stack) {
        return stack.has(DataComponents.COOKING_FUEL);
    }

    private static int getTotalBakeTime(ServerLevel level, OvenBlockEntity oven) {
        CraftingInput input = oven.createRecipeInput();
        return oven.quickCheck.getRecipeFor(input, level).map(recipe -> recipe.value().getBakingTime()).orElse(BURN_TIME_STANDARD);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> SLOTS_FOR_DOWN;
            case UP -> SLOTS_FOR_UP;
            default -> SLOTS_FOR_SIDES;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return !(direction == Direction.DOWN && index == SLOT_FUEL && !stack.is(Items.BUCKET));
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private CraftingInput createRecipeInput() {
        return CraftingInput.of(3, 3, this.items.subList(SLOT_INPUT_START, SLOT_INPUT_END));
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.items.get(index);
        boolean sameItem = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
        this.items.set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        if (index < SLOT_INPUT_END && !sameItem && this.level instanceof ServerLevel serverLevel) {
            this.bakingTotalTime = getTotalBakeTime(serverLevel, this);
            this.bakingProgress = 0;
            this.setChanged();
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new OvenMenu(containerId, inventory, this, this.dataAccess);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return switch (index) {
            case SLOT_RESULT -> false;
            case SLOT_FUEL -> isFuel(stack);
            default -> true;
        };
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            this.recipesUsed.addTo(recipe.id(), 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> items) {
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<RecipeHolder<?>> recipes = this.getRecipesToAwardAndPopExperience(player.level(), player.position());
        player.awardRecipes(recipes);
        for (RecipeHolder<?> recipeHolder : recipes) {
            player.triggerRecipeCrafted(recipeHolder, this.items);
        }
        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
        List<RecipeHolder<?>> list = new ArrayList<>();
        for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> entry : this.recipesUsed.reference2IntEntrySet()) {
            level.recipeAccess().byKey(entry.getKey()).ifPresent(recipe -> {
                list.add(recipe);
                if (recipe.value() instanceof BakingRecipe baking) {
                    createExperience(level, popVec, entry.getIntValue(), baking.getExperience());
                }
            });
        }
        return list;
    }

    private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
        int i = Mth.floor((float) recipeIndex * experience);
        float f = Mth.frac((float) recipeIndex * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }
        ExperienceOrb.award(level, popVec, i);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof ServerLevel serverLevel) {
            this.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
        }
    }

    @Override
    public void fillStackedContents(StackedItemContents helper) {
        for (ItemStack itemstack : this.items) {
            helper.accountStack(itemstack);
        }
    }
}
