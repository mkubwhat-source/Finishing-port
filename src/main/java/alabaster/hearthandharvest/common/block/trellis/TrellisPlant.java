package alabaster.hearthandharvest.common.block.trellis;

import net.minecraft.util.StringRepresentable;

public enum TrellisPlant implements StringRepresentable {
    NONE, VINE, ROSE, RED_GRAPE, GREEN_GRAPE;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public boolean isGrape() {
        return this == RED_GRAPE || this == GREEN_GRAPE;
    }

    public boolean usesAge() {
        return isGrape();
    }
}
