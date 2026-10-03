package com.sidden.flavored.recipe;

import net.minecraft.util.StringRepresentable;

public enum FermentingBookCategory implements StringRepresentable {
    FOOD("food"),
    MISC("misc");

    public static final StringRepresentable.EnumCodec<FermentingBookCategory> CODEC = StringRepresentable.fromEnum(FermentingBookCategory::values);
    private final String name;

    FermentingBookCategory(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
