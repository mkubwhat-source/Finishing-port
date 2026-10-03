# Flavored → bundled into Bountiful Fares 26.3 Fabric - porting status

Flavored (github.com/CodenamedSuper/flavored, 1.21.1 NeoForge) is ported to Minecraft 26.3 Fabric
and **bundled inside the Bountiful Fares jar** (`/home/claude/work/bf-port`, same build). It keeps
its own `flavored:` ids and its own creative tab; `fabric.mod.json` `provides: ["flavored"]`.
Code: `src/main/java/com/sidden/flavored/**`, client `src/client/java/com/sidden/flavored/client/**`,
assets `assets/flavored`, data `data/flavored` (+ a few `data/minecraft` loot/tag files).

## User decisions
- **Merge duplicates**: Flavored's corn/corn seeds/corn bush are dropped in favour of BF maize;
  Flavored flour merged into `bountifulfares:flour`. Registry aliases keep old saves working:
  `flavored:flour→bountifulfares:flour`, `corn→maize`, `corn_seeds→maize_seeds`,
  item `corn_bush→maize_seeds`, block `corn_bush→maize_crop` (`FlavoredMerges`). Recipes that
  used flour/corn now take `#c:flour` / `#c:foods/corn`.
- **Both machines make both**: the keg also makes BF's fermented goods and BF's fermentation
  vessel also makes Flavored's (`_porting_tools/bundle_compat_recipes.py`: 12 keg versions of BF
  vessel recipes - jar/bottle as a *required* fermenter, otherwise sugar, fungi for foul flesh; 5
  vessel versions of Flavored keg recipes under `data/bountifulfares/recipe/fermenting/flavored/`).
  `FermentingRecipe` gained an optional `fermenter_required` field for that.
- **Keep crafting, add oven too**: Flavored's barrier overrides of vanilla recipes are *not*
  applied; BF baked goods additionally get 17 oven recipes (pies/tarts from pastry dough, cakes
  from batter, artisan bread/cookies and walnut cookies from dough, maize bread).
- **Keep `flavored:` ids, own tab.**

## Done
Blocks/items/foods/effects/entities (chocken)/particles/features (cinnamon pillar in jungles)/
loot injection/knife butchering loot/villager (wandering trader) trades/stats/data attachments/
keg + oven + mixing bowl (menus, screens, 26.3 recipe books, Fabric recipe sync)/HUD (sugar crave)/
spiciness tooltip/mixing bowl liquid renderer/drip particles. JEI plugin (fermenting, mixing,
baking categories, catalysts, click areas, fermenter required/optional tooltip) and a new EMI
plugin (1.21.1 Flavored had JEI only). Data migrated by `_porting_tools/flavored_data_migrate.py`
and `flavored_knife_loot.py`.

## Behaviour changes vs 1.21.1 (intentional fixes)
Recipe book has no search tab; tomato sneak-use only throws; pizza slice logic is server-side;
honeycomb on soft cheese is consumed; oven keeps the bowl; unrelated items invalidate the spicy
recipe; milk/totem can't clear crave/booze/hangover early; keg output stacks by components;
Config class dropped. The original cake and ossobuco oven recipes produced 2 of an unstackable
item (invalid - 26.3 refuses to create the stack); they now produce 1. Chocken uses
`Animal.createAnimalAttributes()` (26.3 TemptGoal reads `tempt_range`; the old attributes crashed
the server as soon as a chocken ticked).

## Woodland Mansions (lukidonu/darkstarworks) compatibility
See `golden_grove_cellar.py` and `net.hecco.bountifulfares.compat.woodlandmansions`. That pack
replaces `minecraft:mansion` with a jigsaw structure, so BF's golden apple tree room (injected via
`WoodlandMansionPieces`) never appeared. A `SinglePoolElement.place` hook places a secret cellar
version of the room under the pack's back-right wing (`mansions:mansion/bottom_back_right`),
reached by a flush spruce trapdoor in that wing's workshop floor + ladder. Same rotation, same
chunk box, the pack's own `mansions:mansion_texture` weathering processors. Only runs when that
pack's templates are being placed; no effect otherwise. The target link was the Grand Capitals
folder, but that jar has no mansion (villages/outposts only); its mansion screenshots are of the
sibling "Woodland Mansions" pack in the same collection, which is what this targets.
**Gotcha**: 26.3 structure NBT palettes are `{id, properties}` (BlockState codec). A file in the
old `{Name, Properties}` shape with DataVersion 5023 skips the data fixer and loads as all-air
(`/place template` still reports success). The generator writes the native shape.
Verified in the dev client for rotations NONE, CLOCKWISE_90 and CLOCKWISE_180 (hatch, ladder,
tree present; hatch sits in the workshop floor in front of the bookshelves); production jar
boots on a Fabric server with the pack installed. BF's sapling nursery (the other vanilla
mansion room) is likewise absent with that pack - not added (user only asked for the golden
tree room).

## Released
`Bountiful Fares-4.0.0-26.3+flavored.jar` and `bountifulfares-4.0.0-26.3-with-flavored.zip`.

## Verified
compileJava/compileClientJava, runDatagen (+validation), dev server boot, production jar on a
real Fabric server (boots/stops clean, `place feature flavored:cinnamon_pillar` works), dev client:
Flavored tab (all items textured), keg/oven/mixing bowl GUIs, EMI categories (keg shows BF
compat recipes, oven shows BF pies from pastry dough), chocken adult + baby rendering.
Not verifiable here: the recipe-book button (vanilla's own inventory book doesn't open under
Xvfb either).
