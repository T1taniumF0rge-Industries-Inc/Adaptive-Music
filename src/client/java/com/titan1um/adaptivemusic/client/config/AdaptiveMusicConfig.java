package com.titan1um.adaptivemusic.client.config;

import com.titan1um.adaptivemusic.AdaptiveMusic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class AdaptiveMusicConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(AdaptiveMusic.MOD_ID + ".json");

    public boolean enabled = true;
    public boolean showDebugToasts = true;
    public int scanRadius = 24;
    public int spawnRadius = 96;
    public int homeRadius = 32;
    public int deathMemoryThreshold = 3;
    public int switchCooldownSeconds = 45;
    public double dangerWeight = 1.0;
    public double memoryWeight = 1.0;
    public double homeWeight = 1.0;
    public Map<String, String> music = defaultMusic();

    private static AdaptiveMusicConfig INSTANCE;

    public static AdaptiveMusicConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static AdaptiveMusicConfig load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                AdaptiveMusicConfig config = GSON.fromJson(reader, AdaptiveMusicConfig.class);
                if (config != null) {
                    config.repair();
                    return config;
                }
            } catch (IOException ignored) {
                // Fall through to defaults. Config writes are best-effort for this barebone build.
            }
        }

        AdaptiveMusicConfig config = new AdaptiveMusicConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
            // Keep gameplay uninterrupted if the config folder is not writable.
        }
    }

    public Optional<SoundEvent> soundFor(String mood) {
        String rawId = music.get(mood);
        if (rawId == null || rawId.isBlank()) {
            return Optional.empty();
        }
        Identifier id = Identifier.tryParse(rawId);
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(Registries.SOUND_EVENT.get(id));
    }

    public void toggleEnabled() {
        enabled = !enabled;
        save();
    }

    public void toggleDebugToasts() {
        showDebugToasts = !showDebugToasts;
        save();
    }

    private void repair() {
        if (music == null) {
            music = defaultMusic();
        } else {
            defaultMusic().forEach(music::putIfAbsent);
        }
        scanRadius = clamp(scanRadius, 8, 96);
        spawnRadius = clamp(spawnRadius, 16, 512);
        homeRadius = clamp(homeRadius, 8, 128);
        deathMemoryThreshold = clamp(deathMemoryThreshold, 1, 20);
        switchCooldownSeconds = clamp(switchCooldownSeconds, 5, 600);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Map<String, String> defaultMusic() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("spawn_nostalgia", "minecraft:music.overworld.forest");
        defaults.put("danger", "minecraft:music.overworld.deep_dark");
        defaults.put("death_memory", "minecraft:music.overworld.dripstone_caves");
        defaults.put("home", "minecraft:music.overworld.grove");
        defaults.put("social", "minecraft:music.overworld.flower_forest");
        defaults.put("default", "minecraft:music.overworld.forest");
        return defaults;
    }
}
