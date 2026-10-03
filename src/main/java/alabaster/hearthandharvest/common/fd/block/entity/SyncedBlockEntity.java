package alabaster.hearthandharvest.common.fd.block.entity;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Simple BlockEntity with networking boilerplate.
 */
public class SyncedBlockEntity extends BlockEntity
{
	public SyncedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}
//
//	@Override
//	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//		load(pkt.getTag());
//	}

	/** Hearth and Harvest: builds an update tag from a ValueOutput writer (26.3 save API). */
	protected CompoundTag writeTag(HolderLookup.Provider registries, java.util.function.Consumer<net.minecraft.world.level.storage.ValueOutput> writer) {
		try (net.minecraft.util.ProblemReporter.ScopedCollector collector = new net.minecraft.util.ProblemReporter.ScopedCollector(this.problemPath(), com.mojang.logging.LogUtils.getLogger())) {
			net.minecraft.world.level.storage.TagValueOutput output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(collector, registries);
			writer.accept(output);
			return output.buildResult();
		}
	}

	protected void inventoryChanged() {
		super.setChanged();
		if (level != null)
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
	}
}
