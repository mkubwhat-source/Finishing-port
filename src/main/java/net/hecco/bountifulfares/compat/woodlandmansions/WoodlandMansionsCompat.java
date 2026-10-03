package net.hecco.bountifulfares.compat.woodlandmansions;

import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.util.RandomSource;

/**
 * Compatibility with darkstarworks' "Woodland Mansions" data pack/mod (the lukidonu-derived
 * mansions shown alongside Grand Capitals; mod id {@code woodland_mansions}).
 * <p>
 * Bountiful Fares adds its golden apple tree room to woodland mansions through
 * {@code WoodlandMansionPieces} (see {@code WoodlandMansionFirstFloorMixin}). That pack replaces
 * {@code minecraft:mansion} with a jigsaw structure built from five large fixed templates, so the
 * vanilla room generator never runs and the room would never appear. Instead, whenever the pack's
 * back-right ground-floor wing ({@code mansions:mansion/bottom_back_right}) is placed, a secret
 * cellar version of the room ({@code bountifulfares:compat/woodland_mansions/golden_grove_cellar},
 * built in the pack's style by {@code _porting_tools/golden_grove_cellar.py}) is placed beneath
 * it, reached through a trapdoor set flush into the floor of that wing's workshop.
 * <p>
 * The cellar is placed with the wing's own rotation, clipped to the same chunk box (so it
 * generates chunk by chunk exactly like the wing - its footprint lies inside the wing's), and with
 * the pack's own {@code mansions:mansion_texture} weathering processors so its wood and stone age
 * like the rest of the mansion.
 */
public final class WoodlandMansionsCompat {
    private static final Identifier BACK_RIGHT_WING = Identifier.fromNamespaceAndPath("mansions", "mansion/bottom_back_right");
    private static final Identifier CELLAR = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "compat/woodland_mansions/golden_grove_cellar");
    private static final ResourceKey<StructureProcessorList> MANSION_TEXTURE =
            ResourceKey.create(Registries.PROCESSOR_LIST, Identifier.fromNamespaceAndPath("mansions", "mansion_texture"));
    /** Cellar template origin in the wing's local (unrotated) coordinates: the trapdoor, at cellar-local (1, 12, 1), lands on the workshop floor at wing-local (20, 0, 14). */
    private static final BlockPos CELLAR_OFFSET = new BlockPos(19, -12, 13);

    private WoodlandMansionsCompat() {
    }

    public static void afterPiecePlaced(Identifier template, StructureTemplateManager templates, WorldGenLevel level,
                                        BlockPos piecePos, Rotation rotation, BoundingBox chunkBox) {
        if (!BACK_RIGHT_WING.equals(template)) {
            return;
        }
        StructureTemplate cellar = templates.get(CELLAR).orElse(null);
        if (cellar == null) {
            return;
        }
        BlockPos origin = piecePos.offset(StructureTemplate.transform(CELLAR_OFFSET, Mirror.NONE, rotation, BlockPos.ZERO));
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setBoundingBox(chunkBox)
                .setKnownShape(true)
                .setFinalizeEntities(true)
                .setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        level.registryAccess().lookupOrThrow(Registries.PROCESSOR_LIST).get(MANSION_TEXTURE)
                .ifPresent(list -> list.value().list().forEach(settings::addProcessor));
        cellar.placeInWorld(level, origin, origin, settings, RandomSource.create(level.getSeed() ^ origin.asLong()), Block.UPDATE_CLIENTS);
    }
}
