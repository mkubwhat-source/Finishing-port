package net.hecco.bountifulfares.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Appends Bountiful Fares' own splash texts ({@code texts/splashes.txt}) to vanilla's.
 * <p>
 * 26.3: {@code SplashManager.prepare} now returns an immutable {@code List<Component>} (each line
 * wrapped by the private static {@code literalSplash(String)}, which applies the splash style)
 * instead of a mutable {@code List<String>}. The 1.21.1 mixin {@code addAll}-ed raw strings into
 * the returned list, which now throws {@code UnsupportedOperationException} and makes the client
 * drop every selected resource pack on startup. It now builds a new list and wraps each line with
 * vanilla's own {@code literalSplash}. The {@code apply} injection that only copied the list into
 * an unused field was removed.
 */
@Mixin(SplashManager.class)
public abstract class SplashTextMixin {
    @Unique
    private static final Identifier BOUNTIFUL_FARES_ID = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "texts/splashes.txt");

    @Shadow
    private static Component literalSplash(String text) {
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"))
    protected List<Component> bountifulFares$splashMix(List<Component> original, @Local(argsOnly = true) ResourceManager resourceManager) {
        List<Component> combined = new ArrayList<>(original);
        try (BufferedReader reader = resourceManager.openAsReader(BOUNTIFUL_FARES_ID)) {
            reader.lines()
                    .map(String::trim)
                    // same filter vanilla applies to its own splashes.txt
                    .filter(splashText -> splashText.hashCode() != 125780783)
                    .map(line -> literalSplash(line))
                    .forEach(combined::add);
        } catch (IOException e) {
            return original;
        }
        return combined;
    }
}
