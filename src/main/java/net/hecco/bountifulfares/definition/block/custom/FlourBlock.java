package net.hecco.bountifulfares.definition.block.custom;

import com.mojang.serialization.MapCodec;
import net.hecco.bountifulfares.registry.content.BFParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public class FlourBlock extends FallingBlock {
    public FlourBlock(Properties properties) {
        super(properties);
    }

    // Block.spawnDestroyParticles dropped its Player param in 26.3 (confirmed via javap on Block).
    @Override
    public void spawnDestroyParticles(Level level, BlockPos pos, BlockState state) {
        for (int i = 0; i < level.getRandom().nextIntBetweenInclusive(10, 20); i++) {
            float x = pos.getX() + (level.getRandom().nextFloat() - 0.5f);
            float y = pos.getY() + (level.getRandom().nextFloat() - 0.5f);
            float z = pos.getZ() + (level.getRandom().nextFloat() - 0.5f);
            level.addParticle(BFParticles.FLOUR_CLOUD.get(), x, y, z, (x - pos.getX()) / 4, Math.abs(y - pos.getY()) / 4, (z - pos.getZ()) / 4);
        }
        super.spawnDestroyParticles(level, pos, state);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        for (int i = 0; i < player.level().getRandom().nextIntBetweenInclusive(1, 3); i++) {
            float x = pos.getX() + (player.level().getRandom().nextFloat() - 0.5f);
            float y = pos.getY() + (player.level().getRandom().nextFloat() - 0.5f);
            float z = pos.getZ() + (player.level().getRandom().nextFloat() - 0.5f);
            player.level().addParticle(BFParticles.FLOUR_CLOUD.get(), x, y, z, (x - pos.getX()) / 4, Math.abs(y - pos.getY()) / 4, (z - pos.getZ()) / 4);
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    // onExplosionHit's Level parameter narrowed to ServerLevel in 26.3 (confirmed via javap on
    // BlockBehaviour - same fix already applied to InfusedCandleBlock in an earlier session).
    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        for (int i = 0; i < level.getRandom().nextIntBetweenInclusive(50, 100); i++) {
            float x = pos.getX() + (level.getRandom().nextFloat() - 0.5f);
            float y = pos.getY() + (level.getRandom().nextFloat() - 0.5f);
            float z = pos.getZ() + (level.getRandom().nextFloat() - 0.5f);
            level.addParticle(BFParticles.FLOUR_CLOUD.get(), x, y, z, (x - pos.getX()) / 2, Math.abs(y - pos.getY()) / 2, (z - pos.getZ()) / 2);
        }
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    // FallingBlock.getDustColor(...) is a newly-added abstract method in 26.3 - see the identical
    // note on FruitBlock.
    @Override
    public int getDustColor(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getMapColor(world, pos).col;
    }
}
