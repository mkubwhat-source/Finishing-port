#!/usr/bin/env python3
"""Pass 4: CompoundTag save/load methods of block entities and entities -> 26.3 ValueOutput/ValueInput.
Only rewrites the bodies of saveAdditional/loadAdditional/addAdditionalSaveData/readAdditionalSaveData."""
import re, sys, pathlib

def body_span(s, start):
    i = s.index('{', start) + 1; depth = 1
    while depth:
        if s[i] == '{': depth += 1
        elif s[i] == '}': depth -= 1
        i += 1
    return i

GET = {'getInt': '0', 'getBoolean': 'false', 'getString': '""', 'getFloat': '0.0F', 'getDouble': '0.0D', 'getLong': '0L', 'getShort': '(short) 0', 'getByte': '(byte) 0'}

def convert(s):
    changed = False
    for m in list(re.finditer(r'(void (saveAdditional|loadAdditional|addAdditionalSaveData|readAdditionalSaveData)\()\s*CompoundTag (\w+)\s*(?:,\s*HolderLookup\.Provider (\w+))?\s*\)', s))[::-1]:
        kind = m.group(2); tag = m.group(3); prov = m.group(4)
        out = kind in ('saveAdditional', 'addAdditionalSaveData')
        end = body_span(s, m.end())
        body = s[m.end():end]
        if prov:
            body = re.sub(r'super\.(saveAdditional|loadAdditional)\(\s*' + tag + r'\s*,\s*' + prov + r'\s*\)', r'super.\1(' + tag + ')', body)
            body = re.sub(r'ContainerHelper\.(saveAllItems|loadAllItems)\(\s*' + tag + r'\s*,\s*([\w.]+)\s*,\s*' + prov + r'\s*\)', r'ContainerHelper.\1(' + tag + r', \2)', body)
        if not out:
            for g, d in GET.items():
                body = re.sub(r'\b' + tag + r'\.' + g + r'\(("[^"]*")\)', tag + '.' + g + r'Or(\1, ' + d + ')', body)
            body = re.sub(r'\b' + tag + r'\.getCompound\(("[^"]*")\)', tag + r'.childOrEmpty(\1)', body)
        header = m.group(1) + ('ValueOutput ' if out else 'ValueInput ') + tag + ')'
        s = s[:m.start()] + header + body + s[end:]
        changed = True
    if changed:
        for imp in ('net.minecraft.world.level.storage.ValueInput', 'net.minecraft.world.level.storage.ValueOutput'):
            if f'import {imp};' not in s:
                s = re.sub(r'^(package [^;]+;\s*\n)', r'\1import ' + imp + ';\n', s, count=1, flags=re.M)
    return s, changed

for f in pathlib.Path(sys.argv[1]).rglob('*.java'):
    s = f.read_text()
    s2, ch = convert(s)
    if ch and s2 != s:
        f.write_text(s2); print('updated', f.name)
