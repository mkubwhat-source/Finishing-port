package com.sidden.flavored.client.compat.emi;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.recipe.MixingRecipe;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EmiMixingRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/compat/jei/mixing_bowl.png");
    private final List<EmiIngredient> grid;
    private final EmiIngredient vessel;
    private final EmiIngredient liquid;

    public EmiMixingRecipe(MixingRecipe recipe) {
        super(FlavoredEmiPlugin.MIXING, EmiPort.getId(recipe), 109, 60);
        this.grid = recipe.ingredientsInput().stream().map(EmiIngredient::of).toList();
        this.vessel = recipe.vesselInput().map(EmiIngredient::of).orElse(EmiStack.EMPTY);
        this.liquid = recipe.liquidInput().map(EmiIngredient::of).orElse(EmiStack.EMPTY);
        List<EmiIngredient> in = new ArrayList<>(grid);
        if (!vessel.isEmpty()) in.add(vessel);
        this.inputs = in;
        // the bowl's liquid is not used up by mixing
        this.catalysts = liquid.isEmpty() ? List.of() : List.of(liquid);
        this.outputs = List.of(EmiStack.of(recipe.output()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 109, 60, 0, 0);
        for (int i = 0; i < 6; i++) {
            widgets.addSlot(i < grid.size() ? grid.get(i) : EmiStack.EMPTY, 18 * (i % 3), 18 * (i / 3)).drawBack(false);
        }
        widgets.addSlot(vessel, 91, 7).drawBack(false);
        widgets.addSlot(liquid, 18, 42).drawBack(false).catalyst(true);
        widgets.addSlot(outputs.get(0), 62, 42).drawBack(false).recipeContext(this);
    }
}
