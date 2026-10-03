#!/usr/bin/env python3
"""Convert 1.21.1 model elements' "shade": false to 26.3's "shade_direction_override": "up".

Usage: shade_fix.py <dir>...   (rewrites every model JSON under the given dirs in place)

26.3 no longer reads an element's "shade" flag (only "shade_direction_override", see
CuboidModelElement$Deserializer), so unshaded elements - plants, cross/crop models - got directional
shading and looked darker and washed out. Vanilla 26.3 converted its own "shade": false elements to
"shade_direction_override": "up" (the brightest direction, i.e. no darkening); same here. "shade": true
was the default and is just dropped.
"""
import json
import os
import sys


def fix(model):
    changed = False
    for el in model.get("elements", []):
        if "shade" in el:
            if el.pop("shade") is False and "shade_direction_override" not in el:
                el["shade_direction_override"] = "up"
            changed = True
    return changed


def main(dirs):
    n = 0
    for d in dirs:
        for root, _, files in os.walk(d):
            if "/models" not in root.replace(os.sep, "/"):
                continue
            for f in files:
                if not f.endswith(".json"):
                    continue
                p = os.path.join(root, f)
                raw = open(p, encoding="utf-8").read()
                if '"shade"' not in raw:
                    continue
                m = json.loads(raw)
                if fix(m):
                    indent = "\t" if "\n\t" in raw else 2
                    with open(p, "w", encoding="utf-8") as fh:
                        json.dump(m, fh, indent=indent, ensure_ascii=False)
                        if raw.endswith("\n"):
                            fh.write("\n")
                    n += 1
                    print(p)
    print(n, "models fixed")


if __name__ == "__main__":
    main(sys.argv[1:])
