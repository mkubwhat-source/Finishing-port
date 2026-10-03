package alabaster.hearthandharvest.common.fd.mixin;

import alabaster.hearthandharvest.common.fd.refabricated.HHRecipeBookTypes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/** Saves the open/filtering state of HH's recipe books with the player's other recipe book settings (from FarmersDelightRefabricated). */
@Mixin(RecipeBookSettings.class)
public class RecipeBookSettingsMixin {
    @Unique private RecipeBookSettings.TypeSettings hearthandharvest$cooking = RecipeBookSettings.TypeSettings.DEFAULT;
    @Unique private RecipeBookSettings.TypeSettings hearthandharvest$fermenting = RecipeBookSettings.TypeSettings.DEFAULT;
    @Unique private RecipeBookSettings.TypeSettings hearthandharvest$aging = RecipeBookSettings.TypeSettings.DEFAULT;

    @Unique
    private static RecipeBookSettingsMixin hearthandharvest$self(RecipeBookSettings settings) {
        return (RecipeBookSettingsMixin) (Object) settings;
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<RecipeBookSettings> hearthandharvest$modifyCodec(MapCodec<RecipeBookSettings> original) {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                original.forGetter(Function.identity()),
                RecipeBookSettingsTypeSettingsInvoker.hearthandharvest$invokeCodec("isHearthAndHarvestCookingGuiOpen", "isHearthAndHarvestCookingFilteringCraftable")
                        .forGetter(s -> hearthandharvest$self(s).hearthandharvest$cooking),
                RecipeBookSettingsTypeSettingsInvoker.hearthandharvest$invokeCodec("isHearthAndHarvestFermentingGuiOpen", "isHearthAndHarvestFermentingFilteringCraftable")
                        .forGetter(s -> hearthandharvest$self(s).hearthandharvest$fermenting),
                RecipeBookSettingsTypeSettingsInvoker.hearthandharvest$invokeCodec("isHearthAndHarvestAgingGuiOpen", "isHearthAndHarvestAgingFilteringCraftable")
                        .forGetter(s -> hearthandharvest$self(s).hearthandharvest$aging)
        ).apply(inst, (settings, cooking, fermenting, aging) -> {
            RecipeBookSettingsMixin self = hearthandharvest$self(settings);
            self.hearthandharvest$cooking = cooking;
            self.hearthandharvest$fermenting = fermenting;
            self.hearthandharvest$aging = aging;
            return settings;
        }));
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private RecipeBookSettings hearthandharvest$copy(RecipeBookSettings copy) {
        RecipeBookSettingsMixin other = hearthandharvest$self(copy);
        other.hearthandharvest$cooking = this.hearthandharvest$cooking;
        other.hearthandharvest$fermenting = this.hearthandharvest$fermenting;
        other.hearthandharvest$aging = this.hearthandharvest$aging;
        return copy;
    }

    @Inject(method = "replaceFrom", at = @At("TAIL"))
    private void hearthandharvest$replaceFrom(RecipeBookSettings from, CallbackInfo ci) {
        RecipeBookSettingsMixin other = hearthandharvest$self(from);
        this.hearthandharvest$cooking = other.hearthandharvest$cooking;
        this.hearthandharvest$fermenting = other.hearthandharvest$fermenting;
        this.hearthandharvest$aging = other.hearthandharvest$aging;
    }

    @Inject(method = "getSettings", at = @At("HEAD"), cancellable = true)
    private void hearthandharvest$getSettings(RecipeBookType type, CallbackInfoReturnable<RecipeBookSettings.TypeSettings> cir) {
        if (type == HHRecipeBookTypes.COOKING) cir.setReturnValue(hearthandharvest$cooking);
        else if (type == HHRecipeBookTypes.FERMENTING) cir.setReturnValue(hearthandharvest$fermenting);
        else if (type == HHRecipeBookTypes.AGING) cir.setReturnValue(hearthandharvest$aging);
    }

    @Inject(method = "updateSettings", at = @At("HEAD"), cancellable = true)
    private void hearthandharvest$updateSettings(RecipeBookType type, UnaryOperator<RecipeBookSettings.TypeSettings> updater, CallbackInfo ci) {
        if (type == HHRecipeBookTypes.COOKING) this.hearthandharvest$cooking = updater.apply(hearthandharvest$cooking);
        else if (type == HHRecipeBookTypes.FERMENTING) this.hearthandharvest$fermenting = updater.apply(hearthandharvest$fermenting);
        else if (type == HHRecipeBookTypes.AGING) this.hearthandharvest$aging = updater.apply(hearthandharvest$aging);
        else return;
        ci.cancel();
    }
}
