package com.sidden.flavored.client;

import com.sidden.flavored.Flavored;
import com.sidden.flavored.registry.FlavoredEffects;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Random;

/**
 * The Sugar Crave hunger bar: chocolate-colored food icons drawn over the vanilla food bar while
 * the effect is active (credits in 1.21.1: AppleSkin / Farmer's Delight overlay approach).
 * <p>
 * 26.3/Fabric: registered after the vanilla food bar with Fabric's HUD element registry (1.21.1:
 * NeoForge gui layers). The food bar is the first right-hand status bar, drawn 39 px above the
 * bottom (NeoForge's {@code rightHeight} at that point).
 */
public final class SugarCraveHud {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "sugar_crave");
    private static final Identifier MOD_ICONS_TEXTURE = Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "textures/gui/flavored_icons.png");
    private static final int FOOD_BAR_OFFSET = 39;

    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.FOOD_BAR, ID, SugarCraveHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.gui.hud.isHidden() || minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) return;
        if (player.getVehicle() instanceof LivingEntity) return; // vanilla shows mount health instead of food
        if (player.getEffect(FlavoredEffects.SUGAR_CRAVE) == null) return;

        FoodData stats = player.getFoodData();
        boolean naturalHealing = minecraft.getSingleplayerServer() != null
                && minecraft.getSingleplayerServer().overworld().getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)
                && player.isHurt() && stats.getFoodLevel() >= 18;
        int right = graphics.guiWidth() / 2 + 91;
        int top = graphics.guiHeight() - FOOD_BAR_OFFSET;
        draw(stats, player.tickCount, graphics, right, top, naturalHealing);
    }

    private static void draw(FoodData foodData, int ticks, GuiGraphicsExtractor graphics, int right, int top, boolean naturalHealing) {
        float saturation = foodData.getSaturationLevel();
        int foodLevel = foodData.getFoodLevel();
        Random rand = new Random();
        rand.setSeed(ticks * 312871L);

        for (int j = 0; j < 10; ++j) {
            int x = right - j * 8 - 9;
            int y = top;
            if (saturation <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, MOD_ICONS_TEXTURE, x, y, 0, 0, 9, 9, 256, 256);

            float effectiveHungerOfBar = foodLevel / 2.0F - j;
            int naturalHealingOffset = naturalHealing ? 18 : 0;
            if (effectiveHungerOfBar >= 1) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, MOD_ICONS_TEXTURE, x, y, 18 + naturalHealingOffset, 0, 9, 9, 256, 256);
            } else if (effectiveHungerOfBar >= .5) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, MOD_ICONS_TEXTURE, x, y, 9 + naturalHealingOffset, 0, 9, 9, 256, 256);
            }
        }
    }

    private SugarCraveHud() {
    }
}
