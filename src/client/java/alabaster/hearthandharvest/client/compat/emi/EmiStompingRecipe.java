package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.crafting.StompingBasinRecipe;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Stomping basin; the JEI category's layout and texture (jei_stomping_gui.png). Repeated ingredients
 * share one slot showing the count, and the fluid tank's rounded corners are redrawn over the fluid.
 */
public class EmiStompingRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/jei/jei_stomping_gui.png");
    private static final int U = 33;
    private static final int V = 16;
    private static final int[] INPUT_X = {6, 25};
    private static final int[] INPUT_Y = {4, 22};
    private static final int TANK_X = 78;
    private static final int TANK_Y = 24;
    private static final int TANK_WIDTH = 26;
    private static final int TANK_HEIGHT = 16;

    private final List<EmiIngredient> grouped;
    private final EmiStack resultFluid;
    private final EmiStack resultItem;

    public EmiStompingRecipe(StompingBasinRecipe recipe, Identifier id) {
        super(HHEmiPlugin.STOMPING, id, 116, 42);
        this.grouped = group(recipe.getIngredients());
        this.resultFluid = recipe.getResultFluid().isEmpty() ? EmiStack.EMPTY : HHEmiUtil.fluid(recipe.getResultFluid());
        this.resultItem = recipe.getResultTemplate().map(HHEmiUtil::stack).orElse(EmiStack.EMPTY);
        this.inputs = grouped;
        List<EmiStack> out = new ArrayList<>();
        if (!resultItem.isEmpty()) out.add(resultItem);
        if (!resultFluid.isEmpty()) out.add(resultFluid);
        this.outputs = out;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 116, 42, U, V);
        widgets.addAnimatedTexture(TEXTURE, 48, 20, 24, 17, 176, 15, 10000, true, false, false);
        widgets.addTooltipText(List.of(Component.translatable("hearthandharvest.jei.stomping.tooltip")), 48, 2, 25, 37);
        for (int i = 0; i < grouped.size() && i < 4; i++) {
            widgets.addSlot(grouped.get(i), INPUT_X[i % 2] - 1, INPUT_Y[i / 2] - 1).drawBack(false);
        }
        if (!resultFluid.isEmpty()) {
            long capacity = Math.max(HHEmiUtil.droplets(FluidStack.BUCKET_VOLUME), resultFluid.getAmount());
            widgets.add(new TankWidget(resultFluid, TANK_X, TANK_Y, TANK_WIDTH, TANK_HEIGHT, capacity)).drawBack(false).recipeContext(this);
            int right = TANK_WIDTH - 1;
            int bottom = TANK_HEIGHT - 1;
            for (int[] corner : new int[][]{{0, 0}, {right, 0}, {0, bottom}, {right, bottom}}) {
                widgets.addTexture(TEXTURE, TANK_X + corner[0], TANK_Y + corner[1], 1, 1, U + TANK_X + corner[0], V + TANK_Y + corner[1]);
            }
        }
        if (!resultItem.isEmpty()) {
            widgets.addSlot(resultItem, 82, 1).drawBack(false).recipeContext(this);
        }
    }

    /** Identical ingredients listed several times, collapsed into one ingredient with a count. */
    private static List<EmiIngredient> group(List<Ingredient> ingredients) {
        List<Ingredient> distinct = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            int index = distinct.indexOf(ingredient);
            if (index >= 0) {
                counts.set(index, counts.get(index) + 1);
            } else {
                distinct.add(ingredient);
                counts.add(1);
            }
        }
        List<EmiIngredient> grouped = new ArrayList<>();
        for (int i = 0; i < distinct.size(); i++) {
            grouped.add(HHEmiUtil.ingredient(distinct.get(i)).copy().setAmount(counts.get(i)));
        }
        return grouped;
    }
}
