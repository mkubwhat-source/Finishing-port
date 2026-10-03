package alabaster.hearthandharvest.common.item;

import alabaster.hearthandharvest.common.block.SaltBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SaltBlockItem extends BlockItem {

    public SaltBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        BlockItemStateProperties props = stack.get(DataComponents.BLOCK_STATE);
        if (props != null && "true".equals(props.properties().get("waxed"))) {
            String base = getDescriptionId();
            int dot = base.lastIndexOf('.');
            String waxedKey = dot >= 0
                    ? base.substring(0, dot + 1) + "waxed_" + base.substring(dot + 1)
                    : "waxed_" + base;
            return Component.translatable(waxedKey);
        }
        return super.getName(stack);
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state == null) return null;
        BlockItemStateProperties props = context.getItemInHand().get(DataComponents.BLOCK_STATE);
        if (props != null) {
            String waxedValue = props.properties().get("waxed");
            if (waxedValue != null)
                state = state.setValue(SaltBlock.WAXED, Boolean.parseBoolean(waxedValue));
        }
        return state;
    }
}