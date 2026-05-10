package com.titan1um.adaptivemusic.network;

import com.titan1um.adaptivemusic.AdaptiveMusic;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record HomeSyncPayload(Optional<BlockPos> homePos) implements CustomPayload {
    public static final CustomPayload.Id<HomeSyncPayload> ID = new CustomPayload.Id<>(Identifier.of(AdaptiveMusic.MOD_ID, "home_sync"));
    public static final PacketCodec<RegistryByteBuf, HomeSyncPayload> CODEC = PacketCodecs.optional(BlockPos.PACKET_CODEC)
            .xmap(HomeSyncPayload::new, HomeSyncPayload::homePos)
            .cast();

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
