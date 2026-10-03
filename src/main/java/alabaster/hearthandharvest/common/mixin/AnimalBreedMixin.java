package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Fires {@link HHEvents#BABY_SPAWN} (NeoForge BabyEntitySpawnEvent) in
 * {@code Animal.spawnChildFromBreeding}, before the parents' love state is reset, so the player who
 * caused the breeding is still known.
 */
@Mixin(Animal.class)
public abstract class AnimalBreedMixin {
    @Inject(method = "spawnChildFromBreeding", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;finalizeSpawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;Lnet/minecraft/world/entity/AgeableMob;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void hearthandharvest$babySpawn(ServerLevel level, Animal partner, CallbackInfo ci, AgeableMob offspring) {
        Animal self = (Animal) (Object) this;
        ServerPlayer cause = self.getLoveCause();
        if (cause == null) cause = partner.getLoveCause();
        HHEvents.BABY_SPAWN.invoker().onBabySpawn(self, partner, offspring, cause);
    }
}
