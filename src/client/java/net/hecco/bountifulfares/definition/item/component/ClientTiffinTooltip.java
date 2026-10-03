package net.hecco.bountifulfares.definition.item.component;

import net.minecraft.network.chat.TextColor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

/**
 * 26.3: {@code ClientTooltipComponent}'s immediate-mode {@code renderImage(Font, int, int,
 * GuiGraphics)}/{@code renderText(Font, int, int, Matrix4f, MultiBufferSource.BufferSource)}
 * became {@code extractImage(Font, int, int, int, int, GuiGraphicsExtractor)}/
 * {@code extractText(GuiGraphicsExtractor, Font, int, int)}, and {@code getHeight()} now takes the
 * {@code Font} (all confirmed via javap). Text colors are now full ARGB - vanilla tooltips pass
 * e.g. {@code 0xFFAAAAAA} - so the old {@code ChatFormatting.X.getColor()} RGB values (which the
 * old {@code Font.drawInBatch} silently made opaque) are wrapped in {@link ARGB#opaque(int)}.
 * {@code ChatFormatting.getColor()} itself is gone in 26.3; the same RGB values now live on
 * {@code TextColor.GRAY}/{@code RED}/{@code DARK_GRAY}.
 * Text was drawn with shadow before ({@code drawInBatch(..., true, ...)}); kept.
 */
public class ClientTiffinTooltip implements ClientTooltipComponent {
    private final TiffinContents contents;

    public ClientTiffinTooltip(TiffinContents contents) {
        this.contents = contents;
    }

    @Override
    public int getHeight(Font font) {
        return this.contents.item.isEmpty() ? 12 : this.contents.item.getCount() >= contents.CAPACITY ? 28 : 18;
    }

    @Override
    public int getWidth(Font font) {
        return 48;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        graphics.item(this.contents.getItemStack(), x, y);
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        if (this.contents.item.getCount() != 0) {
            graphics.text(font, this.contents.getCount() + "/" + this.contents.CAPACITY, x + 18, y + 3, ARGB.opaque(TextColor.GRAY.getValue()), true);
            if (this.contents.item.getCount() >= contents.CAPACITY) {
                graphics.text(font, Component.translatable("tooltip.bountifulfares.shulker_tiffin.full"), x + 2, y + 3 + 14, ARGB.opaque(TextColor.RED.getValue()), true);
            }
        } else {
            graphics.text(font, Component.translatable("tooltip.bountifulfares.shulker_tiffin.empty"), x, y, ARGB.opaque(TextColor.DARK_GRAY.getValue()), true);
        }
    }
}
