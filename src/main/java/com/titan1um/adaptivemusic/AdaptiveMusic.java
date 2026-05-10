package com.titan1um.adaptivemusic;

import com.titan1um.adaptivemusic.network.AdaptiveMusicNetworking;
import com.titan1um.adaptivemusic.network.HomeRequestPayload;
import com.titan1um.adaptivemusic.network.HomeSyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public final class AdaptiveMusic implements ModInitializer {
    public static final String MOD_ID = "adaptivemusic";
    public static final String PACKAGE_ID = "com.titan1um.adaptivemusic";

    @Override
    public void onInitialize() {
        AdaptiveMusicNetworking.registerPayloadTypes();
        ServerPlayNetworking.registerGlobalReceiver(HomeRequestPayload.ID, (payload, context) -> sendHomeSync(context.player()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sendHomeSync(handler.player));
    }

    private static void sendHomeSync(ServerPlayerEntity player) {
        if (ServerPlayNetworking.canSend(player, HomeSyncPayload.ID)) {
            ServerPlayNetworking.send(player, new HomeSyncPayload(Optional.ofNullable(player.getSpawnPointPosition())));
        }
    }
}
