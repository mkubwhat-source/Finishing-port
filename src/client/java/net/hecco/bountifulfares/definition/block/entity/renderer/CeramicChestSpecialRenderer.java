package net.hecco.bountifulfares.definition.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.entity.CeramicChestBlockEntity;
import net.hecco.bountifulfares.registry.misc.BFModelLayers;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.joml.Vector3fc;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Item-form renderer for the ceramic chest (26.3). Replaces the old {@code BuiltinItemModelMixin}
 * injection into {@code BlockEntityWithoutLevelRenderer.renderByItem(...)} - that class no longer
 * exists; "builtin/entity" item rendering is now a data-driven {@code minecraft:special} item
 * model backed by a {@link SpecialModelRenderer} registered in
 * {@code SpecialModelRenderers.ID_MAPPER} (same {@code LateBoundIdMapper} pattern as item tints),
 * mirroring vanilla's own {@code ChestSpecialRenderer} (disassembled via javap).
 * <p>
 * Unlike vanilla's chest renderer this one is NOT a {@code NoDataSpecialModelRenderer}: the
 * argument extracted per stack is the ceramic dye color ({@code DataComponents.DYED_COLOR},
 * defaulting to {@link CeramicChestBlockEntity#DEFAULT_COLOR}), applied to lid and bottom but
 * not the lock - the same split the block-entity renderer uses.
 * <p>
 * Referenced from {@code assets/bountifulfares/items/ceramic_chest.json} as
 * {@code {"type": "minecraft:special", "base": "bountifulfares:item/ceramic_chest", "model":
 * {"type": "bountifulfares:ceramic_chest"}}}.
 */
public class CeramicChestSpecialRenderer implements SpecialModelRenderer<Integer> {
    public static final Identifier ID = BountifulFares.id("ceramic_chest");
    private static final Identifier TEXTURE = BountifulFares.id("textures/entity/chest/ceramic.png");
    private static final Function<Identifier, RenderType> RENDER_TYPE = id -> RenderTypes.entityCutout(id, false);

    private final ModelPart root;
    private final Model<Void> lid;
    private final Model<Void> lock;
    private final Model<Void> bottom;

    public CeramicChestSpecialRenderer(ModelPart root) {
        this.root = root;
        this.lid = new Model<Void>(root.getChild("lid"), RENDER_TYPE) {};
        this.lock = new Model<Void>(root.getChild("lock"), RENDER_TYPE) {};
        this.bottom = new Model<Void>(root.getChild("bottom"), RENDER_TYPE) {};
    }

    @Override
    public Integer extractArgument(ItemStack stack) {
        return ARGB.opaque(DyedItemColor.getOrDefault(stack, CeramicChestBlockEntity.DEFAULT_COLOR));
    }

    @Override
    public void submit(Integer color, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        int tint = color == null ? CeramicChestBlockEntity.DEFAULT_COLOR : color;
        // NB: submitModel(Model, S, PoseStack, Identifier, light, overlay, int) takes an *outline*
        // color as its last int and always renders with color -1 (javap: it forwards
        // light, overlay, -1, null, arg7). Tinting needs the full overload: (..., RenderType, light,
        // overlay, color, UvMapping, outlineColor), outline 0 = none, as vanilla's ChestRenderer.
        RenderType renderType = RENDER_TYPE.apply(TEXTURE);
        collector.submitModel(this.lid, null, poseStack, renderType, light, overlay, tint, null, outlineColor);
        collector.submitModel(this.lock, null, poseStack, renderType, light, overlay, -1, null, outlineColor);
        collector.submitModel(this.bottom, null, poseStack, renderType, light, overlay, tint, null, outlineColor);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        this.root.getExtentsForGui(new PoseStack(), output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<Integer> bake(SpecialModelRenderer.BakingContext context) {
            return new CeramicChestSpecialRenderer(context.entityModelSet().bakeLayer(BFModelLayers.CERAMIC_CHEST));
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
