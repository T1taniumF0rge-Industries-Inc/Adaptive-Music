package com.titan1um.adaptivemusic.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class AdaptiveMusicNetworking {
    private AdaptiveMusicNetworking() {
    }

    public static void registerPayloadTypes() {
        PayloadTypeRegistry.playS2C().register(HomeSyncPayload.ID, HomeSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HomeRequestPayload.ID, HomeRequestPayload.CODEC);
    }
}
