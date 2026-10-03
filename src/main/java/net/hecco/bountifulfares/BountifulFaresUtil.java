package net.hecco.bountifulfares;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.function.Supplier;

public class BountifulFaresUtil {
    public static final List<String> WOOD_TYPES = new ArrayList<>(List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "walnut", "hoary", "crimson", "warped"));
    public static Set<Identifier> allBlockIdsInNamespace(String namespace) {
        Set<Identifier> set = BuiltInRegistries.BLOCK.keySet();
        Set<Identifier> a = new HashSet<>();
        for(Identifier id : set) {
            if(Objects.equals(id.getNamespace(), namespace)) {
                a.add(id);
            }
        }
        return a;
    }

    public static Set<Identifier> allCompatBlockIds() {
        Set<Identifier> set = BuiltInRegistries.BLOCK.keySet();
        Set<Identifier> a = new HashSet<>();
        for(Identifier id : set) {
            if (BountifulFares.COMPAT_MANAGER.CONTENT_ID_TO_INTEGRATION.containsKey(id)) {
                a.add(id);
            }
        }
        return a;
    }

    public static Set<Identifier> allCompatItemIds() {
        Set<Identifier> set = BuiltInRegistries.ITEM.keySet();
        Set<Identifier> a = new HashSet<>();
        for(Identifier id : set) {
            if (BountifulFares.COMPAT_MANAGER.CONTENT_ID_TO_INTEGRATION.containsKey(id)) {
                a.add(id);
            }
        }
        return a;
    }

    public static Set<Identifier> allItemIdsInNamespace(String namespace) {
        Set<Identifier> set = BuiltInRegistries.ITEM.keySet();
        Set<Identifier> a = new HashSet<>();
        for(Identifier id : set) {
            if(Objects.equals(id.getNamespace(), namespace)) {
                a.add(id);
            }
        }
        return a;
    }

    public static String toSentenceCase(String s) {
        String words[] = s.split("[\\s|_]");
        StringBuilder capitalizeWord = new StringBuilder();
        for(String w : words){
            String first = w.substring(0,1);
            String afterfirst = w.substring(1);
            capitalizeWord
                    .append(first.toUpperCase())
                    .append(afterfirst)
                    .append(" ");
        }
        return capitalizeWord.toString().trim();
    }
}
