package alabaster.hearthandharvest.client.gui;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.block.entity.container.CaskMenu;
import alabaster.hearthandharvest.common.utilities.HHTextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.awt.Rectangle;

/** The cask screen (26.3: recipe book wiring from {@link AbstractRecipeBookScreen}, GuiGraphicsExtractor rendering). */
public class CaskGUI extends AbstractRecipeBookScreen<CaskMenu> {
    private static final Identifier BACKGROUND_TEXTURE = HearthAndHarvest.id("textures/gui/cask_gui.png");

    public static final Rectangle DIM_LIGHT = new Rectangle(72, 13, 13, 16);
    public static final Rectangle MEDIUM_LIGHT = new Rectangle(72, 13, 13, 16);
    public static final Rectangle BRIGHT_LIGHT = new Rectangle(72, 13, 13, 16);

    private static final Rectangle LEFT_BUBBLE = new Rectangle(10, 55, 9, 24);
    private static final Rectangle RIGHT_BUBBLE = new Rectangle(58, 54, 9, 24);
    private static final int LEFT_BUBBLE_U = 176;
    private static final int RIGHT_BUBBLE_U = 186;
    private static final int BUBBLE_V_BOTTOM = 111;

    private static final Rectangle TIMER_ICON = new Rectangle(94, 16, 8, 11);

    private static final int SEAL_BUTTON_X = 66;
    private static final int SEAL_BUTTON_Y = 56;
    private static final int SEAL_BUTTON_WIDTH = 44;
    private static final int SEAL_BUTTON_HEIGHT = 16;

    private static final int RECIPE_BUTTON_X = 78;
    private static final int RECIPE_BUTTON_Y = 33;

    private static final int LOCKED_AREA_SIZE = 36;
    private static final int LOCKED_SLOT_COLOR = 0x80C6C6C6;
    private static final int INPUT_AREA_X = 20;
    private static final int INPUT_AREA_Y = 25;
    private static final int OUTPUT_AREA_X = 118;
    private static final int OUTPUT_AREA_Y = 25;

    private Button sealButton;

    public CaskGUI(CaskMenu menu, Inventory playerInventory, Component title) {
        super(menu, new HHRecipeBooks.Cask(menu), playerInventory, title);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = 8;
        this.sealButton = this.addRenderableWidget(Button.builder(sealButtonLabel(), button -> {
                    if (this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, CaskMenu.SEAL_BUTTON_ID);
                    }
                })
                .bounds(this.leftPos + SEAL_BUTTON_X, this.topPos + SEAL_BUTTON_Y, SEAL_BUTTON_WIDTH, SEAL_BUTTON_HEIGHT)
                .build());
        updateSealButton();
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + RECIPE_BUTTON_X, this.topPos + RECIPE_BUTTON_Y);
    }

    @Override
    protected void onRecipeBookButtonClick() {
        updateSealButton();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        updateSealButton();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(gui, mouseX, mouseY, partialTicks);
        gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Bubbles
        if (menu.getProgression() != 0) {
            int bubScale = (int) ((((this.menu.getProgression() / 80)) * LEFT_BUBBLE.height) % (LEFT_BUBBLE.height + 1));
            gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos + LEFT_BUBBLE.x, this.topPos + LEFT_BUBBLE.y - bubScale, LEFT_BUBBLE_U, BUBBLE_V_BOTTOM - bubScale, LEFT_BUBBLE.width, bubScale + 1, 256, 256);
            gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos + RIGHT_BUBBLE.x, this.topPos + RIGHT_BUBBLE.y - bubScale, RIGHT_BUBBLE_U, BUBBLE_V_BOTTOM - bubScale, RIGHT_BUBBLE.width, bubScale + 1, 256, 256);
        }

        // Light indicator
        int light = this.menu.blockEntity.getCurrentLightLevel();
        if (light <= 5) {
            gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos + DIM_LIGHT.x, this.topPos + DIM_LIGHT.y, 176, 64, DIM_LIGHT.width, DIM_LIGHT.height, 256, 256);
        } else if (light <= 10) {
            gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos + MEDIUM_LIGHT.x, this.topPos + MEDIUM_LIGHT.y, 176, 48, MEDIUM_LIGHT.width, MEDIUM_LIGHT.height, 256, 256);
        } else {
            gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos + BRIGHT_LIGHT.x, this.topPos + BRIGHT_LIGHT.y, 176, 32, BRIGHT_LIGHT.width, BRIGHT_LIGHT.height, 256, 256);
        }
    }

    @Override
    protected void extractSlots(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        super.extractSlots(gui, mouseX, mouseY);
        // Sealed: grey out the input and output grids over their items (1.21.1 drew this at z=300 after the screen).
        if (!this.menu.blockEntity.isSealed()) return;
        gui.nextStratum();
        gui.fill(INPUT_AREA_X, INPUT_AREA_Y, INPUT_AREA_X + LOCKED_AREA_SIZE, INPUT_AREA_Y + LOCKED_AREA_SIZE, LOCKED_SLOT_COLOR);
        gui.fill(OUTPUT_AREA_X, OUTPUT_AREA_Y, OUTPUT_AREA_X + LOCKED_AREA_SIZE, OUTPUT_AREA_Y + LOCKED_AREA_SIZE, LOCKED_SLOT_COLOR);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        super.extractTooltip(gui, mouseX, mouseY);
        renderLightTooltip(gui, mouseX, mouseY);
        renderProgressTooltip(gui, mouseX, mouseY);
    }

    private void renderProgressTooltip(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        if (!this.isHovering(TIMER_ICON.x, TIMER_ICON.y, TIMER_ICON.width, TIMER_ICON.height, mouseX, mouseY)) return;

        int remaining = this.menu.getRemainingSeconds();
        Component text = remaining <= 0
                ? Component.translatable("container.hearthandharvest.cask.idle")
                : Component.translatable("container.hearthandharvest.cask.remaining", formatDuration(remaining));
        gui.setTooltipForNextFrame(this.font, text, mouseX, mouseY);
    }

    private static Component formatDuration(int seconds) {
        if (seconds >= 3600) {
            return Component.translatable("container.hearthandharvest.cask.hours", seconds / 3600, (seconds % 3600) / 60);
        }
        if (seconds >= 60) {
            return Component.translatable("container.hearthandharvest.cask.minutes", seconds / 60);
        }
        return Component.translatable("container.hearthandharvest.cask.seconds", seconds);
    }

    private void renderLightTooltip(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        if (this.isHovering(DIM_LIGHT.x, DIM_LIGHT.y, DIM_LIGHT.width, DIM_LIGHT.height, mouseX, mouseY)) {
            int light = this.menu.blockEntity.getCurrentLightLevel();
            String key = light <= 5 ? "container.cask.dim" : light <= 10 ? "container.cask.medium" : "container.cask.bright";
            gui.setTooltipForNextFrame(this.font, HHTextUtils.getTranslation(key), mouseX, mouseY);
        }
    }

    private Component sealButtonLabel() {
        return Component.translatable(this.menu.blockEntity.isSealed()
                ? "container.hearthandharvest.cask.unseal"
                : "container.hearthandharvest.cask.seal");
    }

    private void updateSealButton() {
        if (this.sealButton == null) return;
        this.sealButton.setPosition(this.leftPos + SEAL_BUTTON_X, this.topPos + SEAL_BUTTON_Y);
        this.sealButton.setMessage(sealButtonLabel());
        this.sealButton.setTooltip(Tooltip.create(Component.translatable(this.menu.blockEntity.isSealed()
                ? "container.hearthandharvest.cask.sealed.tooltip"
                : "container.hearthandharvest.cask.seal.tooltip")));
    }
}
