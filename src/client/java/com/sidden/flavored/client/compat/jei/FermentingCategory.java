package com.sidden.flavored.client.compat.jei;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.block.entity.KegBlockEntity;
import com.sidden.flavored.recipe.FermentingRecipe;
import com.sidden.flavored.registry.FlavoredBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class FermentingCategory implements IRecipeCategory<FermentingRecipe> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/compat/jei/keg.png");
    private final IDrawable background;
    private final IDrawable icon;

    public FermentingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 109, 26);
        this.icon = helper.createDrawableItemLike(FlavoredBlocks.KEG.get());
    }

    @Override
    public IRecipeType<FermentingRecipe> getRecipeType() {
        return FlavoredJeiPlugin.FERMENTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.flavored.fermenting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FermentingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 28, 5).add(recipe.ingredient());
        // the fermenter: required for bottled Bountiful Fares drinks, otherwise an optional 4x speed-up
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).add(recipe.fermenter())
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(recipe.fermenterRequired()
                        ? "gui.jei.flavored.fermenter_required" : "gui.jei.flavored.fermenter_optional").withStyle(ChatFormatting.GRAY)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 5).add(recipe.resultTemplate());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FermentingRecipe recipe, IFocusGroup focuses) {
        builder.addAnimatedRecipeArrowWidget(KegBlockEntity.MAX_PROGRESS / 2).setPosition(52, 4);
    }

    @Override
    public void draw(FermentingRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
    }
}
