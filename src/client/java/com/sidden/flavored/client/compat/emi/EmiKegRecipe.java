package com.sidden.flavored.client.compat.emi;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.block.entity.KegBlockEntity;
import com.sidden.flavored.recipe.FermentingRecipe;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class EmiKegRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/compat/jei/keg.png");
    private final EmiIngredient fermenter;
    private final boolean fermenterRequired;

    public EmiKegRecipe(FermentingRecipe recipe) {
        super(FlavoredEmiPlugin.FERMENTING, EmiPort.getId(recipe), 109, 26);
        EmiIngredient ingredient = EmiIngredient.of(recipe.ingredient());
        this.fermenter = EmiIngredient.of(recipe.fermenter());
        this.fermenterRequired = recipe.fermenterRequired();
        this.inputs = fermenterRequired ? List.of(ingredient, fermenter) : List.of(ingredient);
        // an optional fermenter only speeds the keg up and is not used up
        this.catalysts = fermenterRequired ? List.of() : List.of(fermenter);
        this.outputs = List.of(EmiStack.of(recipe.result()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 109, 26, 0, 0);
        widgets.addSlot(fermenter, 0, 4).drawBack(false).catalyst(!fermenterRequired)
                .appendTooltip(Component.translatable(fermenterRequired ? "gui.jei.flavored.fermenter_required" : "gui.jei.flavored.fermenter_optional").withStyle(ChatFormatting.GRAY));
        widgets.addSlot(inputs.get(0), 27, 4).drawBack(false);
        widgets.addFillingArrow(52, 4, KegBlockEntity.MAX_PROGRESS * 50 / 2);
        widgets.addSlot(outputs.get(0), 87, 4).drawBack(false).recipeContext(this);
    }
}
