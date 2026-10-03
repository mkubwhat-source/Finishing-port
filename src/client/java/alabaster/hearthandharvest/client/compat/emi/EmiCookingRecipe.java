package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.crafting.CookingPotRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

/** Cooking pot; the JEI category's layout and Farmer's Delight textures. */
public class EmiCookingRecipe extends BasicEmiRecipe {
    private static final Identifier BACKGROUND = HearthAndHarvest.id("textures/gui/jei/cooking_pot.png");
    private static final Identifier SCREEN = HearthAndHarvest.id("textures/gui/cooking_pot.png");

    private final CookingPotRecipe recipe;
    private final List<EmiIngredient> ingredients;
    private final EmiStack container;
    private final EmiStack result;

    public EmiCookingRecipe(CookingPotRecipe recipe, Identifier id) {
        super(HHEmiPlugin.COOKING, id, 116, 56);
        this.recipe = recipe;
        this.ingredients = HHEmiUtil.ingredients(recipe.input());
        ItemStackTemplate containerTemplate = recipe.container();
        this.container = containerTemplate == null ? EmiStack.EMPTY : HHEmiUtil.stack(containerTemplate);
        this.result = HHEmiUtil.stack(recipe.result());
        List<EmiIngredient> in = new ArrayList<>(ingredients);
        if (!container.isEmpty()) in.add(container);
        this.inputs = in;
        this.outputs = List.of(result);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, 116, 56, 0, 0);
        widgets.addAnimatedTexture(SCREEN, 60, 9, 24, 17, 176, 15, 10000, true, false, false);
        widgets.addTexture(SCREEN, 18, 39, 17, 15, 176, 0);
        widgets.addTexture(SCREEN, 64, 2, 8, 11, 176, 32);
        if (recipe.getExperience() > 0) {
            widgets.addTexture(SCREEN, 63, 21, 9, 9, 176, 43);
        }
        HHEmiUtil.addTimeAndExperience(widgets, recipe.getCookTime(), recipe.getExperience(), 61, 2, 22, 28);

        for (int i = 0; i < ingredients.size() && i < CookingPotRecipe.INPUT_SLOTS; i++) {
            widgets.addSlot(ingredients.get(i), (i % 3) * 18, (i / 3) * 18).drawBack(false);
        }
        // the meal as it shows in the pot, the container it is served in, and the served meal
        widgets.addSlot(result, 94, 9).drawBack(false);
        if (!container.isEmpty()) {
            widgets.addSlot(container, 62, 38).drawBack(false);
        }
        widgets.addSlot(result, 94, 38).drawBack(false).recipeContext(this);
    }
}
