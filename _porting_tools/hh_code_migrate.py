#!/usr/bin/env python3
"""Mechanical 1.21.1 NeoForge -> 26.3 Fabric source rewrites for Hearth and Harvest.

Usage: hh_code_migrate.py <mc class index> <java root> [<java root> ...]

The index lists every 26.3 Minecraft class path (net/minecraft/...), one per line, from the
deobfuscated 26.3 common + client-only jars. Only safe, mechanical rewrites happen here; anything
that changes behaviour is ported by hand:
* ResourceLocation -> Identifier.
* Imports of net.minecraft classes that moved are re-pointed at the 26.3 class with the same simple
  name when exactly one exists (or exactly one shares the old package's last segment, e.g.
  animal.Chicken -> animal.chicken.Chicken). Ambiguous/missing ones are reported, left alone.
* Block/Item property constructors -> BFProperties (26.3 needs the registry id on the properties
  before the constructor runs; BFProperties reads it from BFRegistryHelper's registration scope).
* MobEffects constants renamed in 1.21.2; NeoForge ModList -> FabricLoader.
"""
import os, re, sys
from collections import defaultdict

index = [l.strip() for l in open(sys.argv[1]) if l.strip()]
exists = set(index)
by_simple = defaultdict(list)
for p in index:
    by_simple[p.rsplit('/', 1)[1]].append(p)

RENAMES = {'net.minecraft.resources.ResourceLocation': 'net.minecraft.resources.Identifier'}
TEXT = [
    (r'\bResourceLocation\b', 'Identifier'),
    (r'\bBlock\.Properties\.ofFullCopy\(', 'BFProperties.blockCopy('),
    (r'\bBlockBehaviour\.Properties\.ofFullCopy\(', 'BFProperties.blockCopy('),
    (r'\bBlock\.Properties\.of\(\)', 'BFProperties.block()'),
    (r'\bBlockBehaviour\.Properties\.of\(\)', 'BFProperties.block()'),
    (r'\bnew Item\.Properties\(\)', 'BFProperties.item()'),
    (r'\bMobEffects\.MOVEMENT_SPEED\b', 'MobEffects.SPEED'),
    (r'\bMobEffects\.MOVEMENT_SLOWDOWN\b', 'MobEffects.SLOWNESS'),
    (r'\bMobEffects\.DIG_SPEED\b', 'MobEffects.HASTE'),
    (r'\bMobEffects\.DIG_SLOWDOWN\b', 'MobEffects.MINING_FATIGUE'),
    (r'\bMobEffects\.DAMAGE_BOOST\b', 'MobEffects.STRENGTH'),
    (r'\bMobEffects\.HEAL\b', 'MobEffects.INSTANT_HEALTH'),
    (r'\bMobEffects\.HARM\b', 'MobEffects.INSTANT_DAMAGE'),
    (r'\bMobEffects\.JUMP\b', 'MobEffects.JUMP_BOOST'),
    (r'\bMobEffects\.CONFUSION\b', 'MobEffects.NAUSEA'),
    (r'\bMobEffects\.DAMAGE_RESISTANCE\b', 'MobEffects.RESISTANCE'),
    (r'\bModList\.get\(\)\.isLoaded\(', 'FabricLoader.getInstance().isModLoaded('),
    (r'import net\.neoforged\.fml\.ModList;', 'import net.fabricmc.loader.api.FabricLoader;'),
]
unresolved = defaultdict(set)

def fix_import(m):
    static, name = m.group(1) or '', m.group(2)
    if name in RENAMES:
        return f'import {static}{RENAMES[name]};'
    if not name.startswith('net.minecraft.') or name.endswith('*'):
        return m.group(0)
    parts = name.split('.')
    for i, seg in enumerate(parts):
        if seg[:1].isupper():
            break
    else:
        return m.group(0)
    if '/'.join(parts[:i + 1]) in exists:
        return m.group(0)
    cands = by_simple.get(parts[i], [])
    if len(cands) > 1:
        narrowed = [c for c in cands if parts[i - 1] in c.split('/')[:-1]]
        if len(narrowed) == 1:
            cands = narrowed
    if len(cands) == 1:
        rest = parts[i + 1:]
        return f'import {static}' + cands[0].replace('/', '.') + ('.' + '.'.join(rest) if rest else '') + ';'
    unresolved[name].add(cur)
    return m.group(0)

for root in sys.argv[2:]:
    for dp, _, fs in os.walk(root):
        for f in fs:
            if not f.endswith('.java'):
                continue
            cur = os.path.join(dp, f)
            src = open(cur).read()
            out = re.sub(r'import (static )?([\w.*]+);', fix_import, src)
            for a, b in TEXT:
                out = re.sub(a, b, out)
            if 'BFProperties.' in out and 'import net.hecco.bountifulfares.platform.BFProperties;' not in out:
                out = re.sub(r'(package [\w.]+;\n)', r'\1\nimport net.hecco.bountifulfares.platform.BFProperties;', out, count=1)
            if out != src:
                open(cur, 'w').write(out)
for k in sorted(unresolved):
    print('UNRESOLVED', k, len(unresolved[k]))
