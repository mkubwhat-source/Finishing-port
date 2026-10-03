package com.sidden.flavored.client.compat.emi;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.recipe.BakingRecipe;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmiBakingRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/compat/jei/oven.png");
    private final EmiIngredient[] grid = new EmiIngredient[9];
    private final int bakeTime;
    private final float experience;

    public EmiBakingRecipe(BakingRecipe recipe) {
        super(FlavoredEmiPlugin.BAKING, EmiPort.getId(recipe), 118, 82);
        int width = recipe.pattern().width();
        List<Optional<Ingredient>> ingredients = recipe.pattern().ingredients();
        List<EmiIngredient> in = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int x = i % 3, y = i / 3, index = y * width + x;
            EmiIngredient ing = EmiStack.EMPTY;
            if (x < width && index < ingredients.size() && ingredients.get(index).isPresent()) {
                ing = EmiIngredient.of(ingredients.get(index).get());
            }
            grid[i] = ing;
            if (!ing.isEmpty()) in.add(ing);
        }
        this.inputs = in;
        this.outputs = List.of(EmiStack.of(recipe.getResultItem()));
        this.bakeTime = recipe.getBakingTime() <= 0 ? 200 : recipe.getBakingTime();
        this.experience = recipe.getExperience();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 118, 82, 0, 0);
        for (int i = 0; i < 9; i++) {
            widgets.addSlot(grid[i], 18 * (i % 3), 14 + 18 * (i / 3)).drawBack(false);
        }
        widgets.addFillingArrow(61, 15, bakeTime * 50);
        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 97, 38, 15000, false, true, true);
        widgets.addSlot(outputs.get(0), 96, 14).drawBack(false).recipeContext(this);
        if (experience > 0) {
            widgets.addText(Component.translatable("emi.cooking.experience", experience), 118, 0, 0xFF808080, false).horizontalAlign(dev.emi.emi.api.widget.TextWidget.Alignment.END);
        }
        widgets.addText(Component.translatable("emi.cooking.time", bakeTime / 20f), 118, 72, 0xFF808080, false).horizontalAlign(dev.emi.emi.api.widget.TextWidget.Alignment.END);
    }
}
