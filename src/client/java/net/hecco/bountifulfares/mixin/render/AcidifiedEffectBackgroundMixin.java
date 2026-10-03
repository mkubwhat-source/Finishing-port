package net.hecco.bountifulfares.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.registry.content.BFEffects;
import net.hecco.bountifulfares.registry.tags.BFEffectTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 26.3: the HUD status-effect row moved from {@code Gui.renderEffects(GuiGraphics, DeltaTracker)}
 * to {@code Hud.extractEffects(GuiGraphicsExtractor, DeltaTracker)}, and {@code blitSprite} gained
 * a leading {@code RenderPipeline} parameter (so the sprite is now argument index 1). Both
 * background {@code blitSprite(RenderPipeline, Identifier, int, int, int, int)} calls (ambient and
 * normal) are still in that method, the {@code MobEffectInstance} is still a local, and the
 * {@code EFFECT_BACKGROUND_*_SPRITE} constants and HUD sprite art are unchanged (24x24, compared
 * against the 1.21.1 originals) - all confirmed via javap / the jars.
 */
@Mixin(Hud.class)
public abstract class AcidifiedEffectBackgroundMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private static Identifier EFFECT_BACKGROUND_AMBIENT_SPRITE;
    @Shadow @Final private static Identifier EFFECT_BACKGROUND_SPRITE;
    @Unique
    private static final Identifier ACIDFIED_EFFECT_BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "hud/acidified_effect_background");
    @Unique
    private static final Identifier ACIDFIED_EFFECT_BACKGROUND_AMBIENT_TEXTURE = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "hud/acidified_effect_background_ambient");

    @ModifyArg(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), index = 1)
    private Identifier bountifulfares$renderAcidifiedBackgrounds(Identifier sprite, @Local MobEffectInstance effect) {
        if (Services.PLATFORM.get().getBoolConfigValue("acidifiedEffectIconEffects")) {
            Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
            if (collection.stream().map(MobEffectInstance::getEffect).collect(Collectors.toSet()).contains(BFEffects.ACIDIC) && effect.getEffect().value() != BFEffects.ACIDIC.value() && !effect.getEffect().is(BFEffectTags.ACIDIC_BLACKLIST)) {
                if (sprite.equals(EFFECT_BACKGROUND_AMBIENT_SPRITE)) {
                    return ACIDFIED_EFFECT_BACKGROUND_AMBIENT_TEXTURE;
                }
                if (sprite.equals(EFFECT_BACKGROUND_SPRITE)) {
                    return ACIDFIED_EFFECT_BACKGROUND_TEXTURE;
                }
            }
        }
        return sprite;
    }
}
