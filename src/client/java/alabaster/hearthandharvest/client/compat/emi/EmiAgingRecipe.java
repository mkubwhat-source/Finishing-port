package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.crafting.CaskRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.Identifier;

import java.util.List;

/** Cask aging; the JEI category's layout and texture (jei_cask_gui.png). */
public class EmiAgingRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/jei/jei_cask_gui.png");
    private final CaskRecipe recipe;

    public EmiAgingRecipe(CaskRecipe recipe, Identifier id) {
        super(HHEmiPlugin.AGING, id, 116, 42);
        this.recipe = recipe;
        this.inputs = HHEmiUtil.ingredients(recipe.getIngredients());
        this.outputs = List.of(HHEmiUtil.stack(recipe.getOutputTemplate()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 116, 42, 33, 16);
        widgets.addAnimatedTexture(TEXTURE, 51, 12, 24, 17, 176, 15, 10000, true, false, false);
        widgets.addTexture(TEXTURE, 54, 5, 8, 11, 176, 32);
        if (recipe.getExperience() > 0) {
            widgets.addTexture(TEXTURE, 53, 24, 9, 9, 176, 43);
        }
        HHEmiUtil.addTimeAndExperience(widgets, recipe.getCookTime(), recipe.getExperience(), 47, 4, 22, 28);
        for (int i = 0; i < inputs.size() && i < CaskRecipe.INPUT_SLOTS; i++) {
            widgets.addSlot(inputs.get(i), (i % 2) * 18 + 5, (i / 2) * 18 + 3).drawBack(false);
        }
        widgets.addSlot(outputs.get(0), 90, 11).drawBack(false).recipeContext(this);
    }
}
