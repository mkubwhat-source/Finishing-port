package alabaster.hearthandharvest.common.mixin;

import alabaster.hearthandharvest.Config;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;

/**
 * "Stack water bottles" option: water bottles stack to 16, other potions stay unstackable.
 * <p>
 * 26.3: {@code getMaxStackSize} is a default method of {@code ItemInstance} that ItemStack no longer
 * declares, so there is nothing to inject into; this mixin adds the override to ItemStack instead
 * (same result as the interface default, {@code MAX_STACK_SIZE} or 1, when the rule doesn't apply).
 */
@Mixin(ItemStack.class)
public abstract class WaterBottleMixin {

    public int getMaxStackSize() {
        ItemStack stack = (ItemStack) (Object) this;
        int original = stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 1);

        if (!stack.is(Items.POTION)) {
            return original;
        }

        try {
            if (!Config.STACK_WATER_BOTTLES.get()) return original;
        } catch (IllegalStateException e) {
            return original;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            if (contents.is(Potions.WATER)) {
                return 16;
            }
            return 1;
        }

        return 1;
    }
}
