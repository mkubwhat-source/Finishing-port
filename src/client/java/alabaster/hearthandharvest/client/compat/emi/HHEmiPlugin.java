package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import alabaster.hearthandharvest.common.registry.HHModItems;
import alabaster.hearthandharvest.common.registry.HHModRecipeTypes;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiReloadLog;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Hearth and Harvest's EMI plugin (new in this port; 1.21.1 HH only shipped a JEI plugin). Same six
 * categories and layouts as {@code HHJeiPlugin}, with the same category ids, so EMI's JEI bridge (JEMI)
 * skips the JEI versions when both viewers are installed. Native EMI also gets fluid amounts right
 * (JEMI showed HH's 250 mB as "250 L") and shows the keg's ferment time. JEI's info pages still reach
 * EMI through JEMI when JEI is installed.
 */
@EmiEntrypoint
public class HHEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory AGING = category("aging", HHModItems.CASK, HHTextUtils.getTranslation("jei.aging"));
    public static final EmiRecipeCategory FERMENTING = category("fermenting", HHModItems.KEG, HHTextUtils.getTranslation("jei.fermenting"));
    public static final EmiRecipeCategory STOMPING = category("stomping", HHModItems.STOMPING_BASIN, HHTextUtils.getTranslation("jei.stomping"));
    public static final EmiRecipeCategory COOKING = category("cooking", HHModItems.COOKING_POT, TextUtils.JEI("cooking"));
    public static final EmiRecipeCategory CUTTING = category("cutting", HHModItems.CUTTING_BOARD, TextUtils.JEI("cutting"));
    public static final EmiRecipeCategory DECOMPOSITION = category("decomposition", HHModItems.RICH_SOIL, TextUtils.JEI("decomposition"));

    /** A category titled like its JEI counterpart (EMI would otherwise want an emi.category.* key). */
    private static EmiRecipeCategory category(String path, Supplier<Item> icon, Component name) {
        EmiStack stack = EmiStack.of(icon.get());
        return new EmiRecipeCategory(HearthAndHarvest.id(path), stack, stack, EmiRecipeSorting.compareOutputThenInput()) {
            @Override
            public Component getName() {
                return name;
            }
        };
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(AGING);
        registry.addCategory(FERMENTING);
        registry.addCategory(STOMPING);
        registry.addCategory(COOKING);
        registry.addCategory(CUTTING);
        registry.addCategory(DECOMPOSITION);
        registry.addWorkstation(AGING, EmiStack.of(HHModItems.CASK.get()));
        registry.addWorkstation(FERMENTING, EmiStack.of(HHModItems.KEG.get()));
        registry.addWorkstation(STOMPING, EmiStack.of(HHModItems.STOMPING_BASIN.get()));
        registry.addWorkstation(COOKING, EmiStack.of(HHModItems.COOKING_POT.get()));
        registry.addWorkstation(CUTTING, EmiStack.of(HHModItems.CUTTING_BOARD.get()));
        registry.addWorkstation(DECOMPOSITION, EmiStack.of(HHModItems.ORGANIC_COMPOST.get()));

        add(registry, HHModRecipeTypes.AGING.get(), EmiAgingRecipe::new);
        add(registry, HHModRecipeTypes.FERMENTING.get(), EmiFermentingRecipe::new);
        add(registry, HHModRecipeTypes.STOMPING.get(), EmiStompingRecipe::new);
        add(registry, HHModRecipeTypes.COOKING.get(), EmiCookingRecipe::new);
        add(registry, HHModRecipeTypes.CUTTING.get(), EmiCuttingRecipe::new);
        registry.addRecipe(new EmiDecompositionRecipe());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> void add(EmiRegistry registry, RecipeType<T> type, BiFunction<T, Identifier, EmiRecipe> factory) {
        Collection<RecipeHolder<?>> recipes = registry.getRecipes();
        if (recipes == null) return;
        for (RecipeHolder<?> holder : recipes) {
            if (holder.value().getType() != type) continue;
            Identifier id = holder.id().identifier();
            try {
                registry.addRecipe(factory.apply((T) holder.value(), id));
            } catch (Throwable e) {
                EmiReloadLog.warn("Exception thrown when parsing hearthandharvest recipe " + id);
            }
        }
    }
}
