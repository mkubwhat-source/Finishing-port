package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.crafting.BottleCrateRecipe;
import alabaster.hearthandharvest.common.crafting.CaskRecipe;
import alabaster.hearthandharvest.common.crafting.KegRecipe;
import alabaster.hearthandharvest.common.crafting.StompingBasinRecipe;
import alabaster.hearthandharvest.common.fd.crafting.CookingPotRecipe;
import alabaster.hearthandharvest.common.fd.crafting.CuttingBoardRecipe;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class HHModRecipeTypes {
    public static final Supplier<RecipeType<CaskRecipe>> AGING = BFRegistryHelper.registerRecipeType(HearthAndHarvest.MODID, "aging");
    public static final Supplier<RecipeType<StompingBasinRecipe>> STOMPING = BFRegistryHelper.registerRecipeType(HearthAndHarvest.MODID, "stomping");
    public static final Supplier<RecipeType<KegRecipe>> FERMENTING = BFRegistryHelper.registerRecipeType(HearthAndHarvest.MODID, "fermenting");
    public static final Supplier<RecipeType<BottleCrateRecipe>> BOTTLE_CRATE = BFRegistryHelper.registerRecipeType(HearthAndHarvest.MODID, "bottle_crate");
    public static final Supplier<RecipeType<CookingPotRecipe>> COOKING = BFRegistryHelper.registerRecipeType(HearthAndHarvest.MODID, "cooking");
    public static final Supplier<RecipeType<CuttingBoardRecipe>> CUTTING = BFRegistryHelper.registerRecipeType(HearthAndHarvest.MODID, "cutting");

    public static void init() {
    }
}
