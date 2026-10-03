package alabaster.hearthandharvest.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.EnumSet;

public enum CaskRecipeBookTab
{
    MEALS("meals"),
    DRINKS("drinks"),
    MISC("misc");

    public static final Codec<CaskRecipeBookTab> CODEC = Codec.STRING.flatXmap(s -> {
        CaskRecipeBookTab tab = findByName(s);
        if (tab == null) {
            return DataResult.error(() -> "Optional field 'recipe_book_tab' does not match any valid tab. If defined, must be one of the following: " + EnumSet.allOf(CaskRecipeBookTab.class));
        }
        return DataResult.success(tab);
    }, tab -> DataResult.success(tab.toString()));

    public final String name;

    CaskRecipeBookTab(String name) {
        this.name = name;
    }

    public static CaskRecipeBookTab findByName(String name) {
        for (CaskRecipeBookTab value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
