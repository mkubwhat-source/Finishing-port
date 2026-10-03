package net.hecco.bountifulfares.mixin.misc;

import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin extends AbstractContainerScreen<StonecutterMenu> {

    public StonecutterScreenMixin(StonecutterMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    // 26.3: renderRecipes(GuiGraphics, ...) -> extractRecipes(GuiGraphicsExtractor, ...), and the
    // per-recipe icon is drawn with GuiGraphicsExtractor.item(ItemStack, int, int) on a stack
    // freshly resolved from the recipe's SlotDisplay (so mutating it is still safe) - javap.
    @ModifyArg(method = "extractRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/item/ItemStack;II)V"))
    public ItemStack bountifulfares$renderRecipeIcons(ItemStack itemStack) {
        if(Objects.equals(BuiltInRegistries.ITEM.getKey(this.menu.getSlot(0).getItem().getItem()).getNamespace(), BountifulFares.MOD_ID)) {
            if (this.menu.getSlot(0).getItem().has(DataComponents.DYED_COLOR)) {
                itemStack.set(DataComponents.DYED_COLOR, this.menu.getSlot(0).getItem().get(DataComponents.DYED_COLOR));
            } else if (itemStack.has(DataComponents.DYED_COLOR)) {
                itemStack.remove(DataComponents.DYED_COLOR);
            }
        }
        return itemStack;
    }
}