package net.hecco.bountifulfares.definition.block.entity;

import net.hecco.bountifulfares.definition.block.custom.TrellisBlock;
import net.hecco.bountifulfares.definition.networking.payload.TrellisEmptyPayload;
import net.hecco.bountifulfares.definition.networking.payload.TrellisPlantPayload;
import net.hecco.bountifulfares.registry.content.BFBlockEntities;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TrellisBlockEntity extends BlockEntity {
    private ItemStack plant = ItemStack.EMPTY;
    private int stage = 1;
    public TrellisBlockEntity(BlockPos pos, BlockState blockState) {
        super(BFBlockEntities.TRELLIS_BLOCK_ENTITY.get(), pos, blockState);
    }

    public Item getPlant() {
        if (this.plant != null) {
            return this.plant.getItem();
        } else {
            return null;
        }
    }

    public boolean canPlantOn() {
        return plant == ItemStack.EMPTY;
    }

    public void setPlant(Item seed) {
        this.plant = seed.getDefaultInstance();
        this.stage = 1;
        setChanged();
    }

    public void removePlant() {
        this.plant = ItemStack.EMPTY;
        this.stage = 1;
        setChanged();
    }

    public void setStage(int stage) {
        this.stage = stage;
        setChanged();
    }

    public int getStage() {
        return this.stage;
    }

    @Override
    public void setChanged() {
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            ServerLevel level = (ServerLevel) this.getLevel();
            if (plant != ItemStack.EMPTY) {
                BFNetworkingHelper.sendToPlayersTrackingChunk(level, this.getBlockPos(), new TrellisPlantPayload(this.getBlockPos(), this.plant, this.stage));
            } else {
                BFNetworkingHelper.sendToPlayersTrackingChunk(level, this.getBlockPos(), new TrellisEmptyPayload(this.getBlockPos()));
            }
        }
        super.setChanged();

    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (plant != ItemStack.EMPTY) {
            output.store("Plant", ItemStack.OPTIONAL_CODEC, plant);
        }
        if (TrellisBlock.CROPS.containsKey(plant.getItem())) {
            output.putInt("Stage", stage);
        }
        super.saveAdditional(output);
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

    @Override
    protected void loadAdditional(ValueInput input) {
        plant = input.read("Plant", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        stage = input.getIntOr("Stage", stage);
        super.loadAdditional(input);
    }
}
