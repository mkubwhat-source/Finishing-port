package com.sidden.flavored.client.screen;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.menu.MixingBowlMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MixingBowlScreen extends AbstractRecipeBookScreen<MixingBowlMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/mixing_bowl.png");
    private static final Identifier MIX_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "container/mixing_bowl/mix_progress");
    private static final Identifier VALID_SPRITE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "container/mixing_bowl/valid");

    public MixingBowlScreen(MixingBowlMenu menu, Inventory inventory, Component title) {
        super(menu, new FlavoredRecipeBooks.MixingBowl(menu), inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 11, this.height / 2 - 49);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, MIX_PROGRESS_SPRITE, 27, 4, 0, 0, this.leftPos + 102, this.topPos + 31, this.menu.getMixProgress(), 4);
        if (this.menu.shouldDisplayCheckmark()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, VALID_SPRITE, this.leftPos + 108, this.topPos + 64, 10, 10);
        }
    }
}
