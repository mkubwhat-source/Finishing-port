# START HERE: handoff for continuing the Bountiful Fares + Flavored + Hearth and Harvest 26.3 Fabric port

## LATEST STATE (2026-10-03, cloud session; repo github.com/mkubwhat-source/Finishing-port, branch claude/cool-turing-q22lhn)
The project now lives in this git repo (repo root = the old `bf-port/` folder). Branch commits, oldest first:
WIP import (a2fa670) -> HH data migration -> merges/cross-compat (c49f9f6) -> client assets (b4a8551) -> this doc update.

Done this session (details in `_porting_tools/HEARTH_AND_HARVEST_STATUS.md`):
- HH data migrated by `_porting_tools/hh_data_migrate.py` (report: `hh_data_migrate.log`): recipes, loot, tags,
  worldgen, structure NBT, villager trades (now 26.3 villager_trade data), compostables (generated `HHCompostables`),
  cherry-leaves loot modifier (`HHLootModifiers`). Tag files that BF's datagen also writes go through the generated
  `src/client/java/alabaster/hearthandharvest/datagen/HHTagAdditions.java` (called from BF's tag providers), so run
  `./gradlew runDatagen` after re-migrating.
- Merges: fermenter cross-recipes (`_porting_tools/hh_bundle_compat_recipes.py`), Drunk on all alcoholic drinks,
  mulch / jack o' straw / flour block / knife tags.
- Client assets migrated by `_porting_tools/hh_asset_migrate.py` (log: `hh_asset_migrate.log`; extra lang in
  `hh_lang_additions.json`): 0 missing models, textures or lang keys.
- Runtime fixes: WaterBottleMixin, AbstractHorseMixin, the HHModBlocks init cycle, client mixins marked client-only,
  the shapeless remainder codec, the crow spawn rule (worldgen chunk clamp), aliases for item-only renames.
- Verified: compile, runDatagen, dedicated server boots with all data (0 load errors), dev client: HH creative tabs
  fully textured, blocks/block entities/crow render, Cooking Pot / Keg / Cask screens open.
- Lilliput Lane NBT uses vanilla stand-ins for FD blocks not in the bundle (stove -> smoker, tatami -> bamboo mosaic,
  rope fence -> oak fence, canvas wall signs -> cherry/pale oak wall signs, wooden basket -> barrel);
  HH trellises -> BF trellises (one side per block). Not yet confirmed with the user.

NEXT (not done):
1. JEI plugin for HH (in progress, nothing written yet): port HH 1.21.1 `integration/jei` (keg, cask aging, stomping,
   ingredient info pages, bottle crate crafting) and FDR 26.3 `integration/jei` (cooking pot, cutting board) into
   `src/client/java/alabaster/hearthandharvest/client/compat/jei/`, and add it to fabric.mod.json's "jei" entrypoint.
   Model it on `com.sidden.flavored.client.compat.jei.FlavoredJeiPlugin`: recipes come from
   `BFClientRecipes.getAllOfType`; fluids use JEI's Fabric fluid helper (HH FluidStack mB * 81 = droplets).
   EMI shows JEI categories through JEMI; a native EMI plugin (like FlavoredEmiPlugin) is optional.
2. EMI logs ~236 "Untranslated tag" errors: add `tag.item.<ns>.<path>` lang keys for HH and c: tags.
3. Functional play-test: cutting board, cooking pot, keg, cask, stomping basin, grapes on BF trellises, Lilliput Lane /
   corn maze generation, wild crop patches.
4. Build the jar (`./gradlew build -x test`), update the STATUS docs, and add the LICENSE notice for HH (MIT) and FD (MIT).

Environment for a fresh cloud session:
- JDK 25: install Temurin 25 to /opt/jdk25. In `~/.gradle/gradle.properties` set
  `org.gradle.java.installations.paths=/opt/jdk25` and `org.gradle.java.installations.auto-download=false`.
  Copy `_porting_tools/env/mirror.gradle` to `~/.gradle/init.d/` (Maven Central returns HTTP 429 without it).
  Then `export JAVA_HOME=/opt/jdk25`.
- Sources the scripts read (clone to a scratch dir):
  `git clone -b NeoForge-1.21.1 https://github.com/AlabasterLeking/Hearth-And-Harvest hh`,
  `git clone -b fabric/latest/26.3 https://github.com/MehVahdJukaar/FarmersDelightRefabricated fdr`,
  `git clone -b 1.21 https://github.com/vectorwing/FarmersDelight fd121`.
  Vanilla 26.3 data/assets: unzip the minecraft-common and clientOnly jars from `.gradle/loom-cache/minecraftMaven`.
  Fabric API data: unzip the `data/` folder of the fabric-api jars. 1.21.1 client jar: from piston-meta (needed for the
  spawn egg template). Python packages: nbtlib, pillow.
- Re-running migrations: first reset the outputs to the pre-migration commits (data: a2fa670, assets: c49f9f6),
  then run the scripts (usage is in each docstring), then `./gradlew runDatagen`.
- Client test: `Xvfb :99 -screen 0 1280x720x24 +extension GLX`, then
  `DISPLAY=:99 SDL_VIDEO_FORCE_EGL=1 ./gradlew runClient --args="--quickPlaySingleplayer hhtest"`.
  The test world is a copy of runs/server/world with level.dat GameType=1 and allowCommands=1.
  Server smoke test: runs/server needs eula=true and online-mode=false; send "stop" on stdin.

---


## CURRENT WORK (2026-10-03): adding Hearth and Harvest (HH) to the bundle
Source: github.com/AlabasterLeking/Hearth-And-Harvest, branch NeoForge-1.21.1 (v1.4.0). Farmer's Delight
is no longer a dependency; the FD parts HH needs come from FarmersDelightRefabricated (26.3 branch,
commit 615259dd4) and live under `hearthandharvest:` ids.

Read these first:
- `bf-port/_porting_tools/HEARTH_AND_HARVEST_STATUS.md`: the 16 merge decisions the user made, implementation notes, status checklist.
- `bf-port/_porting_tools/hh_port_todo.md`: running list of follow-ups found while porting code.
- Code: `src/main/java/alabaster/hearthandharvest/**` and `src/client/java/alabaster/hearthandharvest/**`.

State:
- Main source set: compiles (0 errors).
- Client source set: compiles (0 errors) as of 2026-10-03; entrypoint `HearthAndHarvestClient`.
- User rule: NO trident-style pitchfork changes. The pitchfork item uses a plain item model.
- Not started: data (recipes, loot, tags, worldgen, data maps, trades), assets (FD + HH textures, models,
  lang, sounds), JEI/EMI plugins, datagen, play-test, LICENSE notice.

Env helpers that were used are in `bf-port/_porting_tools/env/`:
- `dc`: decompiles one MC class with vineflower.
- `jp`: runs javap against the MC and Fabric API jars.
- `hhsnap`: makes this zip.
- `mirror.gradle`: Gradle init script.

`dc` and `jp` expect a `ref/` folder with `common.jar` and `client.jar`, taken from loom's
`.gradle/loom-cache` minecraft jars. Re-create it after `./gradlew compileJava`.

---

## Earlier handoff (BF + Flavored)

Give this whole zip to the new chat and ask it to read this file first.

## What's in this zip
- `HANDOFF.md`: this file.
- `transcript.md`: the last chat (Flavored port + Woodland Mansions room). Its first message is a
  detailed summary of the work before it.
- `jar/Bountiful Fares-4.0.0-26.3+flavored.jar`: latest working build (Bountiful Fares with
  Flavored bundled in). Requires Fabric Loader 0.19.5+ and Fabric API 0.161.0+26.3 on
  Minecraft 26.3. Optional: JEI, EMI, Cloth Config, Mod Menu.
- `bf-port/`: full source tree (Gradle project). Build outputs, run folders and the Gradle cache
  are left out; they regenerate.
  - `bf-port/_porting_tools/STATUS.md`: Bountiful Fares port status, every fix made and why.
  - `bf-port/_porting_tools/FLAVORED_STATUS.md`: Flavored port status, decisions and the mansion room.
  - `bf-port/_porting_tools/*.py`: migration/generator scripts (each documents itself).

## Project in one paragraph
Bountiful Fares (1.21.1, Fabric+NeoForge, needed NexusLib) was ported to a single-loader Fabric
build for Minecraft 26.3 with NexusLib removed. Flavored (1.21.1 NeoForge,
github.com/CodenamedSuper/flavored) was then ported and bundled into the same jar. It keeps its
`flavored:` ids and creative tab, and the two mods share features: BF maize replaces Flavored corn,
and both use `bountifulfares:flour` (aliases keep old saves working). The keg and fermentation
vessel each make the other's fermented goods. BF baked goods also bake in the Flavored oven, and
vanilla crafting recipes are kept. Last addition: a secret golden apple tree cellar for
darkstarworks' "Woodland Mansions" pack (github.com/darkstarworks/minecraft-refresh-collection,
folder `woodland mansions`), since that pack replaces vanilla mansions and BF's own room never
generated there.

## Building (needs JDK 25)
```
cd bf-port
./gradlew compileJava compileClientJava      # compile check
./gradlew runDatagen                          # regenerates src/main/generated, validates items/lang
./gradlew build -x test                       # jar -> build/libs/Bountiful Fares-4.0.0-26.3.jar
```
Toolchain: loom 1.17.21, Gradle 9.7.1, Minecraft 26.3 (unobfuscated, Mojang names).
EMI has no official 26.3 build; `libs/` holds the jar built from the unofficial port
(github.com/link-fgfgui/emi, branch 26.3, commit 2fb0d30). It's compile-only, not bundled.
In the old environment `~/.gradle/gradle.properties` pointed Gradle at JDK 25 with auto-download
off (`org.gradle.java.installations.paths=...`). Without that, the foojay resolver crashed with a
misleading `IBM_SEMERU` error.

## How we work (the user's standing preferences)
- Don't cut corners. If something must be done a certain way, do it that way. Be mindful of usage.
- Verify 26.3 APIs against the real jar (javap) instead of guessing.
- Play-test, don't just compile. Earlier sessions ran the client under Xvfb (see STATUS.md env notes).
- Keep the status docs updated as work progresses.
- Don't make it a git repo or commit unless the user asks (still undecided).

## Open items / ideas not done yet
1. BF's sapling nursery mansion room also doesn't appear with the Woodland Mansions pack (only
   the golden tree room was requested). It could get the same treatment.
2. A cellar under a mansion that generates over ocean floods; mansions on land are fine.
3. Recipe-book buttons on the keg/oven/mixing bowl were never clicked successfully in testing
   (vanilla's own book didn't open under Xvfb either), so they're unverified.
4. Woodland Mansions cellar not checked at rotation COUNTERCLOCKWISE_90 (the other three were).
5. EveryCompat/Selene integrations parked under `_disabled_pending_26.3_deps/` until those mods
   ship 26.3 builds; swap EMI to an official build when one exists.
6. Compat datapacks for other mods are untested (none had 26.3 builds).
