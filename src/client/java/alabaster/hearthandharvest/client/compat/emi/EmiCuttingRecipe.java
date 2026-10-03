package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.crafting.CuttingBoardRecipe;
import alabaster.hearthandharvest.common.fd.crafting.ingredient.ChanceResult;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Cutting board; the JEI category's layout and Farmer's Delight texture. The tool is a catalyst (it
 * is not used up); outputs with a chance get the gold slot and the chance in their tooltip.
 */
public class EmiCuttingRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/jei/cutting_board.png");
    private static final int OUTPUT_GRID_X = 76;
    private static final int OUTPUT_GRID_Y = 10;

    private final EmiIngredient tool;
    private final EmiIngredient input;
    private final List<ChanceResult> results;

    public EmiCuttingRecipe(CuttingBoardRecipe recipe, Identifier id) {
        super(HHEmiPlugin.CUTTING, id, 117, 57);
        this.tool = HHEmiUtil.ingredient(recipe.getTool());
        this.input = HHEmiUtil.ingredient(recipe.getInput());
        this.results = recipe.getRollableResults();
        this.inputs = List.of(input);
        this.catalysts = List.of(tool);
        this.outputs = results.stream().map(result -> {
            EmiStack stack = HHEmiUtil.stack(result.stack());
            return result.chance() != 1 ? stack.setChance(result.chance()) : stack;
        }).toList();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 117, 57, 0, 0);
        widgets.addSlot(tool, 15, 7).drawBack(false).catalyst(true);
        widgets.addSlot(input, 15, 26).drawBack(false);

        int count = results.size();
        for (int i = 0; i < count; i++) {
            int x = OUTPUT_GRID_X + (count > 1 ? 0 : 9) + (i % 2 == 0 ? 0 : 19);
            int y = OUTPUT_GRID_Y + (count > 2 ? 0 : 9) + (i / 2) * 19;
            float chance = results.get(i).chance();
            SlotWidget slot = widgets.addSlot(outputs.get(i), x, y)
                    .customBackground(TEXTURE, chance != 1 ? 18 : 0, 58, 18, 18)
                    .recipeContext(this);
            if (chance != 1) {
                slot.appendTooltip(TextUtils.JEI("chance", chance < 0.01 ? "<1" : (int) (chance * 100)).withStyle(ChatFormatting.GOLD));
            }
        }
    }
}
