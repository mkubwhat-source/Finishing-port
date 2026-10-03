#!/usr/bin/env python3
"""Generate the Bountiful Fares <-> Flavored compatibility recipes of this bundle.

Usage: bundle_compat_recipes.py <bf-port src/main/resources>

Decisions (agreed with the user): both fermenting machines make both mods' fermented goods, and
Bountiful Fares' baked goods can also be baked in Flavored's oven (crafting them stays possible).
Flour is shared through #c:flour, corn through #c:foods/corn (Flavored's recipes are migrated to
those tags by flavored_data_migrate.py).

* Keg (flavored:fermenting) versions of every Bountiful Fares fermentation vessel recipe. The
  vessel needs the drink's container (glass bottle / jar) to collect bottled/jarred results, so
  those keg recipes need that container in the fermenter slot ("fermenter_required"). The others
  use sugar (fungi for foul flesh) as an optional fermenter, like Flavored's own keg recipes.
  Bountiful Fares' fermented spider eye is skipped (Flavored's keg already has one).
* Fermentation vessel (bountifulfares:fermenting) versions of Flavored's keg recipes (the vessel
  has no fermenter slot; its own water + time rules apply). Fermented spider eye is skipped
  (Bountiful Fares' vessel already has one).
* Oven (flavored:baking) versions of Bountiful Fares' breads, pies, tarts, cakes and cookies, in
  Flavored's style: the filling/topping row above the dough (pastry dough for pies and tarts, batter
  for cakes, dough for breads and cookies), 200 ticks.
* Recipe book unlock advancements for the new keg and oven recipes (Flavored's pattern).
"""
import json
import os
import sys

JAR = "bountifulfares:jar"
BOTTLE = "minecraft:glass_bottle"

# (name, ingredient, result, count, color, fermenter, required, category)
KEG = [
    ("apple_cider_jar", "minecraft:apple", "bountifulfares:apple_cider_jar", 1, 0xffe8a5, JAR, True, "food"),
    ("plum_cider_jar", "#c:foods/plums", "bountifulfares:plum_cider_jar", 1, 0xddbfc2, JAR, True, "food"),
    ("hoary_cider_jar", "bountifulfares:hoary_apple", "bountifulfares:hoary_cider_jar", 1, 0xefc9b2, JAR, True, "food"),
    ("elderberry_wine_bottle", "bountifulfares:elderberries", "bountifulfares:elderberry_wine_bottle", 1, 0xd29fc1, BOTTLE, True, "food"),
    ("lapisberry_wine_bottle", "bountifulfares:lapisberries", "bountifulfares:lapisberry_wine_bottle", 1, 0x626ae2, BOTTLE, True, "food"),
    ("mead_bottle", "minecraft:honey_bottle", "bountifulfares:mead_bottle", 1, 0xfff3c8, BOTTLE, True, "food"),
    ("coconut_milk_bottle", "#c:coconut_halves", "bountifulfares:coconut_milk_bottle", 1, 0xd1c6be, BOTTLE, True, "food"),
    ("citrus_essence_from_lemon", "#c:foods/lemons", "bountifulfares:citrus_essence", 2, 0xe7ef95, "minecraft:sugar", False, "food"),
    ("citrus_essence_from_orange", "#c:foods/oranges", "bountifulfares:citrus_essence", 2, 0xe7ef95, "minecraft:sugar", False, "food"),
    ("pickled_beetroot", "minecraft:beetroot", "bountifulfares:pickled_beetroot", 2, 0xb92c40, "minecraft:sugar", False, "food"),
    ("pickled_spongekin", "bountifulfares:spongekin_slice", "bountifulfares:pickled_spongekin", 2, 0x3bc1ab, "minecraft:sugar", False, "food"),
    ("foul_flesh", "minecraft:rotten_flesh", "bountifulfares:foul_flesh", 1, 0x41291f, "#flavored:fungi", False, "misc"),
]

# (name, ingredient, result, color) - Flavored keg recipes for the vessel
VESSEL = [
    ("beer", "flavored:wort", "flavored:beer", 0xc99f4e),
    ("cider", "flavored:apple_juice", "flavored:cider", 0xda9a37),
    ("sweet_berry_wine", "flavored:sweet_berry_juice", "flavored:sweet_berry_wine", 0xbd334a),
    ("glow_berry_wine", "flavored:glow_berry_juice", "flavored:glow_berry_wine", 0xffb034),
    ("soft_cheese", "minecraft:milk_bucket", "flavored:soft_cheese", 0xf6e5d3),
]

PASTRY = "flavored:pastry_dough"
BATTER = "flavored:batter"
DOUGH = "flavored:dough"
# (name, pattern, key, result, count, experience)
OVEN = [(f, [" X ", " # "], {"X": x, "#": PASTRY}, "bountifulfares:" + f, 1, 2.0) for f, x in [
    ("apple_pie", "minecraft:apple"), ("hoary_pie", "bountifulfares:hoary_apple"), ("lemon_pie", "#c:foods/lemons"),
    ("melon_pie", "minecraft:melon_slice"), ("orange_pie", "#c:foods/oranges"), ("plum_pie", "#c:foods/plums"),
    ("elderberry_tart", "#c:foods/elderberries"), ("glow_berry_tart", "minecraft:glow_berries"),
    ("lapisberry_tart", "bountifulfares:lapisberries"), ("passion_fruit_tart", "#c:foods/passion_fruit"),
    ("sweet_berry_tart", "minecraft:sweet_berries"),
]] + [
    ("cocoa_cake", ["CCC", " # "], {"C": "minecraft:cocoa_beans", "#": BATTER}, "bountifulfares:cocoa_cake", 1, 2.0),
    ("coconut_cake", ["CCC", " # "], {"C": "#c:coconut_halves", "#": BATTER}, "bountifulfares:coconut_cake", 1, 2.0),
    ("artisan_bread", [" E ", " # "], {"E": "minecraft:egg", "#": DOUGH}, "bountifulfares:artisan_bread", 1, 1.0),
    ("maize_bread", ["###"], {"#": "#c:foods/corn"}, "bountifulfares:maize_bread", 1, 0.35),
    ("walnut_cookie", [" W ", " # "], {"W": "#c:foods/walnuts", "#": DOUGH}, "bountifulfares:walnut_cookie", 8, 1.0),
    ("artisan_cookie", ["ESE", " # "], {"E": "#c:foods/elderberries", "S": "minecraft:sugar", "#": DOUGH}, "bountifulfares:artisan_cookie", 8, 1.0),
]


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(obj, f, indent=2)
        f.write("\n")


def item_predicate(ingredient):
    return {"items": ingredient}


def unlock_advancement(recipe_id, trigger_ingredient):
    return {
        "parent": "minecraft:recipes/root",
        "criteria": {
            "has_ingredient": {"conditions": {"items": [item_predicate(trigger_ingredient)]}, "trigger": "minecraft:inventory_changed"},
            "has_the_recipe": {"conditions": {"recipes": recipe_id}, "trigger": "minecraft:recipe_unlocked"},
        },
        "requirements": [["has_the_recipe", "has_ingredient"]],
        "rewards": {"recipes": [recipe_id]},
    }


def main(res):
    data = os.path.join(res, "data")
    for name, ing, result, count, color, fermenter, required, category in KEG:
        rid = f"flavored:fermenting/bountifulfares/{name}"
        recipe = {"type": "flavored:fermenting", "category": category, "ingredient": ing, "fermenter": fermenter,
                  "particle_color": f"{color:06x}", "result": {"count": count, "id": result}}
        if required:
            recipe["fermenter_required"] = True
        write(os.path.join(data, "flavored/recipe/fermenting/bountifulfares", name + ".json"), recipe)
        write(os.path.join(data, "flavored/advancement/recipes/fermenting/bountifulfares", name + ".json"), unlock_advancement(rid, ing))
    for name, ing, result, color in VESSEL:
        write(os.path.join(data, "bountifulfares/recipe/fermenting/flavored", name + ".json"),
              {"type": "bountifulfares:fermenting", "ingredient": ing, "particle_color": color, "result": {"id": result}, "result_count": 1})
    for name, pattern, key, result, count, xp in OVEN:
        rid = f"flavored:baking/bountifulfares/{name}"
        write(os.path.join(data, "flavored/recipe/baking/bountifulfares", name + ".json"),
              {"type": "flavored:baking", "bakingtime": 200, "experience": xp, "key": key, "pattern": pattern,
               "result": {"count": count, "id": result}})
        trigger = key.get("#") if key.get("#", "").startswith("flavored:") else next(iter(key.values()))
        write(os.path.join(data, "flavored/advancement/recipes/baking/bountifulfares", name + ".json"), unlock_advancement(rid, trigger))
    print(len(KEG), "keg,", len(VESSEL), "vessel,", len(OVEN), "oven recipes")


if __name__ == "__main__":
    main(sys.argv[1])
