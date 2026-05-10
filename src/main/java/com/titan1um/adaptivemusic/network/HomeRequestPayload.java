package com.titan1um.adaptivemusic.network;

import com.titan1um.adaptivemusic.AdaptiveMusic;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HomeRequestPayload() implements CustomPayload {
    public static final HomeRequestPayload INSTANCE = new HomeRequestPayload();
    public static final CustomPayload.Id<HomeRequestPayload> ID = new CustomPayload.Id<>(Identifier.of(AdaptiveMusic.MOD_ID, "home_request"));
    public static final PacketCodec<RegistryByteBuf, HomeRequestPayload> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
