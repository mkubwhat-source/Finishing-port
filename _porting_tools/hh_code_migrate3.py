#!/usr/bin/env python3
"""Pass 3 of the Hearth and Harvest 1.21.1 -> 26.3 source migration: block/item method signature
changes (codec() removal, updateShape, neighborChanged, bonemeal source, analog output direction, ...)
and a few renamed helpers. Idempotent; run over src/main/java/alabaster/hearthandharvest."""
import re, sys, pathlib

root = pathlib.Path(sys.argv[1])
P = r'(?:final\s+)?(?:@\w+\s+)?'  # optional modifiers before a parameter

def add_import(s, imp):
    if f"import {imp};" in s: return s
    return re.sub(r'^(package [^;]+;\s*\n)', r'\1\nimport ' + imp + ';', s, count=1, flags=re.M)

def remove_method(s, name_regex):
    """Removes '@Override ... name(...) { ... }' with balanced braces."""
    out = s
    while True:
        m = re.search(r'\n([ \t]*)@Override\s*\n\s*(?:public|protected)[^\n{;]*\b' + name_regex + r'\s*\([^)]*\)\s*\{', out)
        if not m: return out
        i = m.end(); depth = 1
        while depth:
            c = out[i]
            if c == '{': depth += 1
            elif c == '}': depth -= 1
            i += 1
        out = out[:m.start()] + out[i:]

for f in root.rglob("*.java"):
    s0 = s = f.read_text()
    is_block = re.search(r'extends\s+\w*(Block|BushBlock|CropBlock)\b', s) is not None

    # Blocks have no codecs in 26.3.
    if is_block:
        s = remove_method(s, r'codec')
        s = re.sub(r'\n[ \t]*public static final MapCodec<[^;]*?> CODEC\s*=\s*(?:simpleCodec\([^;]*\)|RecordCodecBuilder\.mapCodec\(.*?\)\);|[^;]*?propertiesCodec\(\)[^;]*?);', '', s, flags=re.S)

    # getAnalogOutputSignal gained the queried side.
    s = re.sub(r'(int getAnalogOutputSignal\(\s*BlockState (\w+),\s*Level (\w+),\s*BlockPos (\w+))\)', r'\1, Direction direction)', s)

    # updateShape(state, facing, facingState, level, pos, facingPos)
    s = re.sub(r'updateShape\(\s*BlockState (\w+),\s*Direction (\w+),\s*BlockState (\w+),\s*LevelAccessor (\w+),\s*BlockPos (\w+),\s*BlockPos (\w+)\)',
               r'updateShape(BlockState \1, LevelReader \4, ScheduledTickAccess scheduledTickAccess, BlockPos \5, Direction \2, BlockPos \6, BlockState \3, RandomSource updateRandom)', s)
    s = re.sub(r'super\.updateShape\(\s*(\w+),\s*(\w+),\s*(\w+),\s*(\w+),\s*(\w+),\s*(\w+)\)',
               r'super.updateShape(\1, \4, scheduledTickAccess, \5, \2, \6, \3, updateRandom)', s)

    # getCloneItemStack(level, pos, state) -> + includeData
    s = re.sub(r'(ItemStack getCloneItemStack\(\s*LevelReader \w+,\s*BlockPos \w+,\s*BlockState \w+)\)', r'\1, boolean includeData)', s)
    s = re.sub(r'(super\.getCloneItemStack\(\s*\w+,\s*\w+,\s*\w+)\)', r'\1, includeData)', s)

    # entityInside(state, level, pos, entity) -> + effect applier, intersects
    s = re.sub(r'(void entityInside\(\s*BlockState \w+,\s*Level \w+,\s*BlockPos \w+,\s*Entity \w+)\)', r'\1, InsideBlockEffectApplier effectApplier, boolean intersects)', s)
    s = re.sub(r'(super\.entityInside\(\s*\w+,\s*\w+,\s*\w+,\s*\w+)\)', r'\1, effectApplier, intersects)', s)

    # fallOn distance is a double
    s = re.sub(r'(void fallOn\(\s*Level \w+,\s*BlockState \w+,\s*BlockPos \w+,\s*Entity \w+,\s*)float (\w+)\)', r'\1double \2)', s)

    # neighborChanged: fromPos -> orientation
    s = re.sub(r'(void neighborChanged\(\s*BlockState \w+,\s*Level \w+,\s*BlockPos \w+,\s*Block \w+,\s*)BlockPos (\w+),', r'\1@Nullable Orientation orientation,', s)

    # Bonemeal now carries its source.
    s = re.sub(r'(boolean isValidBonemealTarget\(\s*LevelReader \w+,\s*BlockPos \w+,\s*BlockState \w+)\)', r'\1, BonemealSource source)', s)
    s = re.sub(r'(boolean isBonemealSuccess\(\s*Level \w+,\s*RandomSource \w+,\s*BlockPos \w+,\s*BlockState \w+)\)', r'\1, BonemealSource source)', s)
    s = re.sub(r'(void performBonemeal\(\s*ServerLevel \w+,\s*RandomSource \w+,\s*BlockPos \w+,\s*BlockState \w+)\)', r'\1, BonemealSource source)', s)

    # propagatesSkylightDown / getOcclusionShape lost level+pos
    s = re.sub(r'(boolean propagatesSkylightDown\(\s*BlockState \w+)\s*,\s*BlockGetter \w+,\s*BlockPos \w+\)', r'\1)', s)
    s = re.sub(r'(VoxelShape getOcclusionShape\(\s*BlockState \w+)\s*,\s*BlockGetter \w+,\s*BlockPos \w+\)', r'\1)', s)

    # misc renames
    s = re.sub(r'InteractionResult\.sidedSuccess\([^()]*(?:\([^()]*\))?[^()]*\)', 'InteractionResult.SUCCESS', s)
    s = re.sub(r'\b(level|world|lvl|pLevel|serverLevel|this\.level\(\)|level\(\)|entity\.level\(\)|player\.level\(\))\.random\b', r'\1.getRandom()', s)
    s = re.sub(r'LivingEntity\.getSlotForHand\(([\w.()]+)\)', r'\1.asEquipmentSlot()', s)
    s = re.sub(r'\.displayClientMessage\(((?:[^()]|\([^()]*(?:\([^()]*\))*[^()]*\))*?),\s*true\s*\)', r'.sendOverlayMessage(\1)', s)
    s = re.sub(r'\.displayClientMessage\(((?:[^()]|\([^()]*(?:\([^()]*\))*[^()]*\))*?),\s*false\s*\)', r'.sendSystemMessage(\1)', s)
    if 'HHModDataComponents.java' not in f.name:
        s = re.sub(r'\bHHModDataComponents\.([A-Z_0-9]+)\b(?!\s*\.get\(\))(?!\s*\()', r'HHModDataComponents.\1.get()', s)

    if s != s0:
        for imp, trig in [("net.minecraft.core.Direction", "Direction direction)"),
                          ("net.minecraft.world.level.LevelReader", "LevelReader "),
                          ("net.minecraft.world.level.ScheduledTickAccess", "ScheduledTickAccess"),
                          ("net.minecraft.util.RandomSource", "RandomSource "),
                          ("net.minecraft.world.entity.InsideBlockEffectApplier", "InsideBlockEffectApplier"),
                          ("net.minecraft.world.level.redstone.Orientation", "Orientation orientation"),
                          ("org.jspecify.annotations.Nullable", "@Nullable Orientation"),
                          ("net.minecraft.world.level.block.BonemealSource", "BonemealSource source")]:
            if trig in s and not re.search(r'import [\w.]*\.' + imp.split('.')[-1] + ';', s):
                s = add_import(s, imp)
        f.write_text(s)
        print("updated", f.relative_to(root))
