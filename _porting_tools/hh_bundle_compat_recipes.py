#!/usr/bin/env python3
"""Generate the Hearth and Harvest <-> Bountiful Fares / Flavored fermenter recipes of this bundle.

Usage: hh_bundle_compat_recipes.py <bf-port src/main/resources>

User decision 2: the HH keg, Flavored's keg and BF's fermentation vessel stay separate machines, and
each one also makes the other two's drinks (the cask stays HH-only, so vintages stay HH's).

* HH keg (hearthandharvest:fermenting) versions of the BF vessel's and Flavored keg's drinks: item
  recipes (the keg's two ingredient slots, result in its output slots), like HH's own cheese recipes.
  The vessel collects bottled/jarred drinks in their container, so those keg recipes take the
  container as a second ingredient; Flavored's wort/juice is already bottled. Ferment time/experience
  as HH's own wines (1200 ticks, 0.35). Flavored's sweet/glow berry wines need nothing: they are HH's
  sweet/glow berry wine fluids' bottles (decision 13), so the keg already ferments them.
* BF vessel (bountifulfares:fermenting) and Flavored keg (flavored:fermenting) versions of the HH keg's
  wines (juice -> wine; the keg works on the fluids, these machines on the bottles). Flavored keg
  versions use sugar as the optional fermenter like Flavored's own wines. HH's hard cider (apple
  cider fluid = Flavored apple juice) is left to the HH keg: both other machines already turn apple
  juice into Flavored's cider.
* Recipe unlock advancements for the keg recipes (both kegs unlock their recipe books that way).
Particle colours of the HH wines are their fluid textures' average colours.
"""
import json
import os
import sys

JAR = "bountifulfares:jar"
BOTTLE = "minecraft:glass_bottle"

# HH keg versions: (name, ingredients, result)
HH_KEG = [
    ("apple_cider_jar", ["minecraft:apple", JAR], "bountifulfares:apple_cider_jar"),
    ("plum_cider_jar", ["#c:foods/plums", JAR], "bountifulfares:plum_cider_jar"),
    ("hoary_cider_jar", ["bountifulfares:hoary_apple", JAR], "bountifulfares:hoary_cider_jar"),
    ("elderberry_wine_bottle", ["bountifulfares:elderberries", BOTTLE], "bountifulfares:elderberry_wine_bottle"),
    ("lapisberry_wine_bottle", ["bountifulfares:lapisberries", BOTTLE], "bountifulfares:lapisberry_wine_bottle"),
    ("mead_bottle", ["minecraft:honey_bottle"], "bountifulfares:mead_bottle"),
    ("beer", ["flavored:wort"], "flavored:beer"),
    ("cider", ["flavored:apple_juice"], "flavored:cider"),
]

# HH keg wines for the vessel and Flavored's keg: (name, juice, wine, colour)
HH_WINES = [
    ("blueberry_wine", "hearthandharvest:blueberry_juice", "hearthandharvest:blueberry_wine", 0x0a0576),
    ("cherry_wine", "hearthandharvest:cherry_juice", "hearthandharvest:cherry_wine", 0x6d0d19),
    ("green_grape_wine", "hearthandharvest:green_grape_juice", "hearthandharvest:green_grape_wine", 0x5e7605),
    ("red_grape_wine", "hearthandharvest:red_grape_juice", "hearthandharvest:red_grape_wine", 0x2e0a32),
    ("raspberry_wine", "hearthandharvest:raspberry_juice", "hearthandharvest:raspberry_wine", 0x89113d),
]


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(obj, f, indent=2)
        f.write("\n")


def unlock_advancement(recipe_id, trigger_ingredient):
    return {
        "parent": "minecraft:recipes/root",
        "criteria": {
            "has_ingredient": {"conditions": {"items": [{"items": trigger_ingredient}]}, "trigger": "minecraft:inventory_changed"},
            "has_the_recipe": {"conditions": {"recipes": recipe_id}, "trigger": "minecraft:recipe_unlocked"},
        },
        "requirements": [["has_the_recipe", "has_ingredient"]],
        "rewards": {"recipes": [recipe_id]},
    }


def main(res):
    data = os.path.join(res, "data")
    for name, ings, result in HH_KEG:
        rid = "hearthandharvest:fermenting/compat/" + name
        write(os.path.join(data, "hearthandharvest/recipe/fermenting/compat", name + ".json"),
              {"type": "hearthandharvest:fermenting", "experience": 0.35, "ferment_time": 1200, "ingredients": ings,
               "recipe_book_tab": "drinks", "result_item": {"id": result}})
        write(os.path.join(data, "hearthandharvest/advancement/recipes/fermenting/compat", name + ".json"),
              unlock_advancement(rid, ings[0]))
    for name, juice, wine, color in HH_WINES:
        write(os.path.join(data, "bountifulfares/recipe/fermenting/hearthandharvest", name + ".json"),
              {"type": "bountifulfares:fermenting", "ingredient": juice, "particle_color": color, "result": {"id": wine}, "result_count": 1})
        rid = "flavored:fermenting/hearthandharvest/" + name
        write(os.path.join(data, "flavored/recipe/fermenting/hearthandharvest", name + ".json"),
              {"type": "flavored:fermenting", "category": "food", "fermenter": "minecraft:sugar", "ingredient": juice,
               "particle_color": "%06x" % color, "result": {"id": wine}})
        write(os.path.join(data, "flavored/advancement/recipes/fermenting/hearthandharvest", name + ".json"),
              unlock_advancement(rid, juice))
    print(len(HH_KEG), "HH keg,", len(HH_WINES), "vessel,", len(HH_WINES), "Flavored keg recipes")


if __name__ == "__main__":
    main(sys.argv[1])
