package alabaster.hearthandharvest.common.block.entity;



import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import alabaster.hearthandharvest.common.block.CrateBlock;
import alabaster.hearthandharvest.common.registry.HHModBlockEntities;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends HHSyncedBlockEntity implements Clearable, Container {

    public static final int SLOTS_PER_HALF = 9;
    public static final int TOTAL_SLOTS = SLOTS_PER_HALF * 2;

    private final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(HHModBlockEntities.CRATE.get(), pos, state);
    }

    private int activeSlots() {
        BlockState state = getBlockState();
        if (state.hasProperty(CrateBlock.TYPE)
                && state.getValue(CrateBlock.TYPE) == SlabType.DOUBLE) {
            return TOTAL_SLOTS;
        }
        return SLOTS_PER_HALF;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index < activeSlots()
                && items.get(index).isEmpty()
                && (stack.is(HHModTags.BOTTLES) || stack.is(HHModTags.CRATEABLE_ITEMS));
    }

    @Override public int getContainerSize() {
        return activeSlots();
    }

    @Override public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack result = ContainerHelper.removeItem(items, index, count);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        boolean wasEmpty = items.get(index).isEmpty();
        boolean willBeEmpty = stack.isEmpty();
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
        if (level != null && !level.isClientSide()) {
            if (wasEmpty && !willBeEmpty) {
                level.playSound(null, worldPosition,
                        HHModSounds.BOTTLE_INSERT.get(),
                        SoundSource.BLOCKS, 0.6f,
                        0.9f + level.getRandom().nextFloat() * 0.2f);
            } else if (!wasEmpty && willBeEmpty) {
                level.playSound(null, worldPosition,
                        HHModSounds.BOTTLE_REMOVE.get(),
                        SoundSource.BLOCKS, 0.6f,
                        0.9f + level.getRandom().nextFloat() * 0.2f);
            }
        }
    }

    @Override public void clearContent() {
        items.clear();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
    }

}