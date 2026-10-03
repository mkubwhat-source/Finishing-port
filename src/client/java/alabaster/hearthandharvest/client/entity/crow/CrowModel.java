package alabaster.hearthandharvest.client.entity.crow;

import alabaster.hearthandharvest.HearthAndHarvest;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

/** The crow (26.3: an EntityModel over {@link CrowRenderState}; animations are baked KeyframeAnimations). */
public class CrowModel extends EntityModel<CrowRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HearthAndHarvest.id("crow"), "main");
    private final ModelPart crow;
    final ModelPart head;
    private final ModelPart beak;
    private final ModelPart lowerbeak;
    private final KeyframeAnimation walkingAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation flyingAnimation;
    private final KeyframeAnimation glidingAnimation;
    private final KeyframeAnimation sittingAnimation;

    public CrowModel(ModelPart root) {
        super(root);
        this.crow = root.getChild("crow");
        this.head = this.crow.getChild("head");
        this.beak = this.head.getChild("beak");
        this.lowerbeak = this.beak.getChild("lowerbeak");
        this.walkingAnimation = CrowAnimations.walking.bake(root);
        this.idleAnimation = CrowAnimations.idle.bake(root);
        this.flyingAnimation = CrowAnimations.flying.bake(root);
        this.glidingAnimation = CrowAnimations.gliding.bake(root);
        this.sittingAnimation = CrowAnimations.sitting.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition crow = partdefinition.addOrReplaceChild("crow", CubeListBuilder.create(), PartPose.offset(4.5F, 20.0F, 2.25F));

        PartDefinition body = crow.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-5.0F, -0.2705F, 0.7721F));

        tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(12, 8).addBox(-1.5F, -1.0F, 0.35F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2705F, -0.7721F, -0.6545F, 0.0F, 0.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        torso.addOrReplaceChild("torso_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -1.0F, -4.25F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition legs = crow.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(-5.0F, 1.0F, -1.5F));

        legs.addOrReplaceChild("rightleg", CubeListBuilder.create().texOffs(10, 14).addBox(-0.5F, -2.0F, 0.25F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(9, 18).addBox(-0.5F, 2.0F, -0.75F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 1.0F, 0.0F));

        legs.addOrReplaceChild("leftleg", CubeListBuilder.create().texOffs(10, 14).addBox(-0.5F, -2.0F, 0.25F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(9, 18).addBox(-0.5F, 2.0F, -0.75F, 1.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 1.0F, 0.0F));

        PartDefinition wings = crow.addOrReplaceChild("wings", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition leftwing = wings.addOrReplaceChild("leftwing", CubeListBuilder.create(), PartPose.offset(2.5F, -1.6766F, -3.1459F));

        leftwing.addOrReplaceChild("leftwing_r1", CubeListBuilder.create().texOffs(12, 12).addBox(-4.0F, -2.0F, -4.25F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 2.6766F, 3.1459F, -0.3054F, 0.0F, 0.0F));

        PartDefinition rightwing = wings.addOrReplaceChild("rightwing", CubeListBuilder.create(), PartPose.offset(-2.5F, -1.6766F, -3.1459F));

        rightwing.addOrReplaceChild("rightwing_r1", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -2.0F, -4.25F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 2.6766F, 3.1459F, -0.3054F, 0.0F, 0.0F));

        PartDefinition head = crow.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -1.75F, 0.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -2.25F, -5.25F, 0.3054F, 0.0F, 0.0F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 1.0F));

        beak.addOrReplaceChild("upperbeak", CubeListBuilder.create().texOffs(18, 0).addBox(-0.5F, 0.25F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -1.0F));

        PartDefinition lowerbeak = beak.addOrReplaceChild("lowerbeak", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.0873F, 0.0F, 0.0F));

        lowerbeak.addOrReplaceChild("tounge_r1", CubeListBuilder.create().texOffs(16, 4).addBox(-0.5F, 0.75F, -3.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9962F, 0.0872F, -0.0436F, 0.0F, 0.0F));

        lowerbeak.addOrReplaceChild("partlowerbeak_r1", CubeListBuilder.create().texOffs(18, 4).addBox(-0.5F, 0.25F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9962F, 0.0872F, -0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(CrowRenderState state) {
        super.setupAnim(state);
        this.applyHeadRotation(state.yRot, state.xRot);
        if (state.onShoulder) return;
        if (!state.visuallyFlying) {
            this.walkingAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2f, 5f);
        }
        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1f);
        this.flyingAnimation.apply(state.flyingAnimationState, state.ageInTicks, state.flapAnimationSpeed);
        this.glidingAnimation.apply(state.glidingAnimationState, state.ageInTicks, 1f);
        this.sittingAnimation.apply(state.sittingAnimationState, state.ageInTicks, 1f);
        if (!state.heldItem.isEmpty()) {
            this.lowerbeak.xRot += 0.35F;
        }
    }

    public void translateToBeak(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.crow.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
        this.beak.translateAndRotate(poseStack);
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Mth.clamp(headYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -25f, 45);

        this.head.yRot = headYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch * ((float) Math.PI / 180f);
    }
}
