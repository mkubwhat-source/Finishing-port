package net.hecco.bountifulfares.mixin.misc;

import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

/**
 * Stonecutting a dyed Bountiful Fares item keeps its dye color on the result.
 * <p>
 * 26.3: {@code setupResultSlot(int)} no longer builds the result inline (the old injection point,
 * a {@code Level.enabledFeatures()} call with the result as a local, is gone). It now does
 * {@code recipe.ifPresentOrElse(holder -> { resultContainer.setRecipeUsed(holder);
 * resultSlot.set(((StonecutterRecipe) holder.value()).assemble(new SingleRecipeInput(container.getItem(0)))); }, ...)}
 * - confirmed via javap, the consumer compiles to {@code lambda$setupResultSlot$0(RecipeHolder)}.
 * The result is therefore modified as the argument of that {@code Slot.set} call.
 */
@Mixin(StonecutterMenu.class)
public abstract class StonecutterScreenHandlerMixin {

    @Shadow
    @Final
    Slot inputSlot;

    @ModifyArg(method = "lambda$setupResultSlot$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;set(Lnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack bountifulfares$populateResult(ItemStack itemStack) {
        if (Objects.equals(BuiltInRegistries.ITEM.getKey(this.inputSlot.getItem().getItem()).getNamespace(),
                BountifulFares.MOD_ID) && this.inputSlot.getItem().has(DataComponents.DYED_COLOR)) {
            itemStack.set(DataComponents.DYED_COLOR, this.inputSlot.getItem().get(DataComponents.DYED_COLOR));
        }
        return itemStack;
    }
}
