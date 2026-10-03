package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.container.CaskMenu;
import alabaster.hearthandharvest.common.block.entity.container.KegMenu;
import alabaster.hearthandharvest.common.fd.block.entity.container.CookingPotMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class HHModMenuTypes {
    // 1.21.1 opened these with the block position in the extra-data buffer; Fabric's
    // ExtendedMenuType carries it as a typed BlockPos instead.
    public static final Supplier<MenuType<CaskMenu>> CASK_MENU = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID, "cask_menu", BuiltInRegistries.MENU,
            () -> new ExtendedMenuType<>(CaskMenu::new, BlockPos.STREAM_CODEC));
    public static final Supplier<MenuType<KegMenu>> KEG_MENU = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID, "keg_menu", BuiltInRegistries.MENU,
            () -> new ExtendedMenuType<>(KegMenu::new, BlockPos.STREAM_CODEC));
    // Farmer's Delight cooking pot (ported in).
    public static final Supplier<MenuType<CookingPotMenu>> COOKING_POT = BFRegistryHelper.registerTyped(HearthAndHarvest.MODID, "cooking_pot", BuiltInRegistries.MENU,
            () -> new ExtendedMenuType<>(CookingPotMenu::new, BlockPos.STREAM_CODEC));

    public static void init() {
    }
}
