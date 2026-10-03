package alabaster.hearthandharvest.common.fd.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.stats.RecipeBookSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookSettings.TypeSettings.class)
public interface RecipeBookSettingsTypeSettingsInvoker {
    @Invoker("codec")
    static MapCodec<RecipeBookSettings.TypeSettings> hearthandharvest$invokeCodec(String openField, String filteringField) {
        throw new AssertionError();
    }
}
