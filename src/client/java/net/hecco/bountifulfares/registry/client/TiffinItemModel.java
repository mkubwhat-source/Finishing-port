package net.hecco.bountifulfares.registry.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.item.component.TiffinContents;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.mixin.util.ItemStackRenderStateAccessor;
import net.hecco.bountifulfares.mixin.util.LayerRenderStateAccessor;
import net.hecco.bountifulfares.registry.content.BFComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * Item model type {@code bountifulfares:tiffin} - the 26.3 replacement for BOTH of the old
 * tiffin rendering mixins, whose targets no longer exist ({@code ItemRenderer} is gone entirely,
 * and {@code GuiGraphics} became {@code GuiGraphicsExtractor}, which no longer draws items itself
 * but just asks {@link ItemModelResolver} to fill an {@link ItemStackRenderState}). In 26.3 every
 * item render - GUI, hands, frames, ground - goes through an item's data-driven
 * {@link ItemModel}, so the tiffin behavior now lives in its own model type, the same way vanilla's
 * bundle draws its selected item ({@code BundleSelectedItemSpecialRenderer.update} calls
 * {@code resolver.appendItemLayers(state, nestedStack, ...)}, confirmed via {@code javap -c}).
 * <p>
 * Behavior reproduced exactly from the old mixins (1.21.1):
 * <ul>
 *   <li>Hand contexts (old {@code ItemRendererMixin.renderStatic}), config
 *   {@code showTiffinFoodInHand}: render the contained food instead of the tiffin.</li>
 *   <li>GUI, config {@code tiffinCornerFoodIcon} off (old {@code GuiGraphicsMixin}): when the
 *   stack is the player's main- or off-hand stack, draw {@code back}, then the food, then
 *   {@code front} (the food appears inside the open tiffin).</li>
 *   <li>GUI, {@code tiffinCornerFoodIcon} on: draw the tiffin, then the food at half size in the
 *   corner - the old code wrapped the food render in {@code scale(0.5, 0.5, 1)} +
 *   {@code translate(0.5, -0.3, 0)} applied BEFORE the item's display transform; layers now
 *   draw as {@code pose * display * local}, so each appended layer's local transform is
 *   rewritten to {@code display^-1 * M * display * local}, which yields exactly
 *   {@code pose * M * display * local}.</li>
 * </ul>
 * All of it only applies with a level, a living owner and a non-zero seed, as before (the seed
 * check is what limited the old GUI behavior to real inventory/hotbar slots).
 */
public final class TiffinItemModel implements ItemModel {
    public static final Identifier ID = BountifulFares.id("tiffin");
    private static final Matrix4fc CORNER_ICON = new Matrix4f().scale(0.5F, 0.5F, 1.0F).translate(0.5F, -0.3F, 0.0F);

    private enum Variant { HAND, HELD_GUI, CORNER_GUI }

    private final ItemModel model;
    private final ItemModel back;
    private final ItemModel front;

    public TiffinItemModel(ItemModel model, ItemModel back, ItemModel front) {
        this.model = model;
        this.back = back;
        this.front = front;
    }

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext context,
                       ClientLevel level, ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        TiffinContents contents = stack.get(BFComponents.TIFFIN_CONTENTS.get());
        ItemStack food = contents == null ? ItemStack.EMPTY : contents.getItemStack();
        if (!food.isEmpty() && level != null && owner instanceof LivingEntity && seed != 0) {
            if (isHandContext(context)) {
                if (Services.PLATFORM.get().getBoolConfigValue("showTiffinFoodInHand")) {
                    state.appendModelIdentityElement(Variant.HAND);
                    resolver.appendItemLayers(state, food, context, level, owner, seed);
                    return;
                }
            } else if (context == ItemDisplayContext.GUI) {
                if (!Services.PLATFORM.get().getBoolConfigValue("tiffinCornerFoodIcon")) {
                    if (owner instanceof Player player && (player.getMainHandItem() == stack || player.getOffhandItem() == stack)) {
                        state.appendModelIdentityElement(Variant.HELD_GUI);
                        this.back.update(state, stack, resolver, context, level, owner, seed);
                        resolver.appendItemLayers(state, food, context, level, owner, seed);
                        this.front.update(state, stack, resolver, context, level, owner, seed);
                        return;
                    }
                } else {
                    state.appendModelIdentityElement(Variant.CORNER_GUI);
                    this.model.update(state, stack, resolver, context, level, owner, seed);
                    int first = ((ItemStackRenderStateAccessor) state).bountifulfares$getActiveLayerCount();
                    resolver.appendItemLayers(state, food, context, level, owner, seed);
                    applyCornerTransform(state, first, context);
                    return;
                }
            }
        }
        this.model.update(state, stack, resolver, context, level, owner, seed);
    }

    private static boolean isHandContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    private static void applyCornerTransform(ItemStackRenderState state, int firstLayer, ItemDisplayContext context) {
        ItemStackRenderStateAccessor accessor = (ItemStackRenderStateAccessor) state;
        ItemStackRenderState.LayerRenderState[] layers = accessor.bountifulfares$getLayers();
        int end = accessor.bountifulfares$getActiveLayerCount();
        for (int i = firstLayer; i < end; i++) {
            LayerRenderStateAccessor layer = (LayerRenderStateAccessor) layers[i];
            ItemTransform itemTransform = layer.bountifulfares$getItemTransform();
            PoseStack.Pose displayPose = new PoseStack().last();
            if (itemTransform != null) {
                itemTransform.apply(context.leftHand(), displayPose);
            }
            Matrix4f display = new Matrix4f(displayPose.pose());
            Matrix4f local = layer.bountifulfares$getLocalTransform();
            Matrix4f result = new Matrix4f(display).invert().mul(CORNER_ICON).mul(display).mul(local);
            local.set(result);
        }
    }

    public record Unbaked(ItemModel.Unbaked model, ItemModel.Unbaked back, ItemModel.Unbaked front) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemModels.CODEC.fieldOf("model").forGetter(Unbaked::model),
                ItemModels.CODEC.fieldOf("back").forGetter(Unbaked::back),
                ItemModels.CODEC.fieldOf("front").forGetter(Unbaked::front)
        ).apply(instance, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            return new TiffinItemModel(this.model.bake(context, transformation), this.back.bake(context, transformation), this.front.bake(context, transformation));
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            this.model.resolveDependencies(resolver);
            this.back.resolveDependencies(resolver);
            this.front.resolveDependencies(resolver);
        }
    }

    private static boolean bootstrapped = false;

    /** Registers {@code bountifulfares:tiffin} - must run before item model JSON is parsed or datagen serializes it. */
    public static synchronized void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;
            ItemModels.ID_MAPPER.put(ID, Unbaked.MAP_CODEC);
        }
    }
}
