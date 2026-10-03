package alabaster.hearthandharvest.common.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Consumer;

public abstract class HHSyncedBlockEntity extends BlockEntity {

    protected HHSyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return writeTag(registries, this::saveAdditional);
    }

    /** Builds a tag from a 26.3 {@link ValueOutput} writer (for update tags). */
    protected CompoundTag writeTag(HolderLookup.Provider registries, Consumer<ValueOutput> writer) {
        try (ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(problemPath(), LogUtils.getLogger())) {
            TagValueOutput output = TagValueOutput.createWithContext(collector, registries);
            writer.accept(output);
            return output.buildResult();
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void markUpdated() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}
