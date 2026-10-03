package net.hecco.bountifulfares.mixin.gameplay;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;

import net.hecco.bountifulfares.registry.content.BFItems;
import net.hecco.bountifulfares.registry.content.BFSounds;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin {

    @Unique @Final
    private static final ItemParticleOption BOUNTIFULFARES$POP = new ItemParticleOption(ParticleTypes.ITEM, BFItems.POPPED_MAIZE.get());

    // 26.3: cookTick(Level, BlockPos, BlockState, CampfireBlockEntity) became
    // cookTick(ServerLevel, BlockPos, BlockState, CampfireBlockEntity, RecipeManager.CachedCheck)
    // and its locals were reshuffled (javap LocalVariableTable: itemStack, input, result), so the
    // positional CAPTURE_FAILSOFT capture no longer matched. The input stack is taken with a
    // MixinExtras @Local instead (first ItemStack local; the cooked result is the second).
    @Inject(method = "cookTick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static void bountifulfares$addMaizePoppingSound(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache, CallbackInfo ci, @Local(ordinal = 0) ItemStack itemStack) {
        if (itemStack.is(BFItems.MAIZE_SEEDS.get())) {
            level.playSound(null, pos, BFSounds.POPPED_MAIZE_POP.get(), SoundSource.BLOCKS, 1.0f, 1.0f + level.getRandom().nextFloat() / 3);
            level.sendParticles(
                    BOUNTIFULFARES$POP,
                    Vec3.atCenterOf(pos).x(),
                    Vec3.atCenterOf(pos).y() + 0.2D,
                    Vec3.atCenterOf(pos).z(),
                    8,
                    ((double) level.getRandom().nextFloat() - 0.5),
                    ((double) level.getRandom().nextFloat() - 0.5),
                    ((double) level.getRandom().nextFloat() - 0.5),
                    0.1
            );
        }
    }
}