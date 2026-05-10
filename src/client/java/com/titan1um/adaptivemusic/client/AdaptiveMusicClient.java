package com.titan1um.adaptivemusic.client;

import com.titan1um.adaptivemusic.client.config.AdaptiveMusicConfig;
import com.titan1um.adaptivemusic.client.music.AdaptiveMusicDirector;
import com.titan1um.adaptivemusic.network.HomeSyncPayload;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class AdaptiveMusicClient implements ClientModInitializer {
    private static AdaptiveMusicDirector director;

    @Override
    public void onInitializeClient() {
        AdaptiveMusicConfig.get();
        director = new AdaptiveMusicDirector(MinecraftClient.getInstance());
        ClientPlayNetworking.registerGlobalReceiver(HomeSyncPayload.ID, (payload, context) ->
                context.client().execute(() -> director.applyServerHome(payload.homePos())));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> director.prepareForServerJoin());
        ClientTickEvents.END_CLIENT_TICK.register(client -> director.tick());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("adaptivemusic")
                    .executes(context -> openConfig())
                    .then(literal("config").executes(context -> openConfig()))
                    .then(literal("status").executes(context -> status(context.getSource())))
                    .then(literal("home").executes(context -> setHome(context.getSource())))
                    .then(literal("settrack")
                            .then(argument("mood", StringArgumentType.word())
                                    .then(argument("sound", StringArgumentType.string())
                                            .executes(context -> setTrack(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "mood"),
                                                    StringArgumentType.getString(context, "sound")))))));
            dispatcher.register(literal("am")
                    .executes(context -> openConfig())
                    .then(literal("config").executes(context -> openConfig()))
                    .then(literal("status").executes(context -> status(context.getSource())))
                    .then(literal("home").executes(context -> setHome(context.getSource()))));
        });
    }

    public static AdaptiveMusicDirector director() {
        return director;
    }

    private static int openConfig() {
        MinecraftClient.getInstance().execute(() -> director.openConfig());
        return 1;
    }

    private static int status(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(director.statusLine()));
        return 1;
    }

    private static int setHome(FabricClientCommandSource source) {
        director.markHomeHere();
        source.sendFeedback(Text.literal("Adaptive Music home anchor saved at your current position."));
        return 1;
    }

    private static int setTrack(FabricClientCommandSource source, String mood, String sound) {
        AdaptiveMusicConfig config = AdaptiveMusicConfig.get();
        config.music.put(mood, sound);
        config.save();
        source.sendFeedback(Text.literal("Adaptive Music track for '" + mood + "' set to " + sound));
        return 1;
    }
}
