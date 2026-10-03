package com.sidden.flavored.registry;

import com.mojang.serialization.Codec;
import com.sidden.flavored.Flavored;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

/**
 * Per-player chocolate addiction state. 1.21.1 used NeoForge data attachments (serialized, not
 * copied on death); these are Fabric data attachments with the same ids, codec and lifetime.
 */
public final class FlavoredDataAttachments {
    public static final AttachmentType<Integer> CHOCOLATE_ADDICTION = AttachmentRegistry.<Integer>builder()
            .initializer(() -> 0).persistent(Codec.INT)
            .buildAndRegister(Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "chocolate_addiction"));
    public static final AttachmentType<Long> LAST_DRAIN_TICK = AttachmentRegistry.<Long>builder()
            .initializer(() -> 0L).persistent(Codec.LONG)
            .buildAndRegister(Identifier.fromNamespaceAndPath(Flavored.MOD_ID, "last_drain_tick"));

    public static void init() {
    }

    private FlavoredDataAttachments() {
    }
}
