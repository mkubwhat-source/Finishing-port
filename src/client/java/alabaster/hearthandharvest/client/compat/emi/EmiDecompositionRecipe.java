package alabaster.hearthandharvest.client.compat.emi;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.fd.FDTags;
import alabaster.hearthandharvest.common.fd.utility.TextUtils;
import alabaster.hearthandharvest.common.registry.HHModItems;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Organic compost decomposing into rich soil; the JEI category's layout and Farmer's Delight texture. */
public class EmiDecompositionRecipe extends BasicEmiRecipe {
    private static final Identifier TEXTURE = HearthAndHarvest.id("textures/gui/jei/decomposition.png");

    private final EmiIngredient accelerators;

    public EmiDecompositionRecipe() {
        super(HHEmiPlugin.DECOMPOSITION, HearthAndHarvest.id("/decomposition"), 118, 80);
        List<EmiStack> activators = new ArrayList<>();
        BuiltInRegistries.BLOCK.get(FDTags.Blocks.COMPOST_ACTIVATORS).ifPresent(tag -> tag.forEach(block -> {
            if (block.value().asItem() != Items.AIR) activators.add(EmiStack.of(block.value()));
        }));
        this.accelerators = EmiIngredient.of(activators);
        this.inputs = List.of(EmiStack.of(HHModItems.ORGANIC_COMPOST.get()));
        this.outputs = List.of(EmiStack.of(HHModItems.RICH_SOIL.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 118, 80, 0, 0);
        widgets.addTexture(TEXTURE, 63, 53, 22, 22, 119, 0);
        widgets.addTooltipText(List.of(TextUtils.JEI("decomposition.light")), 40, 38, 11, 11);
        widgets.addTooltipText(List.of(TextUtils.JEI("decomposition.fluid")), 53, 38, 11, 11);
        widgets.addTooltipText(List.of(TextUtils.JEI("decomposition.accelerators")), 67, 38, 11, 11);
        widgets.addSlot(inputs.get(0), 8, 25).drawBack(false);
        widgets.addSlot(outputs.get(0), 92, 25).drawBack(false).recipeContext(this);
        if (!accelerators.isEmpty()) {
            widgets.addSlot(accelerators, 63, 53).drawBack(false);
        }
    }
}
