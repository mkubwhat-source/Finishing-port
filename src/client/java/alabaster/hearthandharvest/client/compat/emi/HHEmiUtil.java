package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.platform.fluid.FluidStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/** Conversions and tooltip helpers shared by Hearth and Harvest's EMI recipes. */
final class HHEmiUtil {
    private HHEmiUtil() {
    }

    /**
     * EMI turns an Ingredient into its plain items, which drops the components a custom ingredient
     * matches on (HH's {@code hearthandharvest:vintage} bottles, Fabric's component ingredients such as
     * water bottles). Those are resolved through the ingredient's SlotDisplay instead, which they
     * implement to show exactly the stacks they accept.
     */
    static EmiIngredient ingredient(Ingredient ingredient) {
        Level level = Minecraft.getInstance().level;
        if (ingredient.getCustomIngredient() == null || level == null) {
            return EmiIngredient.of(ingredient);
        }
        return EmiIngredient.of(ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(level))
                .stream().map(EmiStack::of).toList());
    }

    static List<EmiIngredient> ingredients(List<Ingredient> ingredients) {
        return ingredients.stream().map(HHEmiUtil::ingredient).toList();
    }

    static EmiStack stack(ItemStackTemplate template) {
        return EmiStack.of(template.create());
    }

    /** HH fluid stacks count millibuckets; EMI on Fabric counts droplets (81 per mB). */
    static long droplets(int millibuckets) {
        return millibuckets * FluidStack.DROPLETS_PER_MB;
    }

    static EmiStack fluid(FluidStack fluid) {
        return EmiStack.of(fluid.getFluid(), droplets(fluid.getAmount()));
    }

    /** JEI's own "Ns" / "N XP" lines, as HH and Farmer's Delight show them. */
    static void addTimeAndExperience(WidgetHolder widgets, int ticks, float experience, int x, int y, int width, int height) {
        List<Component> lines = new ArrayList<>();
        if (ticks > 0) {
            lines.add(Component.translatable("gui.jei.category.smelting.time.seconds", ticks / 20));
        }
        if (experience > 0) {
            lines.add(Component.translatable("gui.jei.category.smelting.experience", experience));
        }
        if (!lines.isEmpty()) {
            widgets.addTooltipText(lines, x, y, width, height);
        }
    }
}
