package com.sidden.flavored.item;

import net.minecraft.world.item.component.Tool;

import java.util.List;

/** The whisk is a plain item (durability 2000, enchantable 14) with this tool component. */
public final class WhiskItem {
    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 1, true);
    }

    private WhiskItem() {
    }
}
