Scripts used to repack 1.21.1 entity-sheet textures into 26.3's block-model textures
(beds, signs). Each *map.py solves a (rotation/flip, offset) per face rect by exact pixel
matching vanilla 1.21.1 sheets against vanilla 26.3 textures (needs the 1.21.1 client jar and
the 26.3 merged jar; paths are hard-coded to /tmp/claude-0 and the loom cache), and *gen.py
applies the solved mapping to this mod's sheets. The mappings reproduce vanilla pixel-exactly
(red bed; all 11 1.21.1 sign woods). Not part of the build.

## Data/asset migration scripts (26.3)
- `worldgen_migrate.py <old worldgen dir> <new worldgen dir>` - 1.21.1 configured/placed features -> 26.3 `feature/` + `placed_feature/` (rules in its docstring).
- `data_migrate.py <old data dir> <new data dir>` - hand-written recipes, loot tables and advancements -> 26.3 formats.
- `item_defs.py <missing-ids file>` - writes `items/<id>.json` for items whose models are hand-written, with the 1.21.1 item colors as tint sources. The list comes from `BFItemDefinitionValidator` (runs in `runDatagen`).
- `STATUS.md` - the porting status doc (mirrors the project doc).
