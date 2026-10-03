package net.hecco.bountifulfares.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.registry.content.BFEffects;
import net.hecco.bountifulfares.registry.tags.BFEffectTags;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

/**
 * 26.3: {@code EffectRenderingInventoryScreen} is gone; inventory effect rendering lives in
 * {@link EffectsInInventory}. Its per-effect background is now a single nine-sliced sprite
 * ({@code container/inventory/effect_background}, 32x32 border 4 - pixel-identical to 1.21.1's
 * {@code effect_background_small}, verified against the 1.21.1 client jar) plus a new
 * {@code _ambient} variant, drawn by {@code extractBackground(...)} which no longer receives the
 * {@code MobEffectInstance}. So the current effect and the full effect list are captured from
 * {@code extractEffects(...)} right before each {@code extractBackground} call, and the sprite
 * argument (index 1 - {@code blitSprite} gained a leading {@code RenderPipeline}) is swapped there.
 * The acidified art is the mod's old {@code acidified_effect_background_small} (the exact recolor
 * of the old small background), re-registered as a nine-slice sprite with vanilla's metadata; the
 * old 1.21.1 inventory had no ambient variant, so the acidified look is used for both, exactly
 * as it previously overrode every background.
 */
@Mixin(EffectsInInventory.class)
public abstract class AcidifiedInventoryEffectBackgroundMixin {

    @Unique
    private static final Identifier ACIDIFIED_EFFECT_BACKGROUND = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "container/inventory/acidified_effect_background");

    @Unique private MobEffectInstance bountifulfares$currentEffect;
    @Unique private Collection<MobEffectInstance> bountifulfares$activeEffects;

    @Inject(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/Component;IIZI)I"))
    private void bountifulfares$captureEffect(GuiGraphicsExtractor graphics, Collection<MobEffectInstance> activeEffects, int x0, int yStep, int mouseX, int mouseY, int maxWidth, CallbackInfo ci, @Local MobEffectInstance effect) {
        this.bountifulfares$currentEffect = effect;
        this.bountifulfares$activeEffects = activeEffects;
    }

    @Inject(method = "extractEffects", at = @At("RETURN"))
    private void bountifulfares$clearEffect(CallbackInfo ci) {
        this.bountifulfares$currentEffect = null;
        this.bountifulfares$activeEffects = null;
    }

    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), index = 1)
    private Identifier bountifulfares$acidicBackgroundOverlay(Identifier sprite) {
        MobEffectInstance effect = this.bountifulfares$currentEffect;
        Collection<MobEffectInstance> effects = this.bountifulfares$activeEffects;
        if (effect == null || effects == null || !Services.PLATFORM.get().getBoolConfigValue("acidifiedEffectIconEffects")) {
            return sprite;
        }
        boolean acidic = false;
        for (MobEffectInstance instance : effects) {
            if (instance.getEffect().equals(BFEffects.ACIDIC)) {
                acidic = true;
                break;
            }
        }
        if (acidic && effect.getEffect().value() != BFEffects.ACIDIC.value() && !effect.getEffect().is(BFEffectTags.ACIDIC_BLACKLIST)) {
            return ACIDIFIED_EFFECT_BACKGROUND;
        }
        return sprite;
    }
}
