package net.hecco.bountifulfares.lib.compat;

import net.minecraft.core.HolderGetter;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Ported in-tree from NexusLib's {@code ModIntegration} as part of removing the NexusLib
 * dependency during the 26.3 Fabric port.
 */
public interface ModIntegration {

    CompatManager getCompatManager();

    List<String> modIds();

    void registerContent();

    default Supplier<?> registerContent(Identifier location, Supplier<?> content) {
        getCompatManager().CONTENT_ID_TO_INTEGRATION.put(location, this);
        getCompatManager().CONTENT_TO_INTEGRATION.put(content, this);
        return content;
    }

    default boolean shouldCreateDatapack() {return false;}

    @Nullable
    default String getDatapackName() {return null;}

    /**
     * {@code items} is the datagen registry lookup's item getter - 26.3 recipe builders resolve
     * tags through the getter they are given, and {@code BuiltInRegistries.ITEM} has no tags
     * bound during datagen ("Missing tag"), so compat recipes must use this one.
     */
    default void recipeGeneration(RecipeOutput output, HolderGetter<Item> items) {}

    default Criterion<InventoryChangeTrigger.TriggerInstance> has(Identifier id) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, BuiltInRegistries.ITEM.getValue(id)).build())));
    }
    default Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, tag).build())));
    }
}
