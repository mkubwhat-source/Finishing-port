package net.hecco.bountifulfares.mixin.compat;

import com.mojang.datafixers.util.Either;
import net.hecco.bountifulfares.compat.woodlandmansions.WoodlandMansionsCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Lets {@link WoodlandMansionsCompat} add Bountiful Fares' golden apple tree room to the "Woodland Mansions" pack's jigsaw mansions. */
@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {
    @Shadow @Final protected Either<Identifier, StructureTemplate> template;

    @Inject(method = "place", at = @At("RETURN"))
    private void bountifulfares$afterPlace(StructureTemplateManager templates, WorldGenLevel level, StructureManager structureManager,
                                         ChunkGenerator generator, BlockPos position, BlockPos referencePos, Rotation rotation,
                                         BoundingBox chunkBox, RandomSource random, LiquidSettings liquidSettings, boolean keepJigsaws,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            this.template.left().ifPresent(id -> WoodlandMansionsCompat.afterPiecePlaced(id, templates, level, position, rotation, chunkBox));
        }
    }
}
