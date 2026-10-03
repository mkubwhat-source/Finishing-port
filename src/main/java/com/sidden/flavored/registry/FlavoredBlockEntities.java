package com.sidden.flavored.registry;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.block.entity.KegBlockEntity;
import com.sidden.flavored.block.entity.MixingBowlBlockEntity;
import com.sidden.flavored.block.entity.OvenBlockEntity;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class FlavoredBlockEntities {
    public static final Supplier<BlockEntityType<KegBlockEntity>> KEG = BFRegistryHelper.registerBlockEntityType(Flavored.MOD_ID, "keg",
            () -> BFRegistryHelper.createBlockEntity(KegBlockEntity::new, FlavoredBlocks.KEG));
    public static final Supplier<BlockEntityType<MixingBowlBlockEntity>> MIXING_BOWL = BFRegistryHelper.registerBlockEntityType(Flavored.MOD_ID, "mixing_bowl",
            () -> BFRegistryHelper.createBlockEntity(MixingBowlBlockEntity::new, FlavoredBlocks.MIXING_BOWL));
    public static final Supplier<BlockEntityType<OvenBlockEntity>> OVEN = BFRegistryHelper.registerBlockEntityType(Flavored.MOD_ID, "oven",
            () -> BFRegistryHelper.createBlockEntity(OvenBlockEntity::new, FlavoredBlocks.OVEN));

    public static void init() {
    }

    private FlavoredBlockEntities() {
    }
}
