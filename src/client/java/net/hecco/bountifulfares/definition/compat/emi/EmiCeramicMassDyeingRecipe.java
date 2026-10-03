package net.hecco.bountifulfares.definition.compat.emi;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmiCeramicMassDyeingRecipe extends EmiPatternCraftingRecipe {
    // 26.3: DyeItem.byColor is gone - dye items are Items.DYE.pick(color) - and
    // DyedItemColor.applyDyes takes DyeColors rather than DyeItems. The original skipped Unidye's
    // extra "custom_dye" color; colors without a vanilla dye item are skipped the same way.
    private static final List<DyeColor> DYES = Stream.of(DyeColor.values()).filter(EmiCeramicMassDyeingRecipe::hasVanillaDye).toList();
    private final ItemLike ceramicItem;

    public EmiCeramicMassDyeingRecipe(ItemLike ceramicItem, Identifier id) {
        super(List.of(
                EmiIngredient.of(DYES.stream().map(c -> (EmiIngredient) EmiStack.of(Items.DYE.pick(c))).collect(Collectors.toList())),
                        EmiStack.of(ceramicItem)),
                EmiStack.of(ceramicItem), id);
        this.ceramicItem = ceramicItem;
    }

    @Override
    public SlotWidget getInputWidget(int slot, int x, int y) {
        return new GeneratedSlotWidget(r -> {
            List<DyeColor> dyes = getDyes(r);
            int ceramicAmount = r.nextInt(2, 10 - dyes.size());
            if (slot < dyes.size()) {
                return EmiStack.of(Items.DYE.pick(dyes.get(slot)));
            }else if(slot < dyes.size() + ceramicAmount){
                return EmiStack.of(getCeramicStack(r));
            }
            return EmiStack.EMPTY;
        }, unique, x, y);
    }

    @Override
    public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(r -> {
            List<DyeColor> dyes = getDyes(r);
            int ceramicAmount = r.nextInt(2, 10 - dyes.size());
            EmiStack emiStack = EmiStack.of(DyedItemColor.applyDyes(getCeramicStack(r), dyes));
            emiStack.setAmount(ceramicAmount);
            return emiStack;
        }, unique, x, y);
    }

    private List<DyeColor> getDyes(Random random) {
        List<DyeColor> dyes = Lists.newArrayList();
        int amount = random.nextInt(1,8);
        for (int i = 0; i < amount; i++) {
            dyes.add(DYES.get(random.nextInt(DYES.size())));
        }
        return dyes;
    }

    private ItemStack getCeramicStack(Random random) {
        random.nextInt(); //to set the random a bit off

        List<DyeColor> dyes = Lists.newArrayList();
        int amount = random.nextInt(1,4);
        for (int i = 0; i < amount; i++) {
            dyes.add(DYES.get(random.nextInt(DYES.size())));
        }
        return DyedItemColor.applyDyes(new ItemStack(ceramicItem), dyes);
    }

    private static boolean hasVanillaDye(DyeColor color) {
        try {
            return Items.DYE.pick(color) != null;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
