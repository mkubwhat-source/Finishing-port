#!/usr/bin/env python3
"""Write 26.3 item model definitions (assets/<ns>/items/<id>.json) for items whose model was
hand-written in 1.21.1 (src/main/resources/assets/<ns>/models/item/<id>.json).

Usage: item_defs.py <file with one namespaced item id per line>
  The list is what BFItemDefinitionValidator (datagen) reports as missing.

26.3 renders an item only through its items/<id>.json definition; 1.21.1 used
models/item/<id>.json implicitly and applied runtime ItemColor lambdas. Those lambdas are
reproduced here as tint sources, from BountifulFaresClient (1.21.1):
* registerCeramicBlockColor/registerCeramicItemColor and the artisan brush: DYED_COLOR rgb
  (opaque) else DyeableBlockEntity.DEFAULT_COLOR (opaque white = -1) -> vanilla "minecraft:dye"
  with default -1 (Dye.calculate == DyedItemColor.getOrDefault, identical logic).
* grassy_dirt: GrassColor.getDefaultColor() -> "minecraft:grass" temperature 0.5 / downfall 1.0,
  exactly what vanilla's own grass_block item definition uses for that color.
* apple/orange/lemon/plum (flowering) leaves: FoliageColor.getDefaultColor() -> constant
  -12012264, as vanilla's oak_leaves item definition.
The artisan brush's "bountifulfares:dyed" model override (1 when DYED_COLOR is present) becomes
a "minecraft:has_component" condition on minecraft:dyed_color.
"""
import json
import os
import sys

RES = "src/main/resources/assets"

DYE = {"type": "minecraft:dye", "default": -1}
FOLIAGE = {"type": "minecraft:constant", "value": -12012264}
GRASS = {"type": "minecraft:grass", "downfall": 1.0, "temperature": 0.5}

CERAMIC = {
    "ceramic_tiles", "ceramic_tile_stairs", "ceramic_tile_slab", "cracked_ceramic_tiles",
    "checkered_ceramic_tiles", "checkered_ceramic_tile_stairs", "checkered_ceramic_tile_slab",
    "cracked_checkered_ceramic_tiles", "ceramic_mosaic", "ceramic_mosaic_stairs", "ceramic_mosaic_slab",
    "checkered_ceramic_mosaic", "checkered_ceramic_mosaic_stairs", "checkered_ceramic_mosaic_slab",
    "ceramic_tile_pillar", "ceramic_pressure_plate", "ceramic_button", "ceramic_lever", "ceramic_door",
    "ceramic_trapdoor", "ceramic_dish", "ceramic_chest", "solid_ceramic",
}
FOLIAGE_ITEMS = {f"{p}{f}_leaves" for f in ("apple", "orange", "lemon", "plum") for p in ("", "flowering_")}
TINTS = {**{n: [DYE] for n in CERAMIC}, **{n: [FOLIAGE] for n in FOLIAGE_ITEMS}, "grassy_dirt": [GRASS]}


def model(ref, tints=None):
    m = {"type": "minecraft:model", "model": ref}
    if tints:
        m["tints"] = tints
    return m


def definition(ns, name):
    if ns == "bountifulfares" and name == "artisan_brush":
        return {"model": {
            "type": "minecraft:condition",
            "property": "minecraft:has_component",
            "component": "minecraft:dyed_color",
            "on_true": model("bountifulfares:item/artisan_brush_dyed", [DYE]),
            "on_false": model("bountifulfares:item/artisan_brush", [DYE]),
        }}
    tints = TINTS.get(name) if ns == "bountifulfares" else None
    return {"model": model(f"{ns}:item/{name}", tints)}


def main(listfile):
    problems = []
    for line in open(listfile):
        ident = line.strip()
        if not ident:
            continue
        ns, name = ident.split(":", 1)
        if not os.path.exists(os.path.join(RES, ns, "models", "item", name + ".json")):
            problems.append(ident)
            continue
        out = os.path.join(RES, ns, "items", name + ".json")
        os.makedirs(os.path.dirname(out), exist_ok=True)
        with open(out, "w") as f:
            json.dump(definition(ns, name), f, indent=2)
            f.write("\n")
        print("wrote", out)
    if problems:
        print("NO hand-written models/item for:", " ".join(problems))
        sys.exit(1)


if __name__ == "__main__":
    main(sys.argv[1])
