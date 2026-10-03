# HH port: running TODO (things found while porting code, to do in the data/client passes)

## Data / assets
- lang: tooltip for bountifulfares:mead_bottle poison cure (WineBottleItem hasCustomTooltip -> TextUtils.getTranslation("tooltip.mead_bottle")).
- GUI sprite: keg bottle slot icon is now `hearthandharvest:container/slot/bottle` (textures/gui/sprites/container/slot/bottle.png); 1.21.1 used block-atlas `gui/bottle_slot`.
- data maps: vintage_style + fluid_bottle entries for flavored:sweet_berry_wine, flavored:glow_berry_wine, bountifulfares:mead_bottle (merged wines); cask vintage recipes for them.
- tag c:villager_farmlands (block): minecraft:farmland + hearthandharvest:rich_soil_farmland (grape trellis crops use it as soil).
- grapes: data/hearthandharvest/bountifulfares/trellis_crop/{red,green}_grapes.json done; textures done (assets/hearthandharvest/textures/block/*_grape_trellis_1..5.png).
- recipe book categories need lang (recipe book tab names are icons only; check).

## Client (code DONE 2026-10-03: compileClientJava 0 errors; entrypoint HearthAndHarvestClient)
- (done) payload receivers, screens, recipe books (HHRecipeBooks), particles (incl. FD star/steam/sparkle), fluids,
  crow model/renderer/layers, chicken glide + shoulder crows (render-state mixins), keybind, tooltips, seed pouch decoration,
  DisplayModels reload clear, registries on join/disconnect.
- assets for the above:
  - lang `key.category.hearthandharvest.main` (1.21.1 used key.categories.hearthandharvest); `key.hearthandharvest.poop`.
    Poop key is P and requires Shift held (vanilla key mappings have no modifiers).
  - HH 1.21.1 is missing `textures/block/fluid/glow_berry_juice_flow.png` (only its .mcmeta) -> copy the still texture.
  - "milk" fluid uses goat_milk textures.
  - recipe book filter sprites `hearthandharvest:recipe_book/{keg,cask,cooking_pot}_{enabled,disabled}[_highlighted]`.
  - vintage items: item definitions `minecraft:select` on component `hearthandharvest:vintage` (replaces ItemProperties "vintage").
  - cooking pot tab icons changed (FD noodles/dumplings don't exist): corn stew / flavored:apple_juice / tomato sauce.
- not ported (FD-global, not HH): FD vanilla-soup effect tooltips, skillet, stove renderer.

## Code
- (done) crate + salt drip pushReaction moved to block properties.
- (done) BF TrellisCropDefinition gained optional `spreading` (chance, max_height, soil); TrellisBlock.SOIL_REQUIRED hook used for HH config.
- (done) client join/disconnect registries, FD particle providers (particles json + textures still needed), cooking pot tooltip.
- assets: for every models/display/*.json (and vintage overlay models) add items/<same path>.json {"model":{"type":"minecraft:model","model":id}} (DisplayModels uses item_model override).
- (done) DisplayModels cache cleared on resource reload.

## Pitchfork (user, 2026-10-03: no trident-style changes)
- PitchforkSpecialRenderer removed; do NOT add a trident-like special/condition items/pitchfork.json.
- items/pitchfork.json: plain {"model":{"type":"minecraft:model","model":"hearthandharvest:item/pitchfork"}}; thrown entity still uses ThrownPitchforkRenderer/ThrownPitchforkModel (original HH entity).
