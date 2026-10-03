package alabaster.hearthandharvest.client.entity.crow;

import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

/** 26.3 render state for crows (1.21.1 models read the entity directly). */
public class CrowRenderState extends HoldingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState flyingAnimationState = new AnimationState();
    public final AnimationState glidingAnimationState = new AnimationState();
    public final AnimationState sittingAnimationState = new AnimationState();
    public boolean visuallyFlying;
    public float flapAnimationSpeed = 1.0F;
    public float flightPitch;
    public float flightRoll;
    public float bbHeight;
    /** Drawn on a player's shoulder: static pose, only the head follows the player's look. */
    public boolean onShoulder;
}
