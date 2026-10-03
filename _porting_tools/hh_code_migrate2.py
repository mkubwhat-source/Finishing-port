#!/usr/bin/env python3
"""Second mechanical pass for Hearth and Harvest (run after hh_code_migrate.py).

Usage: hh_code_migrate2.py <java root> [...]

* NeoForge fluid/item-handler classes -> HH's Fabric re-implementations
  (alabaster.hearthandharvest.platform.fluid / .inventory), same names and methods.
* Farmer's Delight classes -> the copies ported into alabaster.hearthandharvest.common.fd, FD
  registries -> HH's, FD Configuration -> HH Config.
* javax.annotation (JSR-305, not on the 26.3 classpath) -> JSpecify.
* 26.3 renames that are pure renames: Level.isClientSide field -> method, Entity.moveTo -> snapTo,
  ServerPlayer.serverLevel() -> level(), registryOrThrow -> lookupOrThrow, UseAnim ->
  ItemUseAnimation, DirectionProperty -> EnumProperty<Direction>, FarmBlock -> FarmlandBlock,
  MobSpawnType -> EntitySpawnReason, ItemInteractionResult / InteractionResultHolder ->
  InteractionResult (1.21.2 merged them; SKIP_DEFAULT_BLOCK_INTERACTION is now PASS and
  PASS_TO_DEFAULT_BLOCK_INTERACTION is TRY_WITH_EMPTY_HAND).
Anything with changed behaviour or signatures is ported by hand.
"""
import os, re, sys

IMPORTS = {
    'net.neoforged.neoforge.fluids.FluidStack': 'alabaster.hearthandharvest.platform.fluid.FluidStack',
    'net.neoforged.neoforge.fluids.FluidUtil': 'alabaster.hearthandharvest.platform.fluid.FluidUtil',
    'net.neoforged.neoforge.fluids.FluidActionResult': 'alabaster.hearthandharvest.platform.fluid.FluidActionResult',
    'net.neoforged.neoforge.fluids.capability.IFluidHandler': 'alabaster.hearthandharvest.platform.fluid.IFluidHandler',
    'net.neoforged.neoforge.fluids.capability.IFluidHandlerItem': 'alabaster.hearthandharvest.platform.fluid.IFluidHandlerItem',
    'net.neoforged.neoforge.fluids.capability.templates.FluidTank': 'alabaster.hearthandharvest.platform.fluid.FluidTank',
    'net.neoforged.neoforge.items.ItemStackHandler': 'alabaster.hearthandharvest.platform.inventory.ItemStackHandler',
    'net.neoforged.neoforge.items.IItemHandler': 'alabaster.hearthandharvest.platform.inventory.IItemHandler',
    'net.neoforged.neoforge.items.SlotItemHandler': 'alabaster.hearthandharvest.platform.inventory.SlotItemHandler',
    'net.neoforged.neoforge.items.ItemHandlerHelper': 'alabaster.hearthandharvest.platform.inventory.ItemHandlerHelper',
    'net.neoforged.neoforge.items.wrapper.RecipeWrapper': 'alabaster.hearthandharvest.platform.inventory.RecipeWrapper',
    'net.neoforged.neoforge.items.wrapper.InvWrapper': 'alabaster.hearthandharvest.platform.inventory.InvWrapper',
    'javax.annotation.Nullable': 'org.jspecify.annotations.Nullable',
    'javax.annotation.Nonnull': 'org.jspecify.annotations.NonNull',
    'net.minecraft.world.level.block.FarmBlock': 'net.minecraft.world.level.block.FarmlandBlock',
    'net.minecraft.world.entity.MobSpawnType': 'net.minecraft.world.entity.EntitySpawnReason',
    'net.minecraft.world.item.UseAnim': 'net.minecraft.world.item.ItemUseAnimation',
    'net.minecraft.world.ItemInteractionResult': 'net.minecraft.world.InteractionResult',
    'net.minecraft.world.InteractionResultHolder': 'net.minecraft.world.InteractionResult',
    'net.minecraft.world.level.block.state.properties.DirectionProperty': 'net.minecraft.world.level.block.state.properties.EnumProperty',
    'vectorwing.farmersdelight.common.Configuration': 'alabaster.hearthandharvest.Config',
    'vectorwing.farmersdelight.common.registry.ModItems': 'alabaster.hearthandharvest.common.registry.HHModItems',
    'vectorwing.farmersdelight.common.registry.ModBlocks': 'alabaster.hearthandharvest.common.registry.HHModBlocks',
    'vectorwing.farmersdelight.common.registry.ModBlockEntityTypes': 'alabaster.hearthandharvest.common.registry.HHModBlockEntities',
    'vectorwing.farmersdelight.common.registry.ModParticleTypes': 'alabaster.hearthandharvest.common.registry.HHModParticleTypes',
    'vectorwing.farmersdelight.common.registry.ModDataComponents': 'alabaster.hearthandharvest.common.registry.HHModDataComponents',
    'vectorwing.farmersdelight.common.tag.ModTags': 'alabaster.hearthandharvest.common.fd.FDTags',
    'vectorwing.farmersdelight.common.tag.CommonTags': 'alabaster.hearthandharvest.common.fd.FDTags',
    'vectorwing.farmersdelight.common.tag.CompatibilityTags': 'alabaster.hearthandharvest.common.tag.HHCompatibilityTags',
}
DROP_IMPORTS = [
    'javax.annotation.ParametersAreNonnullByDefault', 'net.minecraft.MethodsReturnNonnullByDefault',
    'net.neoforged.neoforge.fluids.FluidType', 'net.neoforged.neoforge.common.SoundActions',
    'net.neoforged.neoforge.capabilities.Capabilities',
]
TEXT = [
    (r'@ParametersAreNonnullByDefault\s*\n', ''),
    (r'@MethodsReturnNonnullByDefault\s*\n', ''),
    (r'\bFluidType\.BUCKET_VOLUME\b', 'FluidStack.BUCKET_VOLUME'),
    (r'\.isClientSide\b(?!\()', '.isClientSide()'),
    (r'(?<![\w.])isClientSide\b(?![\w(])', 'isClientSide()'),
    (r'\.serverLevel\(\)', '.level()'),
    (r'\.registryOrThrow\(', '.lookupOrThrow('),
    (r'\bUseAnim\.', 'ItemUseAnimation.'),
    (r'\bUseAnim\b', 'ItemUseAnimation'),
    (r'\bDirectionProperty\b', 'EnumProperty<Direction>'),
    (r'\bFarmBlock\b', 'FarmlandBlock'),
    (r'\bMobSpawnType\b', 'EntitySpawnReason'),
    (r'ItemInteractionResult\.PASS_TO_DEFAULT_BLOCK_INTERACTION', 'InteractionResult.TRY_WITH_EMPTY_HAND'),
    (r'ItemInteractionResult\.SKIP_DEFAULT_BLOCK_INTERACTION', 'InteractionResult.PASS'),
    (r'ItemInteractionResult\.sidedSuccess\([^()]*\)', 'InteractionResult.SUCCESS'),
    (r'InteractionResult\.sidedSuccess\([^()]*\)', 'InteractionResult.SUCCESS'),
    (r'ItemInteractionResult\.', 'InteractionResult.'),
    (r'\bItemInteractionResult\b', 'InteractionResult'),
    (r'InteractionResultHolder\.sidedSuccess\([^()]*(?:\([^()]*\))?[^()]*\)', 'InteractionResult.SUCCESS'),
    (r'InteractionResultHolder\.success\([^()]*(?:\([^()]*\))?[^()]*\)', 'InteractionResult.SUCCESS'),
    (r'InteractionResultHolder\.consume\([^()]*(?:\([^()]*\))?[^()]*\)', 'InteractionResult.CONSUME'),
    (r'InteractionResultHolder\.fail\([^()]*(?:\([^()]*\))?[^()]*\)', 'InteractionResult.FAIL'),
    (r'InteractionResultHolder\.pass\([^()]*(?:\([^()]*\))?[^()]*\)', 'InteractionResult.PASS'),
    (r'\bInteractionResultHolder<ItemStack>', 'InteractionResult'),
    (r'\bModItems\.', 'HHModItems.'), (r'\bHHHHModItems\.', 'HHModItems.'),
    (r'\bModBlocks\.', 'HHModBlocks.'), (r'\bHHHHModBlocks\.', 'HHModBlocks.'),
    (r'\bModBlockEntityTypes\.', 'HHModBlockEntities.'),
    (r'\bModParticleTypes\.', 'HHModParticleTypes.'), (r'\bHHHHModParticleTypes\.', 'HHModParticleTypes.'),
    (r'\bModDataComponents\.', 'HHModDataComponents.'), (r'\bHHHHModDataComponents\.', 'HHModDataComponents.'),
    (r'(?<![\w.])ModTags\.(Blocks|Items)\.', r'FDTags.\1.'),
    (r'(?<![\w.])CommonTags\.(Blocks|Items)\.', r'FDTags.\1.'),
    (r'\bConfiguration\.', 'Config.'),
]
FD_PKG = 'alabaster/hearthandharvest/common/fd'

def fd_import(name):
    # vectorwing.farmersdelight.common.X.Y -> alabaster.hearthandharvest.common.fd.X.Y if ported
    rest = name[len('vectorwing.farmersdelight.common.'):]
    return 'alabaster.hearthandharvest.common.fd.' + rest

roots = sys.argv[1:]
src_root = None
for r in roots:
    if r.endswith('/java'):
        pass
for root in roots:
    for dp, _, fs in os.walk(root):
        for f in fs:
            if not f.endswith('.java'):
                continue
            p = os.path.join(dp, f)
            s = open(p).read()
            o = s
            def imp(m):
                static, name = m.group(1) or '', m.group(2)
                if name in DROP_IMPORTS:
                    return ''
                if name in IMPORTS:
                    return f'import {static}{IMPORTS[name]};'
                if name.startswith('vectorwing.farmersdelight.common.'):
                    return f'import {static}{fd_import(name)};'
                return m.group(0)
            s = re.sub(r'import (static )?([\w.*]+);', imp, s)
            for a, b in TEXT:
                s = re.sub(a, b, s)
            # imports that the text rules may need
            def need(cls, fq):
                nonlocal_s = None
            for cls, fq in [('FluidStack', 'alabaster.hearthandharvest.platform.fluid.FluidStack'),
                            ('InteractionResult', 'net.minecraft.world.InteractionResult'),
                            ('ItemUseAnimation', 'net.minecraft.world.item.ItemUseAnimation'),
                            ('EnumProperty', 'net.minecraft.world.level.block.state.properties.EnumProperty'),
                            ('Direction', 'net.minecraft.core.Direction'),
                            ('HHModItems', 'alabaster.hearthandharvest.common.registry.HHModItems'),
                            ('HHModBlocks', 'alabaster.hearthandharvest.common.registry.HHModBlocks'),
                            ('HHModBlockEntities', 'alabaster.hearthandharvest.common.registry.HHModBlockEntities'),
                            ('HHModParticleTypes', 'alabaster.hearthandharvest.common.registry.HHModParticleTypes'),
                            ('HHModDataComponents', 'alabaster.hearthandharvest.common.registry.HHModDataComponents'),
                            ('FDTags', 'alabaster.hearthandharvest.common.fd.FDTags'),
                            ('Config', 'alabaster.hearthandharvest.Config')]:
                if s != o and re.search(r'\b' + cls + r'\b', s) and not re.search(r'import [\w.]*\b' + cls + r';', s) \
                        and not re.search(r'\b(class|interface|enum|record) ' + cls + r'\b', s):
                    pkg = re.search(r'package ([\w.]+);', s).group(1)
                    if fq.rsplit('.', 1)[0] != pkg and not re.search(r'import ' + re.escape(fq.rsplit('.', 1)[0]) + r'\.\*;', s):
                        s = re.sub(r'(package [\w.]+;\n)', lambda m: m.group(1) + f'\nimport {fq};', s, count=1)
            # de-duplicate imports
            lines = s.split('\n'); seen = set(); out = []
            for line in lines:
                if line.startswith('import '):
                    if line in seen:
                        continue
                    seen.add(line)
                out.append(line)
            s = '\n'.join(out)
            if s != o:
                open(p, 'w').write(s)
