package net.hecco.bountifulfares.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.compat.jei.BFRecipeTypes;
import net.hecco.bountifulfares.definition.compat.jei.PropagationRecipe;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.hecco.bountifulfares.registry.tags.BFBlockTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("removal")
public class PrismarinePropagationCategory implements IRecipeCategory<net.hecco.bountifulfares.definition.compat.jei.PropagationRecipe> {
    private final IDrawable icon;
    private final IDrawable background;

    public PrismarinePropagationCategory(IGuiHelper helper) {
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BFBlocks.PRISMARINE_BLOSSOM.get()));
        Identifier backgroundImage = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "textures/gui/jei/propagation.png");
        background = helper.createDrawable(backgroundImage, 0, 0, 92, 49);
    }

    @Override
    public IRecipeType<PropagationRecipe> getRecipeType() {
        return BFRecipeTypes.PRISMARINE_PROPAGATION;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PropagationRecipe recipe, IFocusGroup focusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addItemStack(BFItems.SPONGEKIN_SEEDS.get().getDefaultInstance());
        // JEI 31 renamed RecipeIngredientRole.CATALYST to CRAFTING_STATION (same ordinal/meaning,
        // matching IRecipeCatalystRegistration.addRecipeCatalyst -> addCraftingStation); 26.3's
        // Registry.getTag(TagKey) became getTagOrEmpty(TagKey) (an Iterable of holders).
        List<ItemStack> substrates = new ArrayList<>();
        for (Holder<Block> block : BuiltInRegistries.BLOCK.getTagOrEmpty(BFBlockTags.PRISMARINE_PROPAGATION_SUBSTRATE)) {
            substrates.add(block.value().asItem().getDefaultInstance());
        }
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 27, 27).addItemStacks(substrates);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 6).addItemStack(BFBlocks.PRISMARINE_BLOSSOM.get().asItem().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 27).addItemStack(BFBlocks.SPONGEKIN.get().asItem().getDefaultInstance());
    }

    @Override
    public Component getTitle() {
        return Component.translatable("bountifulfares.prismarine_propagation");
    }

    // JEI 31: getBackground() was removed - size is reported via getWidth()/getHeight() and the
    // background is drawn in draw() (which now takes a GuiGraphicsExtractor).
    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void draw(PropagationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }
}
