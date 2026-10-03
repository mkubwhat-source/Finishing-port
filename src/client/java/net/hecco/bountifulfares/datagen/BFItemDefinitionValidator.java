package net.hecco.bountifulfares.datagen;

import com.sidden.flavored.Flavored;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Datagen-time check that every item this mod registers (in its own namespace or in a compat
 * namespace it registers content under, including the bundled Flavored's) has an {@code assets/<ns>/items/<id>.json} item model
 * definition, either generated or hand-written in {@code src/main/resources}.
 * <p>
 * 26.3 renders an item from that definition only; 1.21.1 implicitly used
 * {@code models/item/<id>.json}, so every item whose model was hand-written needs a definition
 * now, and a missing one only shows up in game as the missing-model cube. Fabric's own
 * strict validation cannot be used for this: it only sees generated files, so it would also
 * flag every hand-written blockstate. Must be added after the model provider.
 */
// Also checks en_us has every owned item's name key and every potion name key (see run()).
public class BFItemDefinitionValidator implements DataProvider {
    private final FabricPackOutput output;

    public BFItemDefinitionValidator(FabricPackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            Path generated = this.output.getOutputFolder();
            Path resources = generated.resolveSibling("resources");
            // Compat content this mod owns is registered under the other mod's namespace; the
            // integrations also register datagen-only stand-ins for that mod's *own* items (planks,
            // wool, ...) which that mod provides assets for, so only owned ids are checked.
            Set<Identifier> ownedCompatContent = BountifulFares.COMPAT_MANAGER.CONTENT_ID_TO_INTEGRATION.keySet();
            List<Identifier> missing = BuiltInRegistries.ITEM.keySet().stream()
                    .filter(id -> OWN_NAMESPACES.contains(id.getNamespace()) || ownedCompatContent.contains(id))
                    .filter(id -> {
                        String rel = "assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json";
                        return !Files.exists(generated.resolve(rel)) && !Files.exists(resources.resolve(rel));
                    })
                    .sorted()
                    .toList();
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Items without an items/<id>.json model definition: " + missing);
            }

            // Every owned item's name key (and each potion's per-container name key) must exist in
            // en_us - 26.3 moved several of these (e.g. potion names now come from Potion#name).
            // Bountiful Fares' en_us is generated (BFLangProvider); the bundled Flavored's is hand-written.
            JsonObject en = readLang(generated.resolve("assets/" + BountifulFares.MOD_ID + "/lang/en_us.json"));
            readLang(resources.resolve("assets/" + Flavored.MOD_ID + "/lang/en_us.json")).entrySet()
                    .forEach(entry -> en.add(entry.getKey(), entry.getValue()));
            List<String> untranslated = new java.util.ArrayList<>();
            BuiltInRegistries.ITEM.entrySet().stream()
                    .filter(e -> OWN_NAMESPACES.contains(e.getKey().identifier().getNamespace()) || ownedCompatContent.contains(e.getKey().identifier()))
                    .filter(e -> !DatagenOnlyItems.IDS.contains(e.getKey().identifier()))
                    .map(e -> e.getValue().getDescriptionId())
                    .filter(key -> !en.has(key))
                    .forEach(untranslated::add);
            BuiltInRegistries.POTION.entrySet().stream()
                    .filter(e -> e.getKey().identifier().getNamespace().equals(BountifulFares.MOD_ID))
                    .forEach(e -> {
                        for (String container : List.of("potion", "splash_potion", "lingering_potion", "tipped_arrow")) {
                            String key = "item.minecraft." + container + ".effect." + e.getValue().name();
                            if (!en.has(key)) untranslated.add(key);
                        }
                    });
            if (!untranslated.isEmpty()) {
                throw new IllegalStateException("Missing en_us translations: " + untranslated.stream().sorted().toList());
            }
        });
    }

    private static final Set<String> OWN_NAMESPACES = Set.of(BountifulFares.MOD_ID, Flavored.MOD_ID);

    private static JsonObject readLang(Path lang) {
        try (var reader = Files.newBufferedReader(lang)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    @Override
    public String getName() {
        return "Item model definition check";
    }
}
