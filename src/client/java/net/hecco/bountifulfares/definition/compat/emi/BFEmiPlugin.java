package net.hecco.bountifulfares.definition.compat.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiReloadLog;
import net.hecco.bountifulfares.definition.item.custom.TiffinItem;
import net.hecco.bountifulfares.definition.recipe.CeramicMassDyeingRecipe;
import net.hecco.bountifulfares.definition.recipe.FermentationRecipe;
import net.hecco.bountifulfares.definition.recipe.MillingRecipe;
import net.hecco.bountifulfares.definition.recipe.TiffinColoringRecipe;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.content.BFMenus;
import net.hecco.bountifulfares.registry.misc.BFRecipes;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import dev.emi.emi.recipe.special.EmiArmorDyeRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO tiffin food crafting recipe (honestly idk how to display that properly, so probably won't be done soon)
@EmiEntrypoint
public class BFEmiPlugin implements EmiPlugin {

    Set<Item> hiddenItems = Stream.concat(
            values(TagKey.create(EmiPort.getItemRegistry().key(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS)).map(Holder::value),
            EmiPort.getDisabledItems()
    ).collect(Collectors.toSet());
    List<Item> dyeableCeramicItems = values(BFItemTags.DYEABLE_CERAMIC_BLOCKS).map(Holder::value).collect(Collectors.toList());

    public static <T> Stream<Holder<T>> values(TagKey<T> key) {
        Minecraft client = Minecraft.getInstance();
        // 26.3: registryOrThrow/getTag -> lookupOrThrow/get
        Registry<T> registry = client.level.registryAccess().lookupOrThrow(key.registry());
        Optional<HolderSet.Named<T>> opt = registry.get(key);
        return opt.map(HolderSet.Named::stream).orElseGet(Stream::of);
    }

    @Override
    public void register(EmiRegistry registry) {
        // 26.3 removed the tag-driven armor dye recipe; the ceramics are now each crafting-dyeable
        // through their own minecraft:crafting_dye recipe (BFRecipeProvider.offerCeramicDyeRecipes).
        // EMI's VanillaPlugin only shows DyeRecipe entries for #cauldron_can_remove_dye items, so
        // show them here with EMI's own armor-dye display, as 1.21.1 EMI did via #minecraft:dyeable.
        for (Item i : dyeableCeramicItems) {
            if (!hiddenItems.contains(i)) {
                addRecipeSafe(registry, () -> new EmiArmorDyeRecipe(i, synthetic("crafting/dying", EmiUtil.subId(i))), null);
            }
        }

        for (CraftingRecipe recipe : getRecipes(registry, RecipeType.CRAFTING)) {
            if (recipe instanceof CeramicMassDyeingRecipe) {
                for (Item i : dyeableCeramicItems) {
                    if (!hiddenItems.contains(i)) {
                        addRecipeSafe(registry, () -> new EmiCeramicMassDyeingRecipe(i, synthetic("crafting/ceramic_mass_dyeing", EmiUtil.subId(i))), recipe);
                    }
                }
            } else if (recipe instanceof TiffinColoringRecipe) {
                for(DyeColor dye : DyeColor.values()) {
                    Item dyeItem = Items.DYE.pick(dye);
                    Identifier sid = synthetic("crafting/tiffin_coloring", EmiUtil.subId(dyeItem));
                    addRecipeSafe(registry, () -> new EmiCraftingRecipe(List.of(EmiIngredient.of(BFItemTags.TIFFINS), EmiStack.of(dyeItem)), EmiStack.of(TiffinItem.getItemFromDye(dye)), sid), recipe);
                }
            }
        }

        registry.addCategory(BFEmiRecipeCategories.FERMENTING);
        registry.addCategory(BFEmiRecipeCategories.MILLING);
        registry.addCategory(BFEmiRecipeCategories.PRISMARINE_PROPAGATION);

        registry.addWorkstation(BFEmiRecipeCategories.FERMENTING, EmiStack.of(BFBlocks.FERMENTATION_VESSEL.get()));
        registry.addWorkstation(BFEmiRecipeCategories.MILLING, EmiStack.of(BFBlocks.GRISTMILL.get()));

        registry.addRecipeHandler(BFMenus.GRISTMILL_SCREEN_HANDLER.get(), new GristmillRecipeHandler());

        for (MillingRecipe recipe : getRecipes(registry, BFRecipes.MILLING.get())) {
            addRecipeSafe(registry, () -> new EmiMillingRecipe(recipe), recipe);
        }
        for (FermentationRecipe recipe : getRecipes(registry, BFRecipes.FERMENTING.get())) {
            addRecipeSafe(registry,
                          () -> EmiFermentationRecipe.quickBuild(recipe), recipe);
        }
        addRecipeSafePropagation(registry, EmiPropagationRecipe::new);
    }

    // 26.3: the client has no RecipeManager; EmiRegistry.getRecipes() is the synchronized recipe
    // set (null before the first sync). This mod's milling/fermenting and special crafting
    // serializers are opted into Fabric's recipe sync in FabricBountifulFares.
    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> Iterable<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        java.util.Collection<RecipeHolder<?>> recipes = registry.getRecipes();
        if (recipes == null) {
            return List.of();
        }
        return recipes.stream().map(RecipeHolder::value).filter(r -> r.getType() == type).map(r -> (T) r).toList();
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Recipe<?> recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing bountifulfares recipe " + (recipe == null ? "" : EmiPort.getId(recipe)));
        }
    }

    private static void addRecipeSafePropagation(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing bountifulfares prismarine propagation recipe");
        }
    }

    private static Identifier synthetic(String type, String name) {
        return EmiPort.id("bountifulfares", "/" + type + "/" + name);
    }
}
