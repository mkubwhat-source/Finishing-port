package com.sidden.flavored.client.compat.jei;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.recipe.MixingRecipe;
import com.sidden.flavored.registry.FlavoredBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

public class MixingCategory implements IRecipeCategory<MixingRecipe> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/compat/jei/mixing_bowl.png");
    private final IDrawable background;
    private final IDrawable icon;

    public MixingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 109, 60);
        this.icon = helper.createDrawableItemLike(FlavoredBlocks.MIXING_BOWL.get());
    }

    @Override
    public IRecipeType<MixingRecipe> getRecipeType() {
        return FlavoredJeiPlugin.MIXING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.flavored.mixing");
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
    public void setRecipe(IRecipeLayoutBuilder builder, MixingRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < 6; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 1 + 18 * (i % 3), 1 + 18 * (i / 3));
            if (i < recipe.ingredientsInput().size()) {
                slot.add(recipe.ingredientsInput().get(i));
            }
        }
        builder.setShapeless();
        IRecipeSlotBuilder vessel = builder.addSlot(RecipeIngredientRole.INPUT, 92, 8);
        recipe.vesselInput().ifPresent(vessel::add);
        // the liquid is not used up by mixing
        IRecipeSlotBuilder liquid = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 19, 43);
        recipe.liquidInput().ifPresent(liquid::add);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 63, 43).add(recipe.outputTemplate());
    }

    @Override
    public void draw(MixingRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
    }
}
