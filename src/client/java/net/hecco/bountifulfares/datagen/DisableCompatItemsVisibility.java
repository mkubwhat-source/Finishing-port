package net.hecco.bountifulfares.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.BountifulFaresUtil;
import net.hecco.bountifulfares.registry.tags.BFItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DisableCompatItemsVisibility extends FabricTagsProvider.ItemTagsProvider {
    public DisableCompatItemsVisibility(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        for (Object i : BountifulFares.COMPAT_MANAGER.CONTENT_TO_INTEGRATION.keySet().stream().map(Supplier::get).toList()) {
            if (i instanceof Item item) {
                tag(BFItemTags.C_HIDDEN_FROM_RECIPE_VIEWERS).addOptional(ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(item)));
            }
        }
    }
}
