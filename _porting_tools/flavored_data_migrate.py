#!/usr/bin/env python3
"""Migrate Flavored's 1.21.1 data (NeoForge; generated + hand-written) into this 26.3 bundle.

Usage: flavored_data_migrate.py <flavored repo> <bf-port src/main/resources> <vanilla 26.3 data/minecraft>

Reuses data_migrate.py's rules (recipes, loot tables, advancements) and adds:
* The flour/corn merge: ingredient references to flavored:flour / flavored:corn become the common
  tags #c:flour / #c:foods/corn (Bountiful Fares' flour and maize are in them, and so is other
  mods' flour/corn); results, loot and tag entries point at bountifulfares:flour / maize /
  maize_seeds; flavored:corn_bush becomes bountifulfares:maize_crop.
* Dropped: Flavored's corn -> corn seeds recipe (Bountiful Fares has maize -> maize seeds), the
  corn bush loot table, and the barrier overrides of vanilla recipes in data/minecraft/recipe
  (bread, cake, pumpkin pie, soups/stews, fermented spider eye) - in this bundle crafting them
  stays possible and the oven / mixing bowl / keg are extra routes.
* Keg (flavored:fermenting) / mixing recipe fields (fermenter, vessel, liquid) are ingredients.
* Enchantment effect requirements: "condition" -> "type", entity predicate type key.
* Item predicates: {"tag": t} -> {"items": "#t"}.
* Loot pools lose their (ignored in 1.21.1, rejected now) "name" field.
* Village farm processor lists: Flavored's extra crop rule (wheat -> its crop, 1.21.1 copies of the
  vanilla files) is appended to the 26.3 vanilla processor list instead.
* data/neoforge/* and data/flavored/neoforge/* (NeoForge data maps / biome modifiers) are ported in
  code (FlavoredMerges, FlavoredFeatures) and not copied; data_maps/item/mixing_bowl became client
  resource-pack data (assets/minecraft/flavored_mixing_bowl_liquids).
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
import data_migrate as dm  # noqa: E402

ITEM_REMAP = {
    "flavored:flour": "bountifulfares:flour",
    "flavored:corn": "bountifulfares:maize",
    "flavored:corn_seeds": "bountifulfares:maize_seeds",
    "flavored:corn_bush": "bountifulfares:maize_crop",
}
INGREDIENT_REMAP = {"flavored:flour": "#c:flour", "flavored:corn": "#c:foods/corn"}
SKIP = {
    "recipe/crafting/corn_seeds.json",
    "loot_table/blocks/corn_bush.json",
    "advancement/recipes/misc/crafting/corn_seeds.json",
}


def remap_ids(o):
    if isinstance(o, dict):
        return {k: remap_ids(v) for k, v in o.items()}
    if isinstance(o, list):
        return [remap_ids(v) for v in o]
    if isinstance(o, str):
        return ITEM_REMAP.get(o, o)
    return o


def ingredient(i):
    i = dm.ingredient(i)
    if isinstance(i, list):
        return [INGREDIENT_REMAP.get(x, x) for x in i]
    return INGREDIENT_REMAP.get(i, i)


def recipe(r):
    r = dict(r)
    if "key" in r:
        r["key"] = {k: ingredient(v) for k, v in r["key"].items()}
    for k in ("ingredient", "base", "addition", "template", "input", "material", "fermenter", "vessel", "liquid"):
        if k in r:
            r[k] = ingredient(r[k])
    if "ingredients" in r:
        r["ingredients"] = [ingredient(x) for x in r["ingredients"]]
    return remap_ids(r)


def item_predicates(o):
    if isinstance(o, dict):
        if "tag" in o and "items" not in o and set(o) <= {"tag", "count", "components", "predicates"}:
            o = dict(o)
            o["items"] = "#" + o.pop("tag")
        return {k: item_predicates(v) for k, v in o.items()}
    if isinstance(o, list):
        return [item_predicates(v) for v in o]
    return o


def advancement(a):
    return remap_ids(item_predicates(dm.advancement(a)))


def loot_table(t):
    for p in t.get("pools", []):
        p.pop("name", None)
    return remap_ids(dm.loot_table(t))


def enchantment(e):
    def fix(o):
        if isinstance(o, dict):
            o = {k: fix(v) for k, v in o.items()}
            if "condition" in o and "type" not in o:
                o["type"] = o.pop("condition")
            if o.get("type") == "minecraft:entity_properties" and "predicate" in o:
                o["predicate"] = dm.entity_predicate(o["predicate"])
            return o
        if isinstance(o, list):
            return [fix(v) for v in o]
        return o
    return fix(e)


def tag(t):
    return {"replace": t.get("replace", False), "values": remap_ids(t["values"])}


def worldgen_feature(f):
    # configured_feature {type, config} -> feature {type, ...config}; state providers as 26.3
    out = {"type": f["type"]}
    for k, v in f["config"].items():
        out[k] = state_provider(v) if k.endswith("_provider") else v
    return remap_ids(out)


def state_provider(p):
    if p.get("type") == "minecraft:simple_state_provider":
        st = p["state"]
        return {"id": st["Name"], **({"properties": st["Properties"]} if "Properties" in st else {})}
    return p


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(obj, f, indent=2)
        f.write("\n")


def main(repo, out_res, vanilla):
    out = os.path.join(out_res, "data")
    for base in ("src/generated/resources/data", "src/main/resources/data"):
        root_dir = os.path.join(repo, base)
        for root, _, files in os.walk(root_dir):
            for fn in files:
                if not fn.endswith(".json"):
                    continue
                src = os.path.join(root, fn)
                rel = os.path.relpath(src, root_dir)          # e.g. flavored/recipe/mixing/dough.json
                ns, kind_rel = rel.split(os.sep, 1)
                kind = kind_rel.split(os.sep)[0]
                if ns == "neoforge" or kind in ("neoforge", "data_maps"):
                    continue
                if kind_rel in SKIP:
                    continue
                data = json.load(open(src))
                if ns == "minecraft" and kind == "recipe":
                    continue  # barrier overrides of vanilla recipes: not applied (see docstring)
                if ns == "minecraft" and kind == "loot_table":
                    continue  # chest tables: FlavoredLoot; entity tables: flavored_knife_loot.py
                if ns == "minecraft" and kind == "worldgen":
                    continue  # processor lists: appended to 26.3 vanilla below
                if kind == "recipe":
                    data = recipe(data)
                elif kind == "advancement":
                    data = advancement(data)
                elif kind == "loot_table":
                    data = loot_table(data)
                elif kind == "enchantment":
                    data = enchantment(data)
                elif kind == "tags":
                    data = tag(data)
                elif kind == "worldgen":
                    sub = kind_rel.split(os.sep)[1]
                    if sub == "configured_feature":
                        data = worldgen_feature(data)
                        kind_rel = kind_rel.replace("configured_feature", "feature")
                    else:
                        data = remap_ids(data)
                write(os.path.join(out, ns, kind_rel), data)
                print(ns, kind_rel)

    # village farms: Flavored's extra crop per biome, appended to the 26.3 vanilla lists
    extra = {"farm_desert": ("flavored:pepper_bush", 0.2), "farm_plains": ("flavored:tomato_bush", 0.2),
             "farm_savanna": ("bountifulfares:maize_crop", 0.1), "farm_snowy": ("flavored:spinach_bush", 0.8),
             "farm_taiga": ("flavored:garlics", 0.2)}
    for name, (block, prob) in extra.items():
        pl = json.load(open(os.path.join(vanilla, "worldgen/processor_list", name + ".json")))
        rule = next(p for p in pl["processors"] if p["processor_type"] == "minecraft:rule")
        rule["rules"].append({
            "input_predicate": {"block": "minecraft:wheat", "predicate_type": "minecraft:random_block_match", "probability": prob},
            "location_predicate": {"predicate_type": "minecraft:always_true"},
            "output_state": block,
        })
        write(os.path.join(out, "minecraft/worldgen/processor_list", name + ".json"), pl)
        print("processor_list", name)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2], sys.argv[3])
