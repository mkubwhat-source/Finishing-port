package net.hecco.bountifulfares.definition.networking.payload;

import net.hecco.bountifulfares.definition.data.trellis.TrellisCropDefinition;
import net.hecco.bountifulfares.definition.data.trellis.TrellisPlantDefinition;
import net.hecco.bountifulfares.definition.networking.BFMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record TrellisSyncPayload(Map<Identifier, TrellisCropDefinition> crops, Map<Identifier, TrellisPlantDefinition> plants) implements CustomPacketPayload {
    public static final Type<TrellisSyncPayload> ID = new Type<>(BFMessages.TRELLIS_SYNC);
    public static final StreamCodec<FriendlyByteBuf, TrellisSyncPayload> CODEC =
            StreamCodec.ofMember(
                    TrellisSyncPayload::encode,
                    TrellisSyncPayload::decode
            );

    private static void encode(TrellisSyncPayload payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.crops.size());
        payload.crops.forEach((id, def) -> {
            buf.writeIdentifier(id);
            buf.writeJsonWithCodec(TrellisCropDefinition.CODEC, def);
        });
        buf.writeVarInt(payload.plants.size());
        payload.plants.forEach((id, def) -> {
            buf.writeIdentifier(id);
            buf.writeJsonWithCodec(TrellisPlantDefinition.CODEC, def);
        });
    }

    private static TrellisSyncPayload decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<Identifier, TrellisCropDefinition> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Identifier id = buf.readIdentifier();
            TrellisCropDefinition def = buf.readLenientJsonWithCodec(TrellisCropDefinition.CODEC);
            map.put(id, def);
        }

        int size2 = buf.readVarInt();
        Map<Identifier, TrellisPlantDefinition> map2 = new HashMap<>();
        for (int i = 0; i < size2; i++) {
            Identifier id = buf.readIdentifier();
            TrellisPlantDefinition def = buf.readLenientJsonWithCodec(TrellisPlantDefinition.CODEC);
            map2.put(id, def);
        }
        return new TrellisSyncPayload(map, map2);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return ID;
    }
}
