#!/usr/bin/env python3
"""Migrate Bountiful Fares' hand-written (non-datagen) 1.21.1 data JSON to the 26.3 formats.

Usage: data_migrate.py <old data dir> <new data dir>
  e.g. data_migrate.py ../bountiful-fares/.../src/main/resources/data src/main/resources/data

Converts, in place under <new>, every file under advancement/, recipe/ and loot_table/ taken
from <old>. Rules, each checked against vanilla 26.3's own data and the 26.3 codecs (javap):

Recipes
* Ingredient is a string ("ns:item" or "#ns:tag") or a list of them; {"item": x} -> "x",
  {"tag": t} -> "#t".

Advancements
* recipe_unlocked's "recipe" -> "recipes".
* A context-entity predicate is one loot condition object, not a list; a list of several becomes
  {"type": "minecraft:all_of", "terms": [...]}. Loot conditions use "type" (was "condition").
* EntityPredicate's "type" -> "minecraft:entity_type".
* Only root advancements (no parent) may carry display.background ("Only advancement roots can
  have background"); 1.21.1 ignored it on children, so it is dropped there. A kept background is
  a ClientAsset id relative to textures/ without ".png".

Loot tables
* "conditions": [..] -> "condition": x (all_of for several); "functions": [..] -> "modifier"
  (a single object, or a list); each function's "function" -> "type", conditions likewise.
* block_state_property {block, properties} -> match_block {blocks, state}.
* Uniform/constant number providers with integral bounds are written as ints (vanilla 26.3 does);
  "bonus_rolls": 0.0 (the default) is dropped.
"""
import json
import os
import sys


def ingredient(i):
    if isinstance(i, list):
        return [ingredient(x) for x in i]
    if isinstance(i, str):
        return i
    if "item" in i:
        return i["item"]
    if "tag" in i:
        return "#" + i["tag"]
    raise ValueError("unhandled ingredient %r" % i)


def recipe(r):
    r = dict(r)
    if "key" in r:
        r["key"] = {k: ingredient(v) for k, v in r["key"].items()}
    for k in ("ingredient", "ingredients", "base", "addition", "template", "input", "material"):
        if k in r:
            r[k] = ingredient(r[k])
    return r


def intify(v):
    if isinstance(v, float) and v.is_integer():
        return int(v)
    if isinstance(v, dict) and v.get("type") in ("minecraft:uniform", "minecraft:constant"):
        return {k: intify(x) for k, x in v.items()}
    return v


def condition(c):
    c = dict(c)
    c["type"] = c.pop("condition")
    if c["type"] == "minecraft:block_state_property":
        c = {"type": "minecraft:match_block", "blocks": c["block"], **({"state": c["properties"]} if "properties" in c else {})}
    if c["type"] == "minecraft:entity_properties" and "predicate" in c:
        c["predicate"] = entity_predicate(c["predicate"])
    for k in ("term",):
        if k in c:
            c[k] = condition(c[k])
    if "terms" in c:
        c["terms"] = [condition(x) for x in c["terms"]]
    return c


def conditions(lst):
    lst = [condition(c) for c in lst]
    return lst[0] if len(lst) == 1 else {"type": "minecraft:all_of", "terms": lst}


def entity_predicate(p):
    p = dict(p)
    if "type" in p:
        p["minecraft:entity_type"] = p.pop("type")
    return p


def function(f):
    f = dict(f)
    f["type"] = f.pop("function")
    if "conditions" in f:
        f["condition"] = conditions(f.pop("conditions"))
    if f["type"] == "minecraft:set_count":
        f["count"] = intify(f["count"])
        if f.get("add") is False:
            del f["add"]
    return f


def modifiers(lst):
    lst = [function(x) for x in lst]
    return lst[0] if len(lst) == 1 else lst


def entry(e):
    e = dict(e)
    if "conditions" in e:
        e["condition"] = conditions(e.pop("conditions"))
    if "functions" in e:
        e["modifier"] = modifiers(e.pop("functions"))
    if "children" in e:
        e["children"] = [entry(x) for x in e["children"]]
    return e


def pool(p):
    out = {}
    if "conditions" in p:
        out["condition"] = conditions(p["conditions"])
    out["entries"] = [entry(e) for e in p["entries"]]
    if "functions" in p:
        out["modifier"] = modifiers(p["functions"])
    out["rolls"] = intify(p["rolls"])
    if p.get("bonus_rolls") not in (None, 0, 0.0):
        out["bonus_rolls"] = intify(p["bonus_rolls"])
    return out


def loot_table(t):
    out = {"type": t["type"], "pools": [pool(p) for p in t.get("pools", [])]}
    if "functions" in t:
        out["modifier"] = modifiers(t["functions"])
    if "random_sequence" in t:
        out["random_sequence"] = t["random_sequence"]
    return out


def advancement(a):
    a = json.loads(json.dumps(a))
    for crit in a.get("criteria", {}).values():
        c = crit.get("conditions", {})
        if crit["trigger"] == "minecraft:recipe_unlocked" and "recipe" in c:
            c["recipes"] = c.pop("recipe")
        for k in ("entity", "player", "parent", "partner", "child", "victim", "killer", "source"):
            if isinstance(c.get(k), list):
                c[k] = conditions(c[k])
    d = a.get("display")
    if d and "background" in d:
        if "parent" in a:
            del d["background"]
        else:
            bg = d["background"]
            ns, path = bg.split(":", 1)
            if path.startswith("textures/") and path.endswith(".png"):
                d["background"] = ns + ":" + path[len("textures/"):-len(".png")]
    return a


CONVERTERS = {"recipe": recipe, "loot_table": loot_table, "advancement": advancement}


def main(old, new):
    for root, _, files in os.walk(old):
        rel = os.path.relpath(root, old)
        parts = rel.split(os.sep)
        kind = parts[1] if len(parts) > 1 else None
        if kind not in CONVERTERS:
            continue
        for fn in files:
            if not fn.endswith(".json"):
                continue
            src = json.load(open(os.path.join(root, fn)))
            dst = os.path.join(new, rel, fn)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            json.dump(CONVERTERS[kind](src), open(dst, "w"), indent=2)
            print(kind, os.path.join(rel, fn))


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
