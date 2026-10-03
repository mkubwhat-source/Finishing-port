package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires {@link HHEvents#PLAYER_TICK_PRE}/{@code POST} (NeoForge PlayerTickEvent). */
@Mixin(Player.class)
public abstract class PlayerTickMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void hearthandharvest$tickPre(CallbackInfo ci) {
        HHEvents.PLAYER_TICK_PRE.invoker().onTick((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void hearthandharvest$tickPost(CallbackInfo ci) {
        HHEvents.PLAYER_TICK_POST.invoker().onTick((Player) (Object) this);
    }
}
