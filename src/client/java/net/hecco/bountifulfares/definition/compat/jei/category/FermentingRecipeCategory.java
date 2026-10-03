package net.hecco.bountifulfares.definition.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.compat.jei.BFRecipeTypes;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.definition.recipe.FermentationRecipe;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("removal")
public class FermentingRecipeCategory implements IRecipeCategory<FermentationRecipe> {
    private final IDrawable fermentationVessel;
    private final IDrawable background;
    private final IDrawable containerIcon;

    public FermentingRecipeCategory(IGuiHelper helper) {
        fermentationVessel = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BFBlocks.FERMENTATION_VESSEL.get().asItem()));
        Identifier backgroundImage = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "textures/gui/jei/fermenting.png");
        background = helper.createDrawable(backgroundImage, 0, 0, 89, 76);
        containerIcon = helper.createDrawable(backgroundImage, 89, 0, 8, 11);
    }

    @Override
    public IRecipeType<FermentationRecipe> getRecipeType() {
        return BFRecipeTypes.FERMENTING;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, IFocusGroup focusGroup) {
        ItemStack resultStack = recipe.getOutput();

        //Code for placing slot locations (input/any misc slot locations)

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 7, 6).addItemStacks(List.of(PotionContents.createItemStack(Items.POTION, Potions.WATER), Items.WATER_BUCKET.getDefaultInstance())); //output slot location
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 50).add(recipe.getIngredient()); //output slot location
        builder.addSlot(RecipeIngredientRole.OUTPUT, 63, 50).addItemStack(resultStack); //output slot location
    }

    // 26.3 / JEI 31: getBackground() was removed from IRecipeCategory - the category now reports
    // its own getWidth()/getHeight() and draws its background in draw(), which takes a
    // GuiGraphicsExtractor instead of GuiGraphics. Item.getCraftingRemainingItem() (Item) became
    // Item.getCraftingRemainder() (ItemStackTemplate, still null when there is none).
    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void draw(FermentationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        if (recipe.getOutput().getItem().getCraftingRemainder() != null) {
            containerIcon.draw(guiGraphics, 67, 33);
        }
    }

    // JEI 31: getTooltipStrings(...) returning a List became getTooltip(ITooltipBuilder, ...).
    @Override
    public void getTooltip(ITooltipBuilder tooltip, FermentationRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        ItemStackTemplate remainder = recipe.getOutput().getItem().getCraftingRemainder();
        if (remainder != null && mouseX >= 67 && mouseX <= 75 && mouseY >= 33 && mouseY <= 44) {
            tooltip.add(Component.translatable("jei.bountifulfares.collect_using").append(Component.translatable(remainder.item().value().getDescriptionId())));
        }
        if (mouseX >= 35 && mouseX <= 47 && mouseY >= 39 && mouseY <= 54) {
            int minutes = (int) Math.floor((double) Services.PLATFORM.get().getIntConfigValue("fermentationTime") / 60);
            int seconds = (int) Math.floor((double) Services.PLATFORM.get().getIntConfigValue("fermentationTime") - (minutes * 60));
            MutableComponent text = Component.literal("");
            if (minutes != 0) {
                text = text.append(minutes + " ").append(Component.translatable("jei.bountifulfares.minutes"));
            }
            if (minutes != 0 && seconds != 0) {
                text = text.append(", ");
            }
            if (seconds != 0) {
                text = text.append(seconds + " ").append(Component.translatable("jei.bountifulfares.seconds"));
            }
            tooltip.add(text);
        }
    }

    @Override
    public Component getTitle() {
        return Component.translatable("bountifulfares.fermenting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return fermentationVessel;
    }
}
