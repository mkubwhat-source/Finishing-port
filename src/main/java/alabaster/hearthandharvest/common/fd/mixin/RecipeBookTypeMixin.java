package alabaster.hearthandharvest.common.fd.mixin;

import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeBookType.class)
public enum RecipeBookTypeMixin {
	HEARTHANDHARVEST_COOKING,
	HEARTHANDHARVEST_FERMENTING,
	HEARTHANDHARVEST_AGING
}
