package net.hecco.bountifulfares;

import net.fabricmc.fabric.api.client.rendering.v1.BlockTintsFactory;
import net.hecco.bountifulfares.definition.block.entity.DyeableBlockEntity;
import net.hecco.bountifulfares.registry.content.BFBlocks;
import net.hecco.bountifulfares.registry.client.TiffinItemModel;
import net.hecco.bountifulfares.definition.block.entity.renderer.CeramicChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.hecco.bountifulfares.platform.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class BountifulFaresClient {

    // NOTE (26.3 color-provider redesign): item tinting is no longer a runtime Java hook - the
    // 1.21.1 ItemColor lambdas are now "tints" in the items' model definitions, using vanilla
    // tint sources: minecraft:dye (ceramics, artisan brush), minecraft:grass (grassy dirt) and
    // minecraft:constant (leaf/log items) - hand-written ones via _porting_tools/item_defs.py,
    // generated ones in BFModelProvider/BFTemplateModels. This class only carries BLOCK tinting.
    public static final List<Pair<BlockTintsFactory, Block>> blockColors = new ArrayList<>();

    static {
        registerCeramicBlockColor(BFBlocks.CERAMIC_TILES.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_TILE_STAIRS.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_TILE_SLAB.get());
        //registerCeramicBlockColor(BFBlocks.CERAMIC_TILE_WALL);
        registerCeramicBlockColor(BFBlocks.CRACKED_CERAMIC_TILES.get());
        registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_TILES.get());
        registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_TILE_STAIRS.get());
        registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_TILE_SLAB.get());
        //registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_TILE_WALL);
        registerCeramicBlockColor(BFBlocks.CRACKED_CHECKERED_CERAMIC_TILES.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_MOSAIC.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_MOSAIC_STAIRS.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_MOSAIC_SLAB.get());
        //registerCeramicBlockColor(BFBlocks.CERAMIC_MOSAIC_WALL);
        registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_MOSAIC.get());
        registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_MOSAIC_STAIRS.get());
        registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_MOSAIC_SLAB.get());
        //registerCeramicBlockColor(BFBlocks.CHECKERED_CERAMIC_MOSAIC_WALL);
        registerCeramicBlockColor(BFBlocks.CERAMIC_TILE_PILLAR.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_PRESSURE_PLATE.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_BUTTON.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_LEVER.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_DOOR.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_TRAPDOOR.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_DISH.get());
        registerCeramicBlockColor(BFBlocks.CERAMIC_CHEST.get());
        registerCeramicBlockColor(BFBlocks.SOLID_CERAMIC.get());

        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.CHAMOMILE_FLOWERS.get()));

        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.GRASSY_DIRT.get()));

        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.APPLE_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.FLOWERING_APPLE_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.APPLE_LOG.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.APPLE_WOOD.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.ORANGE_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.FLOWERING_ORANGE_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.ORANGE_LOG.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.ORANGE_WOOD.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.LEMON_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.FLOWERING_LEMON_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.LEMON_LOG.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.LEMON_WOOD.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.PLUM_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.FLOWERING_PLUM_LEAVES.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.PLUM_LOG.get());
        registerBlockItemColor((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT), BFBlocks.PLUM_WOOD.get());
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT),
                BFBlocks.WALNUT_LEAVES.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT),
                BFBlocks.HANGING_WALNUTS.get()));

        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_CARROTS.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_POTATOES.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_BEETROOTS.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_LEEKS.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_MAIZE.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_ELDERBERRY_VINE.get()));
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor()),
                BFBlocks.WILD_PASSION_FRUIT_VINE.get()));

        // Item-form ceramic chest (replaces the removed BlockEntityWithoutLevelRenderer mixin).
        SpecialModelRenderers.ID_MAPPER.put(CeramicChestSpecialRenderer.ID, CeramicChestSpecialRenderer.Unbaked.MAP_CODEC);
        // Tiffin in-hand / in-GUI food rendering (replaces the removed ItemRenderer/GuiGraphics mixins).
        TiffinItemModel.bootstrap();
    }

    public static void onInitializeClient() {

        // NOTE (26.3 render-type redesign): net.minecraft.client.renderer.RenderType lost its
        // solid()/cutout()/translucent() block-render-type static factories entirely, and
        // Fabric's net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap (which
        // BFClientHelper.setBlockRenderType used to delegate to) was removed from Fabric API
        // with no replacement found anywhere in the 26.3 fabric-api jar. Block render type is no
        // longer an imperative runtime Java registration at all - it's now resolved from the
        // block's own model/texture JSON (confirmed via vanilla's own assets: cutout blocks like
        // leaves/dead_bush/crops need NO explicit declaration at all - cutout is auto-detected
        // from the texture's binary alpha channel; genuinely translucent blocks like glass need
        // an explicit "force_translucent": true flag on the texture entry in their model JSON,
        // e.g. assets/minecraft/models/block/glass.json:
        //   "textures": { "all": { "force_translucent": true, "sprite": "minecraft:block/glass" } }
        // Every block below that only needed CUTOUT should already render correctly with zero
        // changes, since their textures already have the same binary-alpha pixels that made
        // cutout necessary in the first place. The ONE exception is BFBlocks.TINGED_GLASS, which
        // needs TRANSLUCENT - its models/block/tinged_glass.json texture entry now carries
        // "force_translucent": true, as vanilla's stained glass models do. BFClientHelper.setBlockRenderType(...) itself has been removed
        // (see BFClientHelper.java) since there is no Java API left to call.


//        TerraformBoatClientHelper.registerModelLayers(BFBoats.HOARY_BOAT_ID, false);
//        TerraformBoatClientHelper.registerModelLayers(BFBoats.WALNUT_BOAT_ID, false);

        // NOTE (26.3 item-model redesign): the old runtime "dyed" model predicate for the artisan
        // brush is now a minecraft:has_component (minecraft:dyed_color) condition in
        // items/artisan_brush.json.

        // NOTE (26.3 sign redesign): the old Sheets.SIGN_MATERIALS/HANGING_SIGN_MATERIALS puts
        // are gone for good - there is no sign sprite sheet anymore. Sign boards are static block
        // models (textures block/<wood>_sign.png / block/<wood>_hanging_sign.png, repacked from the
        // old entity/signs sheets with a mapping verified pixel-exact against all 11 vanilla woods)
        // generated by BFModelProvider; SignRenderer/HangingSignRenderer only draw the text.
    }

    private static void registerBlockItemColor(BlockTintsFactory color, Block block) {
        blockColors.add(new Pair<>(color, block));
    }

    private static void registerCeramicBlockColor(Block block) {
        // Item-side dyed-ceramic tinting is a minecraft:dye tint in the item definitions.
        blockColors.add(new Pair<>((state, world, pos, list) -> list.add(ARGB.opaque(DyeableBlockEntity.getColor(world, pos))), block));
    }
}
