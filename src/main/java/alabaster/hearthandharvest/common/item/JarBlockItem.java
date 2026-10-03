package alabaster.hearthandharvest.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.context.BlockPlaceContext;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import alabaster.hearthandharvest.common.block.JarBlock;
import alabaster.hearthandharvest.common.block.entity.JarBlockEntity;
import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import alabaster.hearthandharvest.common.registry.HHModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;

import org.jspecify.annotations.Nullable;
import java.util.List;

public class JarBlockItem extends BlockItem implements AgeableItem {
    public static final int DEFAULT_SERVINGS = 4;
    private static final int BAR_COLOR = 0xE8A33D;

    private final Block displayBlock;
    private List<Holder<MobEffect>> cures = List.of();
    private int maxServings = DEFAULT_SERVINGS;
    private static final float SATURATION_BONUS_PER_VINTAGE = 0.1F;

    public JarBlockItem(Block placedBlock, Block displayBlock, Properties properties) {
        super(placedBlock, properties);
        this.displayBlock = displayBlock;
    }

    @SafeVarargs
    public final JarBlockItem cures(Holder<MobEffect>... effects) {
        this.cures = List.of(effects);
        return this;
    }

    @Override
    public boolean canAgeFurther(ItemStack stack) {
        return !isOpened(stack) && VintageHelper.canAgeFurther(stack);
    }

    public JarBlockItem servings(int servings) {
        this.maxServings = Math.max(1, servings);
        return this;
    }

    public Block getDisplayBlock() {
        return displayBlock;
    }

    public int getMaxServings() {
        return maxServings;
    }

    public int getServings(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(HHModDataComponents.SERVINGS.get(), maxServings), 1, maxServings);
    }

    private boolean isEdible(ItemStack stack) {
        return stack.has(DataComponents.FOOD);
    }

    private boolean isOpened(ItemStack stack) {
        return isEdible(stack) && getServings(stack) < maxServings;
    }

    private ItemStack withServings(ItemStack stack, int servings) {
        if (servings >= maxServings) {
            stack.remove(HHModDataComponents.SERVINGS.get());
        } else {
            stack.set(HHModDataComponents.SERVINGS.get(), servings);
        }
        return stack;
    }

    private ItemStack leftoverAfterServing(ItemStack stack) {
        int remaining = getServings(stack) - 1;
        return remaining > 0
                ? withServings(stack.copyWithCount(1), remaining)
                : new ItemStack(HHModItems.JAR.get());
    }

    /** Records which jar went into the placed slot (1.21.1 did this in updateCustomBlockEntityTag). */
    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean result = super.placeBlock(context, state);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (result && !level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof JarBlockEntity be) {
                for (int i = 0; i < JarBlock.SLOTS.length; i++) {
                    if (state.getValue(JarBlock.SLOTS[i])) {
                        be.setSlot(i, stack);
                        be.setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        BlockState existing = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (existing.getBlock() == this.getBlock()) {
            return InteractionResult.PASS;
        }

        return super.place(ctx);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!isEdible(stack) || !stack.has(DataComponents.CONSUMABLE)) {
            return super.finishUsingItem(stack, level, entity);
        }

        // Eat one serving with the vintage's effects and saturation.
        ItemStack serving = VintageHelper.scaledServing(stack, SATURATION_BONUS_PER_VINTAGE, null);
        serving.get(DataComponents.CONSUMABLE).onConsume(level, entity, serving);
        if (!level.isClientSide()) {
            for (Holder<MobEffect> effect : cures) {
                entity.removeEffect(effect);
            }
        }

        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }

        ItemStack leftover = leftoverAfterServing(stack);
        stack.shrink(1);
        if (stack.isEmpty()) {
            return leftover;
        }
        if (entity instanceof Player player && !player.getInventory().add(leftover)) {
            player.drop(leftover, false, net.minecraft.util.Prediction.SERVER_ONLY);
        }
        return stack;
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        return isEdible(stack) ? ItemStackTemplate.fromNonEmptyStack(leftoverAfterServing(stack)) : super.getCraftingRemainder(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isOpened(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getServings(stack) / maxServings);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
        VintageHelper.appendTooltip(stack, tooltip);
        if (!isEdible(stack)) return;

        tooltip.accept(Component.translatable("tooltip.hearthandharvest.servings", getServings(stack), maxServings)
                .withStyle(ChatFormatting.GRAY));

        if (Config.ENABLE_FOOD_EFFECT_TOOLTIP.get()) {
            TextUtils.addFoodEffectTooltip(stack, tooltip, VintageHelper.durationFactor(stack), context.tickRate());
            if (!cures.isEmpty()) {
                MutableComponent names = Component.empty();
                for (int i = 0; i < cures.size(); i++) {
                    if (i > 0) names.append(", ");
                    names.append(Component.translatable(cures.get(i).value().getDescriptionId()));
                }
                tooltip.accept(Component.translatable("tooltip.hearthandharvest.cures", names).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}