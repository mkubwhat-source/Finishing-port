package com.sidden.flavored.client.screen;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.menu.KegMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** 26.3: the recipe book wiring (buttons, input, ghost slots) is AbstractRecipeBookScreen's now. */
public class KegScreen extends AbstractRecipeBookScreen<KegMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/keg.png");
    private static final Identifier FERMENT_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "container/keg/ferment_progress");

    public KegScreen(KegMenu menu, Inventory inventory, Component title) {
        super(menu, new FlavoredRecipeBooks.Keg(menu), inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 7, this.height / 2 - 49);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        int offset = Mth.ceil(this.menu.getProgress() * 24.0F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FERMENT_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 86, this.topPos + 34, offset, 16);
    }
}
