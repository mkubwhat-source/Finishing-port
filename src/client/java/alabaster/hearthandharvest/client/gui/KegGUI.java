package alabaster.hearthandharvest.client.gui;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.client.render.HHRenderUtil;
import alabaster.hearthandharvest.common.block.entity.KegBlockEntity;
import alabaster.hearthandharvest.common.block.entity.container.KegMenu;
import alabaster.hearthandharvest.common.registry.HHModSounds;
import alabaster.hearthandharvest.platform.fluid.FluidStack;
import alabaster.hearthandharvest.platform.fluid.FluidTank;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

/**
 * The keg screen. 26.3: the recipe book wiring (toggle button, input, ghost slots) is
 * {@link AbstractRecipeBookScreen}'s; rendering goes through GuiGraphicsExtractor; fluids are drawn
 * from their fluid model's still sprite (1.21.1 used NeoForge's IClientFluidTypeExtensions).
 */
public class KegGUI extends AbstractRecipeBookScreen<KegMenu> {
    private static final Identifier BACKGROUND = HearthAndHarvest.id("textures/gui/keg_gui.png");

    private static final int INPUT_TANK_X = 8;
    private static final int OUTPUT_TANK_X = 152;
    private static final int TANK_Y = 20;
    private static final int TANK_WIDTH = 16;
    private static final int TANK_HEIGHT = 63;
    private static final int FLUID_INSET = 0;
    private static final int FLUID_BUBBLE_COUNT = 9;
    private static final int FLUID_BUBBLE_COLOR = 0x1AFFFFFF;
    private static final int INPUT_TANK_OVERLAY_U = 192;
    private static final int OUTPUT_TANK_OVERLAY_U = 208;
    private static final int TANK_OVERLAY_V = 0;

    private static final int LEFT_BUBBLES_X = 54;
    private static final int RIGHT_BUBBLES_X = 114;
    private static final int BUBBLES_Y = 28;
    private static final int BUBBLES_WIDTH = 8;
    private static final int BUBBLES_HEIGHT = 47;
    private static final int BUBBLES_U = 176;
    private static final int BUBBLES_V = 3;

    private static final int MODE_BUTTON_X = 69;
    private static final int MODE_BUTTON_Y = 44;
    private static final int MODE_BUTTON_WIDTH = 38;
    private static final int MODE_BUTTON_HEIGHT = 18;

    private static final int RECIPE_BUTTON_X = 78;
    private static final int RECIPE_BUTTON_Y = 66;

    private Button modeButton;

    public KegGUI(KegMenu menu, Inventory inventory, Component title) {
        super(menu, new HHRecipeBooks.Keg(menu), inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.modeButton = this.addRenderableWidget(Button.builder(modeLabel(), button -> {
                    if (this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, KegMenu.MODE_BUTTON_ID);
                    }
                })
                .bounds(this.leftPos + MODE_BUTTON_X, this.topPos + MODE_BUTTON_Y, MODE_BUTTON_WIDTH, MODE_BUTTON_HEIGHT)
                .build());
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(HHModSounds.KEG_OPEN.get(), 0.8F, 0.5F));
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + RECIPE_BUTTON_X, this.topPos + RECIPE_BUTTON_Y);
    }

    @Override
    protected void onRecipeBookButtonClick() {
        if (this.modeButton != null) {
            this.modeButton.setPosition(this.leftPos + MODE_BUTTON_X, this.topPos + MODE_BUTTON_Y);
        }
    }

    private Component modeTooltip() {
        return Component.translatable("container.hearthandharvest.keg." + KegBlockEntity.MODE_NAMES[this.menu.getMode()] + ".tooltip");
    }

    private Component modeLabel() {
        return Component.translatable("container.hearthandharvest.keg." + KegBlockEntity.MODE_NAMES[this.menu.getMode()]);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.modeButton != null) {
            this.modeButton.setMessage(modeLabel());
            this.modeButton.setTooltip(Tooltip.create(modeTooltip()));
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        super.extractTooltip(gui, mouseX, mouseY);
        if (this.hoveredSlot != null && this.menu.getCarried().isEmpty() && this.hoveredSlot.getItem().isEmpty()) {
            Component label = slotLabel(this.hoveredSlot.index);
            if (label != null) gui.setTooltipForNextFrame(this.font, label, mouseX, mouseY);
        }
        this.renderTankTooltip(gui, mouseX, mouseY, INPUT_TANK_X, this.menu.blockEntity.getInputTank());
        this.renderTankTooltip(gui, mouseX, mouseY, OUTPUT_TANK_X, this.menu.blockEntity.getOutputTank());
        this.renderProgressTooltip(gui, mouseX, mouseY);
    }

    private static Component slotLabel(int slotIndex) {
        return switch (slotIndex) {
            case 2 -> Component.translatable("gui.hearthandharvest.keg.container_slot");
            case 3 -> Component.translatable("gui.hearthandharvest.keg.container_output_slot");
            default -> null;
        };
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(gui, mouseX, mouseY, partialTick);
        gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        renderTank(gui, INPUT_TANK_X, INPUT_TANK_OVERLAY_U, this.menu.blockEntity.getInputTank());
        renderFluidBubbles(gui);
        renderTank(gui, OUTPUT_TANK_X, OUTPUT_TANK_OVERLAY_U, this.menu.blockEntity.getOutputTank());
        renderProgressBubbles(gui);
    }

    private void renderFluidBubbles(GuiGraphicsExtractor gui) {
        if (!this.menu.isFermenting()) return;

        FluidTank tank = this.menu.blockEntity.getInputTank();
        if (tank.isEmpty()) return;

        int filled = Mth.clamp(TANK_HEIGHT * tank.getFluidAmount() / tank.getCapacity(), 1, TANK_HEIGHT);
        int surface = TANK_Y + TANK_HEIGHT - filled;
        long time = System.currentTimeMillis() / 60L;

        for (int i = 0; i < FLUID_BUBBLE_COUNT; i++) {
            int rise = (int) ((time / 2L + i * 11L) % filled);
            int y = TANK_Y + TANK_HEIGHT - 1 - rise;
            if (y <= surface) continue;

            int drift = (int) Math.round(Math.sin(time / 9.0D + i * 2.3D) * 2.0D);
            int x = INPUT_TANK_X + 3 + (i * 5) % (TANK_WIDTH - 6) + drift;
            gui.fill(this.leftPos + x, this.topPos + y, this.leftPos + x + 1, this.topPos + y + 1, FLUID_BUBBLE_COLOR);
        }
    }

    private void renderProgressBubbles(GuiGraphicsExtractor gui) {
        int filled = this.menu.getProgressScaled(BUBBLES_HEIGHT);
        if (filled <= 0) return;

        for (int x : new int[]{LEFT_BUBBLES_X, RIGHT_BUBBLES_X}) {
            gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND,
                    this.leftPos + x,
                    this.topPos + BUBBLES_Y + BUBBLES_HEIGHT - filled,
                    BUBBLES_U,
                    BUBBLES_V + BUBBLES_HEIGHT - filled,
                    BUBBLES_WIDTH,
                    filled, 256, 256);
        }
    }

    private void renderTank(GuiGraphicsExtractor gui, int x, int overlayU, FluidTank tank) {
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            renderFluid(gui, this.leftPos + x, this.topPos + TANK_Y, TANK_WIDTH, TANK_HEIGHT, FLUID_INSET, tank.getCapacity(), fluid);
        }
        gui.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos + x, this.topPos + TANK_Y, overlayU, TANK_OVERLAY_V, TANK_WIDTH, TANK_HEIGHT, 256, 256);
    }

    /** Fills a tank area bottom-up with the fluid's still sprite, tinted, in 16px tall slices. */
    static void renderFluid(GuiGraphicsExtractor gui, int left, int top, int tankWidth, int tankHeight, int inset, int capacity, FluidStack fluid) {
        TextureAtlasSprite sprite = HHRenderUtil.stillMaterial(fluid.getFluid()).sprite();
        int tint = HHRenderUtil.fluidColor(fluid.getFluid(), null, null);
        int height = tankHeight - inset * 2;
        int width = tankWidth - inset * 2;
        int filled = Mth.clamp(height * fluid.getAmount() / capacity, 1, height);

        int drawn = 0;
        while (drawn < filled) {
            int slice = Math.min(16, filled - drawn);
            gui.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, left + inset, top + inset + height - drawn - slice, width, slice, tint);
            drawn += slice;
        }
    }

    private void renderTankTooltip(GuiGraphicsExtractor gui, int mouseX, int mouseY, int x, FluidTank tank) {
        if (!this.isHovering(x, TANK_Y, TANK_WIDTH, TANK_HEIGHT, mouseX, mouseY)) return;

        FluidStack fluid = tank.getFluid();
        List<Component> lines = fluid.isEmpty()
                ? List.of(Component.translatable("container.hearthandharvest.keg.empty"))
                : List.of(fluid.getHoverName(),
                Component.translatable("container.hearthandharvest.keg.amount", fluid.getAmount(), tank.getCapacity()));
        gui.setTooltipForNextFrame(this.font, lines, Optional.empty(), mouseX, mouseY);
    }

    private void renderProgressTooltip(GuiGraphicsExtractor gui, int mouseX, int mouseY) {
        boolean hovering = this.isHovering(LEFT_BUBBLES_X, BUBBLES_Y, BUBBLES_WIDTH, BUBBLES_HEIGHT, mouseX, mouseY)
                || this.isHovering(RIGHT_BUBBLES_X, BUBBLES_Y, BUBBLES_WIDTH, BUBBLES_HEIGHT, mouseX, mouseY);
        if (!hovering) return;

        int remaining = this.menu.getRemainingSeconds();
        Component text = remaining <= 0
                ? Component.translatable("container.hearthandharvest.keg.idle")
                : Component.translatable("container.hearthandharvest.keg.remaining", remaining / 60, remaining % 60);
        gui.setTooltipForNextFrame(this.font, text, mouseX, mouseY);
    }
}
