# Hearth and Harvest → bundled into Bountiful Fares 26.3 Fabric - plan & status

Source: github.com/AlabasterLeking/Hearth-And-Harvest, branch `NeoForge-1.21.1` (v1.4.0, latest,
newer than the stale `Fabric-1.21.1` branch). MIT. 279 Java files, ~500 recipes, 267 items.
Bundled into the same jar as BF + Flavored, keeps `hearthandharvest:` ids.

**Farmer's Delight dependency removed.** HH becomes standalone. The FD pieces HH needs are copied
from FarmersDelightRefabricated (github.com/MehVahdJukaar/FarmersDelightRefabricated, branch
`fabric/latest/26.3`, commit 615259dd4, MIT © vectorwing) under `hearthandharvest:` ids.
Credit kept in fabric.mod.json and LICENSE notice.

## User decisions (2026-10-02)
1. **FD machines**: port FD's Cooking Pot and Cutting Board into the mod. Cutting board accepts
   HH cleavers and Flavored's knife (plus any `#c:tools/knife`).
2. **Fermenters**: keep HH Keg (+ Cask aging/vintages), Flavored Keg, BF Fermentation Vessel;
   all cross-compatible (each makes the others' drinks; Cask stays HH-only).
3. **Duplicate drinks merge into existing**: HH sweet_berry_wine / glow_berry_wine /
   sweet_berry_juice / glow_berry_juice → flavored:*; HH mead → bountifulfares:mead_bottle.
   HH hard_cider stays (aged cider). Registry aliases for old ids.
4. **Ingredients**: merge flour (→ bountifulfares:flour), butter, batter (→ flavored:*),
   FD wheat_dough → flavored:dough, HH chocolate_bar → flavored:chocolate.
   **Keep HH cheese wheels** (separate from Flavored aged cheese). Goat cheese stays.
5. **Corn: keep both** HH corn and BF maize. Tags make them interchangeable where sensible.
6. **Trellis** (revised 2026-10-03): keep BOTH. HH's trellis / bamboo_trellis / stripped_bamboo_trellis
   and grape_trellis are back (the user wants HH's multi-piece placement); red/green grapes also stay
   BF trellis plants on every BF wood trellis. Lilliput Lane uses HH's trellises again, as upstream.
7. **Dishes**: merge exact dupes only: HH pizza/pizza_slice → flavored:pizza/pizza_slice
   (HH cheese/meat/veggie pizzas stay); HH pickled_beetroots → bountifulfares:pickled_beetroot.
   Corn dishes stay separate.
8. **Alcohol: all alcoholic drinks give HH Drunk** (Flavored beer/cider/wines and BF wines/mead
   included). Flavored Boozed/Hangover become unused by drinks. BF Stupor is untouched.
9. **FD extras**: map to existing first (tomato→flavored red tomato, beef_patty→flavored ground
   beef, chicken_cuts→flavored chicken drumstick, raw_pasta→flavored pasta, apple_cider→existing
   cider, knives→flavored knife); port the rest from FDR as hearthandharvest: items.
10. **Scarecrow**: keep HH scarecrow; BF Jack o' Straws also repel crows.
11. **Storage**: keep HH apple/golden apple crates AND BF apple/golden apple blocks; merge HH
    flour_bag → bountifulfares:flour_block.
12. **Mulch**: BF walnut/palm mulch (and mulch blocks) gain HH mulch's effect (speeds saplings,
    keeps farmland moist). Farmer's hat / sun hat untouched.
13. **Merged wines gain HH wine features**: flavored:sweet_berry_wine / glow_berry_wine and
    bountifulfares:mead_bottle keep their id/name/texture but use HH's WineBottleItem behaviour
    (cask aging into vintages, 3 glasses per bottle, Drunk scaled by vintage).
14. **Cooking pot stays separate** from Flavored's mixing bowl/oven (no shared recipes).
15. **Rotten tomato crate dropped** (FD rotten tomatoes don't exist in the bundle).
16. **Blades separate**: cleavers keep the +50% meat bonus, Flavored knife keeps its cuts; both are
    in #c:tools/knife so they work on the cutting board and harvest straw.

## Implementation notes
- NeoForge APIs HH used are re-implemented in `alabaster.hearthandharvest.platform`:
  `fluid` (FluidStack in mB, FluidTank, IFluidHandler, FluidUtil with NeoForge's transfer rules,
  adapters to/from Fabric's droplet transfer API, 81 droplets/mB) and `inventory` (FDR's Fabric
  ItemStackHandler/InvWrapper/RecipeWrapper/SlotItemHandler, LGPL-2.1 headers kept from NeoForge;
  FDR's InvWrapper insert check had a missing `!`, fixed). Config: `config/HHConfigSpec`
  (same options/defaults, `config/hearthandharvest-common.toml`).
- HH fluids are plain FlowingFluids with no world block (same as 1.21.1).
- FD code lives in `alabaster.hearthandharvest.common.fd` (+ client `client.fd`), copied from
  FDR 26.3 with registries pointed at HH's. FD 1.21.1's `wild_crop` feature (dropped by FDR 26.3)
  is ported as `hearthandharvest:wild_crop`. Nourishment + Comfort effects ported.
- Crows: NeoForge-extended `crow` MobCategory can't exist on Fabric -> CREATURE, HH spawn rules kept.
- Crabber's Delight palm cabinet/bottle rack stay conditional (that mod has no 26.3 Fabric build).
- Scripts: `_porting_tools/hh_code_migrate.py`, `hh_code_migrate2.py`, `hh_code_migrate3.py`
  (block/item signature changes), `hh_code_migrate4.py` (CompoundTag save/load -> ValueInput/ValueOutput).
- Grapes: Bountiful Fares' `trellis_crop` data gained an optional `spreading` object (chance,
  max_height, soil tag). HH grapes are BF trellis crops (data/hearthandharvest/bountifulfares/trellis_crop)
  that climb/spread onto empty trellises and need `#c:villager_farmlands` under the lowest vine
  (HH config `grapeRequireFarmland` still applies). HH's own trellises are ported too (see
  "Play-test fixes" below); `_porting_tools/hh_dropped_1.21.1` keeps the 1.21.1 sources they came from.
- Recipe books: the cooking pot, keg and cask get their own `RecipeBookType`s
  (HEARTHANDHARVEST_COOKING/FERMENTING/AGING) via class-tweaker enum extension + mixins, as
  FarmersDelightRefabricated does. `bountifulfares.accesswidener` is now a `classTweaker v2` file.
- Merged wines: flavored:sweet_berry_wine / glow_berry_wine and bountifulfares:mead_bottle are now
  HH `WineBottleItem`s (registered in FlavoredItems/BFItems) with HH's food values; mead keeps BF's
  poison cure as a consume effect.
- Crows on shoulders: 26.3 only syncs parrot variants for shoulder entities, so a synced
  `hearthandharvest:shoulder_crows` attachment (kept current by ServerPlayerShoulderMixin) tells
  clients to draw them. Horseshoe attachment is now synced to clients too.
- Removal side effects (nest/jar/trough drops, keg/stomping basin multiblock dissolve) moved from
  Block#onRemove to BlockEntity#preRemoveSideEffects (26.3).
- Running list of follow-ups found during the code port: `_porting_tools/hh_port_todo.md`.
- 2026-10-02: the cloud workspace was reclaimed mid-port once; work-in-progress zips are now sent
  to the user at milestones.

## Status
- [x] Code port, main source set (compiles 2026-10-03)
- [x] Code port, client source set (compiles 2026-10-03): 26.3 render-state renderers, AbstractRecipeBookScreen screens,
      SingleQuadParticle particles, Fabric entrypoint `HearthAndHarvestClient`; no trident-style pitchfork rendering (user).
- [x] FD pot + cutting board port (code + client)
- [x] Data migration (hh_data_migrate.py; server loads it with 0 errors)
- [x] Merges/aliases + cross-compat recipes (hh_bundle_compat_recipes.py, Drunk, tags)
- [x] Client code (renderers, screens, particles, models); assets still pending
- [x] JEI/EMI (2026-10-03, details below; checked in the dev client with JEI alone and with EMI + JEI)
- [x] Assets (hh_asset_migrate.py; checked in the dev client)
- [x] Build (jar now carries LICENSE_bountifulfares), datagen, dev server boot, dev client
- [ ] Functional play-test of the machines and worldgen (see HANDOFF.md NEXT)

## JEI / EMI (2026-10-03)
- JEI: `client/compat/jei/HHJeiPlugin` (entrypoint `jei_mod_plugin`), port of HH 1.21.1's plugin plus the
  FD parts from FarmersDelightRefabricated 26.3's: categories `hearthandharvest:aging` (cask),
  `fermenting` (keg), `stomping`, `cooking`, `cutting`, `decomposition`; HH and FD info pages (FD only
  for the FD items in the bundle: straw, wild cabbages, wild onions; the vintage page lists the merged
  wines/mead/pickled beetroot); catalysts; click areas on the cask and cooking pot screens; transfer
  handlers for the cask (4 inputs, inventory from slot 8) and cooking pot (6 inputs, inventory from 9).
  Recipes come from BFClientRecipes (Fabric recipe sync), fluids in droplets (mB * 81).
- HH 1.21.1's keg category pointed at `textures/gui/jei/jei_keg_gui.png`, which HH never shipped (it drew a
  missing texture). The keg page is now cut from `keg_gui.png`: both tanks with their scale overlays, the
  item slots, animated bubble columns, the glass bottle -> the bottle the result fills (HH fluid->bottle
  data map), and the ferment time printed under the bottles.
- Vintage (`hearthandharvest:vintage`) and other component ingredients are shown through the ingredient's
  SlotDisplay so the right vintage / water bottle appears.
- EMI: `client/compat/emi/HHEmiPlugin` (new; 1.21.1 HH had none). Same six categories, layouts and textures,
  same ids, so JEMI skips the JEI copies ("Skipping recipe category ... native EMI recipe category already
  exists"). EMI's own Ingredient conversion drops components, so custom ingredients are resolved through
  their SlotDisplay (HHEmiUtil). Fluids show in EMI's default unit (liters, 1000 per bucket, so 250 L = 250 mB).
  JEI's info pages reach EMI through JEMI when JEI is installed; EMI alone has no info pages.
- Recipe sync: `bottle_crate` and `shapeless_remainder` serializers are now synced too, so JEI's crafting
  category lists them (JEI itself only syncs minecraft: serializers; the tortilla recipe was checked).
  Note HH 1.21.1 ships no bottle_crate recipe data at all; its JEI code for them was dead upstream.
- Lang: FD's `jei.farmersdelight.*` keys were never migrated (hh_asset_migrate.py had the prefix as
  `farmersdelight.jei.`); fixed in the script and added as `jei.hearthandharvest.*` to the 5 HH locales.
  `gui.jei.category.smelting.time.seconds/experience` added to en_us (as FD does) so EMI-only installs
  read them. FD's JEI textures copied to `textures/gui/jei/{cooking_pot,cutting_board,decomposition}.png`.
- EMI "Untranslated tag" errors: 118 item tags (c:, hearthandharvest:, plus a few compat namespaces)
  now have `tag.item.*` names in HH's en_us (and hh_lang_additions.json). Dev client: 0 untranslated.
- Asset fix found on the way: `item/3d_watering_can.json` failed to load ("Expected between 1 and 6 unique
  faces") because hh_asset_migrate.py removed its `#missing` faces but kept the empty cubes. Upstream's
  model is an untextured draft that no display context uses (1.21.1 and this port both use the 2D model),
  so the script now drops face-less cubes too. The jar models' "Unresolved texture references" warnings
  are inherited from upstream (generic_jar's cube_all parent) and harmless.

## Play-test fixes (2026-10-03)
- Trellis textures on BF trellises (grapes): the renderer clamps the stage to 1..stages.
- Cleaver: per-hit durability via `DataComponents.WEAPON` (`Weapon(2)`, as FDR's knives), throw animation
  `ItemUseAnimation.TRIDENT` (26.3's SPEAR is the kinetic spear; same for pitchfork, horseshoe, hot sauce).
  Cutting board: a tool clicked on an empty board no longer goes on the board (sneak places tools, as FD).
  Missing `block.hearthandharvest.cutting_board.*` messages added to the 5 HH locales.
- Washed-out / desaturated models (corn, crops, coconuts, lanterns...): 26.3 ignores the element key
  `"shade"`; `"shade": false` is now `"shade_direction_override": "up"` (`_porting_tools/shade_fix.py`,
  also built into hh_asset_migrate.py). 33 models (BF, HH, appledog).
- HH trellises restored (user decision 6 revised): `common/block/trellis/*` (TrellisBlock, GrapeTrellisBlock,
  TrellisMaterial/Plant/Shape) and `common/item/TrellisBlockItem` ported from 1.21.1. NeoForge leftovers:
  `isLadder` -> `TrellisClimbMixin` (LivingEntity.onClimbable HEAD; flat-only pieces aren't climbable, as
  upstream), `ItemAbilities.AXE_STRIP` -> HH ItemAbility, burn time -> COOKING_FUEL component (300).
  Only the stick trellis item is the block's `asItem()` (1.21.1 let the stripped bamboo one win).
  Client: vine foliage tint (BlockColorRegistry, tint index 0), `TrellisGhostRenderer` placement preview
  (LevelRenderEvents.AFTER_BLOCK_OUTLINE_EXTRACTION + COLLECT_SUBMITS, translucent custom geometry, 40%
  alpha as upstream, config `trellisPlacementPreview`). Aliases to BF trellises removed. Data/assets come
  back through the migration scripts (no longer DROPPED); Lilliput Lane's NBT is upstream's trellis
  states again (checked block-for-block). Verified in the dev client: placing/extending pieces with the
  preview, vine/rose/grape trellises, grapes planted by hand, climbing, the structure.
- Crate item ("Empty Crate" in chests): it showed the double-crate model. 1.21.1's BEWLR drew one bottom
  crate raised 4px plus its contents; the item definition is now a composite of `item/crate` (parent
  `block/crate_bottom`) and a `hearthandharvest:crate_contents` special model (`CrateItemRenderer`), both
  translated 0.25 up. Filled crates show their items again.
- Re-running hh_asset_migrate.py: it doesn't write `textures/gui/jei/*.png` (copied by hand from FDR) or
  the newline-less `models/item/3d_watering_can.json`; restore them from git after a re-run.
