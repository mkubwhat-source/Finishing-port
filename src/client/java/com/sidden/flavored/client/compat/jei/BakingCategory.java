package com.sidden.flavored.client.compat.jei;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.recipe.BakingRecipe;
import com.sidden.flavored.registry.FlavoredBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
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

import java.util.List;
import java.util.Optional;

public class BakingCategory implements IRecipeCategory<BakingRecipe> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/compat/jei/oven.png");
    private final IDrawable background;
    private final IDrawable icon;

    public BakingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 118, 82);
        this.icon = helper.createDrawableItemLike(FlavoredBlocks.OVEN.get());
    }

    @Override
    public IRecipeType<BakingRecipe> getRecipeType() {
        return FlavoredJeiPlugin.BAKING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.flavored.baking");
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BakingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 15).add(recipe.resultTemplate());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 97, 55);

        // 1.21.1 laid the pattern's ingredients out as if every pattern were 3 wide; they are placed
        // by the pattern's own width now (1-wide patterns such as bread showed shifted before).
        int width = recipe.pattern().width();
        List<Optional<Ingredient>> ingredients = recipe.pattern().ingredients();
        for (int i = 0; i < 9; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 1 + 18 * (i % 3), 15 + 18 * (i / 3));
            int x = i % 3;
            int y = i / 3;
            if (x < width) {
                int index = y * width + x;
                if (index < ingredients.size()) {
                    ingredients.get(index).ifPresent(slot::add);
                }
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, BakingRecipe recipe, IFocusGroup focuses) {
        int bakeTime = recipe.getBakingTime() <= 0 ? 200 : recipe.getBakingTime();
        builder.addAnimatedRecipeArrowWidget(bakeTime).setPosition(61, 15);
        builder.addAnimatedRecipeFlameWidget(300).setPosition(97, 38);
        if (recipe.getExperience() > 0) {
            builder.addText(Component.translatable("gui.jei.category.smelting.experience", recipe.getExperience()), getWidth() - 20, 10)
                    .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
                    .setTextAlignment(HorizontalAlignment.RIGHT)
                    .setColor(0xFF808080);
        }
        builder.addText(Component.translatable("gui.jei.category.smelting.time.seconds", bakeTime / 20), getWidth() - 20, 10)
                .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.RIGHT)
                .setTextAlignment(VerticalAlignment.BOTTOM)
                .setColor(0xFF808080);
    }

    @Override
    public void draw(BakingRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
    }
}
