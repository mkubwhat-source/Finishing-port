package net.hecco.bountifulfares.definition.block.entity;

import net.hecco.bountifulfares.definition.block.custom.DyeableCeramicBlock;
import net.hecco.bountifulfares.registry.content.BFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class CeramicDishBlockEntity extends DyeableBlockEntity implements ImplementedInventory {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    public CeramicDishBlockEntity(BlockPos pos, BlockState state) {
        super(BFBlockEntities.CERAMIC_DISH_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    public boolean canInsertItem() {
        return this.getItem(0).isEmpty();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, inventory);
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, inventory);
        super.loadAdditional(input);
    }

    public void insertItem(ItemStack item) {
        assert level != null;
        this.setItem(0, item.copyWithCount(1));
        setChanged();
    }

    public void removeItem() {
        assert level != null;
        this.setItem(0, Items.AIR.getDefaultInstance());
        setChanged();
    }

    @Override
    public void setChanged()
    {
        if (
                this.getLevel() != null &&
                !this.getLevel().isClientSide() &&
                this.getBlockPos() != null
        )
        {
            Level thisworld = this.getLevel();
            if (this.getItem(0).isEmpty()) {
                DyeableCeramicBlock.sendDishClearPayload(
                        (ServerLevel) thisworld, thisworld.getBlockEntity(this.getBlockPos()));
            }
            else {
                DyeableCeramicBlock.sendDishPayload(
                        (ServerLevel) thisworld, thisworld.getBlockEntity(this.getBlockPos()), this.getItem(0));
            }
        }
        super.setChanged();
    }

    public static int getColor(BlockGetter world, BlockPos pos){
        if(world==null){
            return CeramicDishBlockEntity.DEFAULT_COLOR;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof CeramicDishBlockEntity ceramicDishBlockEntity){
            return ceramicDishBlockEntity.color;
        } else {
            return CeramicDishBlockEntity.DEFAULT_COLOR;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return this.inventory.get(0).isEmpty();
    }

    public void setInventory(NonNullList<ItemStack> list) {
        this.inventory.set(0, list.get(0));
    }

    public ItemStack getRenderStack() {
        return this.getItem(0);
    }

    // CeramicDishBlock used to pop its held item in Block.onRemove(...), which is gone in 26.3
    // (confirmed via javap - no such method on Block/BlockBehaviour anymore). The direct
    // replacement is BlockEntity.preRemoveSideEffects(BlockPos, BlockState), overridden here since
    // CeramicDishBlockEntity is not a vanilla Container and so gets no automatic drop behavior
    // (unlike FDCabinetBlock/GristmillBlock's Container-based block entities).
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) {
            net.minecraft.world.level.block.Block.popResource(this.level, pos, this.getItem(0));
        }
    }

//    @Override
//    public void markDirty() {
//        if (!world.isClient()) {
//            PacketByteBuf data = PacketByteBufs.create();
//            data.writeInt(inventory.size());
//            for (int i = 0; i < inventory.size(); i++) {
//                data.writeItemStack(inventory.get(i));
//            }
//            data.writeBlockPos(getPos());
//            PacketByteBuf colorData = PacketByteBufs.create();
//            colorData.writeInt(color);
//            colorData.writeBlockPos(getPos());
//            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
//                ServerPlayNetworking.send(player, BFMessages.CERAMIC_DISH_ITEM_SYNC, data);
//                ServerPlayNetworking.send(player, BFMessages.CERAMIC_COLOR_SYNC, colorData);
//            }
//        }
//        super.markDirty();
//    }
}
