package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.crafting.StompingBasinRecipe;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Stomping basin: items stomped into a fluid and/or an item. Layout and texture from HH 1.21.1's
 * StompingRecipeCategory: repeated ingredients share one slot showing the count, and the fluid tank's
 * rounded corners are redrawn over the fluid.
 */
public class StompingCategory implements IRecipeCategory<StompingBasinRecipe> {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/jei/jei_stomping_gui.png");
    private static final int WIDTH = 116;
    private static final int HEIGHT = 42;
    private static final int U = 33;
    private static final int V = 16;

    private static final int[] INPUT_X = {6, 25};
    private static final int[] INPUT_Y = {4, 22};
    private static final int ARROW_X = 48;
    private static final int ARROW_Y = 20;
    private static final int TANK_X = 78;
    private static final int TANK_Y = 24;
    private static final int TANK_WIDTH = 26;
    private static final int TANK_HEIGHT = 16;
    private static final int TANK_CAPACITY = FluidStack.BUCKET_VOLUME;
    private static final int ITEM_OUTPUT_X = 83;
    private static final int ITEM_OUTPUT_Y = 2;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawable tankCorners;

    public StompingCategory(IGuiHelper helper) {
        background = helper.createDrawable(TEXTURE, U, V, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(HHModItems.STOMPING_BASIN.get());
        arrow = helper.drawableBuilder(TEXTURE, 176, 15, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        tankCorners = tankCorners(helper);
    }

    /** The four corner pixels of the tank frame, drawn back over the (square) fluid. */
    private static IDrawable tankCorners(IGuiHelper helper) {
        int u = U + TANK_X;
        int v = V + TANK_Y;
        int right = TANK_WIDTH - 1;
        int bottom = TANK_HEIGHT - 1;
        IDrawable topLeft = helper.createDrawable(TEXTURE, u, v, 1, 1);
        IDrawable topRight = helper.createDrawable(TEXTURE, u + right, v, 1, 1);
        IDrawable bottomLeft = helper.createDrawable(TEXTURE, u, v + bottom, 1, 1);
        IDrawable bottomRight = helper.createDrawable(TEXTURE, u + right, v + bottom, 1, 1);
        return new IDrawable() {
            @Override
            public int getWidth() {
                return TANK_WIDTH;
            }

            @Override
            public int getHeight() {
                return TANK_HEIGHT;
            }

            @Override
            public void draw(GuiGraphicsExtractor graphics, int x, int y) {
                topLeft.draw(graphics, x, y);
                topRight.draw(graphics, x + right, y);
                bottomLeft.draw(graphics, x, y + bottom);
                bottomRight.draw(graphics, x + right, y + bottom);
            }
        };
    }

    @Override
    public IRecipeType<StompingBasinRecipe> getRecipeType() {
        return HHJeiPlugin.STOMPING;
    }

    @Override
    public Component getTitle() {
        return HHTextUtils.getTranslation("jei.stomping");
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
    public void setRecipe(IRecipeLayoutBuilder builder, StompingBasinRecipe recipe, IFocusGroup focuses) {
        List<Group> groups = group(recipe.getIngredients());
        for (int i = 0; i < groups.size() && i < 4; i++) {
            Group group = groups.get(i);
            List<ItemStack> stacks = group.ingredient.items().map(item -> new ItemStack(item, group.count)).toList();
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i % 2], INPUT_Y[i / 2]).addItemStacks(stacks);
        }

        FluidStack resultFluid = recipe.getResultFluid();
        if (!resultFluid.isEmpty()) {
            JeiUtil.addFluid(builder.addSlot(RecipeIngredientRole.OUTPUT, TANK_X, TANK_Y), resultFluid)
                    .setFluidRenderer(JeiUtil.droplets(Math.max(TANK_CAPACITY, resultFluid.getAmount())), false, TANK_WIDTH, TANK_HEIGHT)
                    .setOverlay(tankCorners, 0, 0);
        }

        recipe.getResultTemplate().ifPresent(result ->
                builder.addSlot(RecipeIngredientRole.OUTPUT, ITEM_OUTPUT_X, ITEM_OUTPUT_Y).add(result));
    }

    @Override
    public void draw(StompingBasinRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        arrow.draw(graphics, ARROW_X, ARROW_Y);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, StompingBasinRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (JeiUtil.inside(ARROW_X, 2, 25, ARROW_Y + 19, mouseX, mouseY)) {
            tooltip.add(Component.translatable("hearthandharvest.jei.stomping.tooltip"));
        }
    }

    /** Identical ingredients listed several times, collapsed into one slot with a count. */
    private static List<Group> group(List<Ingredient> ingredients) {
        List<Group> groups = new ArrayList<>();
        outer:
        for (Ingredient ingredient : ingredients) {
            for (Group group : groups) {
                if (group.ingredient.equals(ingredient)) {
                    group.count++;
                    continue outer;
                }
            }
            groups.add(new Group(ingredient));
        }
        return groups;
    }

    private static final class Group {
        final Ingredient ingredient;
        int count = 1;

        Group(Ingredient ingredient) {
            this.ingredient = ingredient;
        }
    }
}
