package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.crafting.CookingPotRecipe;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import alabaster.hearthandharvest.common.registry.HHModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/** Cooking pot: Farmer's Delight's category (from FarmersDelightRefabricated 26.3), on HH ids. */
public class CookingCategory implements IRecipeCategory<CookingPotRecipe> {
    private static final int WIDTH = 116;
    private static final int HEIGHT = 56;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable heatIndicator;
    private final IDrawable timeIcon;
    private final IDrawable expIcon;
    private final IDrawableAnimated arrow;

    public CookingCategory(IGuiHelper helper) {
        Identifier widgetBackground = HearthAndHarvest.id("textures/gui/jei/cooking_pot.png");
        Identifier screen = HearthAndHarvest.id("textures/gui/cooking_pot.png");
        background = helper.createDrawable(widgetBackground, 0, 0, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(HHModItems.COOKING_POT.get());
        heatIndicator = helper.createDrawable(screen, 176, 0, 17, 15);
        timeIcon = helper.createDrawable(screen, 176, 32, 8, 11);
        expIcon = helper.createDrawable(screen, 176, 43, 9, 9);
        arrow = helper.drawableBuilder(screen, 176, 15, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public IRecipeType<CookingPotRecipe> getRecipeType() {
        return HHJeiPlugin.COOKING;
    }

    @Override
    public Component getTitle() {
        return TextUtils.JEI("cooking");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookingPotRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.input();
        for (int i = 0; i < ingredients.size() && i < CookingPotRecipe.INPUT_SLOTS; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, (i % 3) * 18 + 1, (i / 3) * 18 + 1).add(ingredients.get(i).display());
        }

        // the meal as it shows in the pot, the container it is served in, and the served meal
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 10).add(recipe.result());
        ItemStackTemplate container = recipe.container();
        if (container != null) {
            builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 63, 39).add(container);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 39).add(recipe.result());
    }

    @Override
    public void draw(CookingPotRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        arrow.draw(graphics, 60, 9);
        heatIndicator.draw(graphics, 18, 39);
        timeIcon.draw(graphics, 64, 2);
        if (recipe.getExperience() > 0) {
            expIcon.draw(graphics, 63, 21);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CookingPotRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (JeiUtil.inside(61, 2, 22, 28, mouseX, mouseY)) {
            JeiUtil.addTimeAndExperience(tooltip, recipe.getCookTime(), recipe.getExperience());
        }
    }
}
