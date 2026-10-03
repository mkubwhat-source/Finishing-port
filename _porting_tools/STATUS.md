
# Bountiful Fares → 26.3 Fabric (dependency-free) porting status

Porting Bountiful Fares (Fabric+NeoForge multiloader mod for Minecraft 1.21.1, depending on the
author's own library mod NexusLib) to a **single-loader Fabric build for Minecraft 26.3**, with
**NexusLib removed entirely** and everything it provided ported in-tree. Upstream:
https://github.com/Heccology/Bountiful-Fares. Original 1.21.1 sources for reference:
`/home/claude/work/bountiful-fares/Bountiful-Fares-1.21-ML/` (NexusLib at `/home/claude/work/nexuslib/`).

Working tree: `/home/claude/work/bf-port` (plain directory, **not yet a git repo**).

## STATUS: build GREEN, datagen COMPLETE (+ self-validation), server AND client PLAY-TESTED
Dedicated server boots/creates a world/stops cleanly. The client was play-tested under Xvfb
(see env notes): title screen (+ BF splash texts), world creation, creative tab (all pages
render, no missing models), dyed ceramics/brush/leaf/grass tints, ceramic chest item + placed
BER (dye tint, lock untinted), dish, trellis, signs, hanging sign, coir bed, gristmill GUI with
a working wheat -> flour milling run, tiffin fill (8/64 tooltip), tiffin open-GUI icon while
selected, eating from a tiffin (7/64, tiffin kept), restoration hearts, acidic effect HUD icon,
BF advancement toast, "Potion of Acidity" naming. Client log: no errors (only the original
mod's own missing scorchkin textures). Datagen output diffed against the original's generated
folder: loot tables and "eat all food"/"eat all bad foods" advancements identical.

### Environment setup (redo every fresh session)
Gradle finds JDK 25 via `~/.gradle/gradle.properties`
(`org.gradle.java.installations.paths=/opt/jdks/jdk-25.0.4.1+1`, auto-download=false - without
it the foojay 0.8.0 resolver crashes on Gradle 9.7 with a misleading `IBM_SEMERU` error).
```
cd /home/claude/work/bf-port && ./gradlew --console=plain -q compileJava compileClientJava
timeout 1500 ./gradlew --console=plain runDatagen > /tmp/dg.log 2>&1; grep -n "ERROR\|Caused by" /tmp/dg.log | head
./gradlew --console=plain build -x test
# server smoke test: runs/server has eula=true + online-mode=false; feed "stop" via a fifo
# client: Xvfb :99 -screen 0 1280x720x24 +extension GLX +extension RENDER -noreset
#   DISPLAY=:99 SDL_VIDEO_FORCE_EGL=1 XDG_RUNTIME_DIR=/tmp/claude-0/xdg ./gradlew runClient
#   (26.3 windows via SDL3 and asks for an sRGB-capable framebuffer; Xvfb's GLX has no sRGB
#   fbconfigs, Mesa EGL does). Drive it with xdotool, screenshots with ImageMagick `import`.
```
Merged MC jar for `javap -p/-c`: `.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-aab4b66517/26.3/minecraft-merged-aab4b66517-26.3.jar`
(full extract at `/tmp/mcfull/`; vanilla data extracted at `/tmp/claude-0/vwg/data/minecraft/`).
Vanilla 1.21.1 client jar at `/tmp/claude-0/client-1.21.1.jar`. Audit scripts:
`/tmp/claude-0/mixinaudit.py`, `/tmp/claude-0/handleraudit.py`. Repack + migration scripts are in
the tree under `_porting_tools/` (see its README).

### Toolchain + NexusLib removal - done
loom 1.17.21, unobfuscated 26.3 (no refmaps: `loom.mixin` block and both `"refmap"` entries
removed). build.gradle `processResources` expands `${version}`/`${mod_id}` again (the multiloader
buildSrc used to). cloth-config via Modrinth; Mod Menu 21.0.0-beta.1; JEI
`maven.modrinth:jei:e9fIxppx`. EveryCompat/Selene and EMI parked under
`_disabled_pending_26.3_deps/` (no 26.3 builds). Repos pinned with `exclusiveContent`.

## Compile-phase work (all done; details in older revisions)
A color providers → tint sources; B BE renderers → render-state; B2 coir bed → static model +
repacked textures; C render layers → JSON; D ItemProperties → range-select; E particles;
F JEI update/EMI parked; G item-render mixins → data + `bountifulfares:tiffin` item model +
`bountifulfares:ceramic_chest` special renderer; H signs → static models + repacked textures;
I GUI/HUD; J JEI 31 + Fabric recipe sync; K misc.

## Runtime-phase work
- **RT-1 Mixin retargets** (LivingEntity, ServerPlayer, ThrownPotion, ArmorStand, Stonecutter x2,
  BlockDustParticle, FlintSteelDisp, CampfireBlockEntity, Villager) - all apply at runtime.
- **RT-2 Tiffin eating bug** (would have deleted the tiffin) fixed in `TiffinItem.finishUsingItem`.
- **RT-3 Behavior fix (flag to user)**: ShearsDispenseMixin no longer overwrites vanilla's
  dispenser shearing success.
- **RT-4 "Block id not set"**: `platform/BFProperties` + registration scopes (~410 call sites).
- **RT-5 "Components not bound yet"**: lazy creative-tab additions; recipes store
  `ItemStackTemplate`; advancement provider now computes item components the way vanilla's
  `RegistryComponentsReport` does (`BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(lookup)`,
  no global binding); potion icon built as an `ItemStackTemplate` with a POTION_CONTENTS patch.
  Also fixed `==` string compare on the namespace.
- **RT-6 Advancements**: 26.3 rejects backgrounds on non-root advancements - dropped from the 33
  children (1.21.1 ignored them, no visual change). Root background id fixed: ClientAsset ids are
  relative to `textures/` without `.png` (was producing `textures/textures/...png.png`).
- **RT-7 Loot tables were silently empty**: the earlier port wrapped a private
  `BlockLootSubProvider` inside a `SimpleFabricLootTableSubProvider`; its `add()` calls filled a
  map nothing emitted → datagen wrote **0** block loot tables without error. 26.3 Fabric API does
  ship `FabricBlockLootSubProvider`; `BFBlockLootTableProvider` now extends it (also gets Fabric's
  strict missing-table validation). 251 tables generated, identical file set to the original.
- **RT-8 Compat datapack recipes**: `ModIntegration.recipeGeneration(RecipeOutput, HolderGetter<Item>)`
  - builders must resolve tags via the datagen lookup, `BuiltInRegistries.ITEM` has no tags in
  datagen ("Missing tag"). All 9 integrations updated.
- **RT-9 Recipe ids**: Fabric 26.3's `FabricRecipeProvider.getRecipeIdentifier` is no longer
  applied, so bare-string `save(exporter, "name")` lands in `minecraft:`. The two citrus-essence
  fermenting recipes now pass an explicit `bountifulfares:` id (matches the original output).
- **RT-10 Dyeable ceramics (would have silently lost a feature)**: 1.21.1 made the 24 ceramic
  items crafting-grid dyeable via the `#minecraft:dyeable` tag + ArmorDyeRecipe. 26.3 removed
  both; each dyeable item now needs its own `minecraft:crafting_dye` recipe. Generated in
  `BFRecipeProvider.offerCeramicDyeRecipes` (vanilla `dyedItem` equivalent, `bountifulfares:` ids,
  group `dyed_ceramic`). `c:dyeable` tag kept.
- **RT-11 Access widener**: stale `AxeItem STRIPPABLES` entry removed (AxeItem is gone; stripping
  uses Fabric `BlockTransformerHelper`).
- **RT-12 Worldgen migrated** (`_porting_tools/worldgen_migrate.py`, rules in its docstring):
  `configured_feature/` → `feature/` with config flattened; BlockState `{id, properties}`;
  providers (`weighted`, `randomized_int`, inline state objects - a *string* is a registry ref);
  `dirt_provider`+`force_dirt` → `below_trunk_provider` (exact 1.21.1 semantics via
  `#cannot_replace_below_tree_trunk`); random_patch/flower folded into the placed features as
  count + trapezoid offset + inner filter, exactly as vanilla 26.3 did its own. Walnut/Hoary trunk
  placers had lost their `setDirtAt` call in an earlier pass - restored as `placeBelowTrunkBlock`.
- **RT-13 Hand-written data migrated** (`_porting_tools/data_migrate.py`): recipes (ingredient
  strings), loot tables (`condition`/`modifier`, `match_block`, int number providers),
  advancements (`recipes`, single entity condition, `minecraft:entity_type`). BF's `minecraft:cake`
  override now takes `#minecraft:eggs` like 26.3 vanilla's cake. **Removed** 24 dead advancements
  from the main pack: 22 `recipes/cooking/*` (their recipes exist nowhere in the original) and 2
  cabinet ones duplicated by the Farmer's Delight compat pack - 26.3 hard-fails registry loading
  on advancements referencing unknown recipes; 1.21.1 tolerated them.

- **RT-14 Client-phase fixes (found by play-testing)**:
  - SplashTextMixin: 26.3 `SplashManager.prepare` returns an immutable `List<Component>`; the old
    `addAll` of raw strings threw on every resource reload and made the client drop all resource
    packs. Now builds a new list via vanilla's `literalSplash`.
  - **158 items had no `items/<id>.json`** (every item whose model was hand-written; 26.3 has no
    implicit models/item fallback, and doesn't log it - they'd render as missing-model cubes).
    Written by `_porting_tools/item_defs.py`. 52 more generated ones pointed at the block model
    instead of the generated item model (fruit logs/woods, jack o'straws, pickets) - datagen now
    registers them; golden apple sapling uses `createPlantWithDefaultItem` (flat item like 1.21.1).
    New datagen check `BFItemDefinitionValidator` fails the run if any owned item lacks a
    definition or an en_us name, or a potion lacks its name keys.
  - Item tints (1.21.1 ItemColor lambdas) now vanilla tint sources: `minecraft:dye` (24
    ceramics + artisan brush, default -1), `minecraft:grass` (grassy dirt), `minecraft:constant`
    (-12012264 fruit leaves + apple/orange/lemon/plum logs & woods; -10967452 walnut leaves).
    Artisan brush "dyed" override -> `minecraft:has_component` dyed_color. The custom
    BFItemTintSources/BFItemModelProperties types were identical to vanilla's and were removed.
  - `SubmitNodeCollector.submitModel(..., Identifier, light, overlay, int)`: the last int is the
    **outline** color and the tint is always -1 - so dyed ceramic chests (block and item)
    rendered white and the trellis plant requested a white outline. All three renderers use the
    full overload (color, null UvMapping, outline 0) and now also submit the crumbling overlay
    (block-breaking cracks) like vanilla's ChestRenderer.
  - Potions: 26.3 names come from `Potion#name`; set to the 1.21.1 registry paths
    (`bountifulfares.long_acidic`, ...) so all 7 lang files' existing keys apply.
  - Chamomile blockstate: dropped always-true `flower_amount` terms (as vanilla pink_petals).
  - sun_hat_head model: added a particle texture (26.3 warns without one).
  - Tinged glass: `force_translucent` on its texture (1.21.1 registered TRANSLUCENT).
- **RT-15 Brewing (6 mixes were lost)**: PotionBrewing is gone; brewing is data-driven
  `minecraft:brewing` recipes. `BFRecipeProvider.offerBrewingRecipes` generates the 6 mixes for
  potion/splash/lingering (18) + gunpowder/dragon's-breath container transforms for the 5 BF
  potions (10), `bountifulfares:brewing/...` ids, format identical to vanilla's.
- **RT-16 Loot strict validation**: blocks the loot provider deliberately skips (hand-written
  tables or intentionally none) are excluded via `excludeFromStrictValidation`.

- **RT-17 Client crash fixed**: right-clicking a water-filled fermentation vessel with an item
  crashed the client (`FermentationVesselBlock`/`FermentationVesselBlockEntity` cast the level to
  `ServerLevel` for recipe lookup; the client runs `useItemOn` too). New
  `platform/BFRecipeLookup` uses `ServerLevel.recipeAccess()` on the server and Fabric's synced
  recipes on the client. `TiffinFoodCraftingRecipe.matches` now returns false off-server.
- **RT-18 Cloth Config optional**: the Mod Menu config screen lives in `ClothConfigScreen` and
  is only offered when cloth-config is installed; fabric.mod.json `suggests` cloth-config and
  modmenu (only fabric-api is required, as in 1.21.1 apart from NexusLib).
- **RT-19** `c:coconuts` tag translated; JEI added as `localRuntime` (dev runs only).
- Additional play-testing done: full fermentation cycle (water -> lemon -> fermented -> 2 citrus
  essence), trellis planting (advancement) / growth / harvest, JEI milling + fermenting
  categories and crafting recipes. The built jar was run on a real Fabric 0.19.5 server with
  only Fabric API 0.161.0+26.3 installed: boots and stops cleanly.

- **RT-20 EMI re-enabled** against the unofficial 26.3 port (github.com/link-fgfgui/emi, branch
  26.3, commit 2fb0d30, built locally into `libs/`; compileOnly + dev localRuntime, not bundled).
  Plugin moved back into `src/client/.../compat/emi` and ported: tag lookup (`lookupOrThrow/get`),
  recipes from `EmiRegistry.getRecipes()` (synced set), dyes via `Items.DYE.pick` +
  `DyedItemColor.applyDyes(stack, List<DyeColor>)`, milling output via `recipe.getOutput()`
  (the port's `EmiPort.getOutput` only knows vanilla recipe classes - milling recipes would have
  shown with no output), and crafting-grid dyeing of the 24 ceramics shown with EMI's
  `EmiArmorDyeRecipe` (1.21.1 EMI showed it via #minecraft:dyeable). `emi` entrypoint restored.
  To build EMI here: an init script pinning mezz.jei/net.fabricmc/org.ow2.asm/neoforged/
  terraformers repos and redirecting Maven Central to Google's mirror, `include("neoforge")`
  commented out locally, JAVA_HOME=JDK 25.
- **RT-21 Special recipe sync bug**: `BFRecipes.registerSpecialRecipe` built MapCodec.unit with a
  fresh instance per recipe but StreamCodec.unit with another, so syncing those recipes failed
  to encode. Now one shared instance (vanilla pattern); ceramic mass dyeing, tiffin coloring and
  tiffin food crafting serializers are synced so recipe viewers see them.
- Translations for the `dungeonsdelight:fleshes` and `supplementaries:lunch_basket_blacklist` tags
  (EMI reported them as untranslated).
- EMI verified in game: fermenting, milling, prismarine propagation, ceramic dyeing + mass dyeing,
  tiffin coloring; no EMI warnings.

## Released
`build/libs/Bountiful Fares-4.0.0-26.3.jar`, sent as bountifulfares-4.0.0-26.3.zip (jar inside).

## What's left
1. Make the working tree a git repo (not done - waiting on the user).
2. Re-check EveryCompat/Selene for 26.3 builds (external blockers); switch EMI to an official
   26.3 build when one exists.
3. Not play-tested: compat datapacks with the actual compat mods (none have 26.3 builds to test
   against), multiplayer on a dedicated server with a separate client.

## Flavored bundled + Woodland Mansions compat (later session)
Flavored is now ported and bundled into this jar; Bountiful Fares also gained a secret golden
apple tree cellar for darkstarworks' "Woodland Mansions" pack. See `_porting_tools/FLAVORED_STATUS.md`
(project doc `claude/flavored-26.3-fabric-bundled-porting-status.md`). Latest release:
`Bountiful Fares-4.0.0-26.3+flavored.jar` / `bountifulfares-4.0.0-26.3-with-flavored.zip`.
