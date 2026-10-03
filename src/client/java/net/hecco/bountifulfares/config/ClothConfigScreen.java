package net.hecco.bountifulfares.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.FabricBountifulFares;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

/**
 * The Cloth Config screen behind Mod Menu's config button. Kept in its own class so that nothing
 * touches Cloth Config classes unless Cloth Config is installed (it is optional at runtime; see
 * {@link BountifulFaresModMenu}).
 */
final class ClothConfigScreen {
    static Screen buildConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable(() -> net.hecco.bountifulfares.config.FabricBFConfig.save(FabricBountifulFares.CONFIG))
                .setTitle(Component.translatable("bountifulfares.configuration.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        Arrays.stream(Category.values()).filter(category -> !category.isChild()).forEach(category -> buildCategory(builder, entryBuilder, category));
        return builder.build();
    }

    private static void buildCategory(ConfigBuilder builder, ConfigEntryBuilder entryBuilder, Category category) {
        ConfigCategory configCategory = builder.getOrCreateCategory(Component.translatable(category.text()));
        Arrays.stream(category.entries()).forEach(entry -> configCategory.addEntry(entry.build(entryBuilder)));
        Arrays.stream(category.children()).forEach(entry -> configCategory.addEntry(buildSubCategory(entryBuilder.startSubCategory(Component.translatable(entry.text())), entryBuilder, entry)));

    }

    private static SubCategoryListEntry buildSubCategory(SubCategoryBuilder subCategoryBuilder, ConfigEntryBuilder entryBuilder, Category category) {
        Arrays.stream(category.entries()).forEach(entry -> subCategoryBuilder.add(entry.build(entryBuilder)));
        Arrays.stream(category.children()).forEach(entry -> subCategoryBuilder.add(buildSubCategory(entryBuilder.startSubCategory(Component.translatable(entry.text())), entryBuilder, entry)));
        return subCategoryBuilder.build();
    }
}
