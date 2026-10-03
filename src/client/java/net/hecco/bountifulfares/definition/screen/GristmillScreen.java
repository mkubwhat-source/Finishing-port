package net.hecco.bountifulfares.definition.screen;

import net.hecco.bountifulfares.BountifulFares;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * 26.3: screens no longer render immediately - {@code renderBg(GuiGraphics, ...)}/
 * {@code render(...)} became {@code extractBackground(GuiGraphicsExtractor, ...)}/
 * {@code extractRenderState(...)}, and {@code RenderSystem.setShader/setShaderColor/
 * setShaderTexture} are gone (the render pipeline is passed to {@code blit} instead). This
 * mirrors vanilla's {@code HopperScreen.extractBackground} exactly (disassembled via javap):
 * {@code super.extractBackground(...)} then {@code blit(RenderPipelines.GUI_TEXTURED, texture,
 * x, y, u, v, w, h, 256, 256)}. The old {@code render()} override (which drew the background a
 * second time and called {@code renderTooltip}) is no longer needed: the base
 * {@code extractRenderState} already extracts contents, carried item and tooltip.
 */
public class GristmillScreen extends AbstractContainerScreen<GristmillMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "textures/gui/gristmill.png");
    private static final Identifier PROGRESS_ARROW = Identifier.fromNamespaceAndPath(BountifulFares.MOD_ID, "textures/gui/gristmill_progress_arrow.png");

    public GristmillScreen(GristmillMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        if (this.menu.isCrafting()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, PROGRESS_ARROW, this.leftPos + 69, this.topPos + 36, 0.0F, 0.0F, this.menu.getScaledProgress(), 14, 256, 256);
        }
    }
}
