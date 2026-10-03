package com.sidden.flavored.client.entity;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.entity.Chocken;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ChockenRenderer extends AgeableMobRenderer<Chocken, ChockenRenderState, ChockenModel> {
    private static final Identifier CHOCKEN_LOCATION = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/entity/chocken.png");

    public ChockenRenderer(EntityRendererProvider.Context context) {
        super(context, new ChockenModel(context.bakeLayer(ChockenModel.LAYER_LOCATION)),
                new ChockenModel(context.bakeLayer(ChockenModel.BABY_LAYER_LOCATION)), 0.3F);
    }

    @Override
    public Identifier getTextureLocation(ChockenRenderState state) {
        return CHOCKEN_LOCATION;
    }

    @Override
    public ChockenRenderState createRenderState() {
        return new ChockenRenderState();
    }

    @Override
    public void extractRenderState(Chocken entity, ChockenRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        float flap = Mth.lerp(partialTicks, entity.oFlap, entity.flap);
        float flapSpeed = Mth.lerp(partialTicks, entity.oFlapSpeed, entity.flapSpeed);
        state.bob = (Mth.sin(flap) + 1.0F) * flapSpeed;
    }
}
