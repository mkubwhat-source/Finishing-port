package net.hecco.bountifulfares.registry.misc;

import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

// 26.3: the four sign ModelLayerLocations (ModelLayers.createSignModelName/
// createHangingSignModelName) were removed - signs no longer have entity-model geometry at all;
// the board is a static block model and SignRenderer/HangingSignRenderer only draw the text
// (see BFTemplateModels.registerSign/registerHangingSign). They were never referenced anyway.
public class BFModelLayers {
    public static final ModelLayerLocation CERAMIC_CHEST = new ModelLayerLocation(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "ceramic_chest"), "main");
    public static final ModelLayerLocation CERAMIC_DOUBLE_CHEST_LEFT = new ModelLayerLocation(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "ceramic_double_chest_left"), "main");
    public static final ModelLayerLocation CERAMIC_DOUBLE_CHEST_RIGHT = new ModelLayerLocation(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "ceramic_double_chest_right"), "main");
    public static final ModelLayerLocation TRELLIS_DEFAULT = new ModelLayerLocation(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "trellis_default"), "main");
    public static final ModelLayerLocation TRELLIS_INVERTED = new ModelLayerLocation(Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "trellis_inverted"), "main");
}