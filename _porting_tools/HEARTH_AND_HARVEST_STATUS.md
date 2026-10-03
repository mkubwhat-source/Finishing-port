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
6. **Trellis**: drop HH trellis / bamboo_trellis / stripped_bamboo_trellis (alias to BF trellis
   blocks); red/green grapes become BF trellis plants on every wood trellis.
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
  (HH config `grapeRequireFarmland` still applies). HH's own trellis blocks/classes are dropped
  (kept for reference in `_porting_tools/hh_dropped_1.21.1`).
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
- [ ] JEI/EMI (next; see HANDOFF.md)
- [x] Assets (hh_asset_migrate.py; checked in the dev client)
- [ ] Build, datagen, dev server/client play-test
