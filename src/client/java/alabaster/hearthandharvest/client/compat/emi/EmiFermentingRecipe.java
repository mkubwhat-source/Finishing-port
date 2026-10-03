package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.KegBlockEntity;
import alabaster.hearthandharvest.common.crafting.KegRecipe;
import alabaster.hearthandharvest.common.registry.HHDataMaps;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * Keg fermenting; the JEI category's layout, cut from the keg screen (keg_gui.png): input tank, two
 * item inputs, the result item, the result tank, the glass bottle and the bottle the result fluid
 * fills, and the ferment time under the bottles.
 */
public class EmiFermentingRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/keg_gui.png");
    private static final int ORIGIN_X = 6;
    private static final int ORIGIN_Y = 18;
    private static final int TANK_Y = 20 - ORIGIN_Y;
    private static final int[] SLOT_Y = {30 - ORIGIN_Y, 57 - ORIGIN_Y};
    private static final int CONTAINER_Y = 23 - ORIGIN_Y;

    private final KegRecipe recipe;
    private final EmiStack inputFluid;
    private final List<EmiIngredient> itemInputs;
    private final EmiStack resultItem;
    private final EmiStack resultFluid;
    private final EmiStack bottle;

    public EmiFermentingRecipe(KegRecipe recipe, Identifier id) {
        super(HHEmiPlugin.FERMENTING, id, 164, 67);
        this.recipe = recipe;
        this.inputFluid = recipe.getInputFluid().isEmpty() ? EmiStack.EMPTY : HHEmiUtil.fluid(recipe.getInputFluid());
        this.itemInputs = HHEmiUtil.ingredients(recipe.getIngredients());
        this.resultItem = recipe.getResultTemplate().map(HHEmiUtil::stack).orElse(EmiStack.EMPTY);
        this.resultFluid = recipe.getResultFluid().isEmpty() ? EmiStack.EMPTY : HHEmiUtil.fluid(recipe.getResultFluid());
        Item bottleItem = recipe.getResultFluid().isEmpty() ? null : HHDataMaps.getBottleForFluid(recipe.getResultFluid().getFluid());
        this.bottle = bottleItem == null ? EmiStack.EMPTY : EmiStack.of(bottleItem);

        List<EmiIngredient> in = new ArrayList<>();
        if (!inputFluid.isEmpty()) in.add(inputFluid);
        in.addAll(itemInputs);
        this.inputs = in;
        List<EmiStack> out = new ArrayList<>();
        if (!resultItem.isEmpty()) out.add(resultItem);
        if (!resultFluid.isEmpty()) out.add(resultFluid);
        if (!bottle.isEmpty()) out.add(bottle);
        this.outputs = out;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 164, 67, ORIGIN_X, ORIGIN_Y);
        for (int x : new int[]{54 - ORIGIN_X, 114 - ORIGIN_X}) {
            widgets.addAnimatedTexture(TEXTURE, x, 28 - ORIGIN_Y, 8, 47, 176, 3, 10000, false, true, false);
            HHEmiUtil.addTimeAndExperience(widgets, recipe.getFermentTime(), recipe.getExperience(), x, 28 - ORIGIN_Y, 8, 47);
        }
        if (recipe.getFermentTime() > 0) {
            widgets.addText(Component.translatable("gui.jei.category.smelting.time.seconds", recipe.getFermentTime() / 20),
                            (69 - ORIGIN_X) + 19, (44 - ORIGIN_Y) + 5, 0xFF404040, false)
                    .horizontalAlign(TextWidget.Alignment.CENTER);
        }

        if (!inputFluid.isEmpty()) {
            tank(widgets, inputFluid, 8 - ORIGIN_X, 192, recipe.getInputFluid());
        }
        for (int i = 0; i < itemInputs.size() && i < SLOT_Y.length; i++) {
            widgets.addSlot(itemInputs.get(i), 32 - ORIGIN_X - 1, SLOT_Y[i] - 1).drawBack(false);
        }
        if (!resultItem.isEmpty()) {
            widgets.addSlot(resultItem, 128 - ORIGIN_X - 1, SLOT_Y[0] - 1).drawBack(false).recipeContext(this);
        }
        if (!resultFluid.isEmpty()) {
            tank(widgets, resultFluid, 152 - ORIGIN_X, 208, recipe.getResultFluid()).recipeContext(this);
        }
        if (!bottle.isEmpty()) {
            widgets.addSlot(EmiStack.of(Items.GLASS_BOTTLE), 69 - ORIGIN_X - 1, CONTAINER_Y - 1).drawBack(false);
            widgets.addSlot(bottle, 91 - ORIGIN_X - 1, CONTAINER_Y - 1).drawBack(false).recipeContext(this);
        }
    }

    private static SlotWidget tank(WidgetHolder widgets, EmiStack fluid, int x, int overlayU, FluidStack amount) {
        long capacity = HHEmiUtil.droplets(Math.max(KegBlockEntity.TANK_CAPACITY, amount.getAmount()));
        SlotWidget tank = widgets.add(new TankWidget(fluid, x, TANK_Y, 16, 63, capacity)).drawBack(false);
        widgets.addTexture(TEXTURE, x, TANK_Y, 16, 63, overlayU, 0);
        return tank;
    }
}
