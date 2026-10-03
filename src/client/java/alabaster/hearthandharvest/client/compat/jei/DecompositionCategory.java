package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.FDTags;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import alabaster.hearthandharvest.common.registry.HHModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * Organic compost slowly turning into rich soil, sped up by sunlight, water and compost activators.
 * Farmer's Delight's category (from FarmersDelightRefabricated 26.3), on HH ids. There is no recipe
 * behind it; the plugin registers a single {@link Decomposition} entry.
 */
public class DecompositionCategory implements IRecipeCategory<DecompositionCategory.Decomposition> {
    private static final int WIDTH = 118;
    private static final int HEIGHT = 80;
    private static final int SLOT_SIZE = 22;

    /** The single, data-less "recipe" this category shows. */
    public static final class Decomposition {
    }

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotIcon;

    public DecompositionCategory(IGuiHelper helper) {
        Identifier texture = HearthAndHarvest.id("textures/gui/jei/decomposition.png");
        background = helper.createDrawable(texture, 0, 0, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(HHModItems.RICH_SOIL.get());
        slotIcon = helper.createDrawable(texture, 119, 0, SLOT_SIZE, SLOT_SIZE);
    }

    @Override
    public IRecipeType<Decomposition> getRecipeType() {
        return HHJeiPlugin.DECOMPOSITION;
    }

    @Override
    public Component getTitle() {
        return TextUtils.JEI("decomposition");
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
    public void setRecipe(IRecipeLayoutBuilder builder, Decomposition recipe, IFocusGroup focuses) {
        List<ItemStack> accelerators = new ArrayList<>();
        BuiltInRegistries.BLOCK.get(FDTags.Blocks.COMPOST_ACTIVATORS).ifPresent(tag -> tag.forEach(block -> {
            ItemStack stack = new ItemStack(block.value());
            if (!stack.is(Items.AIR)) accelerators.add(stack);
        }));

        builder.addSlot(RecipeIngredientRole.INPUT, 9, 26).add(HHModItems.ORGANIC_COMPOST.get());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 26).add(HHModItems.RICH_SOIL.get());
        if (!accelerators.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 64, 54).addItemStacks(accelerators);
        }
    }

    @Override
    public void draw(Decomposition recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        slotIcon.draw(graphics, 63, 53);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, Decomposition recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (JeiUtil.inside(40, 38, 11, 11, mouseX, mouseY)) {
            tooltip.add(TextUtils.JEI("decomposition.light"));
        }
        if (JeiUtil.inside(53, 38, 11, 11, mouseX, mouseY)) {
            tooltip.add(TextUtils.JEI("decomposition.fluid"));
        }
        if (JeiUtil.inside(67, 38, 11, 11, mouseX, mouseY)) {
            tooltip.add(TextUtils.JEI("decomposition.accelerators"));
        }
    }
}
