package alabaster.hearthandharvest.client.mixin;

import alabaster.hearthandharvest.common.registry.HHModBlocks;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Hay and straw rugs are thin carpets; their block-breaking crack overlay is hidden
 * (1.21.1 cancelled BlockRenderDispatcher#renderBreakingTexture; 26.3 extracts breaking
 * states into LevelRenderState, so they are simply not added).
 */
@Mixin(LevelExtractor.class)
public abstract class HideBreakProgressMixin {
    @WrapWithCondition(method = "extractBlockDestroyAnimation",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean hearthandharvest$hideRugDamage(List<Object> list, Object element) {
        if (element instanceof BlockBreakingRenderState state) {
            return !(state.blockState().is(HHModBlocks.HAY_RUG.get()) || state.blockState().is(HHModBlocks.STRAW_RUG.get()));
        }
        return true;
    }
}
