package alabaster.hearthandharvest.client.compat.jei;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.client.fd.gui.CookingPotScreen;
import alabaster.hearthandharvest.client.gui.CaskGUI;
import alabaster.hearthandharvest.common.block.entity.container.CaskMenu;
import alabaster.hearthandharvest.common.crafting.CaskRecipe;
import alabaster.hearthandharvest.common.crafting.KegRecipe;
import alabaster.hearthandharvest.common.crafting.StompingBasinRecipe;
import alabaster.hearthandharvest.common.fd.block.entity.container.CookingPotMenu;
import alabaster.hearthandharvest.common.fd.crafting.CookingPotRecipe;
import alabaster.hearthandharvest.common.fd.crafting.CuttingBoardRecipe;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.registry.HHModMenuTypes;
import alabaster.hearthandharvest.common.registry.HHModRecipeTypes;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
import com.sidden.flavored.registry.FlavoredItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.hecco.bountifulfares.registry.client.BFClientRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Hearth and Harvest's JEI plugin (26.3 / JEI 31 port of HH 1.21.1's {@code integration.jei.JEIPlugin}
 * plus the Farmer's Delight parts HH now carries, from FarmersDelightRefabricated 26.3's plugin):
 * cask aging, keg fermenting, stomping, cooking pot, cutting board and organic compost decomposition.
 * <p>
 * Recipes come from Fabric's client recipe sync (see BFClientRecipes; the serializers are opted in by
 * {@code HearthAndHarvest.onInitialize}). The bottle crate and shapeless-remainder crafting recipes need
 * no code here: they are synced too, so JEI's own crafting category picks them up.
 */
@JeiPlugin
public class HHJeiPlugin implements IModPlugin {
    public static final IRecipeType<CaskRecipe> AGING = IRecipeType.create(HearthAndHarvest.MODID, "aging", CaskRecipe.class);
    public static final IRecipeType<KegRecipe> FERMENTING = IRecipeType.create(HearthAndHarvest.MODID, "fermenting", KegRecipe.class);
    public static final IRecipeType<StompingBasinRecipe> STOMPING = IRecipeType.create(HearthAndHarvest.MODID, "stomping", StompingBasinRecipe.class);
    public static final IRecipeType<CookingPotRecipe> COOKING = IRecipeType.create(HearthAndHarvest.MODID, "cooking", CookingPotRecipe.class);
    public static final IRecipeType<CuttingBoardRecipe> CUTTING = IRecipeType.create(HearthAndHarvest.MODID, "cutting", CuttingBoardRecipe.class);
    public static final IRecipeType<DecompositionCategory.Decomposition> DECOMPOSITION =
            IRecipeType.create(HearthAndHarvest.MODID, "decomposition", DecompositionCategory.Decomposition.class);

    @Override
    public Identifier getPluginUid() {
        return HearthAndHarvest.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new AgingCategory(gui),
                new FermentingCategory(gui),
                new StompingCategory(gui),
                new CookingCategory(gui),
                new CuttingCategory(gui),
                new DecompositionCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AGING, BFClientRecipes.getAllOfType(HHModRecipeTypes.AGING.get()));
        registration.addRecipes(FERMENTING, BFClientRecipes.getAllOfType(HHModRecipeTypes.FERMENTING.get()));
        registration.addRecipes(STOMPING, BFClientRecipes.getAllOfType(HHModRecipeTypes.STOMPING.get()));
        registration.addRecipes(COOKING, BFClientRecipes.getAllOfType(HHModRecipeTypes.COOKING.get()));
        registration.addRecipes(CUTTING, BFClientRecipes.getAllOfType(HHModRecipeTypes.CUTTING.get()));
        registration.addRecipes(DECOMPOSITION, List.of(new DecompositionCategory.Decomposition()));

        // Hearth and Harvest's info pages
        info(registration, "salt", HHModItems.SALT);
        info(registration, "watering_can", HHModItems.WATERING_CAN);
        info(registration, "tree_tapper", HHModItems.TREE_TAPPER);
        info(registration, "sap_bucket", HHModItems.SAP_BUCKET);
        info(registration, "nest", HHModItems.NEST);
        info(registration, "scarecrow", HHModItems.SCARECROW);
        info(registration, "trough", HHModItems.TROUGH);
        info(registration, "sprinkler", HHModItems.SPRINKLER);
        info(registration, "salt_block", HHModItems.SALT_BLOCK);
        info(registration, "jug", HHModItems.JUG);
        info(registration, "cask", HHModItems.CASK);
        info(registration, "keg", HHModItems.KEG);
        // Everything that ages into vintages in the cask. 1.21.1 listed HH's wines and pickles; the
        // merged wines (flavored sweet/glow berry wine, bountifulfares mead) and pickled beetroot
        // replace HH's own ones in this bundle.
        registration.addItemStackInfo(stacks(
                HHModItems.RED_GRAPE_WINE.get(), HHModItems.GREEN_GRAPE_WINE.get(), HHModItems.BLUEBERRY_WINE.get(),
                HHModItems.RASPBERRY_WINE.get(), HHModItems.CHERRY_WINE.get(), FlavoredItems.SWEET_BERRY_WINE.get(),
                FlavoredItems.GLOW_BERRY_WINE.get(), HHModItems.MELON_WINE.get(), net.hecco.bountifulfares.registry.content.BFItems.MEAD_BOTTLE.get(),
                HHModItems.HARD_CIDER.get(), HHModItems.ROOT_BEER.get(), net.hecco.bountifulfares.registry.content.BFItems.PICKLED_BEETROOT.get(),
                HHModItems.PICKLED_CABBAGE.get(), HHModItems.PICKLED_CARROTS.get(), HHModItems.PICKLED_ONIONS.get(),
                HHModItems.PICKLED_POTATOES.get()),
                HHTextUtils.getTranslation("jei.info.vintage_aging"));
        registration.addIngredientInfo(Items.FEATHER, HHTextUtils.getTranslation("jei.info.pluck_chickens"));
        registration.addItemStackInfo(stacks(HHModItems.WILD_RED_GRAPES.get(), HHModItems.RED_GRAPES.get(),
                HHModItems.WILD_GREEN_GRAPES.get(), HHModItems.GREEN_GRAPES.get()),
                HHTextUtils.getTranslation("jei.info.wild_grapes"));

        // Farmer's Delight's info pages, for the FD items this bundle carries (its dough, ham, knives,
        // tomatoes, rice and wild carrots/potatoes/beetroots are not part of it)
        registration.addIngredientInfo(HHModItems.STRAW.get(), TextUtils.JEI("info.straw"));
        registration.addItemStackInfo(stacks(HHModItems.WILD_CABBAGES.get(), HHModItems.CABBAGE.get(), HHModItems.CABBAGE_LEAF.get()),
                TextUtils.JEI("info.wild_cabbages"));
        registration.addItemStackInfo(stacks(HHModItems.WILD_ONIONS.get(), HHModItems.ONION.get()),
                TextUtils.JEI("info.wild_onions"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(AGING, HHModItems.CASK.get());
        registration.addCraftingStation(FERMENTING, HHModItems.KEG.get());
        registration.addCraftingStation(STOMPING, HHModItems.STOMPING_BASIN.get());
        registration.addCraftingStation(COOKING, HHModItems.COOKING_POT.get());
        registration.addCraftingStation(CUTTING, HHModItems.CUTTING_BOARD.get());
        registration.addCraftingStation(DECOMPOSITION, HHModItems.ORGANIC_COMPOST.get());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // the cask's two bubble columns and the cooking pot's progress arrow
        registration.addRecipeClickArea(CaskGUI.class, 10, 31, 9, 24, AGING);
        registration.addRecipeClickArea(CaskGUI.class, 58, 30, 9, 24, AGING);
        registration.addRecipeClickArea(CookingPotScreen.class, 89, 25, 24, 17, COOKING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // cask: 4 input slots, then 4 output slots, then the player inventory
        registration.addRecipeTransferHandler(CaskMenu.class, HHModMenuTypes.CASK_MENU.get(), AGING, 0, 4, 8, 36);
        // cooking pot: 6 input slots, then meal display, container and output, then the player inventory
        registration.addRecipeTransferHandler(CookingPotMenu.class, HHModMenuTypes.COOKING_POT.get(), COOKING, 0, 6, 9, 36);
    }

    private static void info(IRecipeRegistration registration, String key, Supplier<? extends ItemLike> item) {
        registration.addIngredientInfo(item.get(), HHTextUtils.getTranslation("jei.info." + key));
    }

    private static List<ItemStack> stacks(ItemLike... items) {
        return Stream.of(items).map(ItemStack::new).toList();
    }
}
