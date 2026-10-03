package alabaster.hearthandharvest.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

/** Extra data HH attaches to vanilla render states. */
public final class HHRenderStateKeys {
    public static final RenderStateDataKey<Boolean> HOLDING_CHICKEN = RenderStateDataKey.create(() -> "hearthandharvest:holding_chicken");
    /** On a chicken: it is being held overhead by a player (chicken gliding). */
    public static final RenderStateDataKey<Boolean> HELD_CHICKEN = RenderStateDataKey.create(() -> "hearthandharvest:held_chicken");
    /** Which shoulders carry a crow: bit 1 = left, bit 2 = right (HHModAttachments.SHOULDER_CROWS). */
    public static final RenderStateDataKey<Integer> SHOULDER_CROWS = RenderStateDataKey.create(() -> "hearthandharvest:shoulder_crows");

    private HHRenderStateKeys() {}
}
