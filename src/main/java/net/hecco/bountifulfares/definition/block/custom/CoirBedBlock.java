package net.hecco.bountifulfares.definition.block.custom;

import net.hecco.bountifulfares.definition.block.entity.CoirBedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// 26.3 moved the "does this dimension allow sleeping/respawning here" logic (the old
// canSetSpawn(Level)-then-explode dance this class used to reimplement by hand in
// useWithoutItem) into a new EnvironmentAttribute<BedRule> system: AbstractBedBlock.
// useWithoutItem(...) is now concrete (not abstract) and itself drives that logic, calling the
// abstract destroyOnUse(...)/destroyOnLeave(...)/getBedEnvironmentAttribute() hooks - all three
// of which vanilla's own BedBlock (this class's superclass) already implements concretely
// (confirmed via javap: none of them are abstract on BedBlock), so this subclass no longer needs
// - and can no longer usefully override - useWithoutItem at all; it just inherits the same
// bad-respawn-point explosion behavior every vanilla bed color gets.
// Separately, BedBlock no longer implements EntityBlock itself (confirmed via javap - no
// newBlockEntity anywhere in the BedBlock/AbstractBedBlock hierarchy), so this class implements
// it directly to keep attaching its (currently data-less) CoirBedBlockEntity.
public class CoirBedBlock extends BedBlock implements EntityBlock {
    public CoirBedBlock(Properties settings) {
        super(DyeColor.BROWN, settings);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoirBedBlockEntity(pos, state);
    }
}
