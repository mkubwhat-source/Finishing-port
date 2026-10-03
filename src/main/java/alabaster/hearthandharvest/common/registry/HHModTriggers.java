package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.common.fd.advancement.CuttingBoardTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;

import java.util.Optional;

public class HHModTriggers {

    public static final HHSimpleTrigger CROW_STOLE_ITEM = register("crow_stole_item", new HHSimpleTrigger());
    public static final HHSimpleTrigger CROW_DELIVERED_ITEM = register("crow_delivered_item", new HHSimpleTrigger());
    public static final HHSimpleTrigger STOMPED_RECIPE = register("stomped_recipe", new HHSimpleTrigger());
    public static final HHSimpleTrigger ATE_SALTED_FOOD = register("ate_salted_food", new HHSimpleTrigger());
    public static final HHSimpleTrigger CLEAVER_KILL = register("cleaver_kill", new HHSimpleTrigger());
    public static final HHSimpleTrigger PITCHFORK_KILL = register("pitchfork_kill", new HHSimpleTrigger());
    public static final HHSimpleTrigger PLUCKED_CHICKEN = register("plucked_chicken", new HHSimpleTrigger());
    public static final HHSimpleTrigger MILKED_GOAT = register("milked_goat", new HHSimpleTrigger());
    public static final HHSimpleTrigger FOUND_CORN_MAZE = register("found_corn_maze", new HHSimpleTrigger());
    public static final HHSimpleTrigger BOTTLED_SYRUP = register("bottled_syrup", new HHSimpleTrigger());
    public static final HHSimpleTrigger LICKED_SALT_AWAY = register("licked_salt_away", new HHSimpleTrigger());
    public static final HHSimpleTrigger FED_SUGAR_CUBES = register("fed_sugar_cubes", new HHSimpleTrigger());
    public static final HHSimpleTrigger HORSESHOE_RINGER = register("horseshoe_ringer", new HHSimpleTrigger());
    public static final HHSimpleTrigger BIG_PIG_LITTER = register("big_pig_litter", new HHSimpleTrigger());
    public static final HHSimpleTrigger BIG_RABBIT_LITTER = register("big_rabbit_litter", new HHSimpleTrigger());
    public static final HHSimpleTrigger BIG_STOMP = register("big_stomp", new HHSimpleTrigger());
    public static final HHSimpleTrigger CASK_AGED = register("cask_aged", new HHSimpleTrigger());
    public static final HHSimpleTrigger SPRINKLER_EXTINGUISHED = register("sprinkler_extinguished", new HHSimpleTrigger());
    public static final HHSimpleTrigger CROW_STASH_FOUND = register("crow_stash_found", new HHSimpleTrigger());
    public static final HHSimpleTrigger CROW_FULL_TRUST = register("crow_full_trust", new HHSimpleTrigger());
    public static final HHSimpleTrigger CROW_FLOCK_ALARM = register("crow_flock_alarm", new HHSimpleTrigger());
    public static final HHSimpleTrigger MANURE_HIT = register("manure_hit", new HHSimpleTrigger());
    public static final HHSimpleTrigger PLAYER_POOPED = register("player_pooped", new HHSimpleTrigger());
    public static final HHSimpleTrigger PUNGENT_SCARED = register("pungent_scared", new HHSimpleTrigger());
    public static final HHSimpleTrigger TEMPTING_CROWD = register("tempting_crowd", new HHSimpleTrigger());
    public static final HHSimpleTrigger FARMERS_HAT_WORN_OUT = register("farmers_hat_worn_out", new HHSimpleTrigger());
    public static final HHSimpleTrigger FERTILIZER_GREW_CROP = register("fertilizer_grew_crop", new HHSimpleTrigger());
    public static final HHSimpleTrigger HOE_AREA_WORK = register("hoe_area_work", new HHSimpleTrigger());
    public static final HHSimpleTrigger FILLED_BOTTLE_RACK = register("filled_bottle_rack", new HHSimpleTrigger());
    public static final HHSimpleTrigger CHICKEN_GLIDE = register("chicken_glide", new HHSimpleTrigger());
    public static final HHSimpleTrigger CROW_PAIR = register("crow_pair", new HHSimpleTrigger());

    public static Criterion<HHSimpleTrigger.TriggerInstance> criterion(HHSimpleTrigger trigger) {
        return trigger.createCriterion(new HHSimpleTrigger.TriggerInstance(Optional.empty()));
    }

    // Farmer's Delight's cutting board trigger (hearthandharvest:use_cutting_board).
    public static final CuttingBoardTrigger USE_CUTTING_BOARD = register("use_cutting_board", new CuttingBoardTrigger());

    private static <T extends CriterionTrigger<?>> T register(String name, T trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, name), trigger);
    }

    public static void init() {
    }
}
