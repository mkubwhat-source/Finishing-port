package alabaster.hearthandharvest.common.fd.block;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import alabaster.hearthandharvest.common.fd.network.RichSoilBoostParticlesPayload;

public class RichSoilBlock extends Block
{
	public RichSoilBlock(Properties properties) {
		super(properties);
	}

    public static void init() {
        BlockTransformerHelper.registerTilling(HHModBlocks.RICH_SOIL.get(), HHModBlocks.RICH_SOIL_FARMLAND.get());
    }

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// Hearth and Harvest: FD's mushroom colonies are not part of this bundle, so rich soil no
		// longer turns mushrooms into colonies; it only boosts plants.
		tryBoostingPlantsAboveAndBelow(level, pos, random);
	}

	public static void tryBoostingPlantsAboveAndBelow(ServerLevel level, BlockPos pos, RandomSource random) {
		if (Config.RICH_SOIL_BOOST_CHANCE.get() == 0.0 || random.nextFloat() > Config.RICH_SOIL_BOOST_CHANCE.get()) {
			return;
		}

		BlockPos abovePos = pos.above();
		BlockState aboveState = level.getBlockState(abovePos);
		if (!aboveState.is(FDTags.Blocks.PLANTED_FROM_BELOW) && boostPlant(aboveState, abovePos, level, BonemealSource.INTERACTION)) {
			return;
		}

		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);
		if (belowState.is(FDTags.Blocks.PLANTED_FROM_BELOW)) {
			boostPlant(belowState, belowPos, level, BonemealSource.INTERACTION);
		}
	}

	public static boolean boostPlant(BlockState plantState, BlockPos plantPos, ServerLevel level, BonemealSource source) {
		if (plantState.is(FDTags.Blocks.UNAFFECTED_BY_RICH_SOIL)) {
			return false;
		}
		if (plantState.getBlock() instanceof BonemealableBlock growable) {
			if (growable.isValidBonemealTarget(level, plantPos, plantState, source)) {
				growable.performBonemeal(level, level.getRandom(), plantPos, plantState, source);
				for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(level.getChunkAt(plantPos).getPos(), false)) {
					ServerPlayNetworking.send(player, new RichSoilBoostParticlesPayload(plantPos));
				}
				return true;
			}
		}
		return false;
	}

}
