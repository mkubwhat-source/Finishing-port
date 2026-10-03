#!/usr/bin/env python3
"""Builds data/bountifulfares/structure/compat/woodland_mansions/golden_grove_cellar.nbt.

Bountiful Fares' golden apple tree room is injected into *vanilla* woodland mansions through
WoodlandMansionPieces (see WoodlandMansionFirstFloorMixin). darkstarworks' "Woodland Mansions"
pack (the lukidonu-derived mansion shown with Grand Capitals, mod id `woodland_mansions`) replaces
minecraft:mansion with a jigsaw structure, so that code never runs and the room never appears.

This script builds a secret cellar version of the room in that pack's building style: stone-brick
plinth, dark oak log posts, gray terracotta panels and a dark oak rail/beam band like its
interiors, spruce plank floor, red carpet runner, iron chains and lanterns, cobwebbed corners. The
golden apple tree, its candles and the corner trellises are copied from BF's own
bountifulfares_golden_tree.nbt so the centrepiece is unchanged. The cellar sits under the
mansion's prison level and is reached through a spruce trapdoor set flush into the floor of the
workshop in the back-right wing, with a ladder down the corner (see WoodlandMansionsCompat.java).

Room-local coordinates: x/z 0..14 (walls at 0 and 14), y 0 = cellar floor, y 1..10 interior,
y 11 = ceiling, y 12 = the mansion's own floor layer (only the trapdoor cell is written there;
every cell left out of the template is left untouched when placed).
"""
import os
import nbtlib
from nbtlib import Compound, List, Int, String

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
SRC_ROOM = os.path.join(ROOT, 'src/main/resources/data/minecraft/structure/woodland_mansion/bountifulfares_golden_tree.nbt')
OUT = os.path.join(ROOT, 'src/main/resources/data/bountifulfares/structure/compat/woodland_mansions/golden_grove_cellar.nbt')
DATA_VERSION = 5023  # Minecraft 26.3

SX, SY, SZ = 15, 13, 15
blocks = {}


def put(x, y, z, name, **props):
    if ':' not in name:
        name = 'minecraft:' + name
    blocks[(x, y, z)] = (name, tuple(sorted((k, str(v).lower()) for k, v in props.items())))


# ---- shell ---------------------------------------------------------------------------------
POSTS = {0, 4, 7, 10, 14}
for x in range(SX):
    for z in range(SZ):
        edge = x in (0, 14) or z in (0, 14)
        # floor
        if edge:
            put(x, 0, z, 'stone_bricks')
        elif x in (1, 13) and z in (1, 13):
            put(x, 0, z, 'dark_oak_log', axis='y')
        elif z in (1, 13):
            put(x, 0, z, 'dark_oak_log', axis='x')
        elif x in (1, 13):
            put(x, 0, z, 'dark_oak_log', axis='z')
        else:
            put(x, 0, z, 'spruce_planks')
        # ceiling
        put(x, 11, z, 'dark_oak_planks')
        if not edge:
            for y in range(1, 11):
                put(x, y, z, 'air')
            continue
        along = z if x in (0, 14) else x
        corner = x in (0, 14) and z in (0, 14)
        for y in range(1, 11):
            if corner or along in POSTS:
                put(x, y, z, 'dark_oak_log', axis='y')
            elif y == 1:
                put(x, y, z, 'stone_bricks')
            elif y in (4, 9, 10):
                put(x, y, z, 'dark_oak_planks')
            else:
                put(x, y, z, 'gray_terracotta')

# ceiling beams hung one block below the ceiling, across the room
for z in (4, 10):
    for x in range(1, 14):
        put(x, 10, z, 'dark_oak_log', axis='x')

# ---- floor dressing ------------------------------------------------------------------------
# red carpet runner around the planter
for i in range(2, 13):
    for (x, z) in ((i, 2), (i, 12), (2, i), (12, i)):
        put(x, 1, z, 'red_carpet')

# planter: deepslate tile step, dark oak log box, soil bed (the soil mix is BF's original)
for x in range(4, 11):
    for z in range(4, 11):
        if x in (4, 10) or z in (4, 10):
            put(x, 1, z, 'deepslate_tile_slab', type='bottom', waterlogged='false')
for x in range(5, 10):
    for z in range(5, 10):
        if x in (5, 9) and z in (5, 9):
            put(x, 1, z, 'dark_oak_log', axis='y')
        elif z in (5, 9):
            put(x, 1, z, 'dark_oak_log', axis='x')
        elif x in (5, 9):
            put(x, 1, z, 'dark_oak_log', axis='z')
for x in range(6, 9):
    for z in range(6, 9):
        put(x, 0, z, 'coarse_dirt')

# ---- copy the tree, soil and candles from BF's golden tree room ----------------------------
src = nbtlib.load(SRC_ROOM)
pal = src['palette']
for b in src['blocks']:
    x, y, z = map(int, b['pos'])
    st = pal[int(b['state'])]
    name = str(st.get('Name', st.get('id')))
    props = {k: str(v) for k, v in st.get('Properties', st.get('properties', {})).items()}
    inner = 6 <= x <= 8 and 6 <= z <= 8
    if y == 1 and inner:  # soil bed
        put(x, 1, z, name, **props)
    elif y >= 2 and 3 <= x <= 11 and 3 <= z <= 11 and name != 'minecraft:air':
        if name.startswith('minecraft:candle') and not (5 <= x <= 9 and 5 <= z <= 9):
            continue
        put(x, y, z, name, **props)
    elif y == 2 and name == 'bountifulfares:dark_oak_trellis':
        if (x <= 2 and z <= 2):
            continue  # the ladder corner
        for yy in (1, 2):
            put(x, yy, z, name, **props)

# ---- lighting / furnishing -----------------------------------------------------------------
for (x, z) in ((3, 4), (11, 4), (3, 10), (11, 10)):
    put(x, 9, z, 'iron_chain', axis='y', waterlogged='false')
    put(x, 8, z, 'lantern', hanging='true', waterlogged='false')
for (x, z) in ((13, 1), (1, 13), (13, 13)):
    put(x, 1, z, 'lantern', hanging='false', waterlogged='false')
# a reading nook of shelves on the east wall, candles on top
for z in (6, 8):
    put(13, 1, z, 'bookshelf')
    put(13, 2, z, 'bookshelf')
    put(13, 3, z, 'candle', candles=2, lit='true', waterlogged='false')
put(13, 1, 7, 'barrel', facing='west', open='false')
put(13, 2, 7, 'flower_pot')
# cobwebs in the dark upper corners
for (x, y, z) in ((13, 9, 13), (1, 9, 13), (13, 9, 1), (12, 9, 13), (13, 8, 13)):
    put(x, y, z, 'cobweb')

# ---- the hidden way in ---------------------------------------------------------------------
for y in range(1, 12):
    put(1, y, 1, 'ladder', facing='east', waterlogged='false')
put(1, 12, 1, 'spruce_trapdoor', facing='east', half='top', open='false', powered='false', waterlogged='false')

# ---- write -------------------------------------------------------------------------------
palette, index = [], {}
out_blocks = []
for (x, y, z), key in sorted(blocks.items(), key=lambda kv: (kv[0][1], kv[0][2], kv[0][0])):
    if key not in index:
        index[key] = len(palette)
        name, props = key
        # 26.3 structure palettes use the BlockState codec: {id, properties} (older files used
        # {Name, Properties} and are converted by the data fixer on load; a DataVersion-5023 file
        # in the old shape loads as all-air)
        c = Compound({'id': String(name)})
        if props:
            c['properties'] = Compound({k: String(v) for k, v in props})
        palette.append(c)
    out_blocks.append(Compound({'pos': List[Int]([Int(x), Int(y), Int(z)]), 'state': Int(index[key])}))

root = nbtlib.File({
    'DataVersion': Int(DATA_VERSION),
    'size': List[Int]([Int(SX), Int(SY), Int(SZ)]),
    'palette': List[Compound](palette),
    'blocks': List[Compound](out_blocks),
    'entities': List[Compound]([]),
}, gzipped=True)
os.makedirs(os.path.dirname(OUT), exist_ok=True)
root.save(OUT)
print('wrote', OUT, len(out_blocks), 'blocks,', len(palette), 'states')
