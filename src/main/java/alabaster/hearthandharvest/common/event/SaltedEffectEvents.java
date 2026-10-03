package alabaster.hearthandharvest.common.event;

import alabaster.hearthandharvest.platform.event.HHEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import alabaster.hearthandharvest.common.advancement.HHSimpleTrigger;
import alabaster.hearthandharvest.common.registry.HHModTriggers;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.common.registry.HHModDataComponents;
import alabaster.hearthandharvest.common.tag.HHModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class SaltedEffectEvents {

    public static void register() {
        HHEvents.USE_ITEM_FINISH.register(SaltedEffectEvents::onItemUseFinish);
        ServerTickEvents.END_LEVEL_TICK.register(SaltedEffectEvents::onLevelTick);
    }

    private static void onItemUseFinish(LivingEntity entity, ItemStack stack) {
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!stack.has(HHModDataComponents.SALTED.get())) return;

        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) return;

        int bonusHunger = Math.max(1, Math.round(food.nutrition() * Config.SALTED_HUNGER_BONUS.get().floatValue()));
        player.getFoodData().eat(bonusHunger, 0f);

        float saturationGranted = food.nutrition() * food.saturation() * 2.0f;
        float newSaturation = Math.max(0f, player.getFoodData().getSaturationLevel() - saturationGranted * Config.SALTED_SATURATION_PENALTY.get().floatValue());
        player.getFoodData().setSaturation(newSaturation);

        HHSimpleTrigger.trigger(HHModTriggers.ATE_SALTED_FOOD, player);
    }

    private static void onLevelTick(ServerLevel level) {
        if (level.getGameTime() % 20 != 0) return;

        List<Slime> slimes = new ArrayList<>();
        level.getEntities(EntityTypeTest.forClass(Slime.class), Slime::isAlive, slimes);

        List<Slime> toHurt = new ArrayList<>();
        for (Slime slime : slimes) {
            AABB check = slime.getBoundingBox().inflate(0.001);
            boolean touchingSalt = BlockPos.betweenClosedStream(
                    BlockPos.containing(check.minX, check.minY, check.minZ),
                    BlockPos.containing(check.maxX, check.maxY, check.maxZ)
            ).anyMatch(pos -> level.getBlockState(pos).is(HHModTags.SALT_BLOCKS));
            if (touchingSalt) toHurt.add(slime);
        }
        for (Slime slime : toHurt) {
            slime.hurtServer(level, level.damageSources().magic(), 2.0f);
        }
    }
}