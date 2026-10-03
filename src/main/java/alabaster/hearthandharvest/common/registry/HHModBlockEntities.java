package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.*;
import alabaster.hearthandharvest.common.fd.block.entity.CabinetBlockEntity;
import alabaster.hearthandharvest.common.fd.block.entity.CookingPotBlockEntity;
import alabaster.hearthandharvest.common.fd.block.entity.CuttingBoardBlockEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.hecco.bountifulfares.platform.BFRegistryHelper.createBlockEntity;

public class HHModBlockEntities {
    public static final Supplier<BlockEntityType<NestBlockEntity>> NEST = register("nest", () -> createBlockEntity(NestBlockEntity::new, HHModBlocks.NEST));
    public static final Supplier<BlockEntityType<JugBlockEntity>> JUG = register("jug_tile", () -> createBlockEntity(JugBlockEntity::new, HHModBlocks.JUG));
    public static final Supplier<BlockEntityType<KegBlockEntity>> KEG = register("keg_tile", () -> createBlockEntity(KegBlockEntity::new, HHModBlocks.KEG));
    public static final Supplier<BlockEntityType<CaskBlockEntity>> CASK = register("cask_tile", () -> createBlockEntity(CaskBlockEntity::new, HHModBlocks.CASK));
    public static final Supplier<BlockEntityType<BottleRackBlockEntity>> BOTTLE_RACK = register("bottle_rack", () -> {
        List<Supplier<Block>> blocks = new ArrayList<>(List.of(
                HHModBlocks.OAK_BOTTLE_RACK, HHModBlocks.BIRCH_BOTTLE_RACK, HHModBlocks.SPRUCE_BOTTLE_RACK,
                HHModBlocks.JUNGLE_BOTTLE_RACK, HHModBlocks.ACACIA_BOTTLE_RACK, HHModBlocks.DARK_OAK_BOTTLE_RACK,
                HHModBlocks.MANGROVE_BOTTLE_RACK, HHModBlocks.BAMBOO_BOTTLE_RACK, HHModBlocks.CHERRY_BOTTLE_RACK,
                HHModBlocks.CRIMSON_BOTTLE_RACK, HHModBlocks.WARPED_BOTTLE_RACK));
        if (FabricLoader.getInstance().isModLoaded("crabbersdelight")) {
            blocks.add(HHModBlocks.PALM_BOTTLE_RACK);
        }
        return createBlockEntity(BottleRackBlockEntity::new, blocks);
    });
    public static final Supplier<BlockEntityType<JarBlockEntity>> JAR = register("jar_tile", () -> createBlockEntity(JarBlockEntity::new, HHModBlocks.EMPTY_JAR_DISPLAY));
    public static final Supplier<BlockEntityType<CrateBlockEntity>> CRATE = register("crate", () -> createBlockEntity(CrateBlockEntity::new, HHModBlocks.CRATE));
    public static final Supplier<BlockEntityType<StompingBasinBlockEntity>> STOMPING_BASIN = register("stomping_basin_tile", () -> createBlockEntity(StompingBasinBlockEntity::new, HHModBlocks.STOMPING_BASIN));
    public static final Supplier<BlockEntityType<TroughBlockEntity>> TROUGH = register("trough_tile", () -> createBlockEntity(TroughBlockEntity::new, HHModBlocks.TROUGH));
    public static final Supplier<BlockEntityType<TreeTapperBlockEntity>> TREE_TAPPER = register("tree_tapper_tile", () -> createBlockEntity(TreeTapperBlockEntity::new, HHModBlocks.TREE_TAPPER));
    public static final Supplier<BlockEntityType<BasinBlockEntity>> BASIN = register("basin_tile", () -> createBlockEntity(BasinBlockEntity::new, HHModBlocks.BASIN));
    public static final Supplier<BlockEntityType<SprinklerBlockEntity>> SPRINKLER = register("sprinkler_tile", () -> createBlockEntity(SprinklerBlockEntity::new, HHModBlocks.SPRINKLER));

    // Farmer's Delight block entities (ported in). 1.21.1 HH added its drawer and half cabinets to
    // FD's cabinet type with BlockEntityTypeAddBlocksEvent; the type is HH's own now, so they're
    // simply listed here with the full cabinets.
    public static final Supplier<BlockEntityType<CookingPotBlockEntity>> COOKING_POT = register("cooking_pot", () -> createBlockEntity(CookingPotBlockEntity::new, HHModBlocks.COOKING_POT));
    public static final Supplier<BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD = register("cutting_board", () -> createBlockEntity(CuttingBoardBlockEntity::new, HHModBlocks.CUTTING_BOARD));
    public static final Supplier<BlockEntityType<CabinetBlockEntity>> CABINET = register("cabinet", () -> {
        List<Supplier<Block>> cabinets = new ArrayList<>(List.of(
                HHModBlocks.OAK_CABINET, HHModBlocks.SPRUCE_CABINET, HHModBlocks.BIRCH_CABINET, HHModBlocks.JUNGLE_CABINET,
                HHModBlocks.ACACIA_CABINET, HHModBlocks.DARK_OAK_CABINET, HHModBlocks.MANGROVE_CABINET, HHModBlocks.CHERRY_CABINET,
                HHModBlocks.BAMBOO_CABINET, HHModBlocks.CRIMSON_CABINET, HHModBlocks.WARPED_CABINET,
                HHModBlocks.DRAWER,
                HHModBlocks.OAK_HALF_CABINET, HHModBlocks.BIRCH_HALF_CABINET, HHModBlocks.SPRUCE_HALF_CABINET,
                HHModBlocks.JUNGLE_HALF_CABINET, HHModBlocks.ACACIA_HALF_CABINET, HHModBlocks.DARK_OAK_HALF_CABINET,
                HHModBlocks.MANGROVE_HALF_CABINET, HHModBlocks.BAMBOO_HALF_CABINET, HHModBlocks.CHERRY_HALF_CABINET,
                HHModBlocks.CRIMSON_HALF_CABINET, HHModBlocks.WARPED_HALF_CABINET));
        if (FabricLoader.getInstance().isModLoaded("crabbersdelight")) {
            cabinets.add(HHModBlocks.PALM_HALF_CABINET);
        }
        return createBlockEntity(CabinetBlockEntity::new, cabinets);
    });

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> Supplier<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
        return BFRegistryHelper.registerBlockEntityType(HearthAndHarvest.MODID, name, type);
    }

    public static void init() {
        // 1.21.1 renamed wine_rack -> bottle_rack; the alias is in HHRegistryAliases.
    }
}
