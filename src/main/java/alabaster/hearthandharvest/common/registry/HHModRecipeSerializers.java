package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.crafting.*;
import alabaster.hearthandharvest.common.fd.crafting.CookingPotRecipe;
import alabaster.hearthandharvest.common.fd.crafting.CuttingBoardRecipe;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class HHModRecipeSerializers {
    public static final Supplier<RecipeSerializer<CaskRecipe>> AGING = register("aging", CaskRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<StompingBasinRecipe>> STOMPING = register("stomping", StompingBasinRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<KegRecipe>> FERMENTING = register("fermenting", KegRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<BottleCrateRecipe>> BOTTLE_CRATE = register("bottle_crate", BottleCrateRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<ShapelessRemainderRecipe>> SHAPELESS_REMAINDER = register("shapeless_remainder", ShapelessRemainderRecipe.SERIALIZER);
    public static final Supplier<RecipeSerializer<SaltingRecipe>> SALTING = register("salting", SaltingRecipe.SERIALIZER);
    // Farmer's Delight's cooking pot and cutting board recipes, now hearthandharvest:cooking /
    // hearthandharvest:cutting.
    public static final Supplier<RecipeSerializer<CookingPotRecipe>> COOKING = register("cooking", new RecipeSerializer<>(CookingPotRecipe.Serializer.codec(), CookingPotRecipe.Serializer.streamCodec()));
    public static final Supplier<RecipeSerializer<CuttingBoardRecipe>> CUTTING = register("cutting", new RecipeSerializer<>(CuttingBoardRecipe.Serializer.codec(), CuttingBoardRecipe.Serializer.streamCodec()));

    private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> register(String name, RecipeSerializer<T> serializer) {
        return BFRegistryHelper.registerRecipeSerializer(HearthAndHarvest.MODID, name, serializer);
    }

    public static void init() {
    }
}
