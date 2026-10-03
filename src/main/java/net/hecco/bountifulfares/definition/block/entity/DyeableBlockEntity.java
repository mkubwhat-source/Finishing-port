package net.hecco.bountifulfares.definition.block.entity;

import net.hecco.bountifulfares.definition.block.custom.DyeableCeramicBlock;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public abstract class DyeableBlockEntity extends BlockEntity {
    public DyeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static final int DEFAULT_COLOR = ARGB.opaque(16777215);
    public int color = DEFAULT_COLOR;

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (color != DEFAULT_COLOR) {
            output.putInt("color", color);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
        if (color != DEFAULT_COLOR) {
            componentMapBuilder.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        }
        super.collectImplicitComponents(componentMapBuilder);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        if (components.get(DataComponents.DYED_COLOR) == null) {
            color = DEFAULT_COLOR;
        } else {
            color = components.get(DataComponents.DYED_COLOR).rgb();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int stored = input.getIntOr("color", 0);
        color = stored == 0 ? DEFAULT_COLOR : stored;
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
            DyeableCeramicBlock.sendColorPayload(
                    (ServerLevel) thisworld, thisworld.getBlockEntity(this.getBlockPos()), this.color);
        }
        super.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    public static int getColor(BlockGetter world, BlockPos pos){
        if(world==null){
            return DyeableBlockEntity.DEFAULT_COLOR;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof DyeableBlockEntity ceramicTilesBlockEntity){
            return ceramicTilesBlockEntity.color;
        } else if(blockEntity instanceof CeramicChestBlockEntity entity){
            return entity.color;
        } else {
            return DyeableBlockEntity.DEFAULT_COLOR;
        }
    }

//    @Override
////        if (!world.isClient()) {
//            PacketByteBuf data = PacketByteBufs.create();
//            data.writeInt(color);
//            data.writeBlockPos(getPos());
////            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, getPos())) {
////                ServerPlayNetworking.send(player, BFMessages.CERAMIC_COLOR_SYNC, data);
////            }
//        }
//        super.markDirty();
////    }
//
}
