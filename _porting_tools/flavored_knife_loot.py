#!/usr/bin/env python3
"""Generate Flavored's knife-butchering entity loot tables from 26.3 vanilla ones.

Usage: flavored_knife_loot.py <vanilla 26.3 data/minecraft/loot_table/entities dir> <out dir>

1.21.1 Flavored overrode the chicken/cow/mooshroom/pig/sheep loot tables (copies of the 1.21.1
vanilla files) so that a kill with the knife drops the animal's Flavored cut (2-3, cooked if the
animal burned or the weapon smelts loot, +0-1 per Looting level) *instead of* its raw meat.
Rather than shipping stale 1.21.1 copies, this applies exactly those two edits to the 26.3 vanilla
tables: the meat entry gets "not killed with a knife", and a cut pool is appended.
"""
import copy
import json
import os
import sys

MEAT = {
    "chicken": ("minecraft:chicken", "flavored:chicken_drumstick", "flavored:cooked_chicken_drumstick"),
    "cow": ("minecraft:beef", "flavored:ground_beef", "flavored:cooked_ground_beef"),
    "mooshroom": ("minecraft:beef", "flavored:ground_beef", "flavored:cooked_ground_beef"),
    "pig": ("minecraft:porkchop", "flavored:pork_jowl", "flavored:cooked_pork_jowl"),
    "sheep": ("minecraft:mutton", "flavored:mutton_shank", "flavored:cooked_mutton_shank"),
}

KNIFE = {"type": "minecraft:entity_properties", "entity": "direct_attacker",
         "predicate": {"minecraft:equipment": {"mainhand": {"items": "flavored:knife"}}}}
SMELTS = {"type": "minecraft:any_of", "terms": [
    {"type": "minecraft:entity_properties", "entity": "this", "predicate": {"minecraft:flags": {"is_on_fire": True}}},
    {"type": "minecraft:entity_properties", "entity": "direct_attacker", "predicate": {"minecraft:equipment": {"mainhand": {
        "predicates": {"minecraft:enchantments": [{"enchantments": "#minecraft:smelts_loot"}]}}}}},
]}
COUNT = [
    {"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "max": 3, "min": 2}},
    {"type": "minecraft:enchanted_count_increase", "count": {"type": "minecraft:uniform", "max": 1.0, "min": 0.0},
     "enchantment": "minecraft:looting"},
]


def and_condition(entry, cond):
    if "condition" in entry:
        entry["condition"] = {"type": "minecraft:all_of", "terms": [entry["condition"], cond]}
    else:
        entry["condition"] = cond


def main(src, out):
    os.makedirs(out, exist_ok=True)
    for name, (meat, cut, cooked) in MEAT.items():
        table = json.load(open(os.path.join(src, name + ".json")))
        found = 0
        for pool in table["pools"]:
            for entry in pool["entries"]:
                if entry.get("name") == meat:
                    and_condition(entry, {"type": "minecraft:inverted", "term": copy.deepcopy(KNIFE)})
                    found += 1
        if found != 1:
            sys.exit(f"{name}: expected one {meat} entry, found {found}")
        table["pools"].append({"rolls": 1, "entries": [
            {"type": "minecraft:item", "name": cut, "modifier": copy.deepcopy(COUNT),
             "condition": {"type": "minecraft:all_of", "terms": [copy.deepcopy(KNIFE), {"type": "minecraft:inverted", "term": copy.deepcopy(SMELTS)}]}},
            {"type": "minecraft:item", "name": cooked, "modifier": copy.deepcopy(COUNT),
             "condition": {"type": "minecraft:all_of", "terms": [copy.deepcopy(KNIFE), copy.deepcopy(SMELTS)]}},
        ]})
        with open(os.path.join(out, name + ".json"), "w") as f:
            json.dump(table, f, indent=2)
            f.write("\n")
        print("wrote", name)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
