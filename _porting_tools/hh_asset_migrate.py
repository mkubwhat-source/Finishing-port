#!/usr/bin/env python3
"""Migrate Hearth and Harvest's 1.21.1 client assets (NeoForge) into this 26.3 bundle, plus the Farmer's
Delight assets of the FD pieces HH now carries (from FarmersDelightRefabricated 26.3, already 26.3 format).

Usage: hh_asset_migrate.py <hh repo> <fdr 26.3 repo> <bf-port dir> <vanilla 26.3 extract> <1.21.1 client jar>

Writes under <bf-port>/src/main/resources/assets. Rules (checked against vanilla 26.3's own assets):

Models
* NeoForge "render_type": "minecraft:cutout" is dropped: 26.3 picks cutout from the texture (only
  translucency needs "force_translucent", which HH never asked for).
* NeoForge "neoforge:separate_transforms" item models (bottles/jars: 3D in hand, 2D in GUI, on the
  ground and in frames) become what vanilla 26.3 does for spears: a minecraft:select on
  minecraft:display_context in the item definition: each perspective's model for its contexts (2D in
  "gui"/"ground"/"fixed", plus "on_shelf" like "fixed"; the farmer's hat's "head" model), fallback ->
  the 3D model. The 3D base is a model of its own ("<name>_in_hand") unless it is only a
  parent reference, which is then used directly.
* Item model "overrides" on "hearthandharvest:vintage" (the 1.21.1 ItemProperties vintage value) become a
  minecraft:select on the minecraft:component hearthandharvest:vintage (cases 1/2/3). The pitchfork's
  "throwing" override is dropped (user: no trident-style pitchfork; its item uses the plain model).
* Ids: farmersdelight:* models/textures used by HH models -> hearthandharvest:* (the FD textures are
  copied from FarmersDelightRefabricated).

Item definitions (26.3 has no implicit models/item fallback): items/<id>.json for every HH item and every
ported FD item; items/display/<item path>.json in the *item's* namespace for every display model (racks,
crates, nests; DisplayModels points the stack's item_model there) - so the merged wines/mead get theirs
under flavored:/bountifulfares:, vanilla bottles under minecraft:, FD's milk bottle under hearthandharvest:.

Other assets
* Blockstates of registered blocks only. HH's trellises are kept next to BF's (grapes grow on both).
* Textures copied except those of dropped/merged items. HH 1.21.1 lacks
  textures/block/fluid/glow_berry_juice_flow.png (only its .mcmeta): the still texture is used.
* Keg bottle slot icon is now a GUI sprite (textures/gui/sprites/container/slot/bottle.png).
* sounds.json: HH's + FD's entries for the FD sound events HH registers; particles: HH's + FD's star,
  steam, sparkle. Brewin' and Chewin' coaster assets are not copied (no 26.3 build of that mod).
* Lang: HH's languages, minus merged/dropped entries; FD's entries for the FD blocks/items/screens/
  effects HH carries (farmersdelight keys -> hearthandharvest keys); new keys of this port.
A validation pass reports model parents/textures, blockstate models and item definition models that
resolve nowhere (this bundle, vanilla).
"""
import copy
import json
import os
import re
import shutil
import sys

HH = "hearthandharvest"
FD = "farmersdelight"

MERGED_ITEMS = {"mead": "bountifulfares:mead_bottle", "sweet_berry_wine": "flavored:sweet_berry_wine",
                "glow_berry_wine": "flavored:glow_berry_wine", "sweet_berry_juice": "flavored:sweet_berry_juice",
                "glow_berry_juice": "flavored:glow_berry_juice", "pizza": "flavored:pizza", "pizza_slice": "flavored:pizza_slice",
                "pickled_beetroots": "bountifulfares:pickled_beetroot", "flour": "bountifulfares:flour",
                "butter": "flavored:butter", "batter": "flavored:batter", "chocolate_bar": "flavored:chocolate"}
DROPPED = {"rotten_tomato_crate", "flour_bag"}

REPORT = {}


def report(k, v):
    REPORT.setdefault(k, set()).add(v)


def load(p):
    with open(p) as f:
        return json.load(f)


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        json.dump(obj, f, indent=2)
        f.write("\n")


def copyfile(src, dst):
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    shutil.copyfile(src, dst)


def fd_to_hh(o):
    """farmersdelight ids -> hearthandharvest ids in any JSON value."""
    if isinstance(o, dict):
        return {k: fd_to_hh(v) for k, v in o.items()}
    if isinstance(o, list):
        return [fd_to_hh(v) for v in o]
    if isinstance(o, str):
        return re.sub(r"^farmersdelight:", HH + ":", o)
    return o


def mid(i):
    return i if ":" in i else "minecraft:" + i


def model_file(assets, i):
    ns, p = mid(i).split(":", 1)
    return os.path.join(assets, ns, "models", p + ".json")


TEXTURE_FIXES = {
    # 1.21.1 bug: the unripe cheese wheel model named textures that were renamed (missing texture in-game)
    "hearthandharvest:block/unripe_cheddar_cheese_wheel_": "hearthandharvest:block/unripe_cheese_wheel_",
}


def clean_model(m):
    s = json.dumps(m)
    for a, b in TEXTURE_FIXES.items():
        s = s.replace('"%s' % a, '"%s' % b)
    m = fd_to_hh(json.loads(s))
    m.pop("render_type", None)
    m.pop("overrides", None)
    # 26.3 ignores "shade" (see shade_fix.py): unshaded elements use shade_direction_override "up"
    for el in m.get("elements", []):
        if el.pop("shade", True) is False:
            el.setdefault("shade_direction_override", "up")
    m.pop("format_version", None)
    # Blockbench leftovers: faces on "#missing" (never defined). 1.21.1 drew them with the missing
    # sprite (usually zero-area faces); 26.3 item models reject them (missing sprite is on the block
    # atlas, so the model would use two atlases), so they are dropped.
    defined = set(m.get("textures", {}))
    for el in m.get("elements", []):
        faces = el.get("faces", {})
        for f in [f for f, v in faces.items() if v.get("texture") == "#missing" and "missing" not in defined]:
            del faces[f]
    # ...and a cube left with no faces at all is rejected outright ("Expected between 1 and 6 unique
    # faces"), failing the whole model (3d_watering_can had four such cubes), so it goes too.
    if "elements" in m:
        m["elements"] = [el for el in m["elements"] if el.get("faces")]
    return m


class Migrator:
    def __init__(self, hh, fdr, port, vanilla):
        self.hh_roots = [os.path.join(hh, "src/main/resources/assets"), os.path.join(hh, "src/generated/resources/assets")]
        self.fdr_assets = [os.path.join(fdr, "src/main/resources/assets", FD), os.path.join(fdr, "src/generated/resources/assets", FD)]
        self.port = port
        self.out = os.path.join(port, "src/main/resources/assets")
        self.vanilla = os.path.join(vanilla, "assets")
        java = os.path.join(port, "src/main/java/alabaster/hearthandharvest/common/registry")
        self.items = set(re.findall(r'register[A-Za-z]*\(\s*"([a-z0-9_/]+)"', open(os.path.join(java, "HHModItems.java")).read()))
        self.blocks = set(re.findall(r'\(\s*"([a-z0-9_/]+)"', open(os.path.join(java, "HHModBlocks.java")).read()))
        self.written = {}

    # ------------------------------------------------------------------ helpers
    def hh_files(self, sub):
        """(relative path, absolute path) of HH assets under assets/hearthandharvest/<sub>, generated wins."""
        found = {}
        for root in self.hh_roots:
            base = os.path.join(root, HH, sub)
            for r, _, fs in os.walk(base):
                for f in fs:
                    p = os.path.join(r, f)
                    found[os.path.relpath(p, base)] = p
        return found

    def fd_file(self, sub):
        for base in self.fdr_assets:
            p = os.path.join(base, sub)
            if os.path.exists(p):
                return p
        return None

    def put(self, rel, obj):
        p = os.path.join(self.out, rel)
        write(p, obj)
        self.written[p] = obj

    # ------------------------------------------------------------------ models
    def migrate_models(self):
        models = self.hh_files("models")
        self.separate = {}   # model id -> (2d model id, 3d model id)
        self.vintage = {}    # item model id -> {n: model id}
        for rel, p in sorted(models.items()):
            if not rel.endswith(".json"):
                continue
            name = rel[:-5]
            base = os.path.basename(name)
            if base in ("rotten_tomato_crate", "flour_bag"):
                report("model skipped (dropped block)", name)
                continue
            if name.startswith("item/") and re.sub(r"^(vintage/)?(2d_)?|_(aged|fine|reserve)$", "", name[5:]) in MERGED_ITEMS:
                report("model skipped (merged item: its own mod's model is used)", name)
                continue
            if re.fullmatch(r"block/jar_[1-4]", name):
                # unused in 1.21.1 too (no blockstate/item uses them; their parent generic_jar_N never existed)
                report("model skipped (unused, broken in 1.21.1)", name)
                continue
            m = load(p)
            mid_ = HH + ":" + name
            # 1.21.1 drew these with item renderers (BEWLR), which 26.3 doesn't have:
            if name in ("item/pitchfork_in_hand", "item/pitchfork_throwing"):
                report("model skipped (trident-style pitchfork rendering not ported, per user)", name)
                continue
            if name == "item/pitchfork":   # user: plain item model, no trident-style rendering
                self.put(os.path.join(HH, "models", rel), {"parent": "minecraft:item/handheld", "textures": {"layer0": HH + ":item/pitchfork"}})
                continue
            if m.get("parent") in ("builtin/entity", "minecraft:builtin/entity") and name == "item/crate":
                m = dict(m)
                # the BEWLR drew the crate's default state (one bottom crate) raised 4px, plus its contents:
                # that model with the BEWLR model's display transforms; the item definition raises it
                # and adds the contents layer (CrateItemRenderer)
                m["parent"] = HH + ":block/crate_bottom"
            if m.get("loader") == "neoforge:separate_transforms":
                persp = m.get("perspectives", {})
                cases = {}
                for ctx, pm in persp.items():
                    if set(pm) != {"parent"}:
                        raise ValueError("inline perspective model in %s" % name)
                    cases.setdefault(fd_to_hh(pm["parent"]), []).append(ctx)
                    if ctx == "fixed":   # 26.3's shelf display didn't exist; shown like item frames
                        cases[fd_to_hh(pm["parent"])].append("on_shelf")
                b = m["base"]
                if set(b) == {"parent"}:
                    three = b["parent"]
                else:
                    three = mid_ + "_in_hand"
                    b = clean_model(b)
                    if "particle" in m.get("textures", {}) and "particle" not in b.get("textures", {}):
                        b.setdefault("textures", {})["particle"] = fd_to_hh(m["textures"]["particle"])
                    self.put(os.path.join(HH, "models", name + "_in_hand.json"), b)
                self.separate[mid_] = (cases, fd_to_hh(three))
            else:
                self.put(os.path.join(HH, "models", rel), clean_model(m))
            vint = {}
            for o in m.get("overrides", []):
                if "hearthandharvest:vintage" in o["predicate"]:
                    vint[int(o["predicate"]["hearthandharvest:vintage"])] = fd_to_hh(o["model"])
                else:
                    report("item override dropped", "%s %s" % (name, o["predicate"]))
            if vint:
                self.vintage[mid_] = vint
        # HH's minecraft-namespace (vanilla bottles' 3D display models) and its FD display model
        for root in self.hh_roots:
            for ns in ("minecraft", FD):
                base = os.path.join(root, ns, "models")
                for r, _, fs in os.walk(base):
                    for f in fs:
                        rel = os.path.relpath(os.path.join(r, f), base)
                        tns = "minecraft" if ns == "minecraft" else HH
                        self.put(os.path.join(tns, "models", rel), clean_model(load(os.path.join(r, f))))

    def model_ref(self, model_id):
        """An item-definition model node for this model id (splitting separate_transforms, adding vintage)."""
        def plain_or_split(i):
            if i in self.separate:
                cases, three = self.separate[i]
                return {"type": "minecraft:select", "property": "minecraft:display_context",
                        "cases": [{"when": ctxs, "model": {"type": "minecraft:model", "model": mdl}} for mdl, ctxs in cases.items()],
                        "fallback": {"type": "minecraft:model", "model": three}}
            return {"type": "minecraft:model", "model": i}
        node = plain_or_split(model_id)
        if model_id in self.vintage:
            node = {"type": "minecraft:select", "property": "minecraft:component", "component": "hearthandharvest:vintage",
                    "cases": [{"when": n, "model": plain_or_split(m)} for n, m in sorted(self.vintage[model_id].items())],
                    "fallback": node}
        return node

    # ------------------------------------------------------------------ item definitions
    def item_definitions(self):
        for item in sorted(self.items):
            mid_ = HH + ":item/" + item
            if mid_ in self.separate or os.path.exists(model_file(self.out, mid_)):
                node = self.model_ref(mid_)
            elif item == "pitchfork":
                node = {"type": "minecraft:model", "model": HH + ":item/pitchfork"}
            else:
                fd_def = self.fd_file("items/%s.json" % item)
                if fd_def:
                    continue   # FD item: FDR's own definition is copied in migrate_fd
                bm = HH + ":block/" + item
                if os.path.exists(model_file(self.out, bm)):
                    node = {"type": "minecraft:model", "model": bm}
                    report("item definition uses block model", item)
                else:
                    report("ITEM WITHOUT MODEL", item)
                    continue
            if item == "crate":
                raised = {"translation": [0, 0.25, 0], "left_rotation": [0, 0, 0, 1], "right_rotation": [0, 0, 0, 1], "scale": [1, 1, 1]}
                node = {"type": "minecraft:composite", "models": [
                    dict(node, transformation=raised),
                    {"type": "minecraft:special", "base": mid_, "transformation": raised, "model": {"type": HH + ":crate_contents"}}]}
            self.put(os.path.join(HH, "items", item + ".json"), {"model": node})

    def display_definitions(self):
        """items/display/<item path>.json in the item's namespace, for every models/display/** model."""
        for ns in (HH, "minecraft"):
            base = os.path.join(self.out, ns, "models", "display")
            for r, _, fs in os.walk(base):
                for f in sorted(fs):
                    rel = os.path.relpath(os.path.join(r, f), base)[:-5]
                    if os.path.basename(rel).startswith("template_"):
                        continue
                    model = "%s:display/%s" % (ns, rel)
                    target_ns, target = ns, rel
                    if ns == HH and rel in MERGED_ITEMS:   # merged wines / mead: the item now lives elsewhere
                        target_ns, target = MERGED_ITEMS[rel].split(":")
                    self.put(os.path.join(target_ns, "items", "display", target + ".json"), {"model": self.model_ref(model)})

    # ------------------------------------------------------------------ blockstates
    def blockstates(self):
        renames = {"cheddar_cheese_wheel": "cheese_wheel", "unripe_cheddar_cheese_wheel": "unripe_cheese_wheel"}  # HH's own renames
        for rel, p in sorted(self.hh_files("blockstates").items()):
            b = renames.get(rel[:-5], rel[:-5])
            rel = b + ".json"
            if b not in self.blocks:
                report("blockstate skipped (block not registered)", b)
                continue
            bs = fd_to_hh(load(p))
            if rel[:-5] != os.path.basename(p)[:-5]:
                # 1.21.1 renamed the cheddar cheese wheels' blocks/models but not these blockstates (missing model in-game)
                bs = json.loads(json.dumps(bs).replace("block/unripe_cheddar_cheese_wheel", "block/unripe_cheese_wheel")
                                .replace("block/cheddar_cheese_wheel", "block/cheese_wheel"))

            def strip(o):
                if isinstance(o, dict):
                    o.pop("render_type", None)
                    for v in o.values():
                        strip(v)
                elif isinstance(o, list):
                    for v in o:
                        strip(v)
            strip(bs)
            self.put(os.path.join(HH, "blockstates", rel), bs)

    # ------------------------------------------------------------------ textures/sounds/particles
    def textures(self):
        merged_tex = set(MERGED_ITEMS) | DROPPED
        for rel, p in sorted(self.hh_files("textures").items()):
            stem = os.path.basename(rel).split(".")[0]
            if rel.startswith("item/") and stem in merged_tex:
                report("texture skipped (merged/dropped item)", rel)
                continue
            copyfile(p, os.path.join(self.out, HH, "textures", rel))
        still = os.path.join(self.out, HH, "textures/block/fluid/glow_berry_juice_still.png")
        flow = os.path.join(self.out, HH, "textures/block/fluid/glow_berry_juice_flow.png")
        if os.path.exists(still) and not os.path.exists(flow):
            copyfile(still, flow)
        copyfile(os.path.join(self.out, HH, "textures/gui/bottle_slot.png"),
                 os.path.join(self.out, HH, "textures/gui/sprites/container/slot/bottle.png"))
        for root in self.hh_roots:
            base = os.path.join(root, "minecraft", "textures")
            for r, _, fs in os.walk(base):
                for f in fs:
                    copyfile(os.path.join(r, f), os.path.join(self.out, "minecraft", "textures", os.path.relpath(os.path.join(r, f), base)))
        for rel, p in self.hh_files("sounds").items():
            copyfile(p, os.path.join(self.out, HH, "sounds", rel))
        for rel, p in self.hh_files("particles").items():
            self.put(os.path.join(HH, "particles", rel), fd_to_hh(load(p)))

    # ------------------------------------------------------------------ FD
    def migrate_fd(self):
        ids = set()
        for base in self.fdr_assets:
            d = os.path.join(base, "items")
            if os.path.isdir(d):
                ids |= {f[:-5] for f in os.listdir(d)}
        ported_items = ids & self.items
        for item in sorted(ported_items):
            if os.path.exists(os.path.join(self.out, HH, "items", item + ".json")):
                continue
            self.put(os.path.join(HH, "items", item + ".json"), fd_to_hh(load(self.fd_file("items/%s.json" % item))))
        for b in sorted(self.blocks):
            p = self.fd_file("blockstates/%s.json" % b)
            if p and not os.path.exists(os.path.join(self.out, HH, "blockstates", b + ".json")):
                self.put(os.path.join(HH, "blockstates", b + ".json"), fd_to_hh(load(p)))
        # FD sounds for the FD sound events HH registers
        sounds = load(self.fd_file("sounds.json"))
        hh_sounds = {}
        for root in self.hh_roots:
            p = os.path.join(root, HH, "sounds.json")
            if os.path.exists(p):
                hh_sounds.update(load(p))
        registered = set(re.findall(r'sound\("([^"]+)"', open(os.path.join(self.port, "src/main/java/alabaster/hearthandharvest/common/registry/HHModSounds.java")).read()))
        for k, v in sounds.items():
            if k in registered and k not in hh_sounds:
                orig = v
                v = fd_to_hh(v)
                if "subtitle" in v:
                    v["subtitle"] = v["subtitle"].replace("subtitles.farmersdelight.", "subtitles.%s." % HH)
                hh_sounds[k] = v
                for s in orig["sounds"]:
                    name = s if isinstance(s, str) else s["name"]
                    if name.startswith(FD + ":"):
                        src = self.fd_file("sounds/%s.ogg" % name.split(":", 1)[1])
                        copyfile(src, os.path.join(self.out, HH, "sounds", name.split(":", 1)[1] + ".ogg"))
        missing = registered - set(hh_sounds)
        for m in missing:
            report("SOUND EVENT WITHOUT sounds.json ENTRY", m)
        self.put(os.path.join(HH, "sounds.json"), hh_sounds)
        # FD particles HH registers (star, steam, sparkle)
        for name in ("star", "steam", "sparkle"):
            p = self.fd_file("particles/%s.json" % name)
            if p:
                self.put(os.path.join(HH, "particles", name + ".json"), fd_to_hh(load(p)))
        # FD GUI: cooking pot screen, recipe book filter, bowl slot sprite
        for rel in ("textures/gui/cooking_pot.png", "textures/gui/sprites/container/slot/bowl.png"):
            copyfile(self.fd_file(rel), os.path.join(self.out, HH, rel))
        for f in ("enabled", "disabled", "enabled_highlighted", "disabled_highlighted"):
            rel = "textures/gui/sprites/recipe_book/cooking_pot_%s.png" % f
            copyfile(self.fd_file(rel), os.path.join(self.out, HH, rel))
        for name in ("comfort", "nourishment"):
            p = self.fd_file("textures/mob_effect/%s.png" % name)
            if p:
                copyfile(p, os.path.join(self.out, HH, "textures/mob_effect/%s.png" % name))

    def resolve_closure(self):
        """Copy FD models/textures referenced (as hearthandharvest ids) but not present; validate the rest."""
        todo = list(self.written.items())
        seen = set()
        while todo:
            path, obj = todo.pop()
            if path in seen:
                continue
            seen.add(path)
            rel = os.path.relpath(path, self.out)
            kind = rel.split(os.sep)[1]
            refs = []
            if kind == "models":
                if "parent" in obj:
                    refs.append(("model", obj["parent"]))
                for v in obj.get("textures", {}).values():
                    if isinstance(v, dict):
                        v = v.get("sprite", "")
                    if isinstance(v, str) and not v.startswith("#"):
                        refs.append(("texture", v))
            elif kind == "blockstates":
                for m in re.findall(r'"model":\s*"([^"]+)"', json.dumps(obj)):
                    refs.append(("model", m))
            elif kind == "items":
                for m in re.findall(r'"model":\s*"([^"]+)"', json.dumps(obj)):
                    refs.append(("model", m))
            elif kind == "particles":
                for t in obj.get("textures", []):
                    refs.append(("particle", t))
            for rk, r in refs:
                ns, p = mid(r).split(":", 1)
                if rk == "model":
                    target = os.path.join(self.out, ns, "models", p + ".json")
                    found = os.path.exists(target) or os.path.exists(os.path.join(self.vanilla, ns, "models", p + ".json")) \
                        or p == "builtin/generated"
                    if not found and ns == HH:
                        src = self.fd_file("models/%s.json" % p)
                        if src:
                            obj2 = clean_model(load(src))
                            write(target, obj2)
                            self.written[target] = obj2
                            todo.append((target, obj2))
                            found = True
                    if not found:
                        found = os.path.exists(os.path.join(self.port, "src/main/generated/assets", ns, "models", p + ".json"))
                    if not found:
                        report("MISSING MODEL", "%s (from %s)" % (r, rel))
                else:
                    sub = "textures/" + ("particle/" if rk == "particle" else "") + p + ".png"
                    target = os.path.join(self.out, ns, sub)
                    found = os.path.exists(target) or os.path.exists(os.path.join(self.vanilla, ns, sub))
                    if not found and ns == HH:
                        src = self.fd_file(sub)
                        if src:
                            copyfile(src, target)
                            if os.path.exists(src + ".mcmeta"):
                                copyfile(src + ".mcmeta", target + ".mcmeta")
                            report("fd texture copied", sub)
                            found = True
                    if not found and ns == "crabbersdelight":
                        report("texture from Crabber's Delight (palm blocks only exist with that mod)", r)
                        found = True
                    if not found:
                        report("MISSING TEXTURE", "%s (from %s)" % (r, rel))

    # ------------------------------------------------------------------ lang
    def lang(self):
        hh_lang = self.hh_files("lang")
        merged = set(MERGED_ITEMS) | DROPPED
        fd_keys_wanted = re.compile(r"^(block|item)\.farmersdelight\.(%s)(\.[a-z_.]+)?$" % "|".join(sorted(map(re.escape, self.items | self.blocks))))
        fd_lang_dirs = [os.path.join(b, "lang") for b in self.fdr_assets]
        extra_fd_prefixes = ("container.farmersdelight.", "farmersdelight.container.", "farmersdelight.tooltip.",
                             "effect.farmersdelight.", "farmersdelight.block.", "farmersdelight.jei.", "jei.farmersdelight.", "farmersdelight.emi.",
                             "subtitles.farmersdelight.block.cooking_pot", "subtitles.farmersdelight.block.cutting_board",
                             "subtitles.farmersdelight.block.cabinet", "gui.farmersdelight.", "farmersdelight.recipe_book.",
                             "itemGroup.farmersdelight", "tooltip.farmersdelight.cooking_pot.", "tooltip.farmersdelight.placeable")
        fd_item_tooltips = {"tooltip.farmersdelight.%s" % i for i in self.items}
        additions = load(os.path.join(self.port, "_porting_tools/hh_lang_additions.json"))
        additions.pop("_comment", None)
        for rel, p in sorted(hh_lang.items()):
            data = load(p)
            out = {}
            for k, v in data.items():
                parts = k.split(".")
                if len(parts) == 3 and parts[0] in ("item", "block") and parts[1] == HH and parts[2] in merged:
                    report("lang key dropped (merged/dropped)", k)
                    continue
                # HH items using FD's ConsumableItem had FD-style tooltip keys; FD's TextUtils is HH's now
                k = k.replace("tooltip.farmersdelight.", "tooltip.%s." % HH)
                out[k] = v
            locale = rel[:-5]
            for d in fd_lang_dirs:
                fp = os.path.join(d, locale + ".json")
                if not os.path.exists(fp):
                    continue
                for k, v in load(fp).items():
                    if fd_keys_wanted.match(k) or k.startswith(extra_fd_prefixes) or k in fd_item_tooltips:
                        nk = k.replace("farmersdelight", HH)
                        out.setdefault(nk, v)
            if locale == "en_us":
                for k, v in additions.items():
                    out.setdefault(k, v)
            self.put(os.path.join(HH, "lang", rel), out)


def crow_spawn_egg(m, client_1211):
    """26.3 spawn eggs have their own textures (no tinted template_spawn_egg model any more): HH's crow egg
    texture is the 1.21.1 template tinted the way its ItemColor did (base 0x1c2030, spots 0x0d111c)."""
    import zipfile
    from io import BytesIO
    from PIL import Image
    with zipfile.ZipFile(client_1211) as z:
        base = Image.open(BytesIO(z.read("assets/minecraft/textures/item/spawn_egg.png"))).convert("RGBA")
        over = Image.open(BytesIO(z.read("assets/minecraft/textures/item/spawn_egg_overlay.png"))).convert("RGBA")

    def tint(im, color):
        r, g, b = (color >> 16) & 255, (color >> 8) & 255, color & 255
        px = [(p[0] * r // 255, p[1] * g // 255, p[2] * b // 255, p[3]) for p in im.getdata()]
        out = Image.new("RGBA", im.size)
        out.putdata(px)
        return out
    egg = Image.alpha_composite(tint(base, 0x1c2030), tint(over, 0x0d111c))
    path = os.path.join(m.out, HH, "textures/item/crow_spawn_egg.png")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    egg.save(path)
    m.put(os.path.join(HH, "models/item/crow_spawn_egg.json"),
          {"parent": "minecraft:item/generated", "textures": {"layer0": HH + ":item/crow_spawn_egg"}})


def main(hh, fdr, port, vanilla, client_1211):
    m = Migrator(hh, fdr, port, vanilla)
    m.migrate_models()
    crow_spawn_egg(m, client_1211)
    m.blockstates()
    m.textures()
    m.item_definitions()
    m.migrate_fd()
    m.display_definitions()
    m.resolve_closure()
    m.lang()
    for k in sorted(REPORT):
        print("== %s (%d)" % (k, len(REPORT[k])))
        for v in sorted(REPORT[k]):
            print("   ", v)
    print("json written:", len(m.written))


if __name__ == "__main__":
    main(*sys.argv[1:6])
