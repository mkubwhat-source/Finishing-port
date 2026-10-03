#!/usr/bin/env python3
"""Migrate Bountiful Fares' 1.21.1 worldgen JSON to the 26.3 schema.

Usage: worldgen_migrate.py <old worldgen dir> <new worldgen dir>
  e.g. worldgen_migrate.py ../bountiful-fares/.../data/bountifulfares/worldgen src/main/resources/data/bountifulfares/worldgen

Reads <old>/configured_feature/*.json and <old>/placed_feature/*.json (1.21.1 format) and writes
<new>/feature/*.json and <new>/placed_feature/*.json (26.3 format). Every rule below was taken
from vanilla 26.3's own data (data/minecraft/worldgen in the 26.3 jar) and the 26.3 codecs
(field names read from the class files with javap):

* The "worldgen/configured_feature" registry is now "worldgen/feature", and a feature's
  "config" object is flattened into the feature itself (Feature is a record per type now).
* BlockState is {"id": ..., "properties": {...}} (was {"Name": ..., "Properties": {...}});
  a bare "namespace:block" string is also accepted where the field is a BlockState.
* A BlockStateProvider field is a Holder: a string is a *registry reference* (e.g.
  "minecraft:soil_beneath_tree"), so an inline single state is always written as the
  {"id": ...} object. simple_state_provider is gone (the bare state is the provider),
  weighted_state_provider -> "minecraft:weighted", randomized_int_state_provider ->
  "minecraft:randomized_int", rule_based now needs its "type".
* Trees: dirt_provider + force_dirt -> below_trunk_provider. 1.21.1 replaced the block below the
  trunk with dirt_provider unless (force_dirt was false and) it was in #dirt but not grass_block
  or mycelium; that exact "keep" set is 26.3's #cannot_replace_below_tree_trunk, which vanilla's
  "minecraft:soil_beneath_tree" provider uses (-> dirt). So force_dirt=false with a dirt provider
  maps to that reference, force_dirt=false with any other provider to the same rule with that
  provider, and force_dirt=true to the provider itself.
* random_patch / flower (RandomPatchFeature) no longer exist. Vanilla folded each patch into its
  placed feature: count(tries), offset x/z trapezoid(-xz_spread..xz_spread, plateau 0) and
  y trapezoid(-y_spread..y_spread) - the same triangular distribution the old
  nextInt(s+1)-nextInt(s+1) offsets produced - followed by the patch's inner placement
  modifiers, with the placed feature pointing at the inner feature directly. The feature file
  keeps its id and now holds that inner feature.
"""
import json
import os
import sys

TRAPEZOID = lambda n: {"type": "minecraft:trapezoid", "max": n, "min": -n, "plateau": 0}
SOIL_RULE_PREDICATE = {"type": "minecraft:not", "predicate": {"type": "minecraft:matching_block_tag", "tag": "minecraft:cannot_replace_below_tree_trunk"}}


def state_obj(s):
    out = {"id": s["Name"]}
    if s.get("Properties"):
        out["properties"] = {k: (str(v).lower() if isinstance(v, bool) else str(v)) for k, v in s["Properties"].items()}
    return out


def state(s):
    """A BlockState field: bare string when it has no properties."""
    o = state_obj(s)
    return o["id"] if "properties" not in o else o


def provider(p):
    t = p.get("type")
    if t == "minecraft:simple_state_provider":
        return state_obj(p["state"])
    if t == "minecraft:weighted_state_provider":
        return {"type": "minecraft:weighted", "entries": [{"data": state(e["data"]), "weight": e["weight"]} for e in p["entries"]]}
    if t == "minecraft:randomized_int_state_provider":
        return {"type": "minecraft:randomized_int", "property": p["property"], "source": provider(p["source"]), "values": p["values"]}
    if t is None and "fallback" in p:  # 1.21.1 RuleBasedBlockStateProvider (untyped)
        if not p.get("rules"):
            return provider(p["fallback"])  # no rules: always the fallback
        return {"type": "minecraft:rule_based", "fallback": provider(p["fallback"]),
                "rules": [{"if_true": r["if_true"], "then": provider(r["then"])} for r in p["rules"]]}
    raise ValueError("unhandled provider %r" % p)


def placement(mods):
    out = []
    for m in mods:
        m = json.loads(json.dumps(m))
        if m["type"] == "minecraft:block_predicate_filter":
            m["predicate"] = predicate(m["predicate"])
        out.append(m)
    return out


def predicate(p):
    p = dict(p)
    if p["type"] == "minecraft:would_survive":
        p["state"] = state(p["state"])
    for k in ("predicate",):
        if k in p:
            p[k] = predicate(p[k])
    if "predicates" in p:
        p["predicates"] = [predicate(x) for x in p["predicates"]]
    return p


def below_trunk(cfg):
    prov = provider(cfg["dirt_provider"])
    if cfg.get("force_dirt"):
        return prov
    if prov == {"id": "minecraft:dirt"}:
        return "minecraft:soil_beneath_tree"
    return {"type": "minecraft:rule_based", "rules": [{"if_true": SOIL_RULE_PREDICATE, "then": prov}]}


def tree(cfg):
    out = {"type": "minecraft:tree", "below_trunk_provider": below_trunk(cfg)}
    decorators = []
    for d in cfg.get("decorators", []):
        d = dict(d)
        if "block_provider" in d:
            d["block_provider"] = provider(d["block_provider"])
        decorators.append(d)
    out["decorators"] = decorators
    out["foliage_placer"] = cfg["foliage_placer"]
    out["foliage_provider"] = provider(cfg["foliage_provider"])
    out["ignore_vines"] = cfg["ignore_vines"]
    out["minimum_size"] = cfg["minimum_size"]
    if "root_placer" in cfg:
        raise ValueError("root_placer not handled")
    out["trunk_placer"] = cfg["trunk_placer"]
    out["trunk_provider"] = provider(cfg["trunk_provider"])
    return out


def simple_feature(f):
    """Convert an inline 1.21.1 configured feature (as nested in a random patch)."""
    t, cfg = f["type"], f["config"]
    if t == "minecraft:simple_block":
        out = {"type": t, "to_place": provider(cfg["to_place"])}
        if cfg.get("schedule_tick"):
            out["schedule_tick"] = True
        return out
    raise ValueError("unhandled inner feature %s" % t)


def main(old, new):
    patches = {}
    os.makedirs(os.path.join(new, "feature"), exist_ok=True)
    os.makedirs(os.path.join(new, "placed_feature"), exist_ok=True)
    for fn in sorted(os.listdir(os.path.join(old, "configured_feature"))):
        name = fn[:-5]
        src = json.load(open(os.path.join(old, "configured_feature", fn)))
        t, cfg = src["type"], src["config"]
        if t == "minecraft:tree":
            out = tree(cfg)
        elif t in ("minecraft:random_patch", "minecraft:flower", "minecraft:no_bonemeal_flower"):
            inner = cfg["feature"]
            out = simple_feature(inner["feature"])
            patches[name] = [
                {"type": "minecraft:count", "count": cfg["tries"]},
                {"type": "minecraft:offset", "x": TRAPEZOID(cfg["xz_spread"]), "y": TRAPEZOID(cfg["y_spread"]), "z": TRAPEZOID(cfg["xz_spread"])},
            ] + placement(inner.get("placement", []))
        elif t == "minecraft:disk":
            out = {"type": t, "half_height": cfg["half_height"], "radius": cfg["radius"],
                   "state_provider": provider(cfg["state_provider"]), "target": cfg["target"]}
        elif t == "bountifulfares:wild_vine":
            out = {"type": t, "block": state(cfg["block"]), "can_place_on": cfg["can_place_on"], "patch_size": cfg["patch_size"]}
        else:
            raise ValueError("unhandled feature type %s in %s" % (t, fn))
        json.dump(out, open(os.path.join(new, "feature", fn), "w"), indent=2)
        print("feature", name, t)
    for fn in sorted(os.listdir(os.path.join(old, "placed_feature"))):
        src = json.load(open(os.path.join(old, "placed_feature", fn)))
        feat = src["feature"]
        mods = placement(src["placement"])
        key = feat.split(":", 1)[1] if feat.startswith("bountifulfares:") else None
        if key in patches:
            mods = mods + patches[key]
        json.dump({"feature": feat, "placement": mods}, open(os.path.join(new, "placed_feature", fn), "w"), indent=2)
        print("placed", fn[:-5], "(+patch)" if key in patches else "")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
