package com.sidden.flavored.client.screen;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.menu.OvenMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class OvenScreen extends AbstractRecipeBookScreen<OvenMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/oven.png");
    private static final Identifier LIT_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "container/oven/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "container/oven/burn_progress");

    public OvenScreen(OvenMenu menu, Inventory inventory, Component title) {
        super(menu, new FlavoredRecipeBooks.Oven(menu), inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        if (this.menu.isLit()) {
            int lit = Mth.ceil(this.menu.getLitProgress() * 13.0F) + 1;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - lit, this.leftPos + 126, this.topPos + 40 + 14 - lit, 14, lit);
        }
        int burn = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 89, this.topPos + 17, burn, 16);
    }
}
