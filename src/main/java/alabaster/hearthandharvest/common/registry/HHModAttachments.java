package alabaster.hearthandharvest.common.registry;

import alabaster.hearthandharvest.HearthAndHarvest;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Hearth and Harvest's entity data, as Fabric data attachments (1.21.1: NeoForge attachments with
 * the same defaults and serialization). Read with {@code getAttachedOrGet(type, type.initializer())} (the default
 * when unset, like NeoForge's {@code getData}), written with {@code setAttached}.
 */
public class HHModAttachments {
    /** Ticks remaining until a fed animal drops manure. 0 = no pending drop. */
    public static final AttachmentType<Integer> MANURE_POOP_TIMER = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0).persistent(Codec.INT).buildAndRegister(id("manure_poop_timer"));
    /** Game time of the player's last manual poop. -300 default = no cooldown on first use. */
    public static final AttachmentType<Long> PLAYER_LAST_POOP_TIME = AttachmentRegistry.<Long>builder()
            .initializer(() -> -300L).persistent(Codec.LONG).buildAndRegister(id("player_last_poop_time"));
    /** Ticks remaining for fly particles to follow an entity hit by a manure projectile. */
    public static final AttachmentType<Integer> MANURE_FLY_TICKS = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0).persistent(Codec.INT).buildAndRegister(id("manure_fly_ticks"));
    /** Ticks remaining before a chicken can be plucked again. 0 = ready. */
    public static final AttachmentType<Integer> PLUCK_COOLDOWN = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0).persistent(Codec.INT).buildAndRegister(id("pluck_cooldown"));
    /** Packed position of the nest a chicken is walking to. 0 = no claim. Not saved. */
    public static final AttachmentType<Long> CLAIMED_NEST = AttachmentRegistry.<Long>builder()
            .initializer(() -> 0L).buildAndRegister(id("claimed_nest"));
    /** The equipped horseshoe stack (keeps enchantments on removal). Empty = none. */
    public static final AttachmentType<ItemStack> HORSESHOE_ITEM = AttachmentRegistry.<ItemStack>builder()
            .initializer(() -> ItemStack.EMPTY).persistent(ItemStack.OPTIONAL_CODEC)
            .syncWith(ItemStack.OPTIONAL_STREAM_CODEC, AttachmentSyncPredicate.all()).buildAndRegister(id("horseshoe_item"));

    /**
     * Which shoulders of a player carry a crow (bit 1 = left, bit 2 = right). 26.3 only syncs parrot
     * variants for shoulder entities, so crows are synced through this (kept up to date by
     * {@code ServerPlayerShoulderMixin}) for the shoulder render layer. Not saved: recomputed on load.
     */
    public static final AttachmentType<Integer> SHOULDER_CROWS = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0).syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all()).buildAndRegister(id("shoulder_crows"));

    /** Y where a player holding a chicken started gliding (1.21.1: player persistent data). Unset when not gliding. */
    public static final AttachmentType<Double> GLIDE_START_Y = AttachmentRegistry.<Double>builder()
            .persistent(Codec.DOUBLE).buildAndRegister(id("glide_start_y"));

    /** Set on items a crow delivered to its owner (1.21.1: entity persistent data), so crows don't fetch them again. */
    public static final AttachmentType<Boolean> CROW_DELIVERED = AttachmentRegistry.<Boolean>builder()
            .persistent(Codec.BOOL).buildAndRegister(id("crow_delivered"));

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(HearthAndHarvest.MODID, path);
    }

    public static void init() {
    }
}
