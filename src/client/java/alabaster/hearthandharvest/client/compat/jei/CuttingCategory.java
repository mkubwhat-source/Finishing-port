package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.crafting.CuttingBoardRecipe;
import alabaster.hearthandharvest.common.fd.crafting.ingredient.ChanceResult;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import alabaster.hearthandharvest.common.registry.HHModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Cutting board: Farmer's Delight's category (from FarmersDelightRefabricated 26.3), on HH ids. The
 * tool slot shows whatever the recipe's tool ingredient accepts (HH cleavers and Flavored's knife,
 * through {@code #c:tools/knife}). Outputs with a chance get the gold chance slot and tooltip.
 */
public class CuttingCategory implements IRecipeCategory<CuttingBoardRecipe> {
    private static final int WIDTH = 117;
    private static final int HEIGHT = 57;
    private static final int OUTPUT_GRID_X = 76;
    private static final int OUTPUT_GRID_Y = 10;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable slotChance;

    public CuttingCategory(IGuiHelper helper) {
        Identifier texture = HearthAndHarvest.id("textures/gui/jei/cutting_board.png");
        background = helper.createDrawable(texture, 0, 0, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(HHModItems.CUTTING_BOARD.get());
        slot = helper.createDrawable(texture, 0, 58, 18, 18);
        slotChance = helper.createDrawable(texture, 18, 58, 18, 18);
    }

    @Override
    public IRecipeType<CuttingBoardRecipe> getRecipeType() {
        return HHJeiPlugin.CUTTING;
    }

    @Override
    public Component getTitle() {
        return TextUtils.JEI("cutting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 8).add(recipe.getTool().display());
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 27).add(recipe.getInput().display());

        List<ChanceResult> outputs = recipe.getRollableResults();
        for (int i = 0; i < outputs.size(); i++) {
            ChanceResult output = outputs.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_GRID_X + gridX(i, outputs.size()) + 1, OUTPUT_GRID_Y + gridY(i, outputs.size()) + 1)
                    .add(output.stack())
                    .addRichTooltipCallback((view, tooltip) -> {
                        float chance = output.chance();
                        if (chance != 1) {
                            tooltip.add(TextUtils.JEI("chance", chance < 0.01 ? "<1" : (int) (chance * 100)).withStyle(ChatFormatting.GOLD));
                        }
                    });
        }
    }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        List<ChanceResult> outputs = recipe.getRollableResults();
        for (int i = 0; i < outputs.size(); i++) {
            IDrawable frame = outputs.get(i).chance() != 1 ? slotChance : slot;
            frame.draw(graphics, OUTPUT_GRID_X + gridX(i, outputs.size()), OUTPUT_GRID_Y + gridY(i, outputs.size()));
        }
    }

    /** Outputs sit in a 2x2 grid, centred when there are fewer than three (one slot is 19 px). */
    private static int gridX(int index, int count) {
        return (count > 1 ? 0 : 9) + (index % 2 == 0 ? 0 : 19);
    }

    private static int gridY(int index, int count) {
        return (count > 2 ? 0 : 9) + (index / 2) * 19;
    }
}
