package net.hecco.bountifulfares.datagen;

import net.minecraft.client.data.models.model.ItemModelUtils;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.FruitLogBlock;
import net.hecco.bountifulfares.definition.block.custom.PicketsBlock;
import com.mojang.math.Quadrant;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;

public class BFTemplateModels {
    //Pickets model generation by DigitalPear
    public static final ModelTemplate TEMPLATE_PICKETS = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_pickets").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE);
    public static final ModelTemplate TEMPLATE_TRELLIS = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_trellis").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE);
    public static final ModelTemplate TEMPLATE_TRELLIS_0 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_planted_trellis_0").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE, TextureSlot.CROP);
    public static final ModelTemplate TEMPLATE_TRELLIS_1 = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_planted_trellis").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE, TextureSlot.CROP, TextureSlot.FRONT);
    public static final ModelTemplate TEMPLATE_TRELLIS_UPSIDE_DOWN = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_planted_trellis_upside_down").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE, TextureSlot.CROP, TextureSlot.FRONT);
    public static void registerPicketsModels(BlockModelGenerators blockStateModelGenerator, Block picket){
        Identifier modelID = TEMPLATE_PICKETS.create(picket, TextureMapping.defaultTexture(picket), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(picket)
                .with(new ConditionBuilder().term(PicketsBlock.NORTH, true).build(), new MultiVariant(WeightedList.of(new Variant(modelID))))
                .with(new ConditionBuilder()
                                .term(PicketsBlock.NORTH, false)
                                .term(PicketsBlock.SOUTH, false)
                                .term(PicketsBlock.EAST, false)
                                .term(PicketsBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(modelID))))


                .with(new ConditionBuilder().term(PicketsBlock.EAST, true).build(), new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder()
                                .term(PicketsBlock.NORTH, false)
                                .term(PicketsBlock.SOUTH, false)
                                .term(PicketsBlock.EAST, false)
                                .term(PicketsBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))


                .with(new ConditionBuilder().term(PicketsBlock.SOUTH, true).build(), new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder()
                                .term(PicketsBlock.NORTH, false)
                                .term(PicketsBlock.SOUTH, false)
                                .term(PicketsBlock.EAST, false)
                                .term(PicketsBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))


                .with(new ConditionBuilder().term(PicketsBlock.WEST, true).build(), new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder()
                                .term(PicketsBlock.NORTH, false)
                                .term(PicketsBlock.SOUTH, false)
                                .term(PicketsBlock.EAST, false)
                                .term(PicketsBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))))

        );
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(picket.asItem()), TextureMapping.layer0(new Material(getItemId(picket))), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(picket, ModelLocationUtils.getModelLocation(picket.asItem()));
    }

    public static void registerFruitLogModels(BlockModelGenerators blockStateModelGenerator, Block log, Block wood, Block leaves) {
        registerFruitLogModels(blockStateModelGenerator, log, wood, leaves, true);
    }

    /**
     * @param foliageTintedItem 1.21.1 registered an item color (FoliageColor.getDefaultColor(),
     *     i.e. vanilla's constant leaf-item tint) for the apple/orange/lemon/plum log and wood
     *     items only - the golden apple ones stayed untinted - so this is now baked into the
     *     item definition as a constant tint where it applied.
     */
    public static void registerFruitLogModels(BlockModelGenerators blockStateModelGenerator, Block log, Block wood, Block leaves, boolean foliageTintedItem) {
        Identifier logID = BuiltInRegistries.BLOCK.getKey(log);
        Identifier woodID = BuiltInRegistries.BLOCK.getKey(wood);
        Identifier template_fruit_log = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(logID.withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_log_noside = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log_noside").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), logID.getPath() + "_noside").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_log_otherside = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log_otherside").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), logID.getPath() + "_otherside").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_log_side = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log_side").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), logID.getPath() + "_side").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_wood_otherside = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_wood_otherside").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), woodID.getPath() + "_otherside").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_wood_side = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_wood_side").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), woodID.getPath() + "_side").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log").withPrefix("item/")), Optional.empty(), TextureSlot.TEXTURE).create(logID.withPrefix("item/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        registerLogItem(blockStateModelGenerator, log, logID.withPrefix("item/"), foliageTintedItem);
        new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_wood").withPrefix("item/")), Optional.empty(), TextureSlot.TEXTURE).create(woodID.withPrefix("item/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        registerLogItem(blockStateModelGenerator, wood, woodID.withPrefix("item/"), foliageTintedItem);

        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(log)
                .with(new ConditionBuilder().term(FruitLogBlock.NORTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.EAST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.SOUTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.WEST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.LEAFY,true).build(), new MultiVariant(WeightedList.of(new Variant(BuiltInRegistries.BLOCK.getKey(leaves).withPrefix("block/")).with(VariantMutator.UV_LOCK.withValue(true))))));

        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(wood)
                .with(new ConditionBuilder().term(FruitLogBlock.NORTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.EAST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.SOUTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.WEST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.LEAFY,true).build(), new MultiVariant(WeightedList.of(new Variant(BuiltInRegistries.BLOCK.getKey(leaves).withPrefix("block/")).with(VariantMutator.UV_LOCK.withValue(true))))));


    }

    public static void registerFruitLogModels(BlockModelGenerators blockStateModelGenerator, Block log, Block wood) {
        Identifier logID = BuiltInRegistries.BLOCK.getKey(log);
        Identifier woodID = BuiltInRegistries.BLOCK.getKey(wood);
        Identifier template_fruit_log = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(logID.withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_log_noside = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log_noside").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), logID.getPath() + "_noside").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_log_otherside = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log_otherside").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), logID.getPath() + "_otherside").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_log_side = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log_side").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), logID.getPath() + "_side").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_wood_otherside = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_wood_otherside").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), woodID.getPath() + "_otherside").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        Identifier template_fruit_wood_side = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_wood_side").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(Identifier.fromNamespaceAndPath(logID.getNamespace(), woodID.getPath() + "_side").withPrefix("block/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_log").withPrefix("item/")), Optional.empty(), TextureSlot.TEXTURE).create(logID.withPrefix("item/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(log, logID.withPrefix("item/"));
        new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "template_fruit_wood").withPrefix("item/")), Optional.empty(), TextureSlot.TEXTURE).create(woodID.withPrefix("item/"), TextureMapping.defaultTexture(log), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(wood, woodID.withPrefix("item/"));

        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(log)
                .with(new ConditionBuilder().term(FruitLogBlock.NORTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.EAST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.SOUTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.WEST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
        );

        blockStateModelGenerator.blockStateOutput.accept(MultiPartGenerator.multiPart(wood)
                .with(new ConditionBuilder().term(FruitLogBlock.NORTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.EAST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.SOUTH, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.WEST, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, true).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.UP, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.UP, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Y).term(FruitLogBlock.DOWN, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R180)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.EAST, false).term(FruitLogBlock.WEST, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.X).term(FruitLogBlock.WEST, false).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))

                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_log_noside).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_otherside).with(VariantMutator.X_ROT.withValue(Quadrant.R90)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.NORTH, false).term(FruitLogBlock.SOUTH, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
                .with(new ConditionBuilder().term(FruitLogBlock.AXIS, Direction.Axis.Z).term(FruitLogBlock.SOUTH, false).term(FruitLogBlock.EAST, false).term(FruitLogBlock.UP, false).term(FruitLogBlock.WEST, false).term(FruitLogBlock.DOWN, false).build(), new MultiVariant(WeightedList.of(new Variant(template_fruit_wood_side).with(VariantMutator.X_ROT.withValue(Quadrant.R270)))))
        );


    }

    public static void registerJackOStrawModels(BlockModelGenerators blockStateModelGenerator, Block block) {
        Identifier lowerModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_lower").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).createWithSuffix(block, "_lower", TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        Identifier upperModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_upper").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).createWithSuffix(block, "_upper", TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        Identifier upperLitModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_upper").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).createWithSuffix(block, "_upper_lit", TextureMapping.defaultTexture(new Material(BuiltInRegistries.BLOCK.getKey(block).withPath((path) -> "block/" + path + "_lit"))), blockStateModelGenerator.modelOutput);
        new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_inventory").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(BuiltInRegistries.BLOCK.getKey(block).withPrefix("item/"), TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(block, BuiltInRegistries.BLOCK.getKey(block).withPrefix("item/"));

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.LIT)
                        .select(DoubleBlockHalf.LOWER, false, new MultiVariant(WeightedList.of(new Variant(lowerModel))))
                        .select(DoubleBlockHalf.LOWER, true, new MultiVariant(WeightedList.of(new Variant(lowerModel))))
                        .select(DoubleBlockHalf.UPPER, false, new MultiVariant(WeightedList.of(new Variant(upperModel))))
                        .select(DoubleBlockHalf.UPPER, true, new MultiVariant(WeightedList.of(new Variant(upperLitModel))))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                        .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                        .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                        .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270)))
        );
    }

    public static void registerUnlitableJackOStrawModels(BlockModelGenerators blockStateModelGenerator, Block block) {
        Identifier lowerModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_lower").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).createWithSuffix(block, "_lower", TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        Identifier upperModel = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_upper").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).createWithSuffix(block, "_upper", TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "jack_o_straw_inventory").withPrefix("block/")), Optional.empty(), TextureSlot.TEXTURE).create(BuiltInRegistries.BLOCK.getKey(block).withPrefix("item/"), TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(block, BuiltInRegistries.BLOCK.getKey(block).withPrefix("item/"));

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF)
                        .select(DoubleBlockHalf.LOWER, new MultiVariant(WeightedList.of(new Variant(lowerModel))))
                        .select(DoubleBlockHalf.UPPER, new MultiVariant(WeightedList.of(new Variant(upperModel))))
                )
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                        .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                        .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                        .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270)))
        );
    }


    public static void registerTrellis(BlockModelGenerators blockStateModelGenerator, Block block){
        Identifier modelID = TEMPLATE_TRELLIS.create(block, TextureMapping.defaultTexture(block), blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.registerSimpleItemModel(block, modelID);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R0)))))
                        .select(Direction.EAST, new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                        .select(Direction.SOUTH, new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                        .select(Direction.WEST, new MultiVariant(WeightedList.of(new Variant(modelID).with(VariantMutator.Y_ROT.withValue(Quadrant.R270))))))
        );
    }
    public static void registerCropTrellis(BlockModelGenerators blockStateModelGenerator, Block trellis, String trellisId, String vinesId, String foliageId, String modId){
        Identifier modelID1 = TEMPLATE_TRELLIS_0.create(trellis, TextureMapping.defaultTexture(trellis).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(modId, "block/" + trellisId))).put(TextureSlot.CROP, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + vinesId + "_0"))), blockStateModelGenerator.modelOutput);
        Identifier modelID2 = TEMPLATE_TRELLIS_1.createWithSuffix(trellis, "_1", TextureMapping.defaultTexture(trellis).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(modId, "block/" + trellisId))).put(TextureSlot.CROP, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + vinesId + "_1"))).put(TextureSlot.FRONT, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + foliageId + "_1"))), blockStateModelGenerator.modelOutput);
        Identifier modelID3 = TEMPLATE_TRELLIS_1.createWithSuffix(trellis, "_2", TextureMapping.defaultTexture(trellis).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(modId, "block/" + trellisId))).put(TextureSlot.CROP, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + vinesId + "_1"))).put(TextureSlot.FRONT, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + foliageId + "_2"))), blockStateModelGenerator.modelOutput);
        Identifier modelID4 = TEMPLATE_TRELLIS_1.createWithSuffix(trellis, "_3", TextureMapping.defaultTexture(trellis).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(modId, "block/" + trellisId))).put(TextureSlot.CROP, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + vinesId + "_1"))).put(TextureSlot.FRONT, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + foliageId + "_3"))), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(trellis)
                .with(PropertyDispatch.initial(BlockStateProperties.AGE_3)
                        .select(0, new MultiVariant(WeightedList.of(new Variant(modelID1))))
                        .select(1, new MultiVariant(WeightedList.of(new Variant(modelID2))))
                        .select(2, new MultiVariant(WeightedList.of(new Variant(modelID3))))
                        .select(3, new MultiVariant(WeightedList.of(new Variant(modelID4)))))
                .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, VariantMutator.Y_ROT.withValue(Quadrant.R0))
                        .select(Direction.EAST, VariantMutator.Y_ROT.withValue(Quadrant.R90))
                        .select(Direction.SOUTH, VariantMutator.Y_ROT.withValue(Quadrant.R180))
                        .select(Direction.WEST, VariantMutator.Y_ROT.withValue(Quadrant.R270)))
        );
    }

    public static void registerDecorTrellis(BlockModelGenerators blockStateModelGenerator, Block trellis, String trellisId, String vinesId, String foliageId, String modId){
        Identifier modelID4 = TEMPLATE_TRELLIS_1.create(trellis, TextureMapping.defaultTexture(trellis).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(modId, "block/" + trellisId))).put(TextureSlot.CROP, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + vinesId))).put(TextureSlot.FRONT, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + foliageId))), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(trellis)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R0)))))
                        .select(Direction.EAST, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                        .select(Direction.SOUTH, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                        .select(Direction.WEST, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R270))))))
        );
    }

    public static void registerUpsideDownDecorTrellis(BlockModelGenerators blockStateModelGenerator, Block trellis, String trellisId, String vinesId, String foliageId, String modId){
        Identifier modelID4 = TEMPLATE_TRELLIS_UPSIDE_DOWN.create(trellis, TextureMapping.defaultTexture(trellis).put(TextureSlot.TEXTURE, new Material(Identifier.fromNamespaceAndPath(modId, "block/" + trellisId))).put(TextureSlot.CROP, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + vinesId))).put(TextureSlot.FRONT, new Material(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "block/" + foliageId))), blockStateModelGenerator.modelOutput);

        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(trellis)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
                        .select(Direction.NORTH, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R0)))))
                        .select(Direction.EAST, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))))
                        .select(Direction.SOUTH, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))))
                        .select(Direction.WEST, new MultiVariant(WeightedList.of(new Variant(modelID4).with(VariantMutator.Y_ROT.withValue(Quadrant.R270))))))
        );
    }

    /** Vanilla's leaf item tint (FoliageColor.getDefaultColor(), opaque), as in items/oak_leaves.json. */
    public static final int DEFAULT_FOLIAGE_ITEM_TINT = -12012264;

    private static void registerLogItem(BlockModelGenerators generators, Block block, Identifier model, boolean foliageTinted) {
        if (foliageTinted) {
            generators.registerSimpleTintedItemModel(block, model, ItemModelUtils.constantTint(DEFAULT_FOLIAGE_ITEM_TINT));
        } else {
            generators.registerSimpleItemModel(block, model);
        }
    }

    public static Identifier getItemId(Block block) {
        Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
        return identifier.withPrefix("item/");
    }

    /**
     * Mirrors vanilla's {@code BlockFamilyProvider.sign(Block)} (disassembled via javap), which
     * can't be called directly without a registered {@code BlockFamily}: standing sign blockstate
     * from four {@code SIGN_ROT_n} models via {@code BlockModelGenerators.createSign}, wall sign
     * from {@code WALL_SIGN} dispatched on {@code ROTATION_HORIZONTAL_FACING_ALT}, flat item model.
     * {@code #all} = {@code block/<wood>_sign}, {@code #particle} = the planks.
     */
    public static void registerSign(BlockModelGenerators blockStateModelGenerator, Block planksBlock, Block signBlock, Block wallSignBlock) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.ALL, TextureMapping.getBlockTexture(signBlock))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(planksBlock));
        MultiVariant rot0 = BlockModelGenerators.plainVariant(ModelTemplates.SIGN_ROT_0.create(ModelLocationUtils.getModelLocation(signBlock, "_rot_0"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant rot1 = BlockModelGenerators.plainVariant(ModelTemplates.SIGN_ROT_1.create(ModelLocationUtils.getModelLocation(signBlock, "_rot_1"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant rot2 = BlockModelGenerators.plainVariant(ModelTemplates.SIGN_ROT_2.create(ModelLocationUtils.getModelLocation(signBlock, "_rot_2"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant rot3 = BlockModelGenerators.plainVariant(ModelTemplates.SIGN_ROT_3.create(ModelLocationUtils.getModelLocation(signBlock, "_rot_3"), textureMapping, blockStateModelGenerator.modelOutput));
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSign(signBlock, rot0, rot1, rot2, rot3));

        MultiVariant wallVariant = BlockModelGenerators.plainVariant(ModelTemplates.WALL_SIGN.create(wallSignBlock, textureMapping, blockStateModelGenerator.modelOutput));
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(wallSignBlock, wallVariant).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));

        blockStateModelGenerator.registerSimpleFlatItemModel(signBlock.asItem());
    }

    /**
     * Mirrors vanilla's {@code BlockFamilyProvider.hangingSign(Block, Block, Variant)} exactly
     * (disassembled via javap): in 26.3 hanging signs are static block models (the
     * {@code HangingSignRenderer} only draws text now), with {@code #all} = the sign's own
     * {@code block/<wood>_hanging_sign} texture (vanilla's repacked 32x32 layout) and
     * {@code #particle} = the stripped log. The vanilla helper is private and needs a full
     * BlockFamily (the wall block comes from the family map), so the wall block is passed in
     * directly here. (The earlier port of this helper used the log texture
     * for {@code #all} too, which would have wrapped the log bark around the sign geometry.)
     */
    public static void registerHangingSign(BlockModelGenerators blockStateModelGenerator, Block logBlock, Block hangingSignBlock, Block wallHangingSignBlock) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.ALL, TextureMapping.getBlockTexture(hangingSignBlock))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(logBlock));

        MultiVariant rot0 = BlockModelGenerators.plainVariant(ModelTemplates.HANGING_SIGN_ROT_0.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_rot_0"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant rot1 = BlockModelGenerators.plainVariant(ModelTemplates.HANGING_SIGN_ROT_1.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_rot_1"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant rot2 = BlockModelGenerators.plainVariant(ModelTemplates.HANGING_SIGN_ROT_2.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_rot_2"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant rot3 = BlockModelGenerators.plainVariant(ModelTemplates.HANGING_SIGN_ROT_3.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_rot_3"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant attachedRot0 = BlockModelGenerators.plainVariant(ModelTemplates.ATTACHED_HANGING_SIGN_ROT_0.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_attached_rot_0"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant attachedRot1 = BlockModelGenerators.plainVariant(ModelTemplates.ATTACHED_HANGING_SIGN_ROT_1.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_attached_rot_1"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant attachedRot2 = BlockModelGenerators.plainVariant(ModelTemplates.ATTACHED_HANGING_SIGN_ROT_2.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_attached_rot_2"), textureMapping, blockStateModelGenerator.modelOutput));
        MultiVariant attachedRot3 = BlockModelGenerators.plainVariant(ModelTemplates.ATTACHED_HANGING_SIGN_ROT_3.create(ModelLocationUtils.getModelLocation(hangingSignBlock, "_attached_rot_3"), textureMapping, blockStateModelGenerator.modelOutput));

        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createHangingSign(hangingSignBlock, rot0, rot1, rot2, rot3, attachedRot0, attachedRot1, attachedRot2, attachedRot3));

        Identifier wallModel = ModelTemplates.WALL_HANGING_SIGN.create(wallHangingSignBlock, textureMapping, blockStateModelGenerator.modelOutput);
        MultiVariant wallVariant = BlockModelGenerators.plainVariant(wallModel);
        blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.dispatch(wallHangingSignBlock, wallVariant).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));

        blockStateModelGenerator.registerSimpleFlatItemModel(hangingSignBlock.asItem());
    }
}
