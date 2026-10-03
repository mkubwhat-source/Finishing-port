package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.platform.fluid.FluidStack;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.network.chat.Component;

/** Small helpers shared by Hearth and Harvest's JEI categories. */
final class JeiUtil {
    private JeiUtil() {
    }

    static boolean inside(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    /** JEI's own "Ns" / "N XP" lines, as HH and Farmer's Delight show them. */
    static void addTimeAndExperience(ITooltipBuilder tooltip, int ticks, float experience) {
        if (ticks > 0) {
            tooltip.add(Component.translatable("gui.jei.category.smelting.time.seconds", ticks / 20));
        }
        if (experience > 0) {
            tooltip.add(Component.translatable("gui.jei.category.smelting.experience", experience));
        }
    }

    /** HH fluid stacks count millibuckets; JEI on Fabric counts droplets (81 per mB). */
    static long droplets(int millibuckets) {
        return millibuckets * FluidStack.DROPLETS_PER_MB;
    }

    static IRecipeSlotBuilder addFluid(IRecipeSlotBuilder slot, FluidStack fluid) {
        return slot.add(fluid.getFluid(), droplets(fluid.getAmount()));
    }
}
