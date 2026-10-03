package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.KegBlockEntity;
import alabaster.hearthandharvest.common.crafting.KegRecipe;
import alabaster.hearthandharvest.common.registry.HHDataMaps;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Keg fermenting: an input fluid and up to two items become a result fluid and/or item.
 * <p>
 * HH 1.21.1's KegRecipeCategory pointed at {@code textures/gui/jei/jei_keg_gui.png}, which HH never
 * shipped, so its keg page drew a missing texture. This one is cut from the keg's own screen
 * ({@code keg_gui.png}): both tanks with their scale overlays, the item slots and the bubble columns,
 * which fill like the keg's progress bubbles. When the result fluid can be bottled (HH's fluid -> bottle
 * data map), the keg's container slots show the glass bottle and the bottle it fills.
 */
public class FermentingCategory implements IRecipeCategory<KegRecipe> {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/keg_gui.png");
    // the part of keg_gui.png shown, and where it starts in the keg screen
    private static final int ORIGIN_X = 6;
    private static final int ORIGIN_Y = 18;
    private static final int WIDTH = 164;
    private static final int HEIGHT = 67;

    // positions below are keg screen coordinates minus the origin
    private static final int INPUT_TANK_X = 8 - ORIGIN_X;
    private static final int OUTPUT_TANK_X = 152 - ORIGIN_X;
    private static final int TANK_Y = 20 - ORIGIN_Y;
    private static final int TANK_WIDTH = 16;
    private static final int TANK_HEIGHT = 63;
    private static final int INPUT_SLOT_X = 32 - ORIGIN_X;
    private static final int OUTPUT_SLOT_X = 128 - ORIGIN_X;
    private static final int[] SLOT_Y = {30 - ORIGIN_Y, 57 - ORIGIN_Y};
    private static final int CONTAINER_IN_X = 69 - ORIGIN_X;
    private static final int CONTAINER_OUT_X = 91 - ORIGIN_X;
    private static final int CONTAINER_Y = 23 - ORIGIN_Y;
    private static final int[] BUBBLES_X = {54 - ORIGIN_X, 114 - ORIGIN_X};
    private static final int BUBBLES_Y = 28 - ORIGIN_Y;
    private static final int BUBBLES_WIDTH = 8;
    private static final int BUBBLES_HEIGHT = 47;
    // the empty space under the container slots (the screen's mode button) shows the ferment time
    private static final int TIME_X = 69 - ORIGIN_X;
    private static final int TIME_Y = 44 - ORIGIN_Y;
    private static final int TIME_WIDTH = 38;
    private static final int TIME_HEIGHT = 18;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable inputTankOverlay;
    private final IDrawable outputTankOverlay;
    private final IDrawableAnimated bubbles;

    public FermentingCategory(IGuiHelper helper) {
        background = helper.createDrawable(TEXTURE, ORIGIN_X, ORIGIN_Y, WIDTH, HEIGHT);
        icon = helper.createDrawableItemLike(HHModItems.KEG.get());
        inputTankOverlay = helper.createDrawable(TEXTURE, 192, 0, TANK_WIDTH, TANK_HEIGHT);
        outputTankOverlay = helper.createDrawable(TEXTURE, 208, 0, TANK_WIDTH, TANK_HEIGHT);
        bubbles = helper.drawableBuilder(TEXTURE, 176, 3, BUBBLES_WIDTH, BUBBLES_HEIGHT)
                .buildAnimated(200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public IRecipeType<KegRecipe> getRecipeType() {
        return HHJeiPlugin.FERMENTING;
    }

    @Override
    public Component getTitle() {
        return HHTextUtils.getTranslation("jei.fermenting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, KegRecipe recipe, IFocusGroup focuses) {
        FluidStack inputFluid = recipe.getInputFluid();
        if (!inputFluid.isEmpty()) {
            tank(builder, RecipeIngredientRole.INPUT, INPUT_TANK_X, inputFluid, inputTankOverlay);
        }

        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size() && i < SLOT_Y.length; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, SLOT_Y[i]).add(ingredients.get(i).display());
        }

        recipe.getResultTemplate().ifPresent(result ->
                builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, SLOT_Y[0]).add(result));

        FluidStack resultFluid = recipe.getResultFluid();
        if (!resultFluid.isEmpty()) {
            tank(builder, RecipeIngredientRole.OUTPUT, OUTPUT_TANK_X, resultFluid, outputTankOverlay);
            Item bottle = HHDataMaps.getBottleForFluid(resultFluid.getFluid());
            if (bottle != null) {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY, CONTAINER_IN_X, CONTAINER_Y).add(Items.GLASS_BOTTLE);
                builder.addSlot(RecipeIngredientRole.OUTPUT, CONTAINER_OUT_X, CONTAINER_Y).add(bottle);
            }
        }
    }

    private static void tank(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, FluidStack fluid, IDrawable overlay) {
        int capacity = Math.max(KegBlockEntity.TANK_CAPACITY, fluid.getAmount());
        JeiUtil.addFluid(builder.addSlot(role, x, TANK_Y), fluid)
                .setFluidRenderer(JeiUtil.droplets(capacity), false, TANK_WIDTH, TANK_HEIGHT)
                .setOverlay(overlay, 0, 0);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, KegRecipe recipe, IFocusGroup focuses) {
        if (recipe.getFermentTime() > 0) {
            builder.addText(Component.translatable("gui.jei.category.smelting.time.seconds", recipe.getFermentTime() / 20), TIME_WIDTH, TIME_HEIGHT)
                    .setPosition(TIME_X, TIME_Y)
                    .setTextAlignment(HorizontalAlignment.CENTER)
                    .setTextAlignment(VerticalAlignment.CENTER)
                    .setColor(0xFF404040)
                    .setShadow(false);
        }
    }

    @Override
    public void draw(KegRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics);
        for (int x : BUBBLES_X) {
            bubbles.draw(graphics, x, BUBBLES_Y);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, KegRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        for (int x : BUBBLES_X) {
            if (JeiUtil.inside(x, BUBBLES_Y, BUBBLES_WIDTH, BUBBLES_HEIGHT, mouseX, mouseY)) {
                JeiUtil.addTimeAndExperience(tooltip, recipe.getFermentTime(), recipe.getExperience());
            }
        }
    }
}
