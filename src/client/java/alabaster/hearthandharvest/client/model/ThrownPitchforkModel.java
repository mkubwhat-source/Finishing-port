package alabaster.hearthandharvest.client.model;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/** The pitchfork, as thrown and as held (26.3: a plain {@code Model<Unit>}). */
public class ThrownPitchforkModel extends Model<Unit> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HearthAndHarvest.id("thrown_pitchfork"), "main");
	public static final Identifier TEXTURE = HearthAndHarvest.id("textures/entity/pitchfork.png");

	public ThrownPitchforkModel(ModelPart root) {
		super(root, RenderTypes::entityCutout);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -24.0F, -0.5F, 1.0F, 24.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 0).addBox(-2.5F, -25.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 3).addBox(1.5F, -30.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 3).addBox(-2.5F, -30.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 3).addBox(-0.5F, -30.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
			PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}
}
