package alabaster.hearthandharvest.common.registry;

import net.hecco.bountifulfares.platform.BFRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;

import alabaster.hearthandharvest.HearthAndHarvest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class HHModSounds {

    // Crow
    public static final Supplier<SoundEvent> CROW_SQUAWK = sound("entities.crow.squawking");

    public static final Supplier<SoundEvent> CROW_HURT = sound("entities.crow.hurt");

    public static final Supplier<SoundEvent> CROW_EAT = sound("entities.crow.eat");

    public static final Supplier<SoundEvent> CROW_STEP = sound("entities.crow.step");

    // Wine Drink
    public static final Supplier<SoundEvent> WINE_DRINK = sound("items.wine.drink");

    // Bottle Place
    public static final Supplier<SoundEvent> BOTTLE_INSERT = sound("blocks.bottle.insert");

    public static final Supplier<SoundEvent> BOTTLE_REMOVE = sound("blocks.bottle.remove");

    public static final Supplier<SoundEvent> STOMPING_BASIN_STOMP = sound("blocks.stomping_basin.stomp");

    // Cask & Keg
    public static final Supplier<SoundEvent> CASK_SEAL = sound("blocks.cask.seal");

    public static final Supplier<SoundEvent> CASK_UNSEAL = sound("blocks.cask.unseal");

    public static final Supplier<SoundEvent> CASK_AGING = sound("blocks.cask.aging");

    public static final Supplier<SoundEvent> KEG_OPEN = sound("blocks.keg.open");

    public static final Supplier<SoundEvent> KEG_FERMENTING = sound("blocks.keg.fermenting");

    // Manure
    public static final Supplier<SoundEvent> MANURE_THROW = sound("items.manure.throw");
    public static final Supplier<SoundEvent> MANURE_HIT = sound("items.manure.hit");
    public static final Supplier<SoundEvent> FART = fixedSound("entities.fart", 8.0F);

    // Lick
    public static final Supplier<SoundEvent> LICK = sound("entities.lick");

    // Pitchfork
    public static final Supplier<SoundEvent> PITCHFORK_THROW = sound("items.pitchfork.throw");
    public static final Supplier<SoundEvent> PITCHFORK_HIT = sound("items.pitchfork.hit");

    // Cleaver
    public static final Supplier<SoundEvent> CLEAVER_THROW = sound("items.cleaver.throw");
    public static final Supplier<SoundEvent> CLEAVER_HIT = sound("items.cleaver.hit");

    // Horseshoe
    public static final Supplier<SoundEvent> HORSESHOE_THROW = sound("items.horseshoe.throw");
    public static final Supplier<SoundEvent> HORSESHOE_HIT = sound("items.horseshoe.hit");

    // Misc
    public static final Supplier<SoundEvent> SALT_LAMP_SWITCH = sound("blocks.salt_lamp.switch");

    // Farmer's Delight (cooking pot, cutting board, cabinets); sound files from FarmersDelightRefabricated.
    public static final Supplier<SoundEvent> BLOCK_COOKING_POT_BOIL = sound("block.cooking_pot.boil");
    public static final Supplier<SoundEvent> BLOCK_COOKING_POT_BOIL_SOUP = sound("block.cooking_pot.boil_soup");
    public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_PLACE = sound("block.cutting_board.place_item");
    public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_REMOVE = sound("block.cutting_board.remove_item");
    public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_CARVE = sound("block.cutting_board.carve_tool");
    public static final Supplier<SoundEvent> BLOCK_CUTTING_BOARD_KNIFE = sound("block.cutting_board.knife_cut");
    public static final Supplier<SoundEvent> BLOCK_CABINET_OPEN = sound("block.cabinet.open");
    public static final Supplier<SoundEvent> BLOCK_CABINET_CLOSE = sound("block.cabinet.close");

    private static Supplier<SoundEvent> sound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, name);
        return BFRegistryHelper.register(HearthAndHarvest.MODID, name, BuiltInRegistries.SOUND_EVENT, () -> SoundEvent.createVariableRangeEvent(id));
    }

    private static Supplier<SoundEvent> fixedSound(String name, float range) {
        Identifier id = Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, name);
        return BFRegistryHelper.register(HearthAndHarvest.MODID, name, BuiltInRegistries.SOUND_EVENT, () -> SoundEvent.createFixedRangeEvent(id, range));
    }

    public static void init() {
    }
}
