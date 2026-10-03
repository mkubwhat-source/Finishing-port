package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.crafting.CaskRecipe;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Cask aging: up to four ingredients (2x2) age into the result. Layout and texture from HH 1.21.1's
 * AgingRecipeCategory. Ingredients go in through their SlotDisplay so that vintage ingredients
 * ({@code hearthandharvest:vintage}) show the bottles carrying that vintage.
 */
public class AgingCategory implements IRecipeCategory<CaskRecipe> {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/jei/jei_cask_gui.png");
    private static final int WIDTH = 116;
    private static final int HEIGHT = 42;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable timeIcon;
    private final IDrawable expIcon;
    private final IDrawableAnimated arrow;

    public AgingCategory(IGuiHelper helper) {
        background = helper.createDrawable(TEXTURE, 33, 16, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(HHModItems.CASK.get());
        timeIcon = helper.createDrawable(TEXTURE, 176, 32, 8, 11);
        expIcon = helper.createDrawable(TEXTURE, 176, 43, 9, 9);
        arrow = helper.drawableBuilder(TEXTURE, 176, 15, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public IRecipeType<CaskRecipe> getRecipeType() {
        return HHJeiPlugin.AGING;
    }

    @Override
    public Component getTitle() {
        return HHTextUtils.getTranslation("jei.aging");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CaskRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size() && i < CaskRecipe.INPUT_SLOTS; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, (i % 2) * 18 + 6, (i / 2) * 18 + 4)
                    .add(ingredients.get(i).display());
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 12).add(recipe.getOutputTemplate());
    }

    @Override
    public void draw(CaskRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        arrow.draw(graphics, 51, 12);
        timeIcon.draw(graphics, 54, 5);
        if (recipe.getExperience() > 0) {
            expIcon.draw(graphics, 53, 24);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CaskRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (JeiUtil.inside(47, 4, 22, 28, mouseX, mouseY)) {
            JeiUtil.addTimeAndExperience(tooltip, recipe.getCookTime(), recipe.getExperience());
        }
    }
}
